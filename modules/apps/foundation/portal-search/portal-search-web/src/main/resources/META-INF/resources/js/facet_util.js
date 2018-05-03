AUI.add(
	'liferay-search-facet-util',
	function(A) {
		var FacetUtil = {
			addURLParameter: function(key, value, parameterArray) {
				key = encodeURI(key);
				value = encodeURI(value);

				parameterArray[parameterArray.length] = [key, value].join('=');

				return parameterArray;
			},

			changeSelection: function(event) {
				var form = event.currentTarget.form;

				if (!form) {
					return;
				}

				var selections = [];

				var formCheckboxes = $('#' + form.id + ' input.facet-term');

				formCheckboxes.each(
					function(index, value) {
						if (value.checked) {
							selections.push(value.getAttribute('data-term-id'));
						}
					}
				);

				FacetUtil.selectTerms(form, selections);
			},

			clearSelections: function(event) {
				var form = $(event.currentTarget).closest('form')[0];

				if (!form) {
					return;
				}

				var selections = [];

				FacetUtil.selectTerms(form, selections);
			},

			removeURLParameters: function(key, parameterArray) {
				var key = encodeURI(key);

				var newParameters =  parameterArray.filter(
					function(item) {
						var result = true;

						var itemSplit = item.split('=');

						if (itemSplit && (itemSplit[0] === key)) {
							result = false;
						}

						return result;
					}
				);

				return newParameters;
			},

			selectTerms: function(form, selections) {
				var formParameterName = $('#' + form.id + ' input.facet-parameter-name');

				var key = formParameterName[0].value;

				document.location.search = FacetUtil.updateQueryString(key, selections, document.location.search);
			},

			setURLParameters: function(key, values, parameterArray) {
				var newParameters = FacetUtil.removeURLParameters(key, parameterArray);

				values.forEach(
					function (item) {
						newParameters = FacetUtil.addURLParameter(key, item, newParameters);
					}
				);

				return newParameters;
			},

			updateQueryString: function(key, selections, queryString) {
				var parameterArray = queryString.substr(1).split('&').filter(
					function(item) {
						return item.trim() !== '';
					}
				);

				var newParameters = FacetUtil.setURLParameters(key, selections, parameterArray);

				return newParameters.join('&');
			}
		};

		Liferay.namespace('Search').FacetUtil = FacetUtil;
	},
	'',
	{
		requires: []
	}
);