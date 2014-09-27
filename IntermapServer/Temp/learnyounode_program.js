/*
Solution 2
var sum = 0;

for(var i = 2; i < process.argv.length; ++i)
{
	sum += parseInt(process.argv[i]);
}

console.log(sum);

*/

// Solution 3:
/*
var fs = require('fs');

var buf = fs.readFileSync(process.argv[2]);
var strBuf = buf.toString();
var splitStr = strBuf.split("\n");
console.log(splitStr.length-1);
*/

// Solution 4:
/*
function callback(err, data) { 
	var strBuf = data.toString();
	var splitStr = strBuf.split("\n");
	console.log(splitStr.length-1);	
}

var fs = require('fs');

var buf = fs.readFile(process.argv[2], callback);
*/

// Solution 5:
/*
var fs = require('fs');
//var path = require('path');
var re = new RegExp(".*("+process.argv[3]+")$", "g");

fs.readdir(process.argv[2], function(err, list) {
	for(var i = 0; i < list.length; ++i) {		
		//var ext = path.extname(list[i]);
		if(re.test(list[i])) {
			console.log(list[i]);
		}
	}
});
*/
// Solution 6
/*
var mymodule = require("./module.js");

mymodule(process.argv[2], process.argv[3], function(err, list) {
	for(var i = 0; i < list.length; ++i){
		console.log(list[i]);
	}
});
*/
/*
solution.js:

    var filterFn = require('./solution_filter.js')
    var dir = process.argv[2]
    var filterStr = process.argv[3]

    filterFn(dir, filterStr, function (err, list) {
      if (err)
        return console.error('There was an error:', err)

      list.forEach(function (file) {
        console.log(file)
      })
    })

--------------------------------------------------------------------------------

solution_filter.js:

    var fs = require('fs')
    var path = require('path')

    module.exports = function (dir, filterStr, callback) {

      fs.readdir(dir, function (err, list) {
        if (err)
          return callback(err)

        list = list.filter(function (file) {
          return path.extname(file) === '.' + filterStr
        })

        callback(null, list)
      })
    }
*/

// Solution 7
// See: http://nodejs.org/api/http.html
/*
http = require('http');

http.get(process.argv[2], function(response) { 
	response.setEncoding("utf8");
	var data = "";
	
	response.on("data", function(chunk) { 
		// The chunk is a Buffer object.
		data += chunk;
	});

	response.on("error", function(error) { 
		console.log('problem with request: ' + error.message);
	});

    response.on('end', function() {
		console.log(data.length);
		console.log(data);
    });
})
*/
/*
   var http = require('http')
   var bl = require('bl')

   http.get(process.argv[2], function (response) {
     response.pipe(bl(function (err, data) {
       if (err)
         return console.error(err)
       data = data.toString()
       console.log(data.length)
       console.log(data)
     }))
   })
*/
// Solution 9
/*
function getResult(index, callback)
{
	http.get(process.argv[index], function (response) {
		response.pipe(bl(function (err, data) {
			if (err)
				return callback(err, null);

			// Save results for later use.
			results[index] = data.toString();
			return callback(null);			
		}))
	});	
}

var http = require('http')
var bl = require('bl')

var results = {};
var count 

for(var i = 2; i < process.argv.length; ++i) {
	getResult(i, function(err, count) { 
		if(err)
			return console.error(err);
		//console.log(Object.keys(results).length);
		
		if(Object.keys(results).length == process.argv.length - 2) {
			// Print results.
			for(var k = 2; k < process.argv.length; ++k) {
				console.log(results[k]);
			}
		}
		
	});
}
*/
/*
    var http = require('http')
    var bl = require('bl')
    var results = []
    var count = 0

    function printResults () {
      for (var i = 0; i < 3; i++)
        console.log(results[i])
    }

    function httpGet (index) {
      http.get(process.argv[2 + index], function (response) {
        response.pipe(bl(function (err, data) {
          if (err)
            return console.error(err)

          results[index] = data.toString()
          count++

          if (count == 3) // yay! we are the last one!
            printResults()
        }))
      })
    }

    for (var i = 0; i < 3; i++)
      httpGet(i)

*/
/*
// Solution with async:
var async = require('async');
var http = require('http')
var bl = require('bl')

urls = process.argv.slice(2);
results = {}

function httpGet(url, callback) {
	http.get(url, function (response) {
		response.pipe(bl(function (err, data) {
			if (err)
				return callback(err, null);
			
			// Save results for later use.
			return callback(null, data.toString());			
		}))
	});	
}

async.map(urls, httpGet, function(err, results) { 
	for(var i = 0; i < results.length; ++i) 
		console.log(results[i]);
});

*/
/*
// Solution 10
var net = require('net');
var strftime = require('strftime');

var server = net.createServer(function (socket) { 
	// Socket handling logic
	var date = new Date();
	var strDate = strftime("%F %H:%M", date);
	socket.end(strDate+"\n");
});

var port = parseInt(process.argv[2]);
server.listen(port);
*/
/*
var net = require('net')

function zeroFill(i) {
  return (i < 10 ? '0' : '') + i
}

function now () {
  var d = new Date()
  return d.getFullYear() + '-'
    + zeroFill(d.getMonth() + 1) + '-'
    + zeroFill(d.getDate()) + ' '
    + zeroFill(d.getHours()) + ':'
    + zeroFill(d.getMinutes())
}

var server = net.createServer(function (socket) {
  socket.end(now() + '\n')
})

server.listen(Number(process.argv[2]))
*/

