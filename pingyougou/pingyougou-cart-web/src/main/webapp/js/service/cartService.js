app.service("cartService",function ($http) {

    this.getUsername = function () {
        //带随机数是为了每次执行都会访问后台
        return $http.get("cart/getUsername.do?t="+Math.random());
    }

    this.findCartList = function () {
        //获取购物车列表数据
        return $http.get("cart/findCartList.do?t="+Math.random());
    }

    this.addItemToCartList = function (itemId, num) {
        //商品增删
        return $http.get("cart/addItemToCartList.do?itemId="+itemId+"&num="+num);
    }

    this.sumTotalValue=function (cartList) {
        //计算购买总数和总价格
        var totalValue = {"totalNum":0,"totalMoney":0.0};
        for(var i = 0;i < cartList.length;i++){

            var cart = cartList[i];
            for(var j = 0;j < cart.orderItemList.length;j++){

                var orderItem = cart.orderItemList[j];
                totalValue.totalNum += orderItem.num;
                totalValue.totalMoney += orderItem.totalFee;
            }
        }
        return totalValue;
    };

    //提交订单
    this.submitOrder = function (order) {
        return $http.post("../order/add.do",order);
    }
});