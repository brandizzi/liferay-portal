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

package com.liferay.portal.search.elasticsearch7.internal.search.engine.adapter.search;

import com.liferay.portal.search.elasticsearch7.internal.groupby.DefaultGroupByTranslator;
import com.liferay.portal.search.elasticsearch7.internal.groupby.GroupByTranslator;
import com.liferay.portal.search.elasticsearch7.internal.highlight.DefaultHighlighterTranslator;
import com.liferay.portal.search.elasticsearch7.internal.highlight.HighlighterTranslator;
import com.liferay.portal.search.elasticsearch7.internal.query.ElasticsearchQueryTranslator;
import com.liferay.portal.search.elasticsearch7.internal.query.ElasticsearchQueryTranslatorFixture;
import com.liferay.portal.search.elasticsearch7.internal.query.QueryToQueryBuilderTranslator;
import com.liferay.portal.search.elasticsearch7.internal.sort.DefaultSortTranslator;
import com.liferay.portal.search.elasticsearch7.internal.sort.ElasticsearchSortFieldTranslatorFixture;
import com.liferay.portal.search.elasticsearch7.internal.sort.SortTranslator;
import com.liferay.portal.search.elasticsearch7.internal.stats.DefaultStatsTranslator;
import com.liferay.portal.search.elasticsearch7.internal.stats.StatsTranslator;
import com.liferay.portal.search.internal.legacy.groupby.GroupByRequestFactoryImpl;
import com.liferay.portal.search.internal.legacy.stats.StatsRequestBuilderFactoryImpl;
import com.liferay.portal.search.internal.stats.StatsResponseBuilderFactoryImpl;
import com.liferay.portal.search.legacy.groupby.GroupByRequestFactory;
import com.liferay.portal.search.legacy.stats.StatsRequestBuilderFactory;
import com.liferay.portal.search.sort.SortFieldTranslator;

import org.elasticsearch.search.sort.SortBuilder;

/**
 * @author Adam Brandizzi
 */
public class SearchSearchRequestAssemblerFixture {

	public SearchSearchRequestAssembler createSearchSearchRequestAssembler() {
		StatsTranslator statsTranslator = new DefaultStatsTranslator() {
			{
				setStatsResponseBuilderFactory(
					new StatsResponseBuilderFactoryImpl());
			}
		};

		return createSearchSearchRequestAssembler(
			_commonSearchSourceBuilderAssemblerFixture,
			_elasticsearchQueryTranslator,
			_elasticsearchSortFieldTranslatorFixture, statsTranslator);
	}

	public SearchSearchRequestAssembler createSearchSearchRequestAssembler(
		CommonSearchSourceBuilderAssembler commonSearchSourceBuilderAssembler,
		GroupByRequestFactory groupByRequestFactory,
		GroupByTranslator groupByTranslator,
		HighlighterTranslator highlighterTranslator,
		QueryToQueryBuilderTranslator queryToQueryBuilderTranslator,
		SortFieldTranslator<SortBuilder<?>> sortFieldTranslator,
		SortTranslator sortTranslator,
		StatsRequestBuilderFactory statsRequestBuilderFactory,
		StatsTranslator statsTranslator) {

		return new SearchSearchRequestAssemblerImpl() {
			{
				setCommonSearchSourceBuilderAssembler(
					commonSearchSourceBuilderAssembler);
				setGroupByRequestFactory(groupByRequestFactory);
				setGroupByTranslator(groupByTranslator);
				setHighlighterTranslator(highlighterTranslator);
				setQueryToQueryBuilderTranslator(queryToQueryBuilderTranslator);
				setSortFieldTranslator(sortFieldTranslator);
				setSortTranslator(sortTranslator);
				setStatsRequestBuilderFactory(statsRequestBuilderFactory);
				setStatsTranslator(statsTranslator);
			}
		};
	}

	public SearchSearchRequestAssembler createSearchSearchRequestAssembler(
		CommonSearchSourceBuilderAssemblerFixture
			commonSearchSourceBuilderAssemblerFixture,
		ElasticsearchQueryTranslator elasticsearchQueryTranslator,
		ElasticsearchSortFieldTranslatorFixture
			elasticsearchSortFieldTranslatorFixture,
		StatsTranslator statsTranslator) {

		return createSearchSearchRequestAssembler(
			commonSearchSourceBuilderAssemblerFixture.
				createCommonSearchSourceBuilderAssembler(),
			new GroupByRequestFactoryImpl(), new DefaultGroupByTranslator(),
			new DefaultHighlighterTranslator(), elasticsearchQueryTranslator,
			elasticsearchSortFieldTranslatorFixture.
				getElasticsearchSortFieldTranslator(),
			new DefaultSortTranslator(), new StatsRequestBuilderFactoryImpl(),
			statsTranslator);
	}

	public void setUp() {
		_commonSearchSourceBuilderAssemblerFixture =
			new CommonSearchSourceBuilderAssemblerFixture();

		_commonSearchSourceBuilderAssemblerFixture.setUp();

		_elasticsearchQueryTranslatorFixture =
			new ElasticsearchQueryTranslatorFixture();

		_elasticsearchQueryTranslator =
			_elasticsearchQueryTranslatorFixture.
				getElasticsearchQueryTranslator();

		_elasticsearchSortFieldTranslatorFixture =
			new ElasticsearchSortFieldTranslatorFixture(
				_elasticsearchQueryTranslator);
	}

	private CommonSearchSourceBuilderAssemblerFixture
		_commonSearchSourceBuilderAssemblerFixture;
	private ElasticsearchQueryTranslator _elasticsearchQueryTranslator;
	private ElasticsearchQueryTranslatorFixture
		_elasticsearchQueryTranslatorFixture;
	private ElasticsearchSortFieldTranslatorFixture
		_elasticsearchSortFieldTranslatorFixture;

}