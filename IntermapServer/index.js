/**
 * Main javascript file for intermap server.
 */

var port = parseInt(process.argv[2] || 8080);
var Hapi = require('hapi');
var https = require('https');
var url = require('url');
var bl = require('bl'); // https://github.com/rvagg/bl

var LOGIN_STATUS_OK = "LOGIN_STATUS_OK";
var LOGIN_STATUS_TOKEN_ERROR = "LOGIN_STATUS_TOKEN_ERROR";
var LOGIN_STATUS_UNKNOWN_CREDENTIALS = "LOGIN_STATUS_UNKNOWN_CREDENTIALS";

var FACEBOOK_APP_ID = "581376651971597";

// TODO: THIS IS TEMPORARY FOR TESTING PURPOSES ONLY! 
// The real app secret should be stored on the server in an external file and not committed to git.
var FACEBOOK_APP_SECRET = "";

// The facebook app token.
// Retrieved at start-up.
// TODO: Refresh this token every X hours.
fbAppToken = undefined;

//-----------------------------------------------------------------------------
// Links facebook IDs to Intermap IDs.
var facebookIdMap = {
		10154602565895392 : 1,
		10154499884250392 : 2
};

// TODO: Put this in database.
// TODO: In the mongo DB, we can put everything in a single json object 
// and setup an index on facebook_id or normal_id
var users = {
	    1: {
	        email: 'cotegu@gmail.com',
	        password: '123', // TODO: Use bcrypt here
	        name: 'Guillaume',
	        facebook_id: 10154602565895392
	    },
	    2: {
	        email: 'zemustain@msn.com',
	        password: '123', // TODO: Use bcrypt here
	        name: 'Sebastien',
	        facebook_id: 10154499884250392
	    }
	};

//-----------------------------------------------------------------------------
//
/*
var login = function (request, reply) {

    if (request.auth.isAuthenticated) {
    	// Already authenticated.
    	// TODO: No redirect.
        return reply({loginStatus : "OK"});
    }

    var message = '';
    var account = null;

    if (request.method === 'post') {

        if (!request.payload.username ||
            !request.payload.password) {
        	// TODO: Check facebook access here.
            message = 'Missing username or password';
        }
        else {
            account = users[request.payload.username];
            if (!account ||
                account.password !== request.payload.password) {

                message = 'Invalid username or password';
            }
        }
    }
    
    if (request.method === 'get' ||
        message) {

        return reply('<html><head><title>Login page</title></head><body>'
            + (message ? '<h3>' + message + '</h3><br/>' : '')
            + '<form method="post" action="/login">'
            + 'Username: <input type="text" name="username"><br>'
            + 'Password: <input type="password" name="password"><br/>'
            + '<input type="submit" value="Login"></form></body></html>');
    }
    

    request.auth.session.set(account);
    return reply.redirect('/');
};
*/


//-----------------------------------------------------------------------------
//
// Retrieve the app token for this app from Facebook. 
// Callback takes two parameters: err and token.
// TODO: Refresh this token regularly in case it expires (but it shouldn't!).
function getFacebookAppToken(callback) {
	var fbUrl = url.parse("https://graph.facebook.com/oauth/access_token");
	fbUrl.query = {};
	fbUrl.query.client_id = FACEBOOK_APP_ID;
	fbUrl.query.client_secret = FACEBOOK_APP_SECRET;
	fbUrl.query.grant_type = "client_credentials";
	var strFbUrl = url.format(fbUrl);
	
    https.get(strFbUrl, function (response) {
		response.pipe(bl(function (err, data) {
			if (err) {
				return callback({"error" : err }, null);
			}

			try {
				data = data.toString();
				if(data.indexOf("access_token") == 0) {
					return callback(null, data.split("=")[1]);
				}
				
				// Error: No access_token available.
				var jsonData = JSON.parse(data);
				return callback(jsonData, null);
			} catch(err) {
				console.error(err.stack);
				return callback({"error" : "Could not parse response data.", "data" : data}, null);
			}	
		}));
	});
}

//-----------------------------------------------------------------------------
//
// Validates a facebook user token.
function checkFacebookUserToken(userToken, callback) {
	var fbUrl = url.parse("https://graph.facebook.com/debug_token");
	fbUrl.query = {};
	fbUrl.query.input_token = userToken;
	fbUrl.query.access_token = fbAppToken;
	var strFbUrl = url.format(fbUrl);
	
    https.get(strFbUrl, function (response) {
		response.pipe(bl(function (err, data) {
			if (err) {
				return callback({"error" : err }, null);
			}
			
			// Parse the JSON data and pass it to callback.
			data = data.toString();
			try {
				var jsonData = JSON.parse(data);
				
				if(jsonData.error) {
					return callback(jsonData, null);
				}
				else {
					return callback(null, jsonData);
				}
			} catch(err) {
				console.error(err.stack);
				return callback({"error" : "Could not parse response data.", "data" : data}, null);
			}	
		}));
	});
}


