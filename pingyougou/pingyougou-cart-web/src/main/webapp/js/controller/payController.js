app.controller("payController",function ($scope, $location, cartService,payService) {

    $scope.getUsername = function () {
        cartService.getUsername().success(function (respone) {
            $scope.username = respone.username;
        });
    };

    //生成支付二维码
    $scope.createNative = function () {
        //支付业务id
        $scope.outTradeNo = $location.search()["outTradeNo"];
        alert($scope.outTradeNo);
        payService.createNative($scope.outTradeNo).success(function (response) {
            //创建支付地址成功
            if("SUCCESS" == response.result_code){
                //生成支付地址二维码
                var qr = new QRious({
                   element:document.getElementById("qrious"),
                   size:250,
                   level:"M",
                   value:response.code_url
                });

                //查询支付状态
                queryPayStatus($scope.outTradeNo);
            }else{
                //alert("生成二维码失败！");
                alert(response.message);
            }
        });
    }

    //查询支付状态
    queryPayStatus = function (outTradeNo) {
        payService.queryPayStatus(outTradeNo).success(function (response) {
            if(response.success){
                location.href = "paysuccess.html#?money="+$scope.totalFee;
            }else{
                //二维码超时
                if("二维码超时" == response.message){
                    //重新生成二维码
                    $scope.createNative();
                }else{
                    //支付失败页面
                    location.href = "payfail.html";
                }
            }
        });
    }

    //获取总金额
    $scope.getMoney = function () {
        $scope.money = $location.search()["money"];
    }
});