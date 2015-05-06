'use strict';

angular.module('iotDashboard')
  .controller('MapController', ['connectedCarService', '$scope', function(connectedCarService, $scope) {
    $scope.vin = connectedCarService.car.vin;
    $scope.journeys = connectedCarService.journeys;

    $scope.carLocation = connectedCarService.car.location;
    $scope.mapCenter = $scope.carLocation;
    $scope.mapZoom = 14;
    var carIcon = {
        anchor: new google.maps.Point(35,35),
        origin: new google.maps.Point(0, 0),
        scaledSize: new google.maps.Size(70, 70),
        url: 'images/car_marker.png'
    };
    var carIconOptionObject = {
        icon: carIcon
    };

    $scope.carIconOptions = carIconOptionObject;

    $scope.boundsMarkers = [];

    $scope.getCurrentJourney = function getCurrentJourney() {
      return connectedCarService.currentJourney;
    };

    $scope.setCurrentJourney = function setCurrentJourney(journey){
      if(connectedCarService.currentJourney === journey){
        connectedCarService.currentJourney = null;
      } else {
        connectedCarService.currentJourney = journey;
      }
    };

    $scope.$watch('currentPanel', function() {
      if ($scope.currentPanel === 'journey') {
        $scope.mapCenter = _.extend({}, $scope.carLocation);
      } else {
        $scope.mapCenter = $scope.carLocation;
        $scope.mapZoom = 14;
      }
    });

    $scope.$watch('[currentPanel,carLocation]', function(){
        $scope.boundsMarkers.length = 0;

        if($scope.currentPanel === 'journey') {
            _.each($scope.journeys[connectedCarService.car.vin], function(journey){
                var newJourney = _.assign(_.clone(journey), {
                    options: { opacity: 0 }
                });
                $scope.boundsMarkers.push(newJourney);
            });
        }

        $scope.boundsMarkers.push(_.assign(_.clone($scope.carLocation), {
            id: 'car',
            options: { opacity: 0 }
        }));
    }, true);
  }]);
