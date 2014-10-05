var Config = require('./config');
var Login = require('./login');

var Mongoose = require('mongoose');

var Schema = Mongoose.Schema;

var TAG = "DATA:";

//var ObjectId = Schema.ObjectId;
		
//-----------------------------------------------------------------------------
// SCHEMAS
//-----------------------------------------------------------------------------
	
// Regular expression to validate email addresses.
// Taken from: 
// http://stackoverflow.com/questions/46155/validate-email-address-in-javascript
var regexEmail = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
	
var UserSchema = new Schema({
    //user_id         : { type: ObjectId }
    email               : { type: String, unique: true, match: regexEmail }
  , name                : { type: String }
  , hashed_password     : { type: String }
  , salt                : { type: String }
  , facebook_id         : { type: Number, unique: true }
  , last_login          : { type: Date, default: Date.now }
  , creation_date       : { type: Date, default: Date.now }
  , last_remote_address : { type: String }
  , last_updated        : { type: Date }
});

//-----------------------------------------------------------------------------
// MIDDLEWARES
//-----------------------------------------------------------------------------

UserSchema.pre('save', function(next) {
	this.last_updated = Date.now();

	if (!this.isNew) 
		return next();

	if (!(this.password && this.password.length)) {
		next(new Error('Invalid password'));
	} else {
		next();
	}
})


//-----------------------------------------------------------------------------
// VIRTUALS
//-----------------------------------------------------------------------------

UserSchema
	.virtual('password')
	.set(function(password) {
		var salt = Login.makeSalt();
		this._password = password;
		this.salt = salt;
		this.hashed_password = Login.hashPassword(salt, password);
		})
	.get(function() { return this._password });

//-----------------------------------------------------------------------------
// VALIDATIONS
//-----------------------------------------------------------------------------

UserSchema.path('name').validate(function (name) {
  //if (this.skipValidation()) 
  //    return true;
  return name.length > 0;
}, 'Name cannot be blank');

//-----------------------------------------------------------------------------
// 
/*
UserSchema.path('email').validate(function (email, fn) {
	var User = mongoose.model('User');
	//if (this.skipValidation()) 
	//  fn(true);

	// Check only when it is a new user or when email field is modified
	if (this.isNew || this.isModified('email')) {
		User.find({ email: email }).exec(function (err, users) {
			fn(!err && users.length === 0);
		});
	} 
	else { 
		fn(true);
	}
}, 'Email already exists');
*/

//-----------------------------------------------------------------------------
// 
UserSchema.path('hashed_password').validate(function (hashed_password) {
  //if (this.skipValidation()) 
	//return true;
  return hashed_password.length;
}, 'Password cannot be blank');

//-----------------------------------------------------------------------------
// INSTANCE METHODS
//-----------------------------------------------------------------------------

//-----------------------------------------------------------------------------
//
/*
function lastLoginUpdateHandler(err) {
	if(err) {
		console.err(TAG, 
			"Could not update last_updated", err);
	}
	else if(Config.debug) {
		console.log("Updated login time.");
	}
}
*/
/*
UserSchema.methods = {
	
	updateLastLoginDate: function (cb) {
		this.last_login = Date.now();
		this.save(function(err) {
			lastLoginUpdateHandler(err);
		});
	}
}
*/

//-----------------------------------------------------------------------------
// STATICS
//-----------------------------------------------------------------------------

UserSchema.statics = {
	/*updateLastLoginDate: function(userId) {
		User.update({ _id: userId }, { last_login: Date.now() }, 
			{ multi: false }, 
			function (err, numberAffected, raw) {
				lastLoginUpdateHandler(err);
			});
	}*/
	onPostLogin: function(userId, remoteAddress) {
		
		// Update the last login time.
		User.update({ _id: userId }, 
					{ last_login: Date.now(), 
		              last_remote_address: remoteAddress }, 
			{ multi: false }, 
			function (err, numberAffected, raw) {
				if(err || !numberAffected) {
					console.log(TAG, "onPostLogin", 
						"Failed to updated user details for userId = " + userId, err ? err : "");
				}
				else if(Config.debug) {
					console.log(TAG, "onPostLogin", "Updated user details for userId = " + userId);
				}
			});
		if(Config.save_login_history) {
			// See: https://github.com/hapijs/hapi/blob/master/docs/Reference.md#request-properties
			// TODO Save login details into a history table. 
			// var remoteAddress = request.info.remoteAddress;
		}
	}
}

//-----------------------------------------------------------------------------
// NAME & EXPORT SCHEMAS
//-----------------------------------------------------------------------------

var User = Mongoose.model('user', UserSchema);
exports.User = User;

//-----------------------------------------------------------------------------
// FUNCTIONS
//-----------------------------------------------------------------------------

// Initializes the database connection and calls the given callback(err) when 
// completed. 'err' is null if initialization is successful.
exports.init = function (callback) {
	try {
		Mongoose.connect(Config.mongodb.url);
		callback(null);
	} catch(err) {
		callback(err);
	}
} 

