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

import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.elasticsearch7.internal.facet.DefaultFacetProcessor;
import com.liferay.portal.search.elasticsearch7.internal.facet.FacetProcessor;
import com.liferay.portal.search.elasticsearch7.internal.query.ElasticsearchQueryTranslator;
import com.liferay.portal.search.elasticsearch7.internal.query.ElasticsearchQueryTranslatorFixture;
import com.liferay.portal.search.elasticsearch7.internal.stats.DefaultStatsTranslator;
import com.liferay.portal.search.elasticsearch7.internal.stats.StatsTranslator;
import com.liferay.portal.search.elasticsearch7.internal.suggest.ElasticsearchSuggesterTranslatorFixture;
import com.liferay.portal.search.engine.adapter.search.SearchRequestExecutor;
import com.liferay.portal.search.filter.ComplexQueryBuilderFactory;
import com.liferay.portal.search.internal.filter.ComplexQueryBuilderFactoryImpl;
import com.liferay.portal.search.internal.query.QueriesImpl;
import com.liferay.portal.search.internal.stats.StatsResponseBuilderFactoryImpl;
import com.liferay.portal.search.query.Queries;

/**
 * @author Michael C. Han
 */
public class SearchRequestExecutorFixture {

	public SearchRequestExecutor getSearchRequestExecutor() {
		return _searchRequestExecutor;
	}

	public void setUp() {
		FacetProcessor<?> facetProcessor = getFacetProcessor();

		CommonSearchSourceBuilderAssemblerFixture
			commonSearchSourceBuilderAssemblerFixture =
				new CommonSearchSourceBuilderAssemblerFixture();

		commonSearchSourceBuilderAssemblerFixture.setUp();

		SearchSearchRequestAssemblerFixture
			searchSearchRequestAssemblerFixture =
				new SearchSearchRequestAssemblerFixture();

		searchSearchRequestAssemblerFixture.setUp();

		SearchSearchResponseAssemblerFixture
			searchSearchResponseAssemblerFixture =
				new SearchSearchResponseAssemblerFixture();

		ElasticsearchQueryTranslatorFixture
			elasticsearchQueryTranslatorFixture =
				new ElasticsearchQueryTranslatorFixture();

		ElasticsearchQueryTranslator elasticsearchQueryTranslator =
			elasticsearchQueryTranslatorFixture.
				getElasticsearchQueryTranslator();

		StatsTranslator statsTranslator = new DefaultStatsTranslator() {
			{
				setStatsResponseBuilderFactory(
					new StatsResponseBuilderFactoryImpl());
			}
		};

		_searchRequestExecutor = createSearchRequestExecutor(
			commonSearchSourceBuilderAssemblerFixture,
			searchSearchRequestAssemblerFixture,
			searchSearchResponseAssemblerFixture, _elasticsearchClientResolver,
			elasticsearchQueryTranslator, facetProcessor, statsTranslator,
			createComplexQueryBuilderFactory(new QueriesImpl()));
	}

	protected static ComplexQueryBuilderFactory
		createComplexQueryBuilderFactory(Queries queries) {

		return new ComplexQueryBuilderFactoryImpl() {
			{
				setQueries(queries);
			}
		};
	}

	protected static CountSearchRequestExecutor
		createCountSearchRequestExecutor(
			ElasticsearchClientResolver elasticsearchClientResolver,
			CommonSearchSourceBuilderAssembler
				commonSearchSourceBuilderAssembler,
			StatsTranslator statsTranslator) {

		return new CountSearchRequestExecutorImpl() {
			{
				setCommonSearchResponseAssembler(
					new CommonSearchResponseAssemblerImpl() {
						{
							setStatsTranslator(statsTranslator);
						}
					});
				setCommonSearchSourceBuilderAssembler(
					commonSearchSourceBuilderAssembler);
				setElasticsearchClientResolver(elasticsearchClientResolver);
			}
		};
	}

	protected static MultisearchSearchRequestExecutor
		createMultisearchSearchRequestExecutor(
			ElasticsearchClientResolver elasticsearchClientResolver,
			SearchSearchRequestAssembler searchSearchRequestAssembler,
			SearchSearchResponseAssembler searchSearchResponseAssembler) {

		return new MultisearchSearchRequestExecutorImpl() {
			{
				setElasticsearchClientResolver(elasticsearchClientResolver);
				setSearchSearchRequestAssembler(searchSearchRequestAssembler);
				setSearchSearchResponseAssembler(searchSearchResponseAssembler);
			}
		};
	}

	protected static SearchRequestExecutor createSearchRequestExecutor(
		CommonSearchSourceBuilderAssemblerFixture
			commonSearchSourceBuilderAssemblerFixture,
		SearchSearchRequestAssemblerFixture searchSearchRequestAssemblerFixture,
		SearchSearchResponseAssemblerFixture
			searchSearchResponseAssemblerFixture,
		ElasticsearchClientResolver elasticsearchClientResolver,
		ElasticsearchQueryTranslator elasticsearchQueryTranslator,
		FacetProcessor<?> facetProcessor, StatsTranslator statsTranslator,
		ComplexQueryBuilderFactory complexQueryBuilderFactory) {

		CommonSearchSourceBuilderAssembler commonSearchSourceBuilderAssembler =
			commonSearchSourceBuilderAssemblerFixture.
				createCommonSearchSourceBuilderAssembler(
					elasticsearchQueryTranslator, facetProcessor,
					statsTranslator, complexQueryBuilderFactory);

		SearchSearchRequestAssembler searchSearchRequestAssembler =
			searchSearchRequestAssemblerFixture.
				createSearchSearchRequestAssembler();

		SearchSearchResponseAssembler searchSearchResponseAssembler =
			searchSearchResponseAssemblerFixture.
				createSearchSearchResponseAssembler();

		return new ElasticsearchSearchRequestExecutor() {
			{
				setCountSearchRequestExecutor(
					createCountSearchRequestExecutor(
						elasticsearchClientResolver,
						commonSearchSourceBuilderAssembler, statsTranslator));
				setMultisearchSearchRequestExecutor(
					createMultisearchSearchRequestExecutor(
						elasticsearchClientResolver,
						searchSearchRequestAssembler,
						searchSearchResponseAssembler));
				setSearchSearchRequestExecutor(
					createSearchSearchRequestExecutor(
						elasticsearchClientResolver,
						searchSearchRequestAssembler,
						searchSearchResponseAssembler));
				setSuggestSearchRequestExecutor(
					createSuggestSearchRequestExecutor(
						elasticsearchClientResolver));
			}
		};
	}

	protected static SearchSearchRequestExecutor
		createSearchSearchRequestExecutor(
			ElasticsearchClientResolver elasticsearchClientResolver,
			SearchSearchRequestAssembler searchSearchRequestAssembler,
			SearchSearchResponseAssembler searchSearchResponseAssembler) {

		return new SearchSearchRequestExecutorImpl() {
			{
				setElasticsearchClientResolver(elasticsearchClientResolver);
				setSearchSearchRequestAssembler(searchSearchRequestAssembler);
				setSearchSearchResponseAssembler(searchSearchResponseAssembler);
			}
		};
	}

	protected static SuggestSearchRequestExecutor
		createSuggestSearchRequestExecutor(
			ElasticsearchClientResolver elasticsearchClientResolver) {

		return new SuggestSearchRequestExecutorImpl() {
			{
				setElasticsearchClientResolver(elasticsearchClientResolver);

				ElasticsearchSuggesterTranslatorFixture
					elasticsearchSuggesterTranslatorFixture =
						new ElasticsearchSuggesterTranslatorFixture();

				setSuggesterTranslator(
					elasticsearchSuggesterTranslatorFixture.
						getElasticsearchSuggesterTranslator());
			}
		};
	}

	protected FacetProcessor<?> getFacetProcessor() {
		if (_facetProcessor != null) {
			return _facetProcessor;
		}

		return new DefaultFacetProcessor();
	}

	protected void setElasticsearchClientResolver(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		_elasticsearchClientResolver = elasticsearchClientResolver;
	}

	protected void setFacetProcessor(FacetProcessor<?> facetProcessor) {
		_facetProcessor = facetProcessor;
	}

	private ElasticsearchClientResolver _elasticsearchClientResolver;
	private FacetProcessor<?> _facetProcessor;
	private SearchRequestExecutor _searchRequestExecutor;

}