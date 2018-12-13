AUI.add(
	'liferay-custom-search-bar',

	function(A) {

		var FacetUtil = Liferay.Search.FacetUtil;

		var CustomSearchBar = function(form) {

			var instance = this;

			instance.form = form;

			instance.form.on('submit', A.bind(instance._onSubmit, instance));

			instance.keywordsInput = instance.form.one('.search-bar-keywords-input');

			var searchButton = instance.form.one('.search-bar-search-button');

			searchButton.on('click', A.bind(instance._onClick, instance));
		};

		A.mix(
			CustomSearchBar.prototype,
			{
				getKeywords: function() {
					var instance = this;

					var keywords = instance.keywordsInput.val();

					var result = keywords.replace(/^\s+|\s+$/, '');

					return  result
				},

				isSubmitEnabled: function() {
					var instance = this;

					return (instance.getKeywords() !== '');
				},

				search: function(event) {
					var instance = this;

					if (instance.isSubmitEnabled()) {
						FacetUtil.changeInput(event);
					}
				},

				_onClick: function(event) {
					var instance = this;

					FacetUtil.changeInput(event);
				},

				_onSubmit: function(event) {
					var instance = this;

					event.stopPropagation();

					instance.search(event);
				}
			}
		);

		Liferay.namespace('Search').CustomSearchBar = CustomSearchBar;
	},
	'',
	{
		requires: ['liferay-search-facet-util']
	}
);