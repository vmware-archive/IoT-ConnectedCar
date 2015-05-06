'use strict';

angular.module('iotDashboard')
  .filter('probabilityOpacity', function(){
    return function probabilityOpacity(probability){
      return (0.34 + 0.66 * probability);
    };
  });
