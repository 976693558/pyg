package pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.beans.factory.annotation.Value;
import pay.service.WeixinPayService;
import util.HttpClient;

import java.util.HashMap;
import java.util.Map;

@Service(interfaceClass = WeixinPayService.class)
public class WeixinPayServiceImpl implements WeixinPayService {

    @Value("${appid}")
    private String appid;

    @Value("${partner}")
    private String mch_id;

    @Value("${partnerkey}")
    private String partnerkey;

    @Value("${notifyurl}")
    private String notify_url;

    @Override
    public Map<String, String> createNative(String outTradeNo, String totalFee) {
        Map<String,String> returnMap = new HashMap<>();
        try{
            //组合要发送到微信支付的参数
            Map<String,String> param = new HashMap<>();

            //从微信中申请的公众号id
            param.put("appid",appid);
            //从微信申请的商户号
            param.put("mch_id",mch_id);
            //随机字符串
            param.put("nonce_str", WXPayUtil.generateNonceStr());

            //商品描述，可以设置为商品的标题
            param.put("body","品优购");
            //订单号
            param.put("out_trade_no",outTradeNo);
            //交易总金额
            param.put("total_fee",totalFee);
            //当前机器ip
            param.put("spbill_create_ip","127.0.0.1");
            //回调地址
            param.put("notify_url",notify_url);
            //交易类型：扫描支付
            param.put("trade_type","NATIVE");

            //将参数mao转换为微信支付的xml
            String signedXml = WXPayUtil.generateSignedXml(param,partnerkey);

            System.out.println();
            System.out.println("发送到微信统一下单的参数为 = "+signedXml);
            System.out.println();

            //创建httpClient对象并发送信息到微信支付
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(signedXml);
            httpClient.isHttps();
            httpClient.post();

            //获取微信支付返回的数据
            String content = httpClient.getContent();
            System.out.println("微信统一下单返回的内容为 = "+content);

            //转换内容为map并设置返回结果
            Map<String,String> resultMap = WXPayUtil.xmlToMap(content);
            //业务结果
            returnMap.put("result_code",resultMap.get("result_code"));
            //二维码支付地址
            returnMap.put("code_url",resultMap.get("code_url"));
            returnMap.put("outTradeNo",outTradeNo);
            returnMap.put("totalFee",totalFee);
        }catch (Exception e){
            e.printStackTrace();
        }
        return returnMap;
    }

    @Override
    public Map<String, String> queryPayStatus(String outTradeNo) {
        try{
            //组合要发送的参数
            Map<String,String> paramMap = new HashMap<>();
            //从微信申请的的公众号id
            paramMap.put("appid",appid);
            //从微信申请商户号
            paramMap.put("mch_id",mch_id);
            //随机字符串
            paramMap.put("nonce_str",WXPayUtil.generateNonceStr());
            //订单号
            paramMap.put("out_trade_no",outTradeNo);
            //将参数map转换为微信支付需要的xml
            String signedXml = WXPayUtil.generateSignedXml(paramMap, partnerkey);

            System.out.println();
            System.out.println("发送到微信支付查看订单的内容为 = "+signedXml);
            System.out.println();

            //创建httpClient对象并发送信息到微信支付
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            //httpClient.setHttps(true);
            httpClient.setXmlParam(signedXml);
            httpClient.isHttps();
            httpClient.post();

            //获取微信支付返回的数据
            String content = httpClient.getContent();
            System.out.println();
            System.out.println("微信查看订单的内容为 = "+content);
            System.out.println();

            //转换内容为map并设置返回结果
            return WXPayUtil.xmlToMap(content);

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Map<String, String> closeOrder(String outTradeNo) {
        try{
            //组合要发送的参数
            Map<String,String> paramMap = new HashMap<>();
            //从微信申请的的公众号id
            paramMap.put("appid",appid);
            //从微信申请商户号
            paramMap.put("mch_id",mch_id);
            //随机字符串
            paramMap.put("nonce_str",WXPayUtil.generateNonceStr());
            //订单号
            paramMap.put("out_trade_no",outTradeNo);
            //将参数map转换为微信支付需要的xml
            String signedXml = WXPayUtil.generateSignedXml(paramMap, partnerkey);

            System.out.println();
            System.out.println("发送到微信支付关闭订单的内容为 = "+signedXml);
            System.out.println();

            //创建httpClient对象并发送信息到微信支付
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/closeorder");
            //httpClient.setHttps(true);
            httpClient.setXmlParam(signedXml);
            httpClient.isHttps();
            httpClient.post();

            //获取微信支付返回的数据
            String content = httpClient.getContent();
            System.out.println();
            System.out.println("微信关闭订单的内容为 = "+content);
            System.out.println();

            //转换内容为map并设置返回结果
            return WXPayUtil.xmlToMap(content);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
