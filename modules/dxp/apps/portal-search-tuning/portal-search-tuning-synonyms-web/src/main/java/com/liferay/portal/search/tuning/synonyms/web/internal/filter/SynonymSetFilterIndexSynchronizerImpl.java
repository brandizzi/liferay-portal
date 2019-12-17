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

package com.liferay.portal.search.tuning.synonyms.web.internal.filter;

import com.liferay.portal.search.tuning.synonyms.web.internal.index.SynonymSet;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.SynonymSetIndexReader;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.SynonymSetIndexWriter;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.name.SynonymSetIndexNameBuilder;
import com.liferay.portal.search.tuning.synonyms.web.internal.synonym.SynonymIndexer;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adam Brandizzi
 */
@Component(service = SynonymSetFilterIndexSynchronizer.class)
public class SynonymSetFilterIndexSynchronizerImpl
	implements SynonymSetFilterIndexSynchronizer {

	@Override
	public void copyFiltersToIndex(String companyIndexName) {
		for (String synonyms : getAllSynonyms(companyIndexName)) {
			addSynonymSetToIndex(companyIndexName, synonyms);
		}
	}

	@Override
	public void copyIndexToFilters(String indexName) {
		updateFilters(indexName, getSynonyms(indexName));
	}

	protected void addSynonymSetToIndex(String suffix, String synonyms) {
		SynonymSet.SynonymSetBuilder synonymSetBuilder =
			new SynonymSet.SynonymSetBuilder();

		synonymSetBuilder.synonyms(synonyms);

		_synonymSetIndexWriter.create(
			_synonymSetIndexNameBuilder.getSynonymSetIndexName(suffix),
			synonymSetBuilder.build());
	}

	protected String[] getAllSynonyms(String companyIndexName) {
		LinkedHashSet<String> synonyms = Stream.of(
			_synonymSetFilterNameHolder.getFilterNames()
		).map(
			filterName -> _synonymIndexer.getSynonymSets(
				companyIndexName, filterName)
		).flatMap(
			Stream::of
		).collect(
			Collectors.toCollection(LinkedHashSet::new)
		);

		return synonyms.toArray(new String[0]);
	}

	protected String[] getSynonyms(String indexName) {
		List<SynonymSet> synonymSets = _synonymSetIndexReader.search(indexName);

		Stream<SynonymSet> stream = synonymSets.stream();

		return stream.map(
			SynonymSet::getSynonyms
		).toArray(
			String[]::new
		);
	}

	protected void updateFilters(String indexName, String[] synonyms) {
		for (String filterName : _synonymSetFilterNameHolder.getFilterNames()) {
			_synonymIndexer.updateSynonymSets(indexName, filterName, synonyms);
		}
	}

	@Reference
	private SynonymIndexer _synonymIndexer;

	@Reference
	private SynonymSetFilterNameHolder _synonymSetFilterNameHolder;

	@Reference
	private SynonymSetIndexNameBuilder _synonymSetIndexNameBuilder;

	@Reference
	private SynonymSetIndexReader _synonymSetIndexReader;

	@Reference
	private SynonymSetIndexWriter _synonymSetIndexWriter;

}