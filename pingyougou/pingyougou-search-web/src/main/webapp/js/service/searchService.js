app.service("searchService", function ($http) {

    this.search = function (searchMap) {
        alert(searchMap.spec)
        return $http.post("../itemSearch/search.do", searchMap);

    };
});