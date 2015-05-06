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

