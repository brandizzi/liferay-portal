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

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.document.GetDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.GetDocumentResponse;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.query.TermQuery;

import java.util.List;
import java.util.Optional;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bryan Engler
 */
@Component(service = SynonymSetIndexReader.class)
public class SynonymSetIndexReaderImpl implements SynonymSetIndexReader {

	@Override
	public Optional<SynonymSet> fetchOptional(String id) {
		return _getDocumentOptional(
			id
		).map(
			document -> translate(document, id)
		);
	}

	@Override
	public List<SynonymSet> searchByIndexName(String indexName) {
		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		searchSearchRequest.setIndexNames(SynonymSetIndexDefinition.INDEX_NAME);

		TermQuery query = _queries.term(SynonymSetFields.INDEX, indexName);

		searchSearchRequest.setPostFilterQuery(query);

		SearchSearchResponse searchSearchResponse =
			_searchEngineAdapter.execute(searchSearchRequest);

		return _documentToSynonymSetTranslator.translateAll(
			searchSearchResponse.getSearchHits());
	}

	@Reference(unbind = "-")
	protected void setSearchEngineAdapter(
		SearchEngineAdapter searchEngineAdapter) {

		_searchEngineAdapter = searchEngineAdapter;
	}

	protected SynonymSet translate(Document document, String id) {
		return _documentToSynonymSetTranslator.translate(document, id);
	}

	private Optional<Document> _getDocumentOptional(String id) {
		if (Validator.isNull(id)) {
			return Optional.empty();
		}

		GetDocumentRequest getDocumentRequest = new GetDocumentRequest(
			SynonymSetIndexDefinition.INDEX_NAME, id);

		getDocumentRequest.setFetchSource(true);
		getDocumentRequest.setFetchSourceInclude(StringPool.STAR);

		GetDocumentResponse getDocumentResponse = _searchEngineAdapter.execute(
			getDocumentRequest);

		if (getDocumentResponse.isExists()) {
			return Optional.of(getDocumentResponse.getDocument());
		}

		return Optional.empty();
	}

	@Reference
	private DocumentToSynonymSetTranslator _documentToSynonymSetTranslator;

	@Reference
	private Queries _queries;

	@Reference
	private SearchEngineAdapter _searchEngineAdapter;

}