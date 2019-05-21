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

package com.liferay.portal.search.ranking.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.search.ranking.web.internal.constants.ResultsRankingPortletKeys;
import com.liferay.portal.search.synonym.SynonymIndexer;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Filipe Oshiro
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + ResultsRankingPortletKeys.RESULTS_RANKING,
		"mvc.command.name=updateSynonymsEntryAction"
	},
	service = MVCActionCommand.class
)
public class UpdateSynonymSetsMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String newSynonymSets = ParamUtil.getString(
			actionRequest, "synonymSetsInput");

		String originalSynonymSets = ParamUtil.getString(
			actionRequest, "originalSynonymSetsInput");

		long companyId = portal.getCompanyId(actionRequest);

		for (String filterName : _synonymIndexer.getFilterNames()) {
			String[] synonymSets = _synonymIndexer.getSynonymSets(
				companyId, filterName);

			if (ArrayUtil.contains(synonymSets, originalSynonymSets, true)) {
				synonymSets = ArrayUtil.remove(
					synonymSets, originalSynonymSets);
			}

			if ("" != newSynonymSets) {
				synonymSets = ArrayUtil.append(synonymSets, newSynonymSets);
			}

			_synonymIndexer.updateSynonymSets(
				companyId, filterName, synonymSets);
		}

		actionResponse.setRenderParameter("tabs", "synonym-sets");

		sendRedirect(actionRequest, actionResponse);
	}

	@Reference
	protected Portal portal;

	@Reference
	private SynonymIndexer _synonymIndexer;

}