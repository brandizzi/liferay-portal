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

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchContextFactory;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.ranking.web.internal.index.SearchTuningIndexDefinition;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Wade Cao
 */
public class SearchRankingRequest {

	public SearchRankingRequest(
		HttpServletRequest httpServletRequest, Queries queries,
		SearchContainer searchContainer,
		SearchEngineAdapter searchEngineAdapter) {

		_httpServletRequest = httpServletRequest;
		_queries = queries;
		_searchContext = SearchContextFactory.getInstance(httpServletRequest);
		_searchContainer = searchContainer;
		_searchEngineAdapter = searchEngineAdapter;
	}

	public SearchRankingResponse search() {
		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		String keywords = _searchContext.getKeywords();

		if ((keywords != null) && (keywords != StringPool.BLANK)) {
			searchSearchRequest.setQuery(_queries.match("keywords", keywords));
		}
		else {
			searchSearchRequest.setQuery(_queries.matchAll());
		}

		_searchContext.setSorts(_getSort());

		searchSearchRequest.setFetchSource(true);
		searchSearchRequest.setIndexNames(
			SearchTuningIndexDefinition.INDEX_NAME);
		searchSearchRequest.setSize(_searchContainer.getDelta());
		searchSearchRequest.setSorts(_searchContext.getSorts());
		searchSearchRequest.setStart(_searchContainer.getStart());

		SearchSearchResponse searchSearchResponse =
			_searchEngineAdapter.execute(searchSearchRequest);

		SearchRankingResponse searchRankingResponse =
			new SearchRankingResponse();

		SearchHits searchHits = searchSearchResponse.getSearchHits();

		searchRankingResponse.setSearchHits(searchHits);
		searchRankingResponse.setTotalHits((int)searchHits.getTotalHits());

		return searchRankingResponse;
	}

	private Sort _getSort() {
		String orderByCol = ParamUtil.getString(
			_httpServletRequest, "orderByCol", "keywords");
		String orderByType = ParamUtil.getString(
			_httpServletRequest, "orderByType", "asc");

		Sort sort = null;

		boolean orderByAsc = true;

		if (Objects.equals(orderByType, "asc")) {
			orderByAsc = false;
		}

		if (Objects.equals(orderByCol, "modified-date")) {
			sort = new Sort(Field.MODIFIED_DATE, Sort.LONG_TYPE, orderByAsc);
		}
		else {
			sort = new Sort(orderByCol, orderByAsc);
		}

		return sort;
	}

	private final HttpServletRequest _httpServletRequest;
	private final Queries _queries;
	private final SearchContainer _searchContainer;
	private final SearchContext _searchContext;
	private final SearchEngineAdapter _searchEngineAdapter;

}