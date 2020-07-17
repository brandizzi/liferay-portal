/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

AUI.add(
	'liferay-search-facet-util',
	A => {
		var FacetUtil = {
			addURLParameter(key, value, parameterArray) {
				key = encodeURIComponent(key);
				value = encodeURIComponent(value);

				parameterArray[parameterArray.length] = [key, value].join('=');

				return parameterArray;
			},

			changeSelection(event) {
				var form = event.currentTarget.form;

				if (!form) {
					return;
				}

				var selections = [];

				var formCheckboxes = $('#' + form.id + ' input.facet-term');

				formCheckboxes.each((index, value) => {
					if (value.checked) {
						selections.push(value.getAttribute('data-term-id'));
					}
				});

				FacetUtil.selectTerms(form, selections);
			},

			clearSelections(event) {
				var form = $(event.currentTarget).closest('form')[0];

				if (!form) {
					return;
				}

				var selections = [];

				FacetUtil.selectTerms(form, selections);
			},

			removeURLParameters(key, parameterArray) {
				key = encodeURIComponent(key);

				var newParameters = parameterArray.filter(item => {
					var itemSplit = item.split('=');

					if (itemSplit && itemSplit[0] === key) {
						return false;
					}

					return true;
				});

				return newParameters;
			},

			selectTerms(form, selections) {
				var formParameterName = $(
					'#' + form.id + ' input.facet-parameter-name'
				);

				var key = formParameterName[0].value;

				document.location.search = FacetUtil.updateQueryString(
					key,
					selections,
					document.location.search
				);
			},

			setURLParameter(url, name, value) {
				var parts = url.split('?');

				var address = parts[0];

				var queryString = parts[1];

				if (!queryString) {
					queryString = '';
				}

				queryString = Liferay.Search.FacetUtil.updateQueryString(
					name,
					[value],
					queryString
				);

				return address + '?' + queryString;
			},

			setURLParameters(key, values, parameterArray) {
				var newParameters = FacetUtil.removeURLParameters(
					key,
					parameterArray
				);

				values.forEach(item => {
					newParameters = FacetUtil.addURLParameter(
						key,
						item,
						newParameters
					);
				});

				return newParameters;
			},

			updateQueryString(key, selections, queryString) {
				var search = queryString;

				var hasQuestionMark = false;

				if (search[0] === '?') {
					hasQuestionMark = true;
				}

				if (hasQuestionMark) {
					search = search.substr(1);
				}

				var parameterArray = search.split('&').filter(item => {
					return item.trim() !== '';
				});

				var newParameters = FacetUtil.setURLParameters(
					key,
					selections,
					parameterArray
				);

				search = newParameters.join('&');

				if (hasQuestionMark) {
					search = '?' + search;
				}

				return search;
			}
		};

		Liferay.namespace('Search').FacetUtil = FacetUtil;
	},
	'',
	{
		requires: []
	}
);
