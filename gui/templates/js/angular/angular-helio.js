var helio = angular.module('helio', ['ngRoute']);


helio.controller('MainController', function($scope) {
  $scope.message = "John";
});


helio.config(function($routeProvider) {
  $routeProvider
  .when("/", {
    templateUrl : "index.htm"
  })
  .when("/sparql", {
    templateUrl : "sparql2.htm"
  })
  .when("/green", {
    templateUrl : "green.htm"
  })
  .when("/blue", {
    templateUrl : "blue.htm"
  });
});