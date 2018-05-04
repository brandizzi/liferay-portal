AUI.add(
	'liferay-search-bar',
	function(A) {
		var SearchBar = function(form) {
			var instance = this;

			instance.form = form;

			instance.form.on('submit', A.bind(instance._onSubmit, instance));

			var searchButton = instance.form.one('.search-bar-search-button');

			searchButton.on('click', A.bind(instance._onClick, instance));
		};

		A.mix(
			SearchBar.prototype,
			{
				search: function() {
					var instance = this;

					var keywordsInput = instance.form.one('.search-bar-keywords-input');

					var keywords = keywordsInput.val();

					keywords = keywords.replace(/^\s+|\s+$/, '');

					if (keywords !== '') {
						submitForm(instance.form);
					}
				},

				_onClick: function(event) {
					var instance = this;

					instance.search();
				},

				_onSubmit: function(event) {
					var instance = this;

					event.stopPropagation();

					instance.search();
				}
			}
		);

		Liferay.namespace('Search').SearchBar = SearchBar;
	},
	'',
	{
		requires: []
	}
);