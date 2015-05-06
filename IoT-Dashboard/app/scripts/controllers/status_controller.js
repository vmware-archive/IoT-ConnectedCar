'use strict';

angular.module('iotDashboard')
  .controller('StatusController', ['connectedCarService', '$scope', function (connectedCarService, $scope) {
    $scope.car = connectedCarService.car;
  }]);
