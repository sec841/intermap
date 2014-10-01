/**
 * Main javascript file for intermap server.
 */

var port = parseInt(process.argv[2] || 8080);
var Hapi = require('hapi');
var Login = require("./login.js");

var TAG = "INDEX:";

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

