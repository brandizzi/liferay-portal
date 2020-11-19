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

import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.document.DocumentBuilder;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchFixture;
import com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.test.util.RequestExecutorFixture;
import com.liferay.portal.search.engine.adapter.document.IndexDocumentResponse;
import com.liferay.portal.search.engine.adapter.document.UpdateDocumentRequest;
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
public class UpdateDocumentRequestExecutorTest {

	@BeforeClass
	public static void setUpClass() throws Exception {
		_elasticsearchFixture = new ElasticsearchFixture();

		_elasticsearchFixture.setUp();

		_requestExecutorFixture = new RequestExecutorFixture(
			_elasticsearchFixture);

		_requestExecutorFixture.setUp();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_elasticsearchFixture.tearDown();
	}

	@Before
	public void setUp() {
		_updateDocumentRequestExecutor =
			_requestExecutorFixture.getUpdateDocumentRequestExecutor();

		_requestExecutorFixture.createIndex(_INDEX_NAME);
	}

	@After
	public void tearDown() {
		_requestExecutorFixture.deleteIndex(_INDEX_NAME);
	}

	@Test
	public void testUnsetValue() {
		String fieldName = _FIELD_NAME;

		Document document1 = buildDocument(fieldName, "example test");

		IndexDocumentResponse indexDocumentResponse =
			_requestExecutorFixture.indexDocument(_INDEX_NAME, document1);

		String uid = indexDocumentResponse.getUid();

		assertFieldEquals(
			fieldName, document1,
			_requestExecutorFixture.getDocumentById(_INDEX_NAME, uid));

		Document document2 = buildDocumentWithUnsetField(fieldName);

		UpdateDocumentRequest updateDocumentRequest = new UpdateDocumentRequest(
			_INDEX_NAME, uid, document2);

		_updateDocumentRequestExecutor.execute(updateDocumentRequest);

		assertFieldEquals(
			fieldName, document2,
			_requestExecutorFixture.getDocumentById(_INDEX_NAME, uid));
	}

	@Test
	public void testUnsetValueWithArrayWithNull() {
		Document document1 = buildDocument(_FIELD_NAME, "example test");

		IndexDocumentResponse indexDocumentResponse =
			_requestExecutorFixture.indexDocument(_INDEX_NAME, document1);

		String uid = indexDocumentResponse.getUid();

		assertFieldEquals(
			_FIELD_NAME, document1,
			_requestExecutorFixture.getDocumentById(_INDEX_NAME, uid));

		Document document2 = buildDocument(_FIELD_NAME, new String[] {null});

		UpdateDocumentRequest updateDocumentRequest = new UpdateDocumentRequest(
			_INDEX_NAME, uid, document2);

		_updateDocumentRequestExecutor.execute(updateDocumentRequest);

		assertFieldEquals(
			_FIELD_NAME, document2,
			_requestExecutorFixture.getDocumentById(_INDEX_NAME, uid));
	}

	@Test
	public void testUnsetValueWithEmptyArray() {
		Document document1 = buildDocument(_FIELD_NAME, "example test");

		IndexDocumentResponse indexDocumentResponse =
			_requestExecutorFixture.indexDocument(_INDEX_NAME, document1);

		String uid = indexDocumentResponse.getUid();

		assertFieldEquals(
			_FIELD_NAME, document1,
			_requestExecutorFixture.getDocumentById(_INDEX_NAME, uid));

		Document document2 = buildDocument(_FIELD_NAME, new String[0]);

		UpdateDocumentRequest updateDocumentRequest = new UpdateDocumentRequest(
			_INDEX_NAME, uid, document2);

		_updateDocumentRequestExecutor.execute(updateDocumentRequest);

		assertFieldEquals(
			_FIELD_NAME, document2,
			_requestExecutorFixture.getDocumentById(_INDEX_NAME, uid));
	}

	@Test
	public void testUnsetValueWithNull() {
		Document document1 = buildDocument(_FIELD_NAME, "example test");

		IndexDocumentResponse indexDocumentResponse =
			_requestExecutorFixture.indexDocument(_INDEX_NAME, document1);

		String uid = indexDocumentResponse.getUid();

		assertFieldEquals(
			_FIELD_NAME, document1,
			_requestExecutorFixture.getDocumentById(_INDEX_NAME, uid));

		Document document2 = buildDocument(_FIELD_NAME, null);

		UpdateDocumentRequest updateDocumentRequest = new UpdateDocumentRequest(
			_INDEX_NAME, uid, document2);

		_updateDocumentRequestExecutor.execute(updateDocumentRequest);

		assertFieldEquals(
			_FIELD_NAME, document2,
			_requestExecutorFixture.getDocumentById(_INDEX_NAME, uid));
	}

	@Test
	public void testUpdateDocumentWithNoRefresh() {
		doUpdateDocument(false);
	}

	@Test
	public void testUpdateDocumentWithRefresh() {
		doUpdateDocument(true);
	}

	protected void assertFieldEquals(
		String fieldName, Document expectedDocument, Document actualDocument) {

		Assert.assertEquals(
			expectedDocument.getString(fieldName),
			actualDocument.getString(fieldName));
	}

	protected Document buildDocument(String fieldName, String... fieldValue) {
		DocumentBuilder documentBuilder = new DocumentBuilderImpl();

		return documentBuilder.setStrings(
			fieldName, fieldValue
		).build();
	}

	protected Document buildDocument(
		String uid, String fieldName, String fieldValue) {

		DocumentBuilder documentBuilder = new DocumentBuilderImpl();

		return documentBuilder.setString(
			fieldName, fieldValue
		).setString(
			Field.UID, uid
		).build();
	}

	protected Document buildDocumentWithUnsetField(String fieldName) {
		DocumentBuilder documentBuilder = new DocumentBuilderImpl();

		return documentBuilder.unsetValue(
			fieldName
		).build();
	}

	protected void doUpdateDocument(boolean refresh) {
		Document document1 = buildDocument(_FIELD_NAME, "example test");

		IndexDocumentResponse indexDocumentResponse =
			_requestExecutorFixture.indexDocument(_INDEX_NAME, document1);

		String uid = indexDocumentResponse.getUid();

		assertFieldEquals(
			_FIELD_NAME, document1,
			_requestExecutorFixture.getDocumentById(_INDEX_NAME, uid));

		Document document2 = buildDocument(_FIELD_NAME, "updated value");

		UpdateDocumentRequest updateDocumentRequest = new UpdateDocumentRequest(
			_INDEX_NAME, uid, document2);

		updateDocumentRequest.setRefresh(refresh);

		_updateDocumentRequestExecutor.execute(updateDocumentRequest);

		assertFieldEquals(
			_FIELD_NAME, document2,
			_requestExecutorFixture.getDocumentById(_INDEX_NAME, uid));
	}

	private static final String _FIELD_NAME = "testField";

	private static final String _INDEX_NAME = "test_request_index";

	private static ElasticsearchFixture _elasticsearchFixture;
	private static RequestExecutorFixture _requestExecutorFixture;

	private UpdateDocumentRequestExecutor _updateDocumentRequestExecutor;

}