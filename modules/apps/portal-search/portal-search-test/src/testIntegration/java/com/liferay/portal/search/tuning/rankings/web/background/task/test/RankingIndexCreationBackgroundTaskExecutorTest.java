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

package com.liferay.portal.search.tuning.rankings.web.background.task.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskExecutor;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.index.DeleteIndexRequest;
import com.liferay.portal.search.engine.adapter.index.IndicesExistsIndexRequest;
import com.liferay.portal.search.engine.adapter.index.IndicesExistsIndexResponse;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adam Brandizzi
 */
@RunWith(Arquillian.class)
public class RankingIndexCreationBackgroundTaskExecutorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		Assume.assumeNotNull(_rankingIndexCreationBackgroundTaskExecutor);
	}

	@Test
	public void testRankingIndexCreationBackgroundTaskExecutorCreatesIndex()
		throws Exception {

		Company company = createCompanyWithoutRankingsIndex();

		_rankingIndexCreationBackgroundTaskExecutor.execute(null);

		String rankingsIndexName = getRankingsIndexName(company.getCompanyId());

		Assert.assertTrue(isIndexExists(rankingsIndexName));
	}

	@Test
	public void testRankingIndexCreationBackgroundTaskExecutorCreatesIndices()
		throws Exception {

		List<Company> companies = createCompaniesWithoutRankingsIndices(5);

		_rankingIndexCreationBackgroundTaskExecutor.execute(null);

		for (Company company : companies) {
			String rankingsIndexName = getRankingsIndexName(
				company.getCompanyId());

			Assert.assertTrue(isIndexExists(rankingsIndexName));
		}
	}

	protected List<Company> createCompaniesWithoutRankingsIndices(
			int numberOfCompanies)
		throws Exception {

		List<Company> companies = new ArrayList<>();

		for (int i = 0; i < numberOfCompanies; i++) {
			Company company = CompanyTestUtil.addCompany();

			companies.add(company);
		}

		Stream<Company> stream = companies.stream();

		String[] rankingsIndexNames = stream.map(
			Company::getCompanyId
		).map(
			this::getRankingsIndexName
		).collect(
			Collectors.toList()
		).toArray(
			new String[companies.size()]
		);

		deleteIndex(rankingsIndexNames);

		return companies;
	}

	protected Company createCompanyWithoutRankingsIndex() throws Exception {
		Company company = CompanyTestUtil.addCompany();

		String rankingsIndexName = getRankingsIndexName(company.getCompanyId());

		deleteIndex(rankingsIndexName);

		return company;
	}

	protected void deleteIndex(String... rankingsIndexNames) {
		DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(
			rankingsIndexNames);

		_searchEngineAdapter.execute(deleteIndexRequest);
	}

	protected String getRankingsIndexName(long companyId) {
		return _indexNameBuilder.getIndexName(companyId) +
			"-liferay-search-tuning-rankings";
	}

	protected boolean isIndexExists(String rankingsIndexName) {
		IndicesExistsIndexRequest indicesExistsIndexRequest =
			new IndicesExistsIndexRequest(rankingsIndexName);

		IndicesExistsIndexResponse indicesExistsIndexResponse =
			_searchEngineAdapter.execute(indicesExistsIndexRequest);

		return indicesExistsIndexResponse.isExists();
	}

	@Inject
	private IndexNameBuilder _indexNameBuilder;

	@Inject(
		filter = "background.task.executor.class.name=com.liferay.portal.search.tuning.rankings.web.internal.background.task.RankingIndexCreationBackgroundTaskExecutor"
	)
	private BackgroundTaskExecutor _rankingIndexCreationBackgroundTaskExecutor;

	@Inject
	private SearchEngineAdapter _searchEngineAdapter;

}