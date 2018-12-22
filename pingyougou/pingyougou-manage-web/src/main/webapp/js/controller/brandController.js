//表现层


app.controller("brandController", function ($scope, $http ,$controller,brandService) {
    

    //继承baseController
    $controller("baseController",{$scope:$scope});

    //搜索条件
    $scope.searchEntity = {};

    //分页查询
    $scope.findPage = function (page, rows) {
        brandService.findPage(page, rows).success(function (response) {
            alert("response.rows = "+response.rows);
            //更新记录列表
            $scope.list = response.rows;

            alert("response.total = "+response.total);

            //更新总记录数
            $scope.paginationConf.totalItems = response.total;

        }).error(function () {
            alert("数据加载失败!");
        })
    };

    //查询所有列表数据并绑定到list对象,查询品牌列表
    $scope.findAll = function () {
        //get方式异步提交到后台（ajax）
        brandService.findAll().success(function (response) {
            $scope.list = response;
        });
    };

    //保存
    $scope.save = function () {
        var obj;
        if ($scope.entity.id != null) {
            obj = brandService.update($scope.entity);
        } else {
            obj = brandService.add($scope.entity);
        }
        obj.success(function (response) {
            if(response.success){
                //重新加载列表
                $scope.reloadList();
            }else {
                alert(response.message);
            }
        });
    };

    //根据主键查询
    $scope.findOne = function (id) {
        brandService.findOne(id).success(function (response) {
            $scope.entity = response;
        });
    };

    //批量删除
    $scope.delete = function () {
        if($scope.selectedIds.length < 1){
            alert("请选择要删除的记录");
            return ;
        }
        if(confirm("确定要删除所选记录吗？")){
            brandService.delete($scope.selectedIds).success(function (response) {
                if(response.success){
                    $scope.reloadList();
                    $scope.selectedIds = [];
                }else{
                    alert(response.message);
                }
            });
        }
    };

    //搜索框
    $scope.search = function (page,rows) {
        brandService.search(page,rows,$scope.searchEntity).success(function (response) {
            $scope.list = response.rows;
            $scope.paginationConf.totalItems = response.total;
        });
    };
});