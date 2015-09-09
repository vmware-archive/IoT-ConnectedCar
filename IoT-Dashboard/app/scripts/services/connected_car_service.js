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
            $http({method: 'GET', url: configuration.baseUrl + '/journeys/' + vin}).
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
        $http({method: 'GET', url: configuration.baseUrl + '/journeys/' + vin}).
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

