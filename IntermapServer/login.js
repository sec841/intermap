
var Config = require('./config');
var Data = require('./data');

var https = require('https');
var url = require('url');
var bl = require('bl'); // https://github.com/rvagg/bl
var crypto = require('crypto');

var User = Data.User;

var LOGOUT_STATUS_OK = "LOGOUT_STATUS_OK";
var LOGIN_STATUS_OK = "LOGIN_STATUS_OK";
var LOGIN_STATUS_INTERNAL_ERROR = "LOGIN_STATUS_INTERNAL_ERROR";
var LOGIN_STATUS_TOKEN_ERROR = "LOGIN_STATUS_TOKEN_ERROR";
var LOGIN_STATUS_INVALID_CREDENTIALS = "LOGIN_STATUS_INVALID_CREDENTIALS";
var LOGIN_STATUS_UNKNOWN_CREDENTIALS = "LOGIN_STATUS_UNKNOWN_CREDENTIALS";

var TAG = "LOGIN:";

// The facebook app token.
// Retrieved at start-up.
// TODO: Refresh this token every X hours.
fbAppToken = undefined;
/*
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
	        hashed_password: 'TODO',
			salt: makeSalt(), // TODO: Should this really be part of the profile??
	        name: 'Guillaume',
	        facebook_id: 10154602565895392
	    },
	    2: {
	        email: 'zemustain@msn.com',
	        hashed_password: 'TODO', 
			salt: makeSalt(),
	        name: 'Sebastien',
	        facebook_id: 10154499884250392
	    }
	};
*/
//-----------------------------------------------------------------------------
// Create a session for the given user.
function createSession(user) {
	return { 
		id : user.id,
		email : user.email,
		hashed_password : user.hashed_password, 
		salt : user.salt
	};
}

//-----------------------------------------------------------------------------
//
function finalizeLoginRequest(request, user) {
	// Set session cookie and send response.		
	// TODO: Check expiration value?
	// TODO: Save session into redis?
	var session = createSession(user);
	request.auth.session.set(session);
	User.onPostLogin(
		user._id, 
		request.info.remoteAddress
	);	
	console.log("User logged in:", user);
}

//-----------------------------------------------------------------------------
//
function makeSalt() {
	return Math.round((new Date().valueOf() * Math.random())) + '';
}
exports.makeSalt = makeSalt;

//-----------------------------------------------------------------------------
//
function checkPassword(user, plainText) {
    return hashPassword(user.salt, plainText) === user.hashed_password;
}
exports.checkPassword = checkPassword;

//-----------------------------------------------------------------------------
//
function hashPassword(salt, password) {
    if (!password) 
		return '';
    try {
		return crypto
			.createHmac('sha1', salt)
			.update(password)
			.digest('hex');
    } catch (err) {
      return '';
    }
}
exports.hashPassword = hashPassword;

