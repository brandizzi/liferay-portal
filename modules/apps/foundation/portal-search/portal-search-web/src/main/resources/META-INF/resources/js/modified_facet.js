AUI.add(
	'liferay-search-modified-facet',
	function(A) {
		var FacetUtil = Liferay.Search.FacetUtil;

		var ModifiedFacetFilter = function(fromInputDatePicker, toInputDatePicker) {
			var instance = this;

			instance.fromInputDatePicker = fromInputDatePicker;

			instance.toInputDatePicker = toInputDatePicker;
		};

		ModifiedFacetFilter.prototype.filter = function(event) {
			var instance = this;

			var fromDate = instance.fromInputDatePicker.getDate();

			var toDate = instance.toInputDatePicker.getDate();

			var modifiedFromParameter = fromDate.toISOString().slice(0, 10);

			var modifiedToParameter = toDate.toISOString().slice(0, 10);

			var parameterArray = document.location.search.substr(1).split('&');

			var newParameters = FacetUtil.removeURLParameters('modified', parameterArray);

			newParameters = FacetUtil.removeURLParameters('modifiedFrom', newParameters);

			newParameters = FacetUtil.removeURLParameters('modifiedTo', newParameters);

			newParameters = FacetUtil.addURLParameter('modifiedFrom', modifiedFromParameter, newParameters);

			newParameters = FacetUtil.addURLParameter('modifiedTo', modifiedToParameter, newParameters);

			document.location.search = newParameters.join('&');
		};

		Liferay.namespace('Search').ModifiedFacetFilter = ModifiedFacetFilter;
	},
	'',
	{
		requires: ['liferay-search-facet-util']
	}
);