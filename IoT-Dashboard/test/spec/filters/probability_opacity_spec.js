'use strict';

describe('probabilityOpacity', function() {
  beforeEach(function() {
    module('iotDashboard');

    inject(function($filter) {
      this.filter = $filter('probabilityOpacity');
    });
  });

  describe('when the probability is 0', function() {
    it('equals 0.34', function() {
      expect(this.filter(0)).toEqual(0.34);
    });
  });

  describe('when the probability is between 0 and 1', function() {
    it('returns the value mapped between 0.33 and 1', function() {
      expect(this.filter(0.5)).toEqual(0.67);
    });
  });

  describe('when the probability is 1', function() {
    it('equals 1', function() {
      expect(this.filter(1)).toEqual(1);
    });
  });
});
