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

package com.liferay.portal.search.elasticsearch6.internal.synonym;

import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.search.elasticsearch6.internal.connection.ElasticsearchFixture;
import com.liferay.portal.search.elasticsearch6.internal.connection.HealthExpectations;
import com.liferay.portal.search.elasticsearch6.internal.connection.IndexName;
import com.liferay.portal.search.elasticsearch6.internal.document.SingleFieldFixture;
import com.liferay.portal.search.elasticsearch6.internal.index.CompanyIndexFactoryFixture;
import com.liferay.portal.search.elasticsearch6.internal.index.LiferayTypeMappingsConstants;
import com.liferay.portal.search.elasticsearch6.internal.query.QueryBuilderFactories;
import com.liferay.portal.search.synonym.SynonymException;

import org.elasticsearch.cluster.health.ClusterHealthStatus;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

/**
 * @author Adam Brandizzi
 */
public class ElasticsearchSynonymIndexerTest {

	@Before
	public void setUp() throws Exception {
		setUpElasticsearchFixture();

		setUpCompanyIndexFactoryFixture();
		setUpSingleFieldFixture();
	}

	@After
	public void tearDown() throws Exception {
		tearDownSynonyms();

		_elasticsearchFixture.tearDown();
	}

	@Test
	public void testGetSynonymSets() throws Exception {
		ElasticsearchSynonymIndexer elasticsearchSynonymIndexer =
			createElasticsearchSynonymIndexer();

		Assert.assertArrayEquals(
			new String[0],
			elasticsearchSynonymIndexer.getSynonymSets(_indexName));

		String[] synonymSet = {"car, automobile"};

		elasticsearchSynonymIndexer.updateSynonymSets(
			_companyIndexFactoryFixture.getIndexName(), synonymSet);

		waitForElasticsearchToStart();

		Assert.assertArrayEquals(
			synonymSet, elasticsearchSynonymIndexer.getSynonymSets(_indexName));
	}

	@Test
	public void testUpdateSynonymSets() throws Exception {
		assertSynonym("Automobiles can be useful.", "automobile", "car", "car");
	}

	@Test
	public void testUpdateSynonymSetsCaseInsensitiveQueryString()
		throws Exception {

		assertSynonym("Automobiles can be useful.", "automobile", "car", "CAR");
	}

	@Test
	public void testUpdateSynonymSetsCaseInsensitiveSynonym() throws Exception {
		assertSynonym("Automobiles can be useful.", "automobile", "CAR", "Car");
	}

	@Rule
	public TestName testName = new TestName();

	protected void assertSynonym(
			String fieldValue, String indexedSynonym, String searchedSynonym,
			String qureyString)
		throws Exception, SynonymException {

		indexField("title_en_US", fieldValue);

		_singleFieldFixture.assertNoHits(qureyString);

		ElasticsearchSynonymIndexer elasticsearchSynonymIndexer =
			createElasticsearchSynonymIndexer();

		elasticsearchSynonymIndexer.updateSynonymSets(
			_companyIndexFactoryFixture.getIndexName(),
			new String[] {searchedSynonym + "," + indexedSynonym});

		waitForElasticsearchToStart();

		_singleFieldFixture.assertSearch(qureyString, fieldValue);
	}

	protected ElasticsearchSynonymIndexer createElasticsearchSynonymIndexer() {
		return new ElasticsearchSynonymIndexer() {
			{
				elasticsearchClientResolver = _elasticsearchFixture;
				jsonFactory = new JSONFactoryImpl();
			}
		};
	}

	protected void indexField(String field, String content) {
		_singleFieldFixture.setField(field);

		_singleFieldFixture.indexDocument(content);
	}

	protected void setUpCompanyIndexFactoryFixture() throws Exception {
		_companyIndexFactoryFixture = new CompanyIndexFactoryFixture(
			_elasticsearchFixture, testName.getMethodName());

		_indexName = _companyIndexFactoryFixture.getIndexName();

		_companyIndexFactoryFixture.createIndices();
	}

	protected void setUpElasticsearchFixture() throws Exception {
		_elasticsearchFixture = new ElasticsearchFixture(
			ElasticsearchSynonymIndexerTest.class.getSimpleName());

		_elasticsearchFixture.setUp();
	}

	protected void setUpSingleFieldFixture() {
		_singleFieldFixture = new SingleFieldFixture(
			_elasticsearchFixture.getClient(),
			new IndexName(_companyIndexFactoryFixture.getIndexName()),
			LiferayTypeMappingsConstants.LIFERAY_DOCUMENT_TYPE);

		_singleFieldFixture.setQueryBuilderFactory(QueryBuilderFactories.MATCH);
	}

	protected void tearDownSynonyms() throws SynonymException {
		ElasticsearchSynonymIndexer elasticsearchSynonymIndexer =
			createElasticsearchSynonymIndexer();

		elasticsearchSynonymIndexer.updateSynonymSets(
			_companyIndexFactoryFixture.getIndexName(), new String[0]);

		waitForElasticsearchToStart();
	}

	protected void waitForElasticsearchToStart() {
		_elasticsearchFixture.getClusterHealthResponse(
			new HealthExpectations() {
				{
					setActivePrimaryShards(0);
					setActiveShards(0);
					setNumberOfDataNodes(1);
					setNumberOfNodes(1);
					setStatus(ClusterHealthStatus.GREEN);
					setUnassignedShards(0);
				}
			});
	}

	private CompanyIndexFactoryFixture _companyIndexFactoryFixture;
	private ElasticsearchFixture _elasticsearchFixture;
	private String _indexName;
	private SingleFieldFixture _singleFieldFixture;

}