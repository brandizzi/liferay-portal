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

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.test.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.document.DocumentBuilderFactory;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.elasticsearch7.internal.document.DefaultElasticsearchDocumentFactory;
import com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.document.ElasticsearchBulkableDocumentRequestTranslatorImpl;
import com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.document.GetDocumentRequestExecutorImpl;
import com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.document.IndexDocumentRequestExecutor;
import com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.document.IndexDocumentRequestExecutorImpl;
import com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.index.CreateIndexRequestExecutorImpl;
import com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.index.DeleteIndexRequestExecutorImpl;
import com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.index.IndicesOptionsTranslator;
import com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.index.IndicesOptionsTranslatorImpl;
import com.liferay.portal.search.engine.adapter.document.GetDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.GetDocumentResponse;
import com.liferay.portal.search.engine.adapter.index.CreateIndexRequest;
import com.liferay.portal.search.engine.adapter.index.DeleteIndexRequest;
import com.liferay.portal.search.internal.document.DocumentBuilderFactoryImpl;

/**
 * @author Adam Brandizzi
 */
public class RequestExecutorFixture {

	public RequestExecutorFixture(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		_elasticsearchClientResolver = elasticsearchClientResolver;
	}

	public void createIndex(String indexName) {
		_createIndexRequestExecutor.execute(new CreateIndexRequest(indexName));
	}

	public void deleteIndex(String indexName) {
		_deleteIndexRequestExecutor.execute(new DeleteIndexRequest(indexName));
	}

	public CreateIndexRequestExecutorImpl getCreateIndexRequestExecutor() {
		return _createIndexRequestExecutor;
	}

	public DeleteIndexRequestExecutorImpl getDeleteIndexRequestExecutor() {
		return _deleteIndexRequestExecutor;
	}

	public Document getDocumentById(String indexName, String uid) {
		GetDocumentRequest getDocumentRequest = new GetDocumentRequest(
			indexName, uid);

		getDocumentRequest.setFetchSource(true);
		getDocumentRequest.setFetchSourceInclude(StringPool.STAR);

		GetDocumentResponse getDocumentResponse =
			_getDocumentRequestExecutor.execute(getDocumentRequest);

		return getDocumentResponse.getDocument();
	}

	public GetDocumentRequestExecutorImpl getGetDocumentRequestExecutor() {
		return _getDocumentRequestExecutor;
	}

	public IndexDocumentRequestExecutor getIndexDocumentRequestExecutor() {
		return _indexDocumentRequestExecutor;
	}

	public void setUp() {
		_createIndexRequestExecutor = new CreateIndexRequestExecutorImpl() {
			{
				setElasticsearchClientResolver(_elasticsearchClientResolver);
			}
		};

		IndicesOptionsTranslator indicesOptionsTranslator =
			new IndicesOptionsTranslatorImpl();

		_deleteIndexRequestExecutor = new DeleteIndexRequestExecutorImpl() {
			{
				setIndicesOptionsTranslator(indicesOptionsTranslator);
				setElasticsearchClientResolver(_elasticsearchClientResolver);
			}
		};

		ElasticsearchBulkableDocumentRequestTranslatorImpl
			bulkableDocumentRequestTranslator =
				new ElasticsearchBulkableDocumentRequestTranslatorImpl() {
					{
						setElasticsearchDocumentFactory(
							new DefaultElasticsearchDocumentFactory());
					}
				};

		DocumentBuilderFactory documentBuilderFactory =
			new DocumentBuilderFactoryImpl();

		_getDocumentRequestExecutor = new GetDocumentRequestExecutorImpl() {
			{
				setBulkableDocumentRequestTranslator(
					bulkableDocumentRequestTranslator);
				setElasticsearchClientResolver(_elasticsearchClientResolver);
				setDocumentBuilderFactory(documentBuilderFactory);
			}
		};

		_indexDocumentRequestExecutor = new IndexDocumentRequestExecutorImpl() {
			{
				setBulkableDocumentRequestTranslator(
					bulkableDocumentRequestTranslator);
				setElasticsearchClientResolver(_elasticsearchClientResolver);
			}
		};
	}

	private CreateIndexRequestExecutorImpl _createIndexRequestExecutor;
	private DeleteIndexRequestExecutorImpl _deleteIndexRequestExecutor;
	private final ElasticsearchClientResolver _elasticsearchClientResolver;
	private GetDocumentRequestExecutorImpl _getDocumentRequestExecutor;
	private IndexDocumentRequestExecutor _indexDocumentRequestExecutor;

}