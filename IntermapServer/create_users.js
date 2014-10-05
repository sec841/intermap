
var Data = require('./data');
var User = Data.User;

var TAG = "CREATE_USERS:";

Data.init(function(err) {
	if(err) {
		console.error(TAG, err);
		console.error(TAG, "FATAL ERROR: Could not connect to mongodb database.");
		process.exit(1);
	}
	
	var g = new User();
	g.email = 'cotegu@gmail.com';
	g.name = 'Guillaume';
	g.password = 'test';
	g.facebook_id = 10154602565895392;

	g.save(function (err) {
		if(err) {
			console.log(err);
		}
		else {
			console.log("User inserted!");
		}
	});

	g = new User();
	g.email = 'zemustain@msn.com';
	g.name = 'Sebastien';
	g.password = 'test';
	g.facebook_id = 10154499884250392;

	g.save(function (err) {
		if(err) {
			console.log(err);
		}
		else {
			console.log("User inserted!");
		}
	});
});



// END TEST