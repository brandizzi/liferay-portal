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

package com.liferay.portal.search.web.internal.suggestions;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.web.internal.search.suggest.KeywordsSuggestionHolder;
import com.liferay.portal.search.web.internal.suggestions.display.QuerySuggestion;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Adam Brandizzi
 */
public class QuerySuggestionBuilder {

	public List<QuerySuggestion> getRelatedSuggestions() {
		Stream<String> stream = _relatedSuggestionsStrings.stream();

		return stream.map(
			this::_buildQuerySuggestion
		).filter(
			Objects::nonNull
		).collect(
			Collectors.toList()
		);
	}

	public QuerySuggestion getSpellCheckSuggestion() {
		return _buildQuerySuggestion(_spellCheckSuggestion);
	}

	public void setKeywords(String keywords) {
		_keywords = keywords;
	}

	public void setParameterName(String parameterName) {
		_parameterName = parameterName;
	}

	public void setRelatedSuggestionsStrings(
		List<String> relatedSuggestionsStrings) {

		_relatedSuggestionsStrings = relatedSuggestionsStrings;
	}

	public void setSearchURL(String searchURL) {
		_searchURL = searchURL;
	}

	public void setSpellCheckSuggestionString(
		String spellCheckSuggestionString) {

		_spellCheckSuggestion = spellCheckSuggestionString;
	}

	private String _buildFormattedQuery(
		KeywordsSuggestionHolder keywordsSuggestionHolder) {

		List<String> suggestedKeywords =
			keywordsSuggestionHolder.getSuggestedKeywords();

		List<String> elements = new ArrayList<>(suggestedKeywords.size());

		for (String keyword : suggestedKeywords) {
			elements.add(_getKeywordElement(keywordsSuggestionHolder, keyword));
		}

		return String.join(" ", elements);
	}

	private QuerySuggestion _buildQuerySuggestion(String suggestion) {
		if (!_isValidSuggestion(suggestion)) {
			return null;
		}

		KeywordsSuggestionHolder keywordsSuggestionHolder =
			new KeywordsSuggestionHolder(suggestion, _keywords);

		return new QuerySuggestion(
			_buildFormattedQuery(keywordsSuggestionHolder),
			_buildSearchURL(keywordsSuggestionHolder));
	}

	private String _buildSearchURL(
		KeywordsSuggestionHolder keywordsSuggestionHolder) {

		List<String> suggestedKeywords =
			keywordsSuggestionHolder.getSuggestedKeywords();

		String parameterValue = String.join(
			StringPool.SPACE, suggestedKeywords);

		return HttpUtil.setParameter(
			_searchURL, _parameterName, parameterValue);
	}

	private String _getKeywordElement(
		KeywordsSuggestionHolder keywordsSuggestionHolder, String keyword) {

		StringBundler sb = new StringBundler(5);

		sb.append("<span class=\"");

		String keywordCssClass = "unchanged-keyword";

		if (keywordsSuggestionHolder.hasChanged(keyword)) {
			keywordCssClass = "changed-keyword";
		}

		sb.append(keywordCssClass);

		sb.append("\">");
		sb.append(HtmlUtil.escape(keyword));
		sb.append("</span>");

		return sb.toString();
	}

	private boolean _isValidSuggestion(String suggestion) {
		if (Objects.equals(_keywords, suggestion)) {
			return false;
		}

		if (Validator.isNull(suggestion)) {
			return false;
		}

		return true;
	}

	private String _keywords;
	private String _parameterName;
	private List<String> _relatedSuggestionsStrings;
	private String _searchURL;
	private String _spellCheckSuggestion;

}