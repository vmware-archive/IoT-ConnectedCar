'use strict';

describe('MapController', function() {

  beforeEach(module('iotDashboard'));
  beforeEach(inject(function($controller, $rootScope) {
    this.carLocation = { longitude: '2', latitude: '3' };
    this.journeys = [ "journey", "yes", "jethro tull" ];

    this.connectedCarService = {
      car: { location: this.carLocation },
      journeys: this.journeys,
      currentJourney: this.journeys[0]
    };
    this.scope = $rootScope.$new();
    this.scope.currentPanel = null;

    this.controller = $controller('MapController', {
      'connectedCarService': this.connectedCarService,
      '$scope': this.scope
    });
  }));

  it("assigns the car location to the scope", function(){
    expect(this.scope.carLocation).toBe(this.carLocation);
  });

  it("assigns the map center to the scope", function(){
    expect(this.scope.mapCenter).toBe(this.carLocation);
  });

  it("assigns the map zoom to the scope", function() {
    expect(this.scope.mapZoom).toBeTruthy();
  });

  it("assigns the journeys to the scope", function(){
    expect(this.scope.journeys).toBe(this.journeys);
  });

  it("assigns an empty array to boundsMarkers on the scope", function() {
    expect(this.scope.boundsMarkers).toEqual([]);
  });

  describe("#getCurrentJourney", function(){
    it("returns the current journey from the connected car service", function(){
      expect(this.scope.getCurrentJourney()).toEqual(this.connectedCarService.currentJourney);
    });
  });

  describe("#setCurrentJourney", function(){
    it("sets the current journey on the connected car service", function(){
      this.scope.setCurrentJourney(this.journeys[2]);
      expect(this.connectedCarService.currentJourney).toEqual(this.journeys[2]);
    });

    describe("when the journey is already the current journey", function() {
      beforeEach(function() {
        this.connectedCarService.currentJourney = this.journeys[2];
      });

      it("unsets the current journey on the connected car service", function(){
        this.scope.setCurrentJourney(this.journeys[2]);
        expect(this.connectedCarService.currentJourney).toBeFalsy();
      });
    });
  });

  describe("when current panel becomes 'journey'", function(){
    beforeEach(function() {
      this.scope.currentPanel = 'journey';
      this.scope.$apply();
    });

    it("sets map center to a copy of the car location", function(){
      expect(this.scope.mapCenter).toEqual(this.carLocation);
      expect(this.scope.mapCenter).not.toBe(this.carLocation);
    });
  });

  describe("when current panel becomes 'status'", function(){
    beforeEach(function() {
      this.scope.currentPanel = 'status';
      this.scope.mapZoom = 16;
      this.scope.$apply();
    });

    it("sets the map center to be the car location", function(){
      expect(this.scope.mapCenter).toBe(this.carLocation);
    });

    it("sets the zoom level back to the default", function(){
      expect(this.scope.mapZoom).toEqual(14);
    });
  });

  describe("when either journeys or carLocation changes", function() {
    beforeEach(function() {
      this.boundsMarkersReference = this.scope.boundsMarkers;

      this.scope.journeys = [{
        id: '101',
        latitude: '1',
        longitude: '1'
      }];
      this.scope.carLocation = {
        latitude: '2',
        longitude: '2'
      };

      this.scope.$apply();
    });

    it('assigns the journeys and car location to boundsMarkers', function() {
      expect(this.scope.boundsMarkers).toEqual([
        { id: '101', latitude: '1', longitude: '1', options: { opacity: 0 } },
        { id: 'car', latitude: '2', longitude: '2', options: { opacity: 0 } }
      ]);
    });

    it('keeps the original reference', function() {
      expect(this.scope.boundsMarkers).toBe(this.boundsMarkersReference);
    });
  });
});

