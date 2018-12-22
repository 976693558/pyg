app.controller("cartController",function ($scope,cartService) {



    $scope.getUsername = function () {
        cartService.getUsername().success(function (respone) {
            $scope.username = respone.username;
        });
    };

    //查询购物车
    $scope.findCartList = function () {
        cartService.findCartList().success(function (response) {
            $scope.cartList = response;
            //计算购买总数和总价格
            $scope.totalValue = cartService.sumTotalValue(response);
        })
    }

    //商品数量加减
    $scope.addItemToCartList = function (itemId, num) {
        cartService.addItemToCartList(itemId,num).success(function (response) {
            if(response.success){
                $scope.findCartList();
            }else{
                alert(response.message);
            }
        });
    }
});