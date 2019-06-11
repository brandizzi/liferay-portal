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

package com.liferay.portal.search.synonyms.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.synonym.SynonymIndexer;
import com.liferay.portal.search.synonyms.web.internal.constants.SynonymsPortletKeys;

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
		"javax.portlet.name=" + SynonymsPortletKeys.SYNONYMS,
		"mvc.command.name=updateSynonymsEntryAction"
	},
	service = MVCActionCommand.class
)
public class UpdateSynonymSetsMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long companyId = portal.getCompanyId(actionRequest);

		String multipleSynonymSets = ParamUtil.getString(
			actionRequest, "multipleSynonymSets");

		if (Validator.isNotNull(multipleSynonymSets)) {
			String[] multipleSynonymSetsList = multipleSynonymSets.split(";");

			for (String filterName : _synonymIndexer.getFilterNames()) {
				String[] synonymSets = _synonymIndexer.getSynonymSets(
					companyId, filterName);

				for (String synonymToBeDeleted : multipleSynonymSetsList) {
					if (ArrayUtil.contains(
							synonymSets, synonymToBeDeleted, true)) {

						synonymSets = ArrayUtil.remove(
							synonymSets, synonymToBeDeleted);
					}
				}

				_synonymIndexer.updateSynonymSets(
					companyId, filterName, synonymSets);
			}
		}
		else {
			String newSynonymSets = ParamUtil.getString(
				actionRequest, "synonymSetsInput");

			String originalSynonymSets = ParamUtil.getString(
				actionRequest, "originalSynonymSetsInput");

			for (String filterName : _synonymIndexer.getFilterNames()) {
				String[] synonymSets = _synonymIndexer.getSynonymSets(
					companyId, filterName);

				if (ArrayUtil.contains(
						synonymSets, originalSynonymSets, true)) {

					synonymSets = ArrayUtil.remove(
						synonymSets, originalSynonymSets);
				}

				if (!Validator.isBlank(newSynonymSets)) {
					synonymSets = ArrayUtil.append(synonymSets, newSynonymSets);
				}

				_synonymIndexer.updateSynonymSets(
					companyId, filterName, synonymSets);
			}
		}

		actionResponse.setRenderParameter("tabs", "synonym-sets");

		sendRedirect(actionRequest, actionResponse);
	}

	@Reference
	protected Portal portal;

	@Reference
	private SynonymIndexer _synonymIndexer;

}