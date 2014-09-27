// Solution 1
/*
var port = parseInt(process.argv[2] || 8080);

var Hapi = require('hapi');

var server = Hapi.createServer('localhost', port);

server.route({
	path: '/',
	method: 'GET',
	handler: function(request, reply) { 
		reply("Hello Hapi");
	}
});

server.start();
*/
// Solution 2
/*
var port = parseInt(process.argv[2] || 8080);

var Hapi = require('hapi');

var server = Hapi.createServer('localhost', port);

server.route({
	path: '/{name}',
	method: 'GET',
	handler: function(request, reply) { 
		reply("Hello " + request.params.name);
	}
});

server.start();
*/

// Solution 3
/*
var port = parseInt(process.argv[2] || 8080);

var Hapi = require('hapi');

var server = Hapi.createServer('localhost', port);

server.route({
	path: '/',
	method: 'GET',
	handler: {
		file: "index.html"
	}
});

server.start();
*/
/*
var Hapi = require('hapi');
var path = require('path');

var server = Hapi.createServer('localhost', Number(process.argv[2] || 8080))

server.route({
    method: 'GET',
    path: '/',
    handler: {
        file: path.join(__dirname, '/index.html')
    }
});
server.start();
*/

// Solution 4
/*
var Hapi = require('hapi');
var path = require('path');

var server = Hapi.createServer('localhost', Number(process.argv[2] || 8080))

server.route({
    method: 'GET',
    path: '/foo/bar/baz/{file}',
    handler: {
        directory: { path: path.join(__dirname, '/public') }
    }
});
server.start();
*/

// Solution 5
/*
var Hapi = require('hapi');
var path = require('path');

var options = {
	views: {
		path: 'templates',
		engines: {
			html: require('handlebars')
		}
	}
}

var server = Hapi.createServer(
	'localhost', 
	Number(process.argv[2] || 8080),
	options);

server.route({
    method: 'GET',
    path: '/',
    handler: {
        view: "index.html"
    }
});
server.start();
*/
// Solution 6
/*
var port = parseInt(process.argv[2] || 8080);

var Hapi = require('hapi');

var server = Hapi.createServer('localhost', port);

server.route({
	path: '/proxy',
	method: 'GET',
	handler: {
		proxy: {
			host: "localhost",
			port: 65535
		}
	}
});

server.start();
*/

// Solution 7
/*
var Hapi = require('hapi');
var path = require('path');

var options = {
	views: {
		path: path.join(__dirname, 'templates'),
		engines: {
			html: require('handlebars')
		},
		helpersPath: path.join(__dirname, 'helpers')
	}
}

var server = Hapi.createServer(
	'localhost', 
	Number(process.argv[2] || 8080),
	options);

server.route({
    method: 'GET',
    path: '/',
    handler: {
        view: "index.html"
    }
});
server.start();
*/

// Solution 8
/*
var port = parseInt(process.argv[2] || 8080);

var Hapi = require('hapi');
var fs = require('fs');
var rot13 = require('rot13-stream')();
var path = require('path');
var server = Hapi.createServer('localhost', port);

server.route({
	path: '/',
	method: 'GET',
	handler: function(request, reply) {
		var readStream = fs.createReadStream(path.join(__dirname, 'file.txt'));
		reply(readStream.pipe(rot13));
	}
});

server.start();
*/

