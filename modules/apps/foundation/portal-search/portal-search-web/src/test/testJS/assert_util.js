AUI().use(
	'aui',
	function(A) {
		Liferay.Test = Liferay.Test || {};

		var assertSameItems = function(expected, actual) {
			var message = 'Expected [' + expected + ']; got [' + actual + ']';

			assert.equal(expected.length, actual.length, message);
			
			expected.forEach(
				function(item) {
					assert(actual.includes(item), message);
				}
			);
		};

		Liferay.Test.assertSameItems = assertSameItems;

		var assertEmpty = function(array) {
			assert.equal(0, array.length);
		}

		Liferay.Test.assertEmpty = assertEmpty;
	}
);