/* 
 * Hapi server setup. 
 */
// TODO: Make server IP configurable.
 var server = Hapi.createServer('192.168.0.14', port);


//-----------------------------------------------------------------------------
// HANDLERS
//-----------------------------------------------------------------------------
 
 
//-----------------------------------------------------------------------------
//
var logoutHandler = function (request, reply) {
	// TODO: Check if user is logged in / known.
     request.auth.session.clear();
     return reply({logoutStatus : "OK"});
};
 
//-----------------------------------------------------------------------------
//
 var testHandler = function (request, reply) {
	 reply({hello:"Hello " + request.auth.credentials.name});
};
 
//-----------------------------------------------------------------------------
//
 function facebookLoginHandler(request, reply) { 
	if(!request.payload.hasOwnProperty('access_token')) {
		// TODO: Properly log errors with IP addresses and all.
		// TODO: Verifying Graph API Calls with appsecret_proof
		var err = {"error" : "An access_token was not provided."};
		console.error(err);
		return reply(err).code(401);
	}
	
	// TODO: request.payload.accessmethod (fb, etc)
	// Validate access token
	
	// TODO: Check if token is known.
	// Try to put this in cookie?
		
	// TODO: Return user information as response.
		
	// http://hapijs.com/api/v6.0.2#response-object
	var response = reply({loginStatus : "OK"}).hold(); 
	checkFacebookUserToken(request.payload.access_token, function(err, data) { 
		if(err) {
			console.error(err);
			response.source = { 
				loginStatus : LOGIN_STATUS_TOKEN_ERROR, 
				"error" : "Could not check user token."
			};
			response.statusCode = 500;
			return response.send();
		}
		// TODO: Test these errors
		data = data.data;
		
		// Check that the token is valid and for our app.
		if(!data.is_valid || data.app_id != FACEBOOK_APP_ID) {
			console.error(data);
			response.source = {
				loginStatus : LOGIN_STATUS_TOKEN_ERROR, 
				"error" : "Invalid user access token."
			};
			response.statusCode = 401;
			return response.send();
		}
		
		// Now, check that the token is for a known user.
		var account = null;
		var fbUserId = data.user_id;
		if( fbUserId && !isNaN(fbUserId) ) {
			fbUserId = parseInt(fbUserId);
			var uid = facebookIdMap[fbUserId];
			if(uid) {
				account = users[uid];
			}
		}
		
		if(!account) {
			console.error("Unknown user_id:\n");
			console.error(data);
			response.source = {
				loginStatus : LOGIN_STATUS_UNKNOWN_CREDENTIALS, 
				"error" : "Unknown credentials."
			};
			response.statusCode = 401;
			return response.send();
		} 
		
		// TODO : Check expiration value?
		// TODO :  Check user ID
	    request.auth.session.set(account);


		// Token is valid.
		// TODO: Save session into redis?
		response.send();
	});
		
		
}
 
//-----------------------------------------------------------------------------
//
var facebookLoginRouteConfig = {
	payload : {
		output: 'data',
		parse: true,
		allow: 'application/json'
	},
    auth: {
        mode: 'try',
        strategy: 'session'
    },
    plugins: {
        'hapi-auth-cookie': {
            redirectTo: false
        }
    }
};
		


//-----------------------------------------------------------------------------
//
function initServer() {
	console.log("Obtaining facebook app token...");
	getFacebookAppToken(function (err, token) {
		if(err) {
			console.error(err);
			console.error("FATAL ERROR: Could not start server.");
			process.exit(1);
		}
		console.log("Obtaining facebook app token: DONE.");
		fbAppToken = token;
		
		server.start();
		console.log("Server started.");
	});

}

//-----------------------------------------------------------------------------
//
server.pack.register(require('hapi-auth-cookie'), function (err) {

    if (err) {
        throw err; // something bad happened loading the plugin
    }
	
    server.auth.strategy('session', 'cookie', {
        password: 'secret', // used for Iron cookie encoding.
        cookie: 'sid', // the cookie name. Defaults to 'sid'.
        //redirectTo: false, 
        isSecure: false // if false, the cookie is allowed to be transmitted over insecure connections which exposes it to attacks. Defaults to true.
        // TODO: isSecure should be true!
    });
    
	//-----------------------------------------------------------------------------
	// ROUTES
	//-----------------------------------------------------------------------------
	
	server.route({
	  	path: '/logout',
	  	method: 'POST',
	    config: {
	     	handler: logoutHandler,
	        auth: 'session'
	     }
	});
	
	
	server.route({
	  	path: '/login/facebook',
	  	method: 'POST',
	  	config: facebookLoginRouteConfig,
	  	handler: facebookLoginHandler
	});
	
	
	server.route({
	 	method: 'GET',
	    path: '/test',
	    config: {
	     	handler: testHandler,
	        auth: 'session'
	    }
	});

    initServer();
});
// Initialize the server and start it.
// TODO: This server should be https!


