
// TODO: Perhaps use a module like 'nconf'?
var config = {}

config.debug = true;
config.save_login_history = false; // TODO

config.server = {};
config.facebook = {};
config.redis = {};
config.mongodb = {};

config.server.hostname = '192.168.0.14';
config.server.default_port = 8080;

config.facebook.app_id = 581376651971597;
config.facebook.app_secret = '';

//config.redis.uri = process.env.DUOSTACK_DB_REDIS;
//config.redis.host = 'hostname';
//config.redis.port = 6379;

config.mongodb.url = 'mongodb://localhost/intermap';

module.exports = config;