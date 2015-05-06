'use strict';

/**
 * @ngdoc overview
 * @name iotDashboard
 * @description
 *
 * Main module of the application.
 */
angular
  .module('iotDashboard', [
    'services.config',
    'google-maps',
    'percentage'
  ]);

'use strict';

angular.module('services.config', [])
  .constant('configuration', {
    baseUrl: 'http://23.92.235.246:8080'
  });

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

'use strict';

angular.module('iotDashboard')
  .controller('StatusController', ['connectedCarService', '$scope', function (connectedCarService, $scope) {
    $scope.car = connectedCarService.car;
  }]);

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


'use strict';

angular.module('iotDashboard')
  .factory('connectedCarService', ['$http', '$interval', 'configuration', function ($http, $interval, configuration) {
    var car = {
      engineRpm: 0,
      vehicleSpeed: 0,
      coolantTemp: 0,
      fuel: 0,
      location: {
        latitude: '0',
        longitude: '0'
      },
      range: 0,
      mpg: 0,
      vin: ""
    };
    var journeys = {};
    var vin = "";

    function setVin(newVin) {
        vin = newVin;
    }

    function hitConnectedCarEndpoint() {
        if (!(vin in journeys)) {
            console.log("3");
            $http({method: 'GET', url: configuration.baseUrl + '/journeyss/' + vin}).
                success(function (data) {
                    var curJourneys = [];
                    var journeyId = 0;
                    _.each(data["destinations"], function (journeyData) {
                        var journeyName = journeyData.name;

                        if(journeyName.length == 0) {
                            journeyName = journeyId;
                        }

                        var journey = {
                            name: journeyName,
                            id: journeyId,
                            latitude: journeyData.latitude,
                            longitude: journeyData.longitude,
                            probability: 0
                        };

                        var geocoder = new google.maps.Geocoder();
                        var latlng = new google.maps.LatLng(journeyData.latitude, journeyData.longitude);

                        geocoder.geocode({ 'latLng': latlng }, function (results, status) {
                            if (status == google.maps.GeocoderStatus.OK) {
                                if (results[1]) {
                                    journey.name = results[1].formatted_address;
                                } else {
                                    journey.name = 'Location not found';
                                }
                            } else {
                                console.log('Geocoder failed due to: ' + status);
                            }
                        });

                        journeyId++;
                        curJourneys.push(journey);
                    });

                    journeys[vin] = curJourneys;
                    getCarPosition(vin);
                }).
                error(function () {
                });
        } else {
            getCarPosition(vin);
        }
    }

    function getCarPosition(vin) {
        $http({method: 'GET', url: configuration.baseUrl + '/carPositions/' + vin}).
            success(function(sourceData) {
                car.vin = sourceData['vin'];
                car.engineRpm = Math.round(sourceData["rpm"]);
                car.vehicleSpeed = Math.round(sourceData["vehicleSpeed"]);
                car.coolantTemp = Math.round(sourceData["coolantTemp"]);
                car.fuel = Math.round(sourceData["fuelLevelInput"] * 100)/100;
                car.location.latitude = sourceData["latitude"];
                car.location.longitude = sourceData["longitude"];
                car.mpg = Math.round(sourceData["mpgInstantaneous"] * 10) / 10;
                car.range = Math.round(sourceData["remainingRange"] * 10) / 10;

                var newJourneyDataHash = sourceData["predictions"];
                _.each(journeys[vin], function(journey) {
                    var newJourneyData = newJourneyDataHash[journey.id];
                    if (newJourneyData && newJourneyData['probability']) {
                        journey.probability = newJourneyData['probability'];
                    } else {
                        journey.probability = 0;
                    }
                });
            }).
            error(function() {
                car.vin = "";
                car.engineRpm = 0;
                car.vehicleSpeed = 0;
                car.coolantTemp = 0;
                car.fuel = 0;
                car.location.latitude = '0';
                car.location.longitude = '0';
                car.range = 0;
                car.mpg = 0;
            });
    }

    function startPolling() {
        $http({method: 'GET', url: configuration.baseUrl + '/journeyss/' + vin}).
            success(function (data) {
                var curJourneys = [];
                var journeyId = 0;
                _.each(data["destinations"], function (journeyData) {
                    var journeyName = journeyData.name;

                    console.log("journeyName = " + journeyName + " length = " + journeyName.length);
                    if(journeyName.length == 0) {
                        journeyName = journeyId;
                    }

                    var journey = {
                        name: journeyName,
                        id: journeyId,
                        latitude: journeyData.latitude,
                        longitude: journeyData.longitude,
                        probability: 0
                    };

                    var geocoder = new google.maps.Geocoder();
                    var latlng = new google.maps.LatLng(journeyData.latitude, journeyData.longitude);

                    geocoder.geocode({ 'latLng': latlng }, function (results, status) {
                        if (status == google.maps.GeocoderStatus.OK) {
                            if (results[1]) {
                                journey.name = results[1].formatted_address;
                            } else {
                                journey.name = 'Location not found';
                            }
                        } else {
                            console.log('Geocoder failed due to: ' + status);
                        }
                    });

                    journeyId++;
                    curJourneys.push(journey);
                });

                journeys[vin] = curJourneys;
                getCarPosition(vin);
                $interval(hitConnectedCarEndpoint, 500);
            }).
            error(function () {
            });
    }

    return { car: car, journeys: journeys, vin: vin, setVin: setVin, startPolling: startPolling};
  }]);


