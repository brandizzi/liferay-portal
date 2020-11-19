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

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.document;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.document.DocumentBuilderFactory;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchFixture;
import com.liferay.portal.search.elasticsearch7.internal.document.DefaultElasticsearchDocumentFactory;
import com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.index.CreateIndexRequestExecutorImpl;
import com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.index.DeleteIndexRequestExecutorImpl;
import com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.index.IndicesOptionsTranslator;
import com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.index.IndicesOptionsTranslatorImpl;
import com.liferay.portal.search.engine.adapter.document.GetDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.GetDocumentResponse;
import com.liferay.portal.search.engine.adapter.document.IndexDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.IndexDocumentResponse;
import com.liferay.portal.search.engine.adapter.index.CreateIndexRequest;
import com.liferay.portal.search.engine.adapter.index.DeleteIndexRequest;
import com.liferay.portal.search.internal.document.DocumentBuilderFactoryImpl;
import com.liferay.portal.search.internal.document.DocumentBuilderImpl;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Adam Brandizzi
 */
public class IndexDocumentRequestExecutorTest {

	@Before
	public void setUp() throws Exception {
		_elasticsearchFixture = new ElasticsearchFixture();

		_elasticsearchFixture.setUp();

		setUpRequestExecutors();
	}

	@After
	public void tearDown() throws Exception {
		_deleteIndexRequestExecutor.execute(
			new DeleteIndexRequest(_INDEX_NAME));

		_elasticsearchFixture.tearDown();
	}

	@Test
	public void testIndexDocumentWithNoRefresh() {
		doIndexDocument(false);
	}

	@Test
	public void testIndexDocumentWithRefresh() {
		doIndexDocument(true);
	}

	protected void doIndexDocument(boolean refresh) {
		Document document1 = new DocumentBuilderImpl().setString(
			_FIELD_NAME, "example test"
		).build();

		IndexDocumentRequest indexDocumentRequest = new IndexDocumentRequest(
			_INDEX_NAME, document1);

		indexDocumentRequest.setRefresh(refresh);

		IndexDocumentResponse indexDocumentResponse =
			_indexDocumentRequestExecutor.execute(indexDocumentRequest);

		String uid = indexDocumentResponse.getUid();

		GetDocumentRequest getDocumentRequest = new GetDocumentRequest(
			_INDEX_NAME, uid);

		getDocumentRequest.setFetchSource(true);
		getDocumentRequest.setFetchSourceInclude(StringPool.STAR);

		GetDocumentResponse getDocumentResponse =
			_getDocumentRequestExecutor.execute(getDocumentRequest);

		Document document2 = getDocumentResponse.getDocument();

		Assert.assertEquals(
			uid + " -> " + document2.toString(),
			document1.getString(_FIELD_NAME), document2.getString(_FIELD_NAME));
	}

	protected void setUpRequestExecutors() {
		_createIndexRequestExecutor = new CreateIndexRequestExecutorImpl() {
			{
				setElasticsearchClientResolver(_elasticsearchFixture);
			}
		};

		IndicesOptionsTranslator indicesOptionsTranslator =
			new IndicesOptionsTranslatorImpl();

		_deleteIndexRequestExecutor = new DeleteIndexRequestExecutorImpl() {
			{
				setIndicesOptionsTranslator(indicesOptionsTranslator);
				setElasticsearchClientResolver(_elasticsearchFixture);
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
				setElasticsearchClientResolver(_elasticsearchFixture);
				setDocumentBuilderFactory(documentBuilderFactory);
			}
		};

		_indexDocumentRequestExecutor = new IndexDocumentRequestExecutorImpl() {
			{
				setBulkableDocumentRequestTranslator(
					bulkableDocumentRequestTranslator);
				setElasticsearchClientResolver(_elasticsearchFixture);
			}
		};

		_createIndexRequestExecutor.execute(
			new CreateIndexRequest(_INDEX_NAME));
	}

	private static final String _FIELD_NAME = "testField";

	private static final String _INDEX_NAME = "test_request_index";

	private CreateIndexRequestExecutorImpl _createIndexRequestExecutor;
	private DeleteIndexRequestExecutorImpl _deleteIndexRequestExecutor;
	private ElasticsearchFixture _elasticsearchFixture;
	private GetDocumentRequestExecutorImpl _getDocumentRequestExecutor;
	private IndexDocumentRequestExecutor _indexDocumentRequestExecutor;

}