// Solution 11
/*
var http = require('http');
var fs = require('fs');

var server = http.createServer(function (request, response) { 
	response.writeHead(200, { 'content-type' : 'text/plain' });
	var fileStream = fs.createReadStream(process.argv[3]);
	fileStream.pipe(response);
});

server.listen(Number(process.argv[2]));
*/

// Solution 12
/*
var http = require('http');
var map = require('through2-map');

var server = http.createServer(function (request, response) { 
    if (request.method != 'POST')
        return res.end('send me a POST\n');

	response.writeHead(200, { 'content-type' : 'text/plain' });
	request.pipe(map(function (chunk) { 
		return chunk.toString().toUpperCase();
	})).pipe(response);
});

server.listen(Number(process.argv[2]));
*/

// Solution 13
var http = require('http');
var url = require('url');

var server = http.createServer(function (request, response) { 
    if (request.method != 'GET')
        return res.end('send me a GET\n');
	response.writeHead(200, { 'content-type' : 'application/json' });
	
	var urlObj = url.parse(request.url, true);
	if(/^\/api\/parsetime/.test(request.url)) {
		var query = urlObj.query;
		var iso = query.iso;
		var date = new Date(iso);
		
		res = { 'hour' : date.getHours(), 
				'minute' : date.getMinutes(), 
				'second' : date.getSeconds() };
		
		response.end(JSON.stringify(res));
	}
	else if(urlObj.pathname === '/api/unixtime') {
		var query = urlObj.query;
		var iso = query.iso;
		res = {"unixtime" : Date.parse(iso)};
		
		response.end(JSON.stringify(res));
	}
	else { 
		response.writeHead(400);
		response.end("Unknown endpoint: " + urlObj.pathname);
	}
});

server.listen(Number(process.argv[2]));

/*

    var http = require('http')
    var url = require('url')

    function parsetime (time) {
      return {
        hour: time.getHours(),
        minute: time.getMinutes(),
        second: time.getSeconds()
      }
    }

    function unixtime (time) {
      return { unixtime : time.getTime() }
    }

    var server = http.createServer(function (req, res) {
      var parsedUrl = url.parse(req.url, true)
      var time = new Date(parsedUrl.query.iso)
      var result

      if (/^\/api\/parsetime/.test(req.url))
        result = parsetime(time)
      else if (/^\/api\/unixtime/.test(req.url))
        result = unixtime(time)

      if (result) {
        res.writeHead(200, { 'Content-Type': 'application/json' })
        res.end(JSON.stringify(result))
      } else {
        res.writeHead(404)
        res.end()
      }
    })
    server.listen(Number(process.argv[2]))
*/