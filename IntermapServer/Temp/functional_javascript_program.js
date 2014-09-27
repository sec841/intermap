// Solution 1
/*
function upperCaser(input) {
	return input.toUpperCase();
}

module.exports = upperCaser
*/

// Solution 2
/*
function repeat(func, num) {
	if( num <= 0 )
		return;
	func();
	repeat(func, num-1);
}

module.exports = repeat;
*/

// Solution 3
/*
function doubleAll(numbers) {
  // SOLUTION GOES HERE
  return numbers.map(function(currentValue, index, array) { 
	return currentValue * 2;
  });
}

module.exports = doubleAll
*/

// Solution 4
/*
function getShortMessages(messages) {
	return messages.filter(function(value) {
		return value.message.length < 50;
	}).map(function(value, index, array) { 
		return value.message;
	});
}

module.exports = getShortMessages;
*/

// Solution 5
/*
function checkUsersValid(goodUsers) {
  return function(submittedUsers) {
	return submittedUsers.every(function (submittedUser) { 
		return goodUsers.some(function (goodUser) { 
			return goodUser.id === submittedUser.id;
		});
	});
  };
}
module.exports = checkUsersValid
*/

// Solution 6
/*
function countWords(inputWords) {
  return inputWords.reduce(function (previousValue, currentValue) { 
		if(!previousValue[currentValue])
			previousValue[currentValue] = 1;
		else
			previousValue[currentValue]++;
		return previousValue;
  }, {});
}

module.exports = countWords;
*/
/*
function countWords(arr) {
  return arr.reduce(function(countMap, word) {
    countMap[word] = ++countMap[word] || 1 // increment or initialize to 1

    return countMap
  }, {}) // second argument to reduce initialises countMap to {}
}

module.exports = countWords
*/

// Solution 7
/*
function reduce(arr, fn, initialValue) {
	if(!arr.length)
		return initialValue;
    var curr = arr[0];  
    var tail = arr.slice(1);
	newValue = fn(initialValue, curr, 0, arr);  
    return reduce(tail, fn, newValue)
	
  //fn(prev, curr, index, arr);
}

module.exports = reduce
*/
/*
      function reduce(arr, fn, initial) {
        return (function reduceOne(index, value) {
          if (index > arr.length - 1) return value // end condition
          return reduceOne(index + 1, fn(value, arr[index], index, arr)) // calculate & pass values to next step
        })(0, initial) // IIFE. kick off recursion with initial values
      }

      module.exports = reduce;
*/
/*
var duck = {
  quack: function() {
    console.log('quack')
  }
}

duck.hasOwnProperty('quack') // => true
*/
// Solution 8
/*
function duckCount() {
  // SOLUTION GOES HERE
  var args = Array.prototype.slice.call(arguments);

  return args.reduce(function(prev, object) {
	if( Object.prototype.hasOwnProperty.call(object, 'quack') )
		return prev+1;
	return prev;
  }, 0);
}

module.exports = duckCount;
*/
/*
function duckCount() {
  return Array.prototype.slice.call(arguments).filter(function(obj) {
    return Object.prototype.hasOwnProperty.call(obj, 'quack')
  }).length
}

module.exports = duckCount


*/

// Solution 9
/*
var slice = Array.prototype.slice

function logger(namespace) {
  return function() {
	var args = slice.call(arguments, 0);
	console.log.apply(null, [namespace].concat(args));
  }
}

module.exports = logger
*/

// Solution 10
/*
module.exports = function(namespace) {
  return console.log.bind(console, namespace);
}
*/

// Solution 11
/*
module.exports = function arrayMap(arr, fn) {
	// fn(item)
	return arr.reduce(function(prev, curr, index, arr) { 
		return prev.concat(fn(curr));
	},[]);
}
*/

// Solution 12
/*
function Spy(target, method) {
	var result = {count: 0};
	
	var originalFn = target[method];
	
	target[method] = function() { 
		result.count++;
		return originalFn.apply(target, arguments);
	};
	
	return result;
}

module.exports = Spy
*/

// Solution 13:
function repeat(operation, num) {
    if (num <= 0) 
		return

    operation();

    // release control every 10 or so
    // iterations.
    // 10 is arbitrary.
    if (num % 10 === 0) {
        setTimeout(function() {
			repeat(operation, --num)
		})
    } else {
		repeat(operation, --num)
    }
}


module.exports = repeat