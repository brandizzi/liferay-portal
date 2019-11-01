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
import com.liferay.portal.search.tuning.synonyms.web.internal.index.SynonymSet;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.SynonymSetIndexReader;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.SynonymSetIndexWriter;
import com.liferay.portal.search.tuning.synonyms.web.internal.synonym.SynonymIndexer;

import java.util.List;
import java.util.Optional;
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
		"mvc.command.name=editSynonymSet"
	},
	service = MVCActionCommand.class
)
public class EditSynonymSetsMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String indexName = _indexNameBuilder.getIndexName(
			portal.getCompanyId(actionRequest));

		updateSynonymSetIndex(
			indexName, ParamUtil.getString(actionRequest, "synonymSet"),
			getSynonymSetOptional(actionRequest));

		updateSynonymSetFilters(indexName, getSynonyms(indexName));

		sendRedirect(actionRequest, actionResponse);
	}

	protected String[] getSynonyms(String indexName) {
		List<SynonymSet> synonymSets = _synonymSetIndexReader.searchByIndexName(
			indexName);

		Stream<SynonymSet> stream = synonymSets.stream();

		String[] synonyms = stream.map(
			SynonymSet::getSynonyms
		).toArray(
			String[]::new
		);

		return synonyms;
	}

	protected Optional<SynonymSet> getSynonymSetOptional(
		ActionRequest actionRequest) {

		return Optional.ofNullable(
			ParamUtil.getString(actionRequest, "synonymSetId", null)
		).flatMap(
			_synonymSetIndexReader::fetchOptional
		);
	}

	protected void updateSynonymSetFilters(
		String indexName, String[] synonyms) {

		for (String filterName : _FILTER_NAMES) {
			_synonymIndexer.updateSynonymSets(indexName, filterName, synonyms);
		}
	}

	protected void updateSynonymSetIndex(
		String indexName, String synonyms,
		Optional<SynonymSet> synonymSetOptional) {

		SynonymSet.SynonymSetBuilder synonymSetBuilder =
			new SynonymSet.SynonymSetBuilder();

		synonymSetBuilder.index(
			indexName
		).synonyms(
			synonyms
		);

		synonymSetOptional.ifPresent(
			synonymSet1 -> synonymSetBuilder.id(synonymSet1.getId()));

		if (synonymSetOptional.isPresent()) {
			_synonymSetIndexWriter.update(synonymSetBuilder.build());
		}
		else {
			_synonymSetIndexWriter.create(synonymSetBuilder.build());
		}
	}

	@Reference
	protected Portal portal;

	private static final String[] _FILTER_NAMES = {
		"liferay_filter_synonym_en", "liferay_filter_synonym_es"
	};

	@Reference
	private IndexNameBuilder _indexNameBuilder;

	@Reference
	private SynonymIndexer _synonymIndexer;

	@Reference
	private SynonymSetIndexReader _synonymSetIndexReader;

	@Reference
	private SynonymSetIndexWriter _synonymSetIndexWriter;

}