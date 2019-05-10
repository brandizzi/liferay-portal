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

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.ranking.web.internal.constants.SearchTuningPortletKeys;
import com.liferay.portal.search.ranking.web.internal.index.Ranking;
import com.liferay.portal.search.ranking.web.internal.index.RankingCriteriaBuilderFactory;
import com.liferay.portal.search.ranking.web.internal.index.RankingIndexReader;
import com.liferay.portal.search.ranking.web.internal.index.RankingIndexWriter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Kevin Tan
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + SearchTuningPortletKeys.SEARCH_TUNING,
		"mvc.command.name=/results_ranking/edit"
	},
	service = MVCActionCommand.class
)
public class EditRankingMVCActionCommand extends BaseMVCActionCommand {

	protected static List<String> update(
		List<String> strings, String[] addStrings, String[] removeStrings) {

		List<String> newStrings;

		if (ListUtil.isEmpty(strings)) {
			newStrings = Arrays.asList(addStrings);
		}
		else {
			newStrings = new ArrayList<>(strings);

			Collections.addAll(newStrings, addStrings);
		}

		newStrings.removeAll(Arrays.asList(removeStrings));

		return newStrings;
	}

	protected Ranking addRanking(ActionRequest actionRequest, String index) {
		Ranking ranking = new Ranking();

		String resultActionCmd = ParamUtil.getString(
			actionRequest, "resultActionCmd");
		String resultActionUid = ParamUtil.getString(
			actionRequest, "resultActionUid");

		if (!resultActionCmd.isEmpty() && !resultActionUid.isEmpty()) {
			if (resultActionCmd.equals(SearchRankingConstants.PIN)) {
				ranking.setPins(
					Arrays.asList(new Ranking.Pin(0, resultActionUid)));
			}
			else {
				ranking.setHiddenIds(ListUtil.fromString(resultActionUid));
			}
		}

		String keywords = ParamUtil.getString(actionRequest, "keywords");
		Date displayDate = null;
		Date modifiedDate = new Date();

		ranking.setDisplayDate(displayDate);
		ranking.setIndex(index);
		ranking.setQueryString(keywords);
		ranking.setModifiedDate(modifiedDate);
		ranking.setStatus(WorkflowConstants.STATUS_DRAFT);

		String resultsRankingUid = rankingIndexWriter.create(ranking);

		ranking.setUid(resultsRankingUid);

		return ranking;
	}

	protected void deleteRanking(String resultsRankingUid) {
		rankingIndexWriter.remove(resultsRankingUid);
	}

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		String redirect = ParamUtil.getString(actionRequest, "redirect");

		String indexParam = ParamUtil.getString(actionRequest, "index-name");

		if (cmd.equals(Constants.ADD)) {
			if (rankingExistsForKeyword(actionRequest)) {
				SessionErrors.add(actionRequest, Exception.class);

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");

				return;
			}

			Ranking ranking = addRanking(
				actionRequest, getIndexName(actionRequest, indexParam));

			redirect = getSaveAndContinueRedirect(
				actionRequest, ranking, redirect);
		}
		else if (cmd.equals(Constants.UPDATE)) {
			if (rankingExistsForAliases(actionRequest)) {
				SessionErrors.add(actionRequest, Exception.class);

				actionResponse.setRenderParameter("mvcPath", "/error.jsp");

				return;
			}

			updateRanking(actionRequest);
		}
		else if (cmd.equals(Constants.DELETE)) {
			String resultsRankingUid = ParamUtil.getString(
				actionRequest, "resultsRankingUid");

			deleteRanking(resultsRankingUid);
		}

