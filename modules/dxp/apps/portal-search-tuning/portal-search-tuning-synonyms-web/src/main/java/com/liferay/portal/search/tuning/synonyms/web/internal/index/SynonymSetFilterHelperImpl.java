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

package com.liferay.portal.search.tuning.synonyms.web.internal.index;

import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.tuning.synonyms.web.internal.synonym.SynonymIndexer;

import java.util.List;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adam Brandizzi
 */
@Component(service = SynonymSetFilterHelper.class)
public class SynonymSetFilterHelperImpl implements SynonymSetFilterHelper {

	@Override
	public void updateFilters(long companyId) {
		updateFilters(_indexNameBuilder.getIndexName(companyId));
	}

	@Override
	public void updateFilters(String indexName) {
		updateFilters(indexName, getSynonyms(indexName));
	}

	protected String[] getSynonyms(String indexName) {
		List<SynonymSet> synonymSets = _synonymSetIndexReader.searchByIndexName(
			indexName);

		Stream<SynonymSet> stream = synonymSets.stream();

		return stream.map(
			SynonymSet::getSynonyms
		).toArray(
			String[]::new
		);
	}

	protected void updateFilters(String indexName, String[] synonyms) {
		for (String filterName : _FILTER_NAMES) {
			_synonymIndexer.updateSynonymSets(indexName, filterName, synonyms);
		}
	}

	private static final String[] _FILTER_NAMES = {
		"liferay_filter_synonym_en", "liferay_filter_synonym_es"
	};

	@Reference
	private IndexNameBuilder _indexNameBuilder;

	@Reference
	private SynonymIndexer _synonymIndexer;

	@Reference
	private SynonymSetIndexReader _synonymSetIndexReader;

}