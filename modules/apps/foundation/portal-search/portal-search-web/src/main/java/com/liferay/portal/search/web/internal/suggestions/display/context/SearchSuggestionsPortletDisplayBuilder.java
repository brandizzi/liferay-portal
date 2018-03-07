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

package com.liferay.portal.search.web.internal.suggestions.display.context;

import com.liferay.portal.search.web.internal.suggestions.display.QuerySuggestion;

import java.util.Collections;
import java.util.List;

/**
 * @author Adam Brandizzi
 */
public class SearchSuggestionsPortletDisplayBuilder {

	public SearchSuggestionsPortletDisplayContext build() {
		SearchSuggestionsPortletDisplayContext
			searchSuggestionsPortletDisplayContext =
				new SearchSuggestionsPortletDisplayContext();

		searchSuggestionsPortletDisplayContext.setHasRelatedSuggestions(
			_relatedSuggestionsEnabled && !_relatedSuggestions.isEmpty());
		searchSuggestionsPortletDisplayContext.setHasSpellCheckSuggestion(
			_spellCheckSuggestionEnabled && (_spellCheckSuggestion != null));
		searchSuggestionsPortletDisplayContext.setRelatedSuggestions(
			_relatedSuggestions);
		searchSuggestionsPortletDisplayContext.setRelatedSuggestionsEnabled(
			_relatedSuggestionsEnabled);
		searchSuggestionsPortletDisplayContext.setSpellCheckSuggestion(
			_spellCheckSuggestion);
		searchSuggestionsPortletDisplayContext.setSpellCheckSuggestionEnabled(
			_spellCheckSuggestionEnabled);

		return searchSuggestionsPortletDisplayContext;
	}

	public void setRelatedSuggestions(
		List<QuerySuggestion> relatedSuggestions) {

		_relatedSuggestions = relatedSuggestions;
	}

	public void setRelatedSuggestionsEnabled(
		boolean relatedSuggestionsEnabled) {

		_relatedSuggestionsEnabled = relatedSuggestionsEnabled;
	}

	public void setSpellCheckSuggestion(QuerySuggestion spellCheckSuggestion) {
		_spellCheckSuggestion = spellCheckSuggestion;
	}

	public void setSpellCheckSuggestionEnabled(
		boolean spellCheckSuggestionEnabled) {

		_spellCheckSuggestionEnabled = spellCheckSuggestionEnabled;
	}

	private List<QuerySuggestion> _relatedSuggestions = Collections.emptyList();
	private boolean _relatedSuggestionsEnabled;
	private QuerySuggestion _spellCheckSuggestion;
	private boolean _spellCheckSuggestionEnabled;

}