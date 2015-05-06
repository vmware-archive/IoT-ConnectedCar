/* jshint camelcase:false */
'use strict';

describe('connectedCarService', function() {

  beforeEach(module('iotDashboard'));
  beforeEach(inject(function($httpBackend, $interval, connectedCarService) {
    this.interval = $interval;
    this.httpBackend = $httpBackend;

    this.connectedCarHandler = $httpBackend.when('GET', 'http://localhost:3000/connectedCar/latest');
    this.journeysHandler = $httpBackend.when('GET', 'http://localhost:3000/connectedCar/journeys');

    this.connectedCarService = connectedCarService;
    this.originalJourneysRef = connectedCarService.journeys;
  }));

  it('initializes car values', function() {
    expect(this.connectedCarService.car.engineRpm).toEqual(0);
    expect(this.connectedCarService.car.vehicleSpeed).toEqual(0);
    expect(this.connectedCarService.car.coolantTemp).toEqual(0);
    expect(this.connectedCarService.car.fuel).toEqual(0);
    expect(this.connectedCarService.car.location.latitude).toEqual('0');
    expect(this.connectedCarService.car.location.longitude).toEqual('0');
    expect(this.connectedCarService.car.mpg).toEqual(0);
    expect(this.connectedCarService.car.range).toEqual(0);
  });

  it('initializes journeys to []', function(){
    expect(this.connectedCarService.journeys).toEqual([]);
  });

  describe('when the journeys endpoint responds unsuccessfully', function() {
    beforeEach(function() {
      this.journeysHandler.respond(400, 'i done goofed');
      this.httpBackend.flush();
    });

    it('keeps journeys set to the original reference', function() {
      expect(this.connectedCarService.journeys).toBe(this.originalJourneysRef);
    });
  });

  describe('when the journeys endpoint responds successfully', function() {
    beforeEach(function() {
      this.journeysHandler.respond(200, {
        "journeys": [
          { id: '1', name: 'Place 1', lat: '123', long: '456' },
          { id: '2', name: 'Place 2', lat: '789', long: '012' }
        ]
      });
      this.httpBackend.flush();
    });

    it('sets the journeys', function() {
      expect(this.connectedCarService.journeys).toEqual([
        { id: '1', name: 'Place 1', latitude: '123', longitude: '456', probability: 0 },
        { id: '2', name: 'Place 2', latitude: '789', longitude: '012', probability: 0 }
      ]);
    });

    it('keeps journeys set to the original reference', function() {
      expect(this.connectedCarService.journeys).toBe(this.originalJourneysRef);
    });

    describe('when the next poll responds successfully', function() {
      beforeEach(function() {
        this.connectedCarHandler.respond(200, {
          engine_rpm: 2000,
          vehicle_speed: 20,
          coolant_temp: 80,
          fuel_level_input: 12,
          latitude: '41.123',
          longitude: '-81.444',
          mpg_instantaneous: 34.2354,
          predictions: {
            RemainingRange: 453.2,
            ClusterPredictions: {
              "1": {
                EndLocation: ['123','456'],
                Probability: 0.9
              }
            }
          }
        });
        this.interval.flush(501);
        this.httpBackend.flush();
      });

      it('updates the car values', function() {
        expect(this.connectedCarService.car.engineRpm).toEqual(2000);
        expect(this.connectedCarService.car.vehicleSpeed).toEqual(20);
        expect(this.connectedCarService.car.coolantTemp).toEqual(80);
        expect(this.connectedCarService.car.fuel).toEqual(12);
        expect(this.connectedCarService.car.location.latitude).toEqual('41.123');
        expect(this.connectedCarService.car.location.longitude).toEqual('-81.444');
        expect(this.connectedCarService.car.mpg).toEqual(34.2354);
        expect(this.connectedCarService.car.range).toEqual(453.2);
      });

      it("updates the journeys", function(){
        expect(this.connectedCarService.journeys).toEqual([
          { id: '1', name: 'Place 1', latitude: '123', longitude: '456', probability: 0.9 },
          { id: '2', name: 'Place 2', latitude: '789', longitude: '012', probability: 0 }
        ]);
      });

      it('keeps journeys set to the original reference', function() {
        expect(this.connectedCarService.journeys).toBe(this.originalJourneysRef);
      });
    });

    describe('when the next poll responds unsuccessfully', function() {
      beforeEach(function() {
        this.connectedCarHandler.respond(400);
        this.interval.flush(501);
        this.httpBackend.flush();
      });

      it('updates the car values', function() {
        expect(this.connectedCarService.car.engineRpm).toEqual(0);
        expect(this.connectedCarService.car.vehicleSpeed).toEqual(0);
        expect(this.connectedCarService.car.coolantTemp).toEqual(0);
        expect(this.connectedCarService.car.fuel).toEqual(0);
        expect(this.connectedCarService.car.location.latitude).toEqual('0');
        expect(this.connectedCarService.car.location.longitude).toEqual('0');
        expect(this.connectedCarService.car.mpg).toEqual(0);
        expect(this.connectedCarService.car.range).toEqual(0);
      });

      it('keeps journeys set to the original reference', function() {
        expect(this.connectedCarService.journeys).toBe(this.originalJourneysRef);
      });
    });
  });
});

