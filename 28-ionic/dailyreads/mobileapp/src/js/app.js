// Ionic Starter App

// angular.module is a global place for creating, registering and retrieving Angular modules
// 'starter' is the name of this angular module example (also set in a <body> attribute in index.html)
// the 2nd parameter is an array of 'requires'
angular.module('dailyReads', ['ionic'])

.run(($ionicPlatform) => {
  $ionicPlatform.ready(() => {
    if(window.cordova && window.cordova.plugins.Keyboard) {
      // Hide the accessory bar by default (remove this to show the accessory bar above the keyboard
      // for form inputs)
      cordova.plugins.Keyboard.hideKeyboardAccessoryBar(true);

      // Don't remove this line unless you know what you are doing. It stops the viewport
      // from snapping when text inputs are focused. Ionic handles this internally for
      // a much nicer keyboard experience.
      cordova.plugins.Keyboard.disableScroll(true);
    }
    if(window.StatusBar) {
      StatusBar.styleDefault();
    }
  });
})

.config(($stateProvider,$urlRouterProvider) => {
  $stateProvider.state("home",{
    cache: false,
    url :'/home',
    controller :'HomeController',
    templateUrl : 'views/home/home.html'
  });

  $urlRouterProvider.otherwise('/home');
})

.controller('HomeController',($scope,$http) => {
  $scope.news = []
  $scope.reloadFavs = function(){
    $http.get("http://localhost:5000/api/")
          .success(function(data){
            $scope.news = data;
            $scope.$broadcast('scroll.refreshComplete');
         }).error(function(data, status, headers, config){
           console.log('oops error occured while refreshing data',JSON.stringify(data));
            $scope.$broadcast('scroll.refreshComplete');
         });
  }

  $scope.reloadFavs();
})
