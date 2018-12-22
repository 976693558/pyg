app.service("payService",function ($http) {

    //生成二维码
    this.createNative = function (outTradeNo) {
        return $http.get("pay/createNative.do?outTradeNo="+outTradeNo+
        "&r="+Math.random());
    };

    this.queryPayStatus = function (outTradeNo) {
        return $http.get("pay/queryPayStatus.do?outTradeNo="+outTradeNo+
        "&r="+Math.random());
    };
});