'use strict';

describe('MainController', function() {

  beforeEach(module('iotDashboard'));
  beforeEach(inject(function($controller) {
    this.scope = {};

    this.controller = $controller('MainController', {
      '$scope': this.scope
    });
  }));

  it('assigns the currentPanel to be the status panel', function(){
    expect(this.scope.currentPanel).toEqual('status');
  });
});
