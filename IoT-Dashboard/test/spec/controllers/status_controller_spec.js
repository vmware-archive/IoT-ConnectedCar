'use strict';

describe('StatusController', function() {

  beforeEach(module('iotDashboard'));
  beforeEach(inject(function($controller) {
    this.scope = {};
    this.car = 'imacar';
    this.connectedCarService = { car: this.car };

    this.controller = $controller('StatusController', {
      'connectedCarService': this.connectedCarService,
      '$scope': this.scope
    });
  }));

  it("assigns the car to the scope", function(){
    expect(this.scope.car).toBe(this.car);
  });
});