		sendRedirect(actionRequest, actionResponse, redirect);
	}

	protected String getIndexName(
		ActionRequest actionRequest, String indexParam) {

		String index;

		if (Validator.isBlank(indexParam)) {
			long companyId = portal.getCompanyId(actionRequest);

			index = "liferay-" + companyId;
		}
		else {
			index = indexParam;
		}

		return index;
	}

	protected String getSaveAndContinueRedirect(
			ActionRequest actionRequest, Ranking ranking, String redirect)
		throws Exception {

		PortletConfig portletConfig = (PortletConfig)actionRequest.getAttribute(
			JavaConstants.JAVAX_PORTLET_CONFIG);

		LiferayPortletURL portletURL = PortletURLFactoryUtil.create(
			actionRequest, portletConfig.getPortletName(),
			PortletRequest.RENDER_PHASE);

		portletURL.setParameter(
			"mvcRenderCommandName", "editResultsRankingEntry");
		portletURL.setParameter(Constants.CMD, Constants.UPDATE, false);
		portletURL.setParameter("redirect", redirect, false);
		portletURL.setParameter("resultsRankingUid", ranking.getUid(), false);
		portletURL.setParameter(
			"aliases", StringUtil.merge(ranking.getAliases(), StringPool.COMMA),
			false);
		portletURL.setParameter("keywords", ranking.getQueryString(), false);
		portletURL.setWindowState(actionRequest.getWindowState());

		return portletURL.toString();
	}

	protected boolean rankingExistsForAliases(ActionRequest actionRequest) {
		String index = ParamUtil.getString(actionRequest, "index-name");

		if (Validator.isBlank(index)) {
			long companyId = portal.getCompanyId(actionRequest);

			index = "liferay-" + companyId;
		}

		String[] aliases = ParamUtil.getStringValues(actionRequest, "aliases");

		if (ArrayUtil.isEmpty(aliases)) {
			return false;
		}

		String resultsRankingUid = ParamUtil.getString(
			actionRequest, "resultsRankingUid");

		return rankingIndexReader.exists(
			rankingCriteriaBuilderFactory.builder(
			).aliases(
				aliases
			).index(
				index
			).uid(
				resultsRankingUid
			).build());
	}

	protected boolean rankingExistsForKeyword(ActionRequest actionRequest) {
		String index = ParamUtil.getString(actionRequest, "index-name");

		if (Validator.isBlank(index)) {
			long companyId = portal.getCompanyId(actionRequest);

			index = "liferay-" + companyId;
		}

		String keywords = ParamUtil.getString(actionRequest, "keywords");

		return rankingIndexReader.exists(
			rankingCriteriaBuilderFactory.builder(
			).index(
				index
			).queryString(
				keywords
			).build());
	}

	protected void updateRanking(ActionRequest actionRequest) {
		String uid = ParamUtil.getString(actionRequest, "resultsRankingUid");

		String[] aliases = ParamUtil.getStringValues(actionRequest, "aliases");

		String[] hiddenAdded = ParamUtil.getStringValues(
			actionRequest, "hiddenIdsAdded");
		String[] hiddenRemoved = ParamUtil.getStringValues(
			actionRequest, "hiddenIdsRemoved");

		String[] pinnedIds = ParamUtil.getStringValues(
			actionRequest, "pinnedIds");
		int pinnedIdsEndIndex = ParamUtil.getInteger(
			actionRequest, "pinnedIdsEndIndex");
		int pinnedIdsStartIndex = ParamUtil.getInteger(
			actionRequest, "pinnedIdsStartIndex");

		Optional<Ranking> optional = rankingIndexReader.fetch(uid);

		if (!optional.isPresent()) {
			return;
		}

		Ranking ranking = optional.get();

		ranking.setAliases(aliases);

		ranking.setHiddenIds(
			update(ranking.getHiddenIds(), hiddenAdded, hiddenRemoved));

		List<Ranking.Pin> originalPinnedDocuments = ranking.getPins();

		List<Ranking.Pin> newPinnedDocuments = new ArrayList<>();

		for (int i = 0; i < pinnedIds.length; i++) {
			newPinnedDocuments.add(new Ranking.Pin(i, pinnedIds[i]));
		}

		if (ListUtil.isNotEmpty(newPinnedDocuments)) {
			ranking.setPins(newPinnedDocuments);
		}
		else {
			ranking.setPins(null);
		}

		int workflowAction = ParamUtil.getInteger(
			actionRequest, "workflowAction", WorkflowConstants.ACTION_PUBLISH);

		if (workflowAction == WorkflowConstants.ACTION_SAVE_DRAFT) {

			// @TODO Save draft action

			ranking.setStatus(WorkflowConstants.STATUS_DRAFT);
		}
		else {

			// @TODO Publish action

			ranking.setStatus(WorkflowConstants.STATUS_APPROVED);
		}

		rankingIndexWriter.update(ranking);
	}

	@Reference
	protected Portal portal;

	@Reference
	protected RankingCriteriaBuilderFactory rankingCriteriaBuilderFactory;

	@Reference
	protected RankingIndexReader rankingIndexReader;

	@Reference
	protected RankingIndexWriter rankingIndexWriter;

}