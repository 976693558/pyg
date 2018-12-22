package cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import entity.TbPayLog;
import order.service.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pay.service.WeixinPayService;
import vo.Result;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/pay")
@RestController
public class payController {

    @Reference
    private OrderService orderService;

    @Reference
    private WeixinPayService weixinPayService;

    //根据支付日志id找到微信支付创建支付订单返回支付二维码地址等信息
    @GetMapping("/createNative")
    public Map<String,String> createNative(String outTradeNo){

        //查找支付日志信息
        TbPayLog tbPayLog = orderService.findPatLoByOutTradeNo(outTradeNo);
        if(tbPayLog != null){
            //不为空，到支付系统进行提交订单并返回支付地址
            return weixinPayService.createNative(outTradeNo,tbPayLog.getTotalFee().toString());
        }
        return new HashMap<>();
    }

    //根据支付日志id查询订单状态
    @GetMapping("/queryPayStatus")
    public Result queryPayStatus(String outTradeNo){
        Result result = Result.fail("支付失败！");
        try{
            //统计请求次数
            int count = 0;
            //不断查询
            while(true){
                //到微信支付查询支付状态
                Map<String,String> resultMap = weixinPayService.queryPayStatus(outTradeNo);
                //未支付
                if(resultMap == null){
                    break;
                }
                //已支付
                if("SUCCESS".equals(resultMap.get("trade_state"))){
                    result = Result.ok("支付成功！");
                    //需要更新订单，支付日志支付状态
                    orderService.updateOrderStatus(outTradeNo,resultMap.get("transaction_id"));

                    break;
                }

                //每3秒钟查询一次
                Thread.sleep(3000);

                //3分钟之内每3秒查询一次，超过3分钟返回二维码超时页面中自动生成新的二维码
                count++;
                if(count > 60){
                    result = Result.fail("二维码超时");
                    break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
}
