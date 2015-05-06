'use strict';

describe('JourneyController', function() {

  beforeEach(module('iotDashboard'));
  beforeEach(inject(function($controller, $rootScope) {
    this.scope = $rootScope.$new();

    this.connectedCarService = {
      journeys: [
        { id: '1', name: 'Place 1', latitude: '123', longitude: '456', probability: '0.9' },
        { id: '2', name: 'Place 2', latitude: '789', longitude: '012', probability: '0.7' }
      ],
      car: 'a car'
    };

    this.controller = $controller('JourneyController', {
      'connectedCarService': this.connectedCarService,
      '$scope': this.scope
    });
  }));

  it('assigns journeys from the connecteCarService', function() {
    expect(this.scope.journeys).toEqual([
      { id: '1', name: 'Place 1', latitude: '123', longitude: '456', probability: '0.9' },
      { id: '2', name: 'Place 2', latitude: '789', longitude: '012', probability: '0.7' }
    ]);
  });

  it('assigns car from the connectedCarService', function() {
    expect(this.scope.car).toEqual('a car');
  });

  describe('#setCurrentJourney', function() {
    beforeEach(function() {
      this.currentJourney = this.connectedCarService.journeys[1];
      this.scope.setCurrentJourney(this.currentJourney);
    });

    it('sets the current journey on the connectedCarService', function() {
      expect(this.connectedCarService.currentJourney).toBe(this.currentJourney);
    });

    describe("when the journey is already the current journey", function() {
      beforeEach(function() {
        this.connectedCarService.currentJourney = this.scope.journeys[1];
      });

      it("unsets the current journey on the connected car service", function(){
        this.scope.setCurrentJourney(this.scope.journeys[1]);
        expect(this.connectedCarService.currentJourney).toBeFalsy();
      });
    });
  });

  describe('#getCurrentJourney', function(){
    beforeEach(function(){
      this.connectedCarService.currentJourney = "Bon Jovi";
    });

    it("returns the current journey on the connectedCarService", function(){
      expect(this.scope.getCurrentJourney()).toBe(this.connectedCarService.currentJourney);
    });
  });
});