// Solution 9
/*
var port = parseInt(process.argv[2] || 8080);

var Hapi = require('hapi');
var Joi = require('joi');

var fs = require('fs');
var server = Hapi.createServer('localhost', port);

var routeConfig = {
    handler: function (request, reply) {
		reply({ 
			breed: request.params.breed,
			mood: request.query.mood,
			age: request.query.age		
		});
    },
	validate: {
		params: {
			//breed: Joi.string().required()
			breed: Joi.string().min(8).max(100)
		},
		query: {
			mood: Joi.string().valid(["neutral","happy","sad"]).default("neutral"),
			age: Joi.number().integer().min(13).max(100).default(13)
		}
	}
}

server.route({
	path: '/chickens/{breed}',
	method: 'GET',
	config: routeConfig
});

server.start();
*/
// Solution 10
/*
var port = parseInt(process.argv[2] || 8080);

var Hapi = require('hapi');
var Joi = require('joi');

var fs = require('fs');
var server = Hapi.createServer('localhost', port);

var routeConfig = {
    handler: function (request, reply) {
		reply("login successful");
    },
    validate: {
        payload : Joi.object({
            isGuest: Joi.boolean(),
			username: Joi.alternatives().when('isGuest', { is: false, then: Joi.string().required(), otherwise: Joi.string() }),
            password: Joi.string().alphanum(),
            accessToken: Joi.string().alphanum(),
            //birthyear: Joi.number().integer().min(1900).max(2013),
            //email: Joi.string().email()
        }).options({allowUnknown: true})
		  //.with('username', 'birthyear')
		  .without('password', 'accessToken')
    }
}

server.route({
	path: '/login',
	method: 'POST',
	config: routeConfig
});

server.start();
*/
// Official answer:
/*
    var Hapi = require('hapi');
    var Joi = require('joi');

    var server = Hapi.createServer('localhost', Number(process.argv[2] || 8080))
;

    server.route({
        method: 'POST',
        path: '/login',
        config: {
            handler: function (request, reply) {

                reply('login successful');
            },
            validate: {
                payload: Joi.object({
                    isGuest: Joi.boolean().required(),
                    username: Joi.when('isGuest', { is: false, then: Joi.require
d() }),
                    password: Joi.string().alphanum(),
                    accessToken: Joi.string().alphanum()
                }).options({ allowUnknown: true }).without('password', 'accessTo
ken')
            }
        }
    });

    server.start();
*/
// Solution 11
/*
var port = parseInt(process.argv[2] || 8080);

var Hapi = require('hapi');

var fs = require('fs');
var server = Hapi.createServer('localhost', port);

var routeConfig = {
	payload : {
		output: 'stream',
		parse: true,
		allow: 'multipart/form-data'
	},
    handler: function (request, reply) {
		var body = '';
		request.payload.file.on('data', function(data) {
			body += data;
		});
		request.payload.file.on('end', function() {
			var ret = {
				description: request.payload.description,
				file : {
					data: body,
					filename: request.payload.file.hapi.filename,
					headers: request.payload.file.hapi.headers
				}
			}
			reply(JSON.stringify(ret));
		});
	}
}

server.route({
	path: '/upload',
	method: 'POST',
	config: routeConfig
});

server.start();
*/

// Solution 12
var port = parseInt(process.argv[2] || 8080);
var Hapi = require('hapi');

var serverOptions = {
	state: {
		cookies: {
			parse: true, // parse and store in request.cookies
			//clearInvalid: false, // remove invalid cookies
			//strictHeader: true, // don't allow violations of RFC 6265
			failAction: 'log' // may also be 'ignore' or 'error'
		}
	}
}

var server = Hapi.createServer(
	'localhost', 
	port,
	serverOptions);


/*
This configuration will make it so the cookie named 'session' 
has a session time-life (will be deleted when the 
browser is closed), is flagged both secure 
and HTTP only (see RFC 6265, specifically sections 4.1.2.5 
and 4.1.2.6 for more details about these flags), and tells 
hapi that the value is a base64 encoded JSON string. 
Full documentation for the server.state() options can be found 
in the API reference.

https://github.com/hapijs/hapi/blob/master/docs/Reference.md#serverstatename-options
*/
// define the session cookie
server.state('session', {
	path: '/{path*}',
    ttl: 10,
	domain: 'localhost',
    //isSecure: true,
    //isHttpOnly: true,
    encoding: 'base64json'
});

server.route({
	path: '/set-cookie',
	method: 'GET',
	//config: routeConfigSetCookie,
	handler: function (request, reply) {
		// In this example, hapi will reply with the string 
		// Hello as well as set a cookie named data to a 
		// base64 encoded string representation of the given object.
		reply('Hello').state('session', {key : 'makemehapi'});

	}
});

server.route({
	path: '/check-cookie',
	method: 'GET',
	//config: routeConfigCheckCookie,
	handler: function (request, reply) {
		var session = request.state.session;
		if(!session) {
			reply("unauthorized").code(401);
		}
		if(session.key === 'makemehapi') {
			reply({user: 'hapi'});
		}
	}
});

server.start();

// Official answer:
/*
    var Hapi = require('hapi');

    var options = {
      state: {
        cookies: {
          parse: true ,
          failAction: 'log'
        }
      }
    };

    var server = Hapi.createServer('localhost', Number(process.argv[2] || 8000)
 options);


    server.state('session', {
      path: '/{path*}',
      encoding: 'base64json',
      ttl: 10,
      domain: 'localhost'
    });


    server.route(
      {
        method: 'GET',
        path: '/set-cookie',
        config: {
          handler: function (request, reply) {

            return reply({
              message : 'success'
            }).state('session', {
              key : 'makemehapi'
            });
          }
        }
      }
    )

    server.route(
      {
        method: 'GET',
        path: '/check-cookie',
        config: {
          handler: function (request, reply) {

            var session = request.state.session;
            var result;
            if (session) {
              result = {
                user : 'hapi'
              };
            } else {
              result = new Hapi.error.unauthorized('Missing authentication');
            }
            reply(result);
          }
        }
      }
    );

    server.start();

*/