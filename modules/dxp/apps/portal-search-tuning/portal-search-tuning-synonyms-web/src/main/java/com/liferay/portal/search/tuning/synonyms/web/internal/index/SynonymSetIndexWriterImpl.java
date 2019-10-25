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

import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.document.DeleteDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.IndexDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.IndexDocumentResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adam Brandizzi
 */
@Component(service = SynonymSetIndexWriter.class)
public class SynonymSetIndexWriterImpl implements SynonymSetIndexWriter {

	@Override
	public String create(SynonymSet synonymSet) {
		IndexDocumentResponse indexDocumentResponse =
			_searchEngineAdapter.execute(
				new IndexDocumentRequest(
					SynonymSetIndexDefinition.INDEX_NAME,
					_synonymSetToDocumentTranslator.translate(synonymSet)));

		return indexDocumentResponse.getUid();
	}

	@Override
	public void remove(String id) {
		DeleteDocumentRequest deleteDocumentRequest = new DeleteDocumentRequest(
			SynonymSetIndexDefinition.INDEX_NAME, id);

		deleteDocumentRequest.setRefresh(true);

		_searchEngineAdapter.execute(deleteDocumentRequest);
	}

	@Override
	public void update(SynonymSet ranking) {
		IndexDocumentRequest indexDocumentRequest = new IndexDocumentRequest(
			SynonymSetIndexDefinition.INDEX_NAME, ranking.getId(),
			_synonymSetToDocumentTranslator.translate(ranking));

		indexDocumentRequest.setRefresh(true);

		_searchEngineAdapter.execute(indexDocumentRequest);
	}

	@Reference(unbind = "-")
	protected void setSearchEngineAdapter(
		SearchEngineAdapter searchEngineAdapter) {

		_searchEngineAdapter = searchEngineAdapter;
	}

	@Reference(unbind = "-")
	protected void setSynonymSetToDocumentTranslator(
		SynonymSetToDocumentTranslator synonymSetToDocumentTranslator) {

		_synonymSetToDocumentTranslator = synonymSetToDocumentTranslator;
	}

	private SearchEngineAdapter _searchEngineAdapter;
	private SynonymSetToDocumentTranslator _synonymSetToDocumentTranslator;

}