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

import com.liferay.portal.search.aggregation.AggregationResults;
import com.liferay.portal.search.document.DocumentBuilderFactory;
import com.liferay.portal.search.elasticsearch7.internal.SearchHitDocumentTranslatorImpl;
import com.liferay.portal.search.elasticsearch7.internal.search.response.DefaultSearchResponseTranslator;
import com.liferay.portal.search.elasticsearch7.internal.search.response.SearchResponseTranslator;
import com.liferay.portal.search.elasticsearch7.internal.stats.DefaultStatsTranslator;
import com.liferay.portal.search.elasticsearch7.internal.stats.StatsTranslator;
import com.liferay.portal.search.geolocation.GeoBuilders;
import com.liferay.portal.search.highlight.HighlightFieldBuilderFactory;
import com.liferay.portal.search.hits.SearchHitBuilderFactory;
import com.liferay.portal.search.hits.SearchHitsBuilderFactory;
import com.liferay.portal.search.internal.aggregation.AggregationResultsImpl;
import com.liferay.portal.search.internal.document.DocumentBuilderFactoryImpl;
import com.liferay.portal.search.internal.geolocation.GeoBuildersImpl;
import com.liferay.portal.search.internal.groupby.GroupByResponseFactoryImpl;
import com.liferay.portal.search.internal.highlight.HighlightFieldBuilderFactoryImpl;
import com.liferay.portal.search.internal.hits.SearchHitBuilderFactoryImpl;
import com.liferay.portal.search.internal.hits.SearchHitsBuilderFactoryImpl;
import com.liferay.portal.search.internal.legacy.stats.StatsRequestBuilderFactoryImpl;
import com.liferay.portal.search.internal.legacy.stats.StatsResultsTranslatorImpl;
import com.liferay.portal.search.internal.stats.StatsResponseBuilderFactoryImpl;

/**
 * @author Adam Brandizzi
 */
public class SearchSearchResponseAssemblerFixture {

	public SearchSearchResponseAssembler createSearchSearchResponseAssembler() {
		StatsTranslator statsTranslator = new DefaultStatsTranslator() {
			{
				setStatsResponseBuilderFactory(
					new StatsResponseBuilderFactoryImpl());
			}
		};

		CommonSearchResponseAssembler commonSearchResponseAssembler =
			new CommonSearchResponseAssemblerImpl() {
				{
					setStatsTranslator(statsTranslator);
				}
			};

		SearchResponseTranslator searchResponseTranslator =
			new DefaultSearchResponseTranslator() {
				{
					setGroupByResponseFactory(new GroupByResponseFactoryImpl());
					setSearchHitDocumentTranslator(
						new SearchHitDocumentTranslatorImpl());
					setStatsRequestBuilderFactory(
						new StatsRequestBuilderFactoryImpl());
					setStatsResultsTranslator(new StatsResultsTranslatorImpl());
					setStatsTranslator(statsTranslator);
				}
			};

		return createSearchSearchResponseAssembler(
			commonSearchResponseAssembler, searchResponseTranslator);
	}

	public SearchSearchResponseAssembler createSearchSearchResponseAssembler(
		AggregationResults aggregationResults,
		CommonSearchResponseAssembler commonSearchResponseAssembler,
		DocumentBuilderFactory documentBuilderFactory, GeoBuilders geoBuilders,
		HighlightFieldBuilderFactory highlightFieldBuilderFactory,
		SearchHitBuilderFactory searchHitBuilderFactory,
		SearchHitsBuilderFactory searchHitsBuilderFactory,
		SearchResponseTranslator searchResponseTranslator) {

		return new SearchSearchResponseAssemblerImpl() {
			{
				setAggregationResults(aggregationResults);
				setCommonSearchResponseAssembler(commonSearchResponseAssembler);
				setDocumentBuilderFactory(documentBuilderFactory);
				setGeoBuilders(geoBuilders);
				setHighlightFieldBuilderFactory(highlightFieldBuilderFactory);
				setSearchHitBuilderFactory(searchHitBuilderFactory);
				setSearchHitsBuilderFactory(searchHitsBuilderFactory);
				setSearchResponseTranslator(searchResponseTranslator);
			}
		};
	}

	public SearchSearchResponseAssembler createSearchSearchResponseAssembler(
		CommonSearchResponseAssembler commonSearchResponseAssembler,
		SearchResponseTranslator searchResponseTranslator) {

		return createSearchSearchResponseAssembler(
			new AggregationResultsImpl(), commonSearchResponseAssembler,
			new DocumentBuilderFactoryImpl(), new GeoBuildersImpl(),
			new HighlightFieldBuilderFactoryImpl(),
			new SearchHitBuilderFactoryImpl(),
			new SearchHitsBuilderFactoryImpl(), searchResponseTranslator);
	}

}