'use strict';

angular.module('iotDashboard')
  .controller('MainController', ['connectedCarService', '$http', '$scope', 'configuration', function (connectedCarService, $http, $scope, configuration) {
    $scope.currentPanel = 'status';

    $scope.updateVin = function() {
        connectedCarService.setVin($scope.selectedVin);
    };

    $http({
        method: 'GET',
        url: configuration.baseUrl + '/carPositions'
    }).success(function (result) {
        $scope.vins = result['_embedded']['carPositions'];
        $scope.selectedVin = result['_embedded']['carPositions'][0].vin;
        connectedCarService.setVin($scope.selectedVin);
        connectedCarService.startPolling();
    });
}]);
