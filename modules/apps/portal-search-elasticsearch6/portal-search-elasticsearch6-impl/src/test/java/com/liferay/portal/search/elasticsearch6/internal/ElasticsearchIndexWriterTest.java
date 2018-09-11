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

package com.liferay.portal.search.elasticsearch6.internal;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.search.elasticsearch6.internal.connection.ElasticsearchConnectionManager;
import com.liferay.portal.search.elasticsearch6.internal.connection.ElasticsearchFixture;
import com.liferay.portal.search.elasticsearch6.internal.index.IndexNameBuilder;
import com.liferay.portal.search.test.util.indexing.DocumentFixture;

import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.client.Client;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author Adam Brandizzi
 */
public class ElasticsearchIndexWriterTest {

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		documentFixture = new DocumentFixture();

		documentFixture.setUp();

		elasticsearchFixture = new ElasticsearchFixture(
			ElasticsearchIndexWriterTest.class.getSimpleName());

		elasticsearchFixture.setUp();
	}

	@After
	public void tearDown() throws Exception {
		elasticsearchFixture.tearDown();

		documentFixture.tearDown();
	}

	@Test
	public void testDeleteDocument() throws Exception {
		ElasticsearchIndexWriter elasticsearchIndexWriter =
			createElasticsearchIndexWriter();

		elasticsearchIndexWriter.deleteDocument(
			createSearchContext(), RandomTestUtil.randomString());
	}

	@Test
	public void testDeleteDocumentHandlesRaceConditionGracefully()
		throws Exception {

		ElasticsearchIndexWriter elasticsearchIndexWriter =
			createElasticsearchIndexWriter();

		Mockito.when(
			deleteRequestBuilder.get()
		).thenThrow(
			getIllegalStateExceptionCausedByInterruptedException()
		);

		elasticsearchIndexWriter.deleteDocument(
			createSearchContext(), RandomTestUtil.randomString());
	}

	@Test
	public void testDeleteDocumentHandlesNotFoundIndexGracefully()
		throws Exception {

		ElasticsearchIndexWriter elasticsearchIndexWriter =
			createElasticsearchIndexWriter();

		Mockito.when(
			deleteRequestBuilder.get()
		).thenThrow(
			getIllegalStateExceptionCausedByInterruptedException()
		);

		elasticsearchIndexWriter.deleteDocument(
			createSearchContext(), RandomTestUtil.randomString());
	}

	protected Exception getIllegalStateExceptionCausedByInterruptedException() {
		Exception ie = new InterruptedException();

		return new IllegalStateException(ie);
	}

	protected static SearchContext createSearchContext() {
		SearchContext searchContext = new SearchContext();

		searchContext.setCompanyId(RandomTestUtil.randomLong());
		searchContext.setGroupIds(new long[] {RandomTestUtil.randomLong()});

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setHighlightEnabled(false);
		queryConfig.setHitsProcessingEnabled(true);
		queryConfig.setScoreEnabled(false);

		searchContext.setStart(QueryUtil.ALL_POS);

		return searchContext;
	}

	protected ElasticsearchIndexWriter createElasticsearchIndexWriter() {
		Mockito.when(
			elasticsearchConnectionManager.getClient()
		).thenReturn(
			client
		);

		Mockito.when(
			client.prepareDelete(
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString())
		).thenReturn(
			deleteRequestBuilder
		);

		Mockito.when(
			deleteRequestBuilder.get()
		).thenReturn(
			deleteResponse
		);

		ElasticsearchIndexWriter elasticsearchIndexWriter =
			new ElasticsearchIndexWriter();

		ReflectionTestUtil.setFieldValue(
			elasticsearchIndexWriter, "elasticsearchConnectionManager",
			elasticsearchConnectionManager);

		ReflectionTestUtil.setFieldValue(
			elasticsearchIndexWriter, "indexNameBuilder", indexNameBuilder);

		return elasticsearchIndexWriter;
	}

	protected void setUpElasticsearchFixture() throws Exception {
		elasticsearchFixture = new ElasticsearchFixture(
			ElasticsearchIndexWriterTest.class.getSimpleName());

		elasticsearchFixture.setUp();
	}

	@Mock
	protected Client client;

	@Mock
	protected DeleteRequestBuilder deleteRequestBuilder;

	@Mock
	protected DeleteResponse deleteResponse;

	protected DocumentFixture documentFixture;

	@Mock
	protected ElasticsearchConnectionManager elasticsearchConnectionManager;

	protected ElasticsearchFixture elasticsearchFixture;

	@Mock
	protected IndexNameBuilder indexNameBuilder;

}