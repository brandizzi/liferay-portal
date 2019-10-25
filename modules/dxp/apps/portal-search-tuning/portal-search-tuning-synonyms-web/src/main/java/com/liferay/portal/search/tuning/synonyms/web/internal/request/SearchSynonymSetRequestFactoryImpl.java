/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.portal.search.tuning.synonyms.web.internal.request;

import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.search.SearchContextFactory;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.sort.Sort;
import com.liferay.portal.search.sort.SortOrder;
import com.liferay.portal.search.sort.Sorts;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.SynonymSetFields;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Adam Brandizzi
 */
public class SearchSynonymSetRequestFactoryImpl
	implements SearchSynonymSetRequestFactory {

	public SearchSynonymSetRequestFactoryImpl(
		Queries queries, Sorts sorts, SearchEngineAdapter searchEngineAdapter) {

		_queries = queries;
		_sorts = sorts;
		_searchEngineAdapter = searchEngineAdapter;
	}

	public SearchSynonymSetRequest getSearchSynonymSetRequest(
		HttpServletRequest httpServletRequest,
		SearchContainer searchContainer) {

		String orderByCol = ParamUtil.getString(
			httpServletRequest, "orderByCol",
			SynonymSetFields.SYNONYMS_KEYWORD);
		String orderByType = ParamUtil.getString(
			httpServletRequest, "orderByType", "asc");
		
		return new SearchSynonymSetRequest(
			_queries, _getSorts(orderByCol, orderByType),
			searchContainer,
			SearchContextFactory.getInstance(httpServletRequest),
			_searchEngineAdapter);
	}
 
	private Collection<Sort> _getSorts(String orderByCol, String orderByType) {
		SortOrder sortOrder = SortOrder.ASC;

		if (Objects.equals(orderByType, "desc")) {
			sortOrder = SortOrder.DESC;
		}

		return Arrays.asList(_sorts.field(orderByCol, sortOrder));
	}

	private final Queries _queries;
	private final SearchEngineAdapter _searchEngineAdapter;
	private final Sorts _sorts;

}