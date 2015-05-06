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
