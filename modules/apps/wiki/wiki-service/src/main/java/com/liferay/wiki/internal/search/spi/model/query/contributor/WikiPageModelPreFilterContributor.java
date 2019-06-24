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

package com.liferay.wiki.internal.search.spi.model.query.contributor;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.search.spi.model.query.contributor.ModelPreFilterContributor;
import com.liferay.portal.search.spi.model.registrar.ModelSearchSettings;
import com.liferay.wiki.service.WikiNodeService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luan Maoski
 */
@Component(
	immediate = true,
	property = "indexer.class.name=com.liferay.wiki.model.WikiPage",
	service = ModelPreFilterContributor.class
)
public class WikiPageModelPreFilterContributor
	implements ModelPreFilterContributor {

	@Override
	public void contribute(
		BooleanFilter booleanFilter, ModelSearchSettings modelSearchSettings,
		SearchContext searchContext) {

		workflowStatusModelPreFilterContributor.contribute(
			booleanFilter, modelSearchSettings, searchContext);

		long[] nodeIds = searchContext.getNodeIds();

		if (ArrayUtil.isNotEmpty(nodeIds)) {
			TermsFilter nodesIdTermsFilter = new TermsFilter(Field.NODE_ID);

			for (long nodeId : nodeIds) {
				try {
					_wikiNodeService.getNode(nodeId);
				}
				catch (Exception e) {
					if (_log.isDebugEnabled()) {
						_log.debug("Unable to get wiki node " + nodeId, e);
					}

					continue;
				}

				nodesIdTermsFilter.addValue(String.valueOf(nodeId));
			}

			if (!nodesIdTermsFilter.isEmpty()) {
				booleanFilter.add(nodesIdTermsFilter, BooleanClauseOccur.MUST);
			}
		}
	}

	@Reference(target = "(model.pre.filter.contributor.id=WorkflowStatus)")
	protected ModelPreFilterContributor workflowStatusModelPreFilterContributor;

	private static final Log _log = LogFactoryUtil.getLog(
		WikiPageModelPreFilterContributor.class);

	@Reference
	private WikiNodeService _wikiNodeService;

}