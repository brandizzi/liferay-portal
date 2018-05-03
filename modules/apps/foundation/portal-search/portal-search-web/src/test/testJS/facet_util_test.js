'use strict';

describe(
	'Liferay.Search.FacetUtil',
	function() {
		before(
			function(done) {
				AUI().use(
					'liferay-search-facet-util',
					function(A) {
						done();
					}
				);
			}
		);

		describe(
			'unit',
			function() {
				describe(
					'.setURLParameters()',
					function() {
						it(
							'should add new selections.',
							function(done) {
								var parameterArray = Liferay.Search.FacetUtil.setURLParameters('key', ['sel1', 'sel2'], []);

								assert.equal(2, parameterArray.length)
								assert(parameterArray.includes('key=sel1'));
								assert(parameterArray.includes('key=sel2'));

								done();
							}
						);

						it(
							'should remove old selections.',
							function(done) {
								var parameterArray = Liferay.Search.FacetUtil.setURLParameters('key', ['sel2', 'sel3'], ['key=sel1']);

								assert.equal(2, parameterArray.length)
								assert(!parameterArray.includes('key=sel1'));
								assert(parameterArray.includes('key=sel2'));
								assert(parameterArray.includes('key=sel3'));

								done();
							}
						);

						it(
							'should preserve other selections.',
							function(done) {
								var parameterArray = Liferay.Search.FacetUtil.setURLParameters('key1', ['sel1'], ['key2=sel2']);

								assert.equal(2, parameterArray.length)
								assert(parameterArray.includes('key1=sel1'));
								assert(parameterArray.includes('key2=sel2'));

								done();
							}
						);
					}
				);

				describe(
					'.removeURLParameters()',
					function() {
						it(
							'remove given parameter.',
							function(done) {
								var parameterArray = Liferay.Search.FacetUtil.removeURLParameters('key', ['key=sel1', 'key=sel2']);

								assert.equal(0, parameterArray.length);

								done();
							}
						);

						it(
							'should preserve other parameters.',
							function(done) {
								var parameterArray = Liferay.Search.FacetUtil.removeURLParameters('key1', ['key1=sel1', 'key2=sel2']);

								assert.equal(1, parameterArray.length);
								assert(parameterArray.includes('key2=sel2'));

								done();
							}

						);

					}
				);
				describe(
					'.updateQueryString()',
					function() {
						it(
							'should remove old selections.',
							function(done) {
								var queryString = Liferay.Search.FacetUtil.updateQueryString('key', ['sel2', 'sel3'], '?key=sel1');

								assert.equal(queryString, 'key=sel2&key=sel3');

								done();
							}
						);

						it(
							'should add new selections.',
							function(done) {
								var queryString = Liferay.Search.FacetUtil.updateQueryString('key1', ['sel1'], '?key2=sel2');

								assert.equal(queryString, 'key2=sel2&key1=sel1');

								done();
							}
						);
					}
				);
			}
		);
	}
);