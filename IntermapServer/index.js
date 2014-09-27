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

var FACEBOOK_APP_ID = "581376651971597";
var FACEBOOK_APP_SECRET = "f54aa6ddb7beb86f5bc15581ba1fef87";

// The facebook app token.
// Retrieved at start-up.
// TODO: Refresh this token every X hours.
fbAppToken = undefined;





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
 var server = Hapi.createServer('192.168.0.14', port);
 
 var loginRouteConfig = {
	payload : {
		output: 'data',
		parse: true,
		allow: 'application/json'
	}
}
 
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
		
		if(!data.is_valid || data.app_id != FACEBOOK_APP_ID) {
			console.error(data);
			response.source = {
				loginStatus : LOGIN_STATUS_TOKEN_ERROR, 
				"error" : "Invalid user access token."
			};
			response.statusCode = 401;
			return response.send();
		}
		
		// TODO : Check expiration value?

		// Token is valid.
		// TODO: Save session into redis?
		response.send();
	});
		
		
}
 
server.route({
	path: '/login/facebook',
	method: 'POST',
	config: loginRouteConfig,
	handler: facebookLoginHandler
});

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

// Initialize the server and start it.
// TODO: This server should be https!
initServer();

