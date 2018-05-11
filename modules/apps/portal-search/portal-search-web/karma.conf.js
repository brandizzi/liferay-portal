var liferayKarmaAlloyConfig = require('liferay-karma-alloy-config');
var liferayKarmaConfig = require('liferay-karma-config');

module.exports = function(config) {
	liferayKarmaConfig(config);

	config.files = [];

	liferayKarmaAlloyConfig(config);

	config.autowatch = false;
	config.browserConsoleLogOptions = {
		format: '%b %T: %m',
		level: 'log',
		terminal: true
	};
	config.singleRun = true;

	var resourcesPath = 'src/main/resources/META-INF/resources/';
	var testPath = 'src/test/testJS/';

	config.files.push(
		{
			included: true,
			pattern: resourcesPath + '/**/!(config)*.js'
		},
		{
			included: true,
			pattern: testPath + '/*_util.js'
		},
		'src/test/testJS/*_test.js'
	);
};