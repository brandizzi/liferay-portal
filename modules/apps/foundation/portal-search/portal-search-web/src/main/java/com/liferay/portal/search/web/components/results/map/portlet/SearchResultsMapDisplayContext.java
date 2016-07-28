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

package com.liferay.portal.search.web.components.results.map.portlet;

import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.search.web.internal.result.display.context.SearchResultSummaryDisplayContext;
import com.liferay.portal.search.web.search.request.SearchResponse;

import java.io.Serializable;

import java.util.List;

/**
 * @author André de Oliveira
 */
public class SearchResultsMapDisplayContext implements Serializable {

	public static final String ATTRIBUTE = "SearchResultsMapDisplayContext";

	public SearchResultsMapDisplayContext(
		SearchResponse searchResponse, String keywords, String mapMarkersJSON) {

		_searchResponse = searchResponse;
		_keywords = keywords;
		_mapMarkersJSON = mapMarkersJSON;
	}

	public String getKeywords() {
		return _keywords;
	}

	public String getMapMarkersJSON() {
		return _mapMarkersJSON;
	}

	public String[] getQueryTerms() {
		return _searchResponse.getHighlights();
	}

	public SearchResponse getSearchResponse() {
		return _searchResponse;
	}

	public SearchContainer<Document> getSearchResultsContainer() {
		List<Document> documents = _searchResponse.getDocuments();

		SearchContainer<Document> searchContainer = new SearchContainer<>();

		searchContainer.setResults(documents);

		return searchContainer;
	}

	public SearchResultSummaryDisplayContext getSummary(Document document) {
		return _searchResultsSummariesHolder.get(document);
	}

	public void setSearchResultsSummariesHolder(
		SearchResultsSummariesHolder searchResultsSummariesHolder) {

		_searchResultsSummariesHolder = searchResultsSummariesHolder;
	}

	private final String _keywords;
	private final String _mapMarkersJSON;
	private final SearchResponse _searchResponse;
	private SearchResultsSummariesHolder _searchResultsSummariesHolder;

}