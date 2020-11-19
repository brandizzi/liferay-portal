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

import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchFixture;
import com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.test.util.RequestExecutorFixture;
import com.liferay.portal.search.engine.adapter.document.IndexDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.IndexDocumentResponse;
import com.liferay.portal.search.internal.document.DocumentBuilderImpl;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Adam Brandizzi
 */
public class IndexDocumentRequestExecutorTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		_elasticsearchFixture = new ElasticsearchFixture();

		_elasticsearchFixture.setUp();

		_requestExecutorFixture = new RequestExecutorFixture(
			_elasticsearchFixture);

		_requestExecutorFixture.setUp();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		_elasticsearchFixture.tearDown();
	}

	@Before
	public void setUp() {
		_indexDocumentRequestExecutor =
			_requestExecutorFixture.getIndexDocumentRequestExecutor();

		_requestExecutorFixture.createIndex(_INDEX_NAME);
	}

	@After
	public void tearDown() {
		_requestExecutorFixture.deleteIndex(_INDEX_NAME);
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

		Document document2 = _requestExecutorFixture.getDocumentById(
			_INDEX_NAME, uid);

		Assert.assertEquals(
			uid + " -> " + document2.toString(),
			document1.getString(_FIELD_NAME), document2.getString(_FIELD_NAME));
	}

	private static final String _FIELD_NAME = "testField";

	private static final String _INDEX_NAME = "test_request_index";

	private static ElasticsearchFixture _elasticsearchFixture;
	private static RequestExecutorFixture _requestExecutorFixture;

	private IndexDocumentRequestExecutor _indexDocumentRequestExecutor;

}