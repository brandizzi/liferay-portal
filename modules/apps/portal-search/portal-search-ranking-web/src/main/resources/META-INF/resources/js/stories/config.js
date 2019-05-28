import {addParameters, configure} from '@storybook/react';

addParameters(
	{
		options: {

			/**
			 * show/hide tool bar
			 * @type {Boolean}
			 */
			isToolshown: true,

			/**
			 * Name to display in the top left corner
			 * Default: 'Storybook'
			 * @type {String}
			 */
			'theme.brandTitle': 'Search Ranking'
		}
	}
);

function loadStories() {
	// eslint-disable-next-line global-require
	require('../stories');
}

configure(loadStories, module);