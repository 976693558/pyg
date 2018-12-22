package pay.service;

import java.util.Map;

public interface WeixinPayService {
    Map<String,String> createNative(String outTradeNo, String s);

    Map<String,String> queryPayStatus(String outTradeNo);

    Map<String,String> closeOrder(String outTradeNo);
}
