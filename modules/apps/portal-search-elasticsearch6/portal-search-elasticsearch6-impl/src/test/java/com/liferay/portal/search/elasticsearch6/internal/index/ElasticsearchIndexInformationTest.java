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

package com.liferay.portal.search.elasticsearch6.internal.index;

import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.search.elasticsearch6.internal.connection.ElasticsearchFixture;
import com.liferay.portal.search.elasticsearch6.internal.util.ResourceUtil;
import com.liferay.portal.search.test.util.AssertUtils;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

/**
 * @author Adam Brandizzi
 */
public class ElasticsearchIndexInformationTest {

	@Before
	public void setUp() throws Exception {
		setUpElasticsearchFixture();

		setUpIndexFixture();
		setUpElasticsearchIndexInformation();
	}

	@After
	public void tearDown() throws Exception {
		_elasticsearchFixture.tearDown();
	}

	@Test
	public void testGetCompanyIndexName() throws Exception {
		long companyId = RandomTestUtil.randomLong();

		_indexFixture.createIndex(getIndexNameBuilder(companyId));

		Assert.assertEquals(
			getIndexNameBuilder(companyId),
			_elasticsearchIndexInformation.getCompanyIndexName(companyId));
	}

	@Test
	public void testGetFieldMappings() throws Exception {
		long companyId = 123;

		String indexName = getIndexNameBuilder(companyId);

		_indexFixture.createIndex(indexName);

		JSONObject typeMappingsJSONObject = loadJSONObject(
			testName.getMethodName());

		_indexFixture.addTypeMappings(indexName, typeMappingsJSONObject);

		String fieldMappings = _elasticsearchIndexInformation.getFieldMappings(
			indexName);

		JSONObject actualJSONObject = _jsonFactory.createJSONObject(
			fieldMappings);

		AssertUtils.assertEquals("", typeMappingsJSONObject, actualJSONObject);
	}

	@Test
	public void testGetIndexNames() throws Exception {
		String indexName = "test-index";

		_indexFixture.createIndex(indexName);

		String[] indexNames = _elasticsearchIndexInformation.getIndexNames();

		Assert.assertEquals(indexNames.toString(), 1, indexNames.length);
		Assert.assertEquals(indexName, indexNames[0]);
	}

	@Rule
	public TestName testName = new TestName();

	protected static String getIndexNameBuilder(long companyId) {
		return "test-" + companyId;
	}

	protected JSONObject loadJSONObject(String suffix) throws Exception {
		String json = ResourceUtil.getResourceAsString(
			getClass(),
			"ElasticsearchIndexInformationTest-" + suffix + ".json");

		return _jsonFactory.createJSONObject(json);
	}

	protected void setUpElasticsearchFixture() throws Exception {
		_elasticsearchFixture = new ElasticsearchFixture(
			ElasticsearchIndexInformationTest.class.getSimpleName());

		_elasticsearchFixture.setUp();
	}

	protected void setUpElasticsearchIndexInformation() {
		_elasticsearchIndexInformation = new ElasticsearchIndexInformation() {
			{
				elasticsearchConnectionManager =
					_elasticsearchFixture.geElasticsearchConnectionManager();
				indexNameBuilder =
					ElasticsearchIndexInformationTest::getIndexNameBuilder;
			}
		};
	}

	protected void setUpIndexFixture() {
		_indexFixture = new IndexFixture(_elasticsearchFixture);
	}

	private ElasticsearchFixture _elasticsearchFixture;
	private ElasticsearchIndexInformation _elasticsearchIndexInformation;
	private IndexFixture _indexFixture;
	private final JSONFactory _jsonFactory = new JSONFactoryImpl();

}