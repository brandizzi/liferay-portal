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

package com.liferay.portal.search.tuning.synonyms.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.tuning.synonyms.web.internal.constants.SynonymsPortletKeys;
import com.liferay.portal.search.tuning.synonyms.web.internal.filter.SynonymSetFilterIndexSynchronizer;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.SynonymSet;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.SynonymSetIndexReader;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.SynonymSetIndexWriter;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.name.SynonymSetIndexName;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.name.SynonymSetIndexNameBuilder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
		"mvc.command.name=deleteSynonymSet"
	},
	service = MVCActionCommand.class
)
public class DeleteSynonymSetsMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String companyIndexName = _indexNameBuilder.getIndexName(
			portal.getCompanyId(actionRequest));

		SynonymSetIndexName synonymSetIndexName =
			_synonymSetIndexNameBuilder.getSynonymSetIndexName(
				companyIndexName);

		removeSynonymSets(
			synonymSetIndexName,
			getDeletedSynonymSets(actionRequest, synonymSetIndexName));

		_synonymSetFilterIndexSynchronizer.copyIndexToFilters(companyIndexName);

		sendRedirect(actionRequest, actionResponse);
	}

	protected List<SynonymSet> getDeletedSynonymSets(
		ActionRequest actionRequest, SynonymSetIndexName synonymSetIndexName) {

		return Stream.of(
			ParamUtil.getStringValues(actionRequest, "rowIds")
		).map(
			id -> _synonymSetIndexReader.fetchOptional(synonymSetIndexName, id)
		).filter(
			Optional::isPresent
		).map(
			Optional::get
		).collect(
			Collectors.toList()
		);
	}

	protected void removeSynonymSets(
		SynonymSetIndexName synonymSetIndexName,
		List<SynonymSet> deletedSynonymSets) {

		for (SynonymSet synonymSet : deletedSynonymSets) {
			_synonymSetIndexWriter.remove(
				synonymSetIndexName, synonymSet.getId());
		}
	}

	@Reference
	protected Portal portal;

	@Reference
	private IndexNameBuilder _indexNameBuilder;

	@Reference
	private SynonymSetFilterIndexSynchronizer
		_synonymSetFilterIndexSynchronizer;

	@Reference
	private SynonymSetIndexNameBuilder _synonymSetIndexNameBuilder;

	@Reference
	private SynonymSetIndexReader _synonymSetIndexReader;

	@Reference
	private SynonymSetIndexWriter _synonymSetIndexWriter;

}