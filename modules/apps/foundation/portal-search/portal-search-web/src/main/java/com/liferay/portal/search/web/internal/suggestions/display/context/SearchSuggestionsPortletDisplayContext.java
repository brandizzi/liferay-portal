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
public class SearchSuggestionsPortletDisplayContext {

	public List<QuerySuggestion> getRelatedSuggestions() {
		return _relatedSuggestions;
	}

	public QuerySuggestion getSpellCheckSuggestion() {
		return _spellCheckSuggestion;
	}

	public boolean hasRelatedSuggestions() {
		return _hasRelatedSuggestions;
	}

	public boolean hasSpellCheckSuggestion() {
		return _hasSpellCheckSuggestion;
	}

	public boolean isRelatedSuggestionsEnabled() {
		return _relatedSuggestionsEnabled;
	}

	public boolean isSpellCheckSuggestionEnabled() {
		return _spellCheckSuggestionEnabled;
	}

	public void setHasRelatedSuggestions(boolean hasRelatedSuggestions) {
		_hasRelatedSuggestions = hasRelatedSuggestions;
	}

	public void setHasSpellCheckSuggestion(boolean hasSpellCheckSuggestion) {
		_hasSpellCheckSuggestion = hasSpellCheckSuggestion;
	}

	public void setRelatedSuggestions(
		List<QuerySuggestion> relatedSuggestions) {

		_relatedSuggestions = relatedSuggestions;
	}

	public void setRelatedSuggestionsEnabled(
		boolean relatedSuggestionsEnabled) {

		_relatedSuggestionsEnabled = relatedSuggestionsEnabled;
	}

	public void setSpellCheckSuggestion(QuerySuggestion spellCheckSuggesion) {
		_spellCheckSuggestion = spellCheckSuggesion;
	}

	public void setSpellCheckSuggestionEnabled(
		boolean spellCheckSuggestionEnabled) {

		_spellCheckSuggestionEnabled = spellCheckSuggestionEnabled;
	}

	private boolean _hasRelatedSuggestions;
	private boolean _hasSpellCheckSuggestion;
	private List<QuerySuggestion> _relatedSuggestions = Collections.emptyList();
	private boolean _relatedSuggestionsEnabled;
	private QuerySuggestion _spellCheckSuggestion;
	private boolean _spellCheckSuggestionEnabled;

}