//-----------------------------------------------------------------------------
//
// Retrieve the app token for this app from Facebook. 
// Callback takes two parameters: err and token.
// TODO: Refresh this token regularly in case it expires (but it shouldn't!).
function getFacebookAppToken(callback) {
	var fbUrl = url.parse("https://graph.facebook.com/oauth/access_token");
	fbUrl.query = {};
	fbUrl.query.client_id = Config.facebook.app_id;
	fbUrl.query.client_secret = Config.facebook.app_secret;
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

//-----------------------------------------------------------------------------
// HANDLERS
//-----------------------------------------------------------------------------
  
//-----------------------------------------------------------------------------
//
var loginRouteConfig = {
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
var logoutHandler = function (request, reply) {
	// TODO: Check if user is logged in / known.
    request.auth.session.clear();
    return reply({logoutStatus : LOGOUT_STATUS_OK});
};
 
//-----------------------------------------------------------------------------
//
 var testHandler = function (request, reply) {
	reply({hello:"Hello " + request.auth.credentials.email});
};
  
//-----------------------------------------------------------------------------
//
var basicLoginHandler = function (request, reply) {

    if (request.auth.isAuthenticated) {
    	// Already authenticated.
		User.onPostLogin(
			request.auth.credentials.id, 
			request.info.remoteAddress);
		
		console.log("User logged in with cookie:", 
			request.auth.credentials.email);
        return reply({loginStatus : LOGIN_STATUS_OK});
    }
	
	var email = request.payload.email;
	var password = request.payload.password;
	
    if (!(email && password)) {
		// The email and password must not be null.
        return reply({loginStatus: LOGIN_STATUS_INVALID_CREDENTIALS}).code(401);
    }
    else {
		// Find user account in database by email address.
		var response = reply({loginStatus : LOGIN_STATUS_OK}).hold(); 
		
		User.findOne({ email: email }).exec(function (err, user) {
			if(err) {
				console.error(err);
				response.source = { 
					loginStatus : LOGIN_STATUS_INTERNAL_ERROR
				};
				response.statusCode = 500;
				return response.send();
			}
			else if(user === null || 
				!checkPassword(user, password)) {
				// TODO: Log this error properly.
				response.source = { 
					loginStatus : LOGIN_STATUS_UNKNOWN_CREDENTIALS
				};
				response.statusCode = 401;
				return response.send();
			}
		
			// User successfully authenticated.
			// Set session cookie and send response.
			finalizeLoginRequest(request, user);
			return response.send();
		});
    }
};
 
//-----------------------------------------------------------------------------
//
 function facebookLoginHandler(request, reply) { 
 
	// TODO: Add some extra security / flood protection.
	// TODO: Cache repeated responses if we already know the request?
    if (request.auth.isAuthenticated) {
    	// Already authenticated.
		console.log("Credentials", request.auth.credentials);
		User.onPostLogin(
			request.auth.credentials.id, 
			request.info.remoteAddress);
			
		console.log("User logged in with cookie:", 
			request.auth.credentials.email);
        return reply({loginStatus : LOGIN_STATUS_OK});
    }
 
	if(!request.payload.hasOwnProperty('access_token')) {
		// TODO: Properly log errors with IP addresses and all.
		// TODO: Verifying Graph API Calls with appsecret_proof
		var err = {"error" : "An access_token was not provided."};
		console.error(err);
		return reply(err).code(401);
	}

	// http://hapijs.com/api/v6.0.2#response-object
	var response = reply({loginStatus : LOGIN_STATUS_OK}).hold(); 
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
		if(!data.is_valid || data.app_id != Config.facebook.app_id) {
			console.error(data);
			response.source = {
				loginStatus : LOGIN_STATUS_TOKEN_ERROR, 
				"error" : "Invalid user access token."
			};
			response.statusCode = 401;
			return response.send();
		}
		
		// Now, check that the token is from a known user.
		// Find user account in database by facebook_id (facebook user id).
		User.findOne({ facebook_id: data.user_id }).exec(function (err, user) {
			if(err) {
				// TODO: handleError() function.
				console.error(err);
				response.source = { 
					loginStatus : LOGIN_STATUS_INTERNAL_ERROR, 
					"error" : "Could not check user token."
				};
				response.statusCode = 500;
				return response.send();
			}
			else if(user === null) {
				// TODO: Log this error properly.
				response.source = { 
					loginStatus : LOGIN_STATUS_UNKNOWN_CREDENTIALS
				};
				response.statusCode = 401;
				return response.send();
			}
			
			// User successfully authenticated.
			finalizeLoginRequest(request, user);	
			return response.send();			
		});
	});
}

//-----------------------------------------------------------------------------
// EXPORTS
//-----------------------------------------------------------------------------

// Initializes the login routes and calls the given callback(err) when 
// completed. 'err' is null if initialization is successful.
exports.init = function (server, callback) {

    server.auth.strategy('session', 'cookie', {
        password: 'secret', // used for Iron cookie encoding. TODO: Change this password!  Should be in the DB.
        cookie: 'sid', // the cookie name. Defaults to 'sid'.
        //redirectTo: false, 
		clearInvalid: true, //  if true, any authentication cookie that fails validation will be marked as expired in the response and cleared. Defaults to false.
        isSecure: false // if false, the cookie is allowed to be transmitted over insecure connections which exposes it to attacks. Defaults to true. TODO: Set this to true!
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
	  	path: '/login',
	  	method: 'POST',
	  	config: loginRouteConfig,
	  	handler: basicLoginHandler
	});
	
	server.route({
	  	path: '/login/facebook',
	  	method: 'POST',
	  	config: loginRouteConfig,
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

	console.log(TAG, "Obtaining facebook app token...");
	getFacebookAppToken(function (err, token) {
		if(err) {
			return callback(err);
		}
		console.log(TAG, "Obtaining facebook app token: DONE.");
		fbAppToken = token;

		return callback(null);
	});

}