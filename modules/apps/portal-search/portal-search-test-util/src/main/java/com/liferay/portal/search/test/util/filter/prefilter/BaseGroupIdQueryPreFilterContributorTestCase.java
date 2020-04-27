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

package com.liferay.portal.search.test.util.filter.prefilter;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.service.GroupLocalServiceWrapper;
import com.liferay.portal.search.internal.spi.model.query.contributor.GroupIdQueryPreFilterContributor;
import com.liferay.portal.search.spi.model.query.contributor.QueryPreFilterContributor;
import com.liferay.portal.search.test.util.indexing.BaseIndexingTestCase;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Joshua Cords
 */
public abstract class BaseGroupIdQueryPreFilterContributorTestCase
	extends BaseIndexingTestCase {

	@Test
	public void testNoEmptyClauses() {
		assertSearch(
			indexingTestHelper -> {
				QueryPreFilterContributor contributor =
					new GroupIdQueryPreFilterContributorTestWrapper();

				BooleanFilter booleanFilter = new BooleanFilter();

				SearchContext searchContext =
					indexingTestHelper.getSearchContext();

				searchContext.setGroupIds(new long[] {11111});

				contributor.contribute(booleanFilter, searchContext);

				_assertEmptyClauses(booleanFilter.getMustBooleanClauses());
				_assertEmptyClauses(booleanFilter.getMustNotBooleanClauses());
				_assertEmptyClauses(booleanFilter.getShouldBooleanClauses());
			});
	}

	private void _assertEmptyClauses(List<BooleanClause<Filter>> clauses) {
		Assert.assertEquals(clauses.toString(), 0, clauses.size());
	}

	private class GroupIdQueryPreFilterContributorTestWrapper
		extends GroupIdQueryPreFilterContributor {

		public GroupIdQueryPreFilterContributorTestWrapper() {
			_setGroupLocalService();
		}

		private void _setGroupLocalService() {
			groupLocalService = new GroupLocalServiceWrapper(null) {

				@Override
				public Group getGroup(long groupId) throws PortalException {
					return _group;
				}

				@Override
				public boolean isLiveGroupActive(Group group) {
					return false;
				}

			};
		}

		private Group _group = Mockito.mock(Group.class);

	}

}