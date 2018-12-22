
app.controller("baseController",function ($scope) {

    //id数组
    $scope.selectedIds = [];

    //初始化页面参数
    $scope.paginationConf = {
        currentPage:1,//当前页
        totalItems:0,//总记录数
        itemsPerPage:10,//页大小
        perPageOptions:[10,20,30,40,50],//可选择的每页大小
        onChange:function () {//当上述参数发生变化时触发的函数
            $scope.reloadList();
        }
    };

    //刷新页面函数
    $scope.reloadList = function () {
        //$scope.findPage($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
        $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
    };

    //选择记录选择或反选
    $scope.updateSelection = function ($event,id) {
        if($event.target.checked){
            $scope.selectedIds.push(id);
        }else{
            var index = $scope.selectedIds.indexOf(id);
            //删除位置，删除个数
            $scope.selectedIds.splice(index,1);
        }
    }

    //将一个json数组格式字符串的某个ket对应的值串起来显示，使用，分隔
    $scope.jsonToString = function (jsonStr, key) {
        var str = "";
        var jsonArray = JSON.parse(jsonStr);
        for(var i = 0; i < jsonArray.length;i++){
            if(i > 0){
                str +=",";
            }
            str += jsonArray[i][key];
        }
        return str;
    }
});
