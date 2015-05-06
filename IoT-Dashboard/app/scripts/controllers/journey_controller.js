'use strict';

angular.module('iotDashboard')
  .controller('JourneyController', ['connectedCarService', '$scope', function(connectedCarService, $scope) {

    $scope.vin = connectedCarService.vin;
    $scope.journeys = connectedCarService.journeys;
    $scope.car = connectedCarService.car;

    $scope.setCurrentJourney = function setCurrentJourney(journey){
      if(connectedCarService.currentJourney === journey){
        connectedCarService.currentJourney = null;
      } else {
        connectedCarService.currentJourney = journey;
      }
    };

    $scope.getCurrentJourney = function getCurrentJourney() {
      return connectedCarService.currentJourney;
    };
}]);

