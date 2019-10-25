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
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.document.GetDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.GetDocumentResponse;

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
		return Optional.ofNullable(
			_getDocument(id)
		).map(
			document -> translate(document, id)
		);
	}

	@Reference(unbind = "-")
	protected void setSearchEngineAdapter(
		SearchEngineAdapter searchEngineAdapter) {

		_searchEngineAdapter = searchEngineAdapter;
	}

	protected SynonymSet translate(Document document, String id) {
		return _documentToSynonymSetTranslator.translate(document, id);
	}

	private Document _getDocument(String id) {
		GetDocumentRequest getDocumentRequest = new GetDocumentRequest(
			SynonymSetIndexDefinition.INDEX_NAME, id);

		getDocumentRequest.setFetchSource(true);
		getDocumentRequest.setFetchSourceInclude(StringPool.STAR);

		GetDocumentResponse getDocumentResponse = _searchEngineAdapter.execute(
			getDocumentRequest);

		if (getDocumentResponse.isExists()) {
			return getDocumentResponse.getDocument();
		}

		return null;
	}

	@Reference
	private DocumentToSynonymSetTranslator _documentToSynonymSetTranslator;

	private SearchEngineAdapter _searchEngineAdapter;

}