'use strict';

angular.module('iotDashboard')
  .directive('journeyMarker', function() {
    return {
      restrict: 'E',
      scope: {
        journey: '=',
        isCurrentJourney: '=',
        setCurrentJourneyCallback: '='
      },
      link: function(scope) {
        scope.clickMarkerCallback = function clickMarkerCallback() {
          scope.setCurrentJourneyCallback(scope.journey);
        };

        scope.closeWindowCallback = function closeWindowCallback() {
          scope.setCurrentJourneyCallback();
        };

        scope.journeyMarkerIcon = {
          url: 'images/icon_marker.png',
          scaledSize: { height: 40, width: 30 }
        };

          scope.id = scope.journey.id.toString();
      },
      templateUrl: 'templates/journey_marker.html'
    };
  });

/* jshint newcap:false */
'use strict';

angular.module('iotDashboard')
  .directive('radialMeter', function() {
    return {
      restrict: 'E',
      scope: {
        id: '@',
        value: '=',
        min: '=',
        max: '=',
        label: '@',
        size: '=',
        strokeWidth: '=',
        suffix: '='
      },
      templateUrl: 'templates/meter.html' ,
      link: function(scope) {
        var size = scope.size;
        var strokeWidth = scope.strokeWidth;
        var r = (size-strokeWidth)/2;

        function valueToPath(value) {
          var cx = size/2;
          var cy = size/2;
          var startX = cx+r*Math.sin(-3/4*Math.PI);
          var startY = cy-r*Math.cos(-3/4*Math.PI);

          var percentage = (value - scope.min)/(scope.max-scope.min);
          var theta = (-3/4)*Math.PI + (3/2)*Math.PI*percentage;
          var largeArcSweep = (percentage < 2/3) ? 0 : 1;
          var x = cx+r*Math.sin(theta);
          var y = cy-r*Math.cos(theta);

          return [
            ["M", startX, startY],
            ["A", r, r, 0, largeArcSweep, 1, x, y]
          ];
        }

        var paper = Raphael(scope.id, size, size);

        var backgroundPath = paper.path()
          .attr({
            "stroke-width": strokeWidth
          })
          .attr({
            path: valueToPath(scope.max)
          });

        var foregroundPath = paper.path()
          .attr({
            "stroke-width": strokeWidth
          })
          .attr({
            path: valueToPath(scope.min)
          });

        backgroundPath.node.setAttribute("class", "radial-meter-background-path");
        foregroundPath.node.setAttribute("class", "radial-meter-foreground-path");

        scope.$watch('value', function(){
          foregroundPath.stop().animate({path: valueToPath(scope.value)}, 300);
        }, true);
      }
    };
  });


/* jshint newcap:false */
'use strict';

angular.module('iotDashboard')
  .directive('fillMeter', function(){
    return {
      restrict: 'E',
      scope: {
        id: '@',
        value: '=',
        min: '=',
        max: '=',
        label: '@',
        size: '=',
        strokeWidth: '=',
        innerRadius: '=',
        suffix: '='
      },
      templateUrl: 'templates/meter.html',
      link: function(scope){
        var size = scope.size;
        var strokeWidth = scope.strokeWidth;
        var innerR = scope.innerRadius;
        var r = (size-strokeWidth)/2;

        function valueToClipRect(value) {
          var percentage = (value-scope.min)/(scope.max-scope.min);
          var clipRectStartY = size - ((percentage*2*innerR) + (size/2-innerR));
          return "0 " + clipRectStartY + " " + size + " " + size;
        }

        var paper = Raphael(scope.id, size, size);

        var background = paper.circle(size/2, size/2, r)
          .attr({
            "stroke-width": strokeWidth
          });

        var fill = paper.circle(size/2, size/2, innerR);

        background.node.setAttribute("class", "fill-meter-background");
        fill.node.setAttribute("class", "fill-meter-fill");

        scope.$watch('value', function() {
          fill.attr({"clip-rect": valueToClipRect(scope.value)});
        }, true);
      }
    };
  });

'use strict';

angular.module('iotDashboard')
  .filter('probabilityOpacity', function(){
    return function probabilityOpacity(probability){
      return (0.34 + 0.66 * probability);
    };
  });
