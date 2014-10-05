/**
 * Main javascript file for intermap server.
 */

var Config = require('./config');
var Login = require("./login");
var Data = require("./data");
var Hapi = require('hapi');

var port = parseInt(process.argv[2] || Config.server.default_port);

var TAG = "INDEX:";

/* 
 * Hapi server setup. 
 */
 var server = Hapi.createServer(Config.server.hostname, port);

//-----------------------------------------------------------------------------
// HANDLERS
//-----------------------------------------------------------------------------
 
//-----------------------------------------------------------------------------
//
function initServer(pluginError) {

    if (pluginError) {
		console.error(TAG, pluginError);
		console.error(TAG, "FATAL ERROR: Could not start server.");
		throw pluginError; // something bad happened loading the plugin
    }
	
	Login.init(server, function(err) {
		if(err) {
			console.error(TAG, err);
			console.error(TAG, "FATAL ERROR: Could not start server.");
			process.exit(1);
		}
		
		Data.init(function(err) {
			if(err) {
				console.error(TAG, err);
				console.error(TAG, "FATAL ERROR: Could not connect to mongodb database.");
				process.exit(1);
			}
		});

		
		// Login initialization completed.
		// Start the server.
		server.start();
		console.log("Server started.");
	});
}
//-----------------------------------------------------------------------------
//
server.pack.register(require('hapi-auth-cookie'), initServer);
// Initialize the server and start it.
// TODO: This server should be https!
//

