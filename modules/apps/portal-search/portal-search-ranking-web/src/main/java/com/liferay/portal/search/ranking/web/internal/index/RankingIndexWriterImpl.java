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

package com.liferay.portal.search.ranking.web.internal.index;

import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.document.DeleteDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.IndexDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.IndexDocumentResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author André de Oliveira
 */
@Component(service = RankingIndexWriter.class)
public class RankingIndexWriterImpl implements RankingIndexWriter {

	@Override
	public String create(Ranking ranking) {
		IndexDocumentResponse indexDocumentResponse =
			_searchEngineAdapter.execute(
				new IndexDocumentRequest(
					SearchTuningIndexDefinition.INDEX_NAME,
					_rankingToDocumentTranslator.translate(ranking)));

		return indexDocumentResponse.getUid();
	}

	@Override
	public void remove(String uid) {
		_searchEngineAdapter.execute(
			new DeleteDocumentRequest(
				SearchTuningIndexDefinition.INDEX_NAME, uid));
	}

	@Override
	public void update(Ranking ranking) {
		_searchEngineAdapter.execute(
			new IndexDocumentRequest(
				SearchTuningIndexDefinition.INDEX_NAME, ranking.getUid(),
				_rankingToDocumentTranslator.translate(ranking)));
	}

	@Reference(unbind = "-")
	protected void setRankingToDocumentTranslator(
		RankingToDocumentTranslator rankingToDocumentTranslator) {

		_rankingToDocumentTranslator = rankingToDocumentTranslator;
	}

	@Reference(unbind = "-")
	protected void setSearchEngineAdapter(
		SearchEngineAdapter searchEngineAdapter) {

		_searchEngineAdapter = searchEngineAdapter;
	}

	private RankingToDocumentTranslator _rankingToDocumentTranslator;
	private SearchEngineAdapter _searchEngineAdapter;

}