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
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.query.Query;
import com.liferay.portal.search.sort.Sorts;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.mockito.Mock;

/**
 * @author Adam Brandizzi
 */
public class SearchSynonymSetRequestFactoryTest {

	@Mock
	private Queries _queries;

	@Mock
	private Sorts _sorts;

	@Mock
	private SearchEngineAdapter _searchEngineAdapter;

	private HttpServletRequest _httpServletRequest;

	private SearchContainer _searchContainer;

	@Test
	public void testGetSorts() {
		SearchSynonymSetRequestFactory searchSynonymSetRequestFactory =
			new SearchSynonymSetRequestFactoryImpl(
				_queries, _sorts, _searchEngineAdapter);

		searchSynonymSetRequestFactory.getSearchSynonymSetRequest(
			_httpServletRequest, _searchContainer);
	}
	
}
	
