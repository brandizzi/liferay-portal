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

import com.liferay.portal.search.aggregation.AggregationTranslator;
import com.liferay.portal.search.aggregation.pipeline.PipelineAggregationTranslator;
import com.liferay.portal.search.elasticsearch7.internal.aggregation.ElasticsearchAggregationVisitorFixture;
import com.liferay.portal.search.elasticsearch7.internal.aggregation.pipeline.ElasticsearchPipelineAggregationVisitorFixture;
import com.liferay.portal.search.elasticsearch7.internal.facet.DefaultFacetProcessor;
import com.liferay.portal.search.elasticsearch7.internal.facet.DefaultFacetTranslator;
import com.liferay.portal.search.elasticsearch7.internal.facet.FacetProcessor;
import com.liferay.portal.search.elasticsearch7.internal.facet.FacetTranslator;
import com.liferay.portal.search.elasticsearch7.internal.filter.ElasticsearchFilterTranslatorFixture;
import com.liferay.portal.search.elasticsearch7.internal.filter.FilterToQueryBuilderTranslator;
import com.liferay.portal.search.elasticsearch7.internal.query.ElasticsearchQueryTranslatorFixture;
import com.liferay.portal.search.elasticsearch7.internal.query.QueryToQueryBuilderTranslator;
import com.liferay.portal.search.elasticsearch7.internal.stats.DefaultStatsTranslator;
import com.liferay.portal.search.elasticsearch7.internal.stats.StatsTranslator;
import com.liferay.portal.search.filter.ComplexQueryBuilderFactory;
import com.liferay.portal.search.internal.filter.ComplexQueryBuilderImpl;
import com.liferay.portal.search.internal.query.QueriesImpl;
import com.liferay.portal.search.internal.stats.StatsResponseBuilderFactoryImpl;
import com.liferay.portal.search.query.Queries;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.PipelineAggregationBuilder;

/**
 * @author Adam Brandizzi
 */
public class CommonSearchSourceBuilderAssemblerFixture {

	public CommonSearchSourceBuilderAssembler
		createCommonSearchSourceBuilderAssembler() {

		return createCommonSearchSourceBuilderAssembler(new QueriesImpl());
	}

	public CommonSearchSourceBuilderAssembler
		createCommonSearchSourceBuilderAssembler(
			AggregationTranslator<AggregationBuilder> aggregationTranslator,
			ComplexQueryBuilderFactory complexQueryBuilderFactory,
			FacetTranslator facetTranslator,
			FilterToQueryBuilderTranslator filterToQueryBuilderTranslator,
			com.liferay.portal.search.elasticsearch7.internal.legacy.query.
				QueryToQueryBuilderTranslator
					legacyElasticsearchQueryTranslator,
			PipelineAggregationTranslator<PipelineAggregationBuilder>
				pipelineAggregationTranslator,
			QueryToQueryBuilderTranslator queryToQueryBuilderTranslator,
			StatsTranslator statsTranslator) {

		return new CommonSearchSourceBuilderAssemblerImpl() {
			{
				setAggregationTranslator(aggregationTranslator);
				setComplexQueryBuilderFactory(complexQueryBuilderFactory);
				setFacetTranslator(facetTranslator);
				setFilterToQueryBuilderTranslator(
					filterToQueryBuilderTranslator);
				setLegacyQueryToQueryBuilderTranslator(
					legacyElasticsearchQueryTranslator);
				setPipelineAggregationTranslator(pipelineAggregationTranslator);
				setQueryToQueryBuilderTranslator(queryToQueryBuilderTranslator);
				setStatsTranslator(statsTranslator);
			}
		};
	}

	public CommonSearchSourceBuilderAssembler
		createCommonSearchSourceBuilderAssembler(Queries queries) {

		return createCommonSearchSourceBuilderAssembler(
			_elasticsearchAggregationVisitorFixture.
				getElasticsearchAggregationVisitor(),
			createComplexQueryBuilderFactory(queries), createFacetTranslator(),
			_elasticsearchFilterTranslatorFixture.
				getElasticsearchFilterTranslator(),
			_legacyElasticsearchQueryTranslatorFixture.
				getElasticsearchQueryTranslator(),
			_elasticsearchPipelineAggregationVisitorFixture.
				getElasticsearchPipelineAggregationVisitor(),
			_elasticsearchQueryTranslatorFixture.
				getElasticsearchQueryTranslator(),
			createStatsTranslator());
	}

	public CommonSearchSourceBuilderAssembler
		createCommonSearchSourceBuilderAssembler(
			QueryToQueryBuilderTranslator queryToQueryBuilderTranslator,
			FacetProcessor<?> facetProcessor, StatsTranslator statsTranslator,
			ComplexQueryBuilderFactory complexQueryBuilderFactory) {

		return createCommonSearchSourceBuilderAssembler(
			_elasticsearchAggregationVisitorFixture.
				getElasticsearchAggregationVisitor(),
			complexQueryBuilderFactory, createFacetTranslator(facetProcessor),
			_elasticsearchFilterTranslatorFixture.
				getElasticsearchFilterTranslator(),
			_legacyElasticsearchQueryTranslatorFixture.
				getElasticsearchQueryTranslator(),
			_elasticsearchPipelineAggregationVisitorFixture.
				getElasticsearchPipelineAggregationVisitor(),
			queryToQueryBuilderTranslator, statsTranslator);
	}

	public void setUp() {
		_elasticsearchQueryTranslatorFixture =
			new ElasticsearchQueryTranslatorFixture();
		_elasticsearchAggregationVisitorFixture =
			new ElasticsearchAggregationVisitorFixture();
		_elasticsearchFilterTranslatorFixture =
			new ElasticsearchFilterTranslatorFixture();
		_elasticsearchPipelineAggregationVisitorFixture =
			new ElasticsearchPipelineAggregationVisitorFixture();
		_legacyElasticsearchQueryTranslatorFixture =
			new com.liferay.portal.search.elasticsearch7.internal.legacy.query.
				ElasticsearchQueryTranslatorFixture();
	}

	protected ComplexQueryBuilderFactory createComplexQueryBuilderFactory(
		Queries queries) {

		return () -> new ComplexQueryBuilderImpl(queries, null);
	}

	protected FacetTranslator createFacetTranslator() {
		return createFacetTranslator(new DefaultFacetProcessor());
	}

	protected FacetTranslator createFacetTranslator(
		FacetProcessor<?> facetProcessor) {

		return new DefaultFacetTranslator() {
			{
				setFacetProcessor(
					(FacetProcessor<SearchRequestBuilder>)facetProcessor);

				setFilterTranslator(
					_elasticsearchFilterTranslatorFixture.
						getElasticsearchFilterTranslator());
			}
		};
	}

	protected StatsTranslator createStatsTranslator() {
		return new DefaultStatsTranslator() {
			{
				setStatsResponseBuilderFactory(
					new StatsResponseBuilderFactoryImpl());
			}
		};
	}

	private ElasticsearchAggregationVisitorFixture
		_elasticsearchAggregationVisitorFixture;
	private ElasticsearchFilterTranslatorFixture
		_elasticsearchFilterTranslatorFixture;
	private ElasticsearchPipelineAggregationVisitorFixture
		_elasticsearchPipelineAggregationVisitorFixture;
	private ElasticsearchQueryTranslatorFixture
		_elasticsearchQueryTranslatorFixture;
	private com.liferay.portal.search.elasticsearch7.internal.legacy.query.
		ElasticsearchQueryTranslatorFixture
			_legacyElasticsearchQueryTranslatorFixture;

}