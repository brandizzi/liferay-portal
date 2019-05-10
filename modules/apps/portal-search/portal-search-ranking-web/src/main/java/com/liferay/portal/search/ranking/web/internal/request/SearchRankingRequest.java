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

package com.liferay.portal.search.ranking.web.internal.request;

import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.ranking.web.internal.index.SearchTuningIndexDefinition;

/**
 * @author Wade Cao
 */
public class SearchRankingRequest {

	public SearchRankingRequest(
		Queries queries, SearchEngineAdapter searchEngineAdapter) {

		_queries = queries;
		_searchEngineAdapter = searchEngineAdapter;
	}

	public SearchRankingResponse search() {
		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		searchSearchRequest.setFetchSource(true);
		searchSearchRequest.setIndexNames(
			SearchTuningIndexDefinition.INDEX_NAME);
		searchSearchRequest.setQuery(_queries.matchAll());

		SearchSearchResponse searchSearchResponse =
			_searchEngineAdapter.execute(searchSearchRequest);

		SearchRankingResponse searchRankingResponse =
			new SearchRankingResponse();

		searchRankingResponse.setSearchHits(
			searchSearchResponse.getSearchHits());

		return searchRankingResponse;
	}

	private final Queries _queries;
	private final SearchEngineAdapter _searchEngineAdapter;

}