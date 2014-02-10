var bennuAdmin = angular.module('bennuAdmin', [
  'ngRoute'
]);

var i18n = function(input) {
  var tag = BennuPortal.locale.tag;
  if(input && input[tag]) {
    return input[tag];
  }
  return '!!' + JSON.stringify(input) + '!!';
}

bennuAdmin.filter('i18n', function () {
  return i18n;
});

bennuAdmin.config(['$routeProvider',
  function($routeProvider) {
    $routeProvider.
      when('/configuration', {
        templateUrl: 'template/PortalConfiguration.html',
        controller: 'PortalConfigurationCtrl'
      }).
      when('/menu/:id', {
        templateUrl: 'template/Menu.html',
        controller: 'MenuController'
      }).
      when('/system/info', {
        templateUrl: 'template/SystemInfo.html',
        controller: 'SystemInfoController'
      }).
      when('/system/logger', {
        templateUrl: 'template/Logger.html',
        controller: 'LoggerController'
      }).
      otherwise({
        redirectTo: '/configuration'
      });
  }]);

bennuAdmin.controller('PortalConfigurationCtrl', ['$scope', '$http', function ($scope, $http) {
  $http.get('../../api/bennu-portal/configuration').success(function (data) {
    $scope.locales = BennuPortal.locales;
    $scope.menu = data;
  });
  $scope.error = '';
  $scope.save = function() {
    $http.put('../../api/bennu-portal/configuration/' + $scope.menu.id, $scope.menu).success(function () {
      location.reload();
    });
  }
  $scope.fileNameChanged = function(e) {
    var files = e.files; // FileList object
    $scope.error = '';
    for ( var i = 0; i < files.length; i++) {
      var file = files[i];
      if (!file.type.match("image.*")) {
          $scope.error = "<p>Apenas são aceites imagens</p>";
          continue;
      }
      if (file.size > 200 * 1024) { // 200kb
          $scope.error = "<p>Imagem muito grande. Tamanho máximo : 200kb</p>";
          continue;
      }
      var reader = new FileReader();
      reader.onload = (function(f) {
          return function(e) {
              var content = e.target.result;
              var picBase64 = content.substr(content.indexOf(",") + 1, content.length);
              $scope.$apply(function () {
                $scope.menu.logo = picBase64;
                $scope.menu.logoType = file.type;
              });
          };
      })(file);
      reader.readAsDataURL(file);
    }
  }
}]);

bennuAdmin.controller('SystemInfoController', [ '$scope', '$http', function ($scope, $http) {
  $http.get('../../api/bennu-core/system/info').success(function (data) {
    $scope.data = data;
  });
}]);

bennuAdmin.controller('LoggerController', [ '$scope', '$http', function ($scope, $http) {
  $scope.changeLevel = function(logger, level) {
    $http.get('../../api/bennu-core/system/logger/' + logger.name + '/' + level).success(function (data) {
      $scope.loggers = data.loggers;
    });
  }
  $http.get('../../api/bennu-core/system/logger').success(function (data) {
    $scope.server = data.server;
    $scope.loggers = data.loggers;
  });
}]);