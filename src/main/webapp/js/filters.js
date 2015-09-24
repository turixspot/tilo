'use strict';

/* Filters */
angular.module('tilo.filters', [])
.filter('property', function() {
	  return function(input, property, value) {
	    var i=0, len=input.length;
	    for (; i<len; i++) {
	      if (input[i][property] == value) {
	        return input[i];
	      }
	    }
	    return null;
	  }
})
.filter('formatMillis', function() {
    return function(diff) {
    	  var str = '';
    	  var values = {
    	    'h': 60 * 60 * 1000,
    	    'm': 60 * 1000,
    	    's': 1000 
    	  };

    	  for (var x in values) {
    	    var amount = Math.floor(diff / values[x]);
    	    
    	    if (amount >= 1) {
    	       str += amount + x + ' ';
    	       diff -= amount * values[x];
    	    }
    	  }
    	  
    	  return str;
    	};
});