package seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import entity.TbSeckillOrder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pay.service.WeixinPayService;
import seckill.service.SeckillOrderService;
import vo.Result;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/pay")
@RestController
public class payController {

    @Reference
    private SeckillOrderService seckillOrderService;

    @Reference
    private WeixinPayService weixinPayService;

    //根据支付日志id到微信支付创建支付订单并返回支付二维码地址等信息
    @GetMapping("/createNative")
    public Map<String,String> createNative(String outTradeNo){
        //根据订单id查询放在redis中的订单
        TbSeckillOrder order = seckillOrderService.getSeckilOrderInRedisByOrderId(outTradeNo);
        if(order != null){
            //到支付系统进行提交订单并返回支付地址
            String totalFee = (long)(order.getMoney().doubleValue() * 100)+"";
            System.out.println();
            System.out.println("totalFee ==============" + totalFee);
            System.out.println();
            //上传支付订单号和交易金额生成支付二维码
            return weixinPayService.createNative(outTradeNo,totalFee);
        }
        return new HashMap<>();
    }

    //根据订单id查询订单支付状态
    @GetMapping("/queryPayStatus")
    public Result queryPayStatus(String outTradeNo){
        Result result = Result.fail("支付失败！");

        try{
            int count = 0;
            //不停查询
            while(true){
                System.out.println();
                System.out.println("count ==============="+count);
                System.out.println();
                //到微信支付查询支付状态
                Map<String, String> resultMap = weixinPayService.queryPayStatus(outTradeNo);

                if(resultMap == null){
                    break;
                }
                if("SUCCESS".equals(resultMap.get("trade_state"))){
                    result = Result.ok("支付成功！");
                    //更新订单的支付状态
                    seckillOrderService.saveOrderInRedisToDb(outTradeNo,resultMap.get("transaction_id"));

                    break;
                }

                //每3秒查询一次
                Thread.sleep(3000);

                //在1分钟之内每3秒查询一次，如果过了1分钟返回支付超市页面
                count++;
                if(count > 3){
                    result = Result.fail("支付超时！");


                    //关闭微信支付的订单
                    resultMap = weixinPayService.closeOrder(outTradeNo);
                    //如果再关闭中支付了，那么标识为支付成功
                    if(resultMap != null && "ORDERPAID".equals(resultMap.get("err_code"))){
                        seckillOrderService.saveOrderInRedisToDb(outTradeNo,resultMap.get("transaction_id"));
                        break;
                    }
                    System.out.println();
                    System.out.println("11111111111111111111111111");
                    System.out.println();
                    //删除redis中订单
                    seckillOrderService.deleteOrderInRedis(outTradeNo);
                    break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
}
