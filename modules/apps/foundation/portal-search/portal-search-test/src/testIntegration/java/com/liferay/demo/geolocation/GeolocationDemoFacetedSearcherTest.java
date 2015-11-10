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

package com.liferay.demo.geolocation;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.filter.GeoDistanceFilter;
import com.liferay.portal.kernel.search.geolocation.GeoDistance;
import com.liferay.portal.kernel.search.geolocation.GeoLocationPoint;
import com.liferay.portal.kernel.test.util.SearchContextTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.PrintWriter;
import java.io.StringWriter;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author André de Oliveira
 */
@RunWith(Arquillian.class)
public class GeolocationDemoFacetedSearcherTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule testRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testSearch() throws Exception {
		assertSearch(10, 1);
		assertSearch(500, 2);
		assertSearch(1000, 15);
		assertSearch(2000, 100);
		assertSearch(5000, 461);
		assertSearch(10000, 960);
		assertSearch(20000, 1000);
	}

	protected void assertSearch(double distanceInMeters, int size)
		throws Exception {

		GeoDistanceFilter f = new GeoDistanceFilter(
			FIELD, EPICENTER, new GeoDistance(distanceInMeters));

		ArrayList<GeoLocationPoint> points = search(f);

		Assert.assertEquals(size, points.size());
	}

	protected void print(ArrayList<GeoLocationPoint> points) {
		StringWriter w = new StringWriter();
		PrintWriter pw = new PrintWriter(w);

		for (GeoLocationPoint p : points)
			pw.println("" + p.getLatitude() + "," + p.getLongitude());

		System.out.println("--------");
		System.out.println(w.toString());
		System.out.println("--------");
	}

	protected ArrayList<GeoLocationPoint> search(GeoDistanceFilter f)
		throws Exception, SearchException {

		long groupId = 20146;

		SearchContext searchContext = SearchContextTestUtil.getSearchContext(
			groupId);

		String searchableContent = "latitude";

		searchContext.setKeywords(searchableContent);

		Indexer<?> indexer = new GeolocationDemoFacetedSearcher(f);

		Hits hits = indexer.search(searchContext);

		Document[] docs = hits.getDocs();

		ArrayList<GeoLocationPoint> points = new ArrayList<>(docs.length);

		for (Document document : docs) {
			Field field = document.getField(FIELD);

			points.add(field.getGeoLocationPoint());
		}

		print(points);

		return points;
	}

	protected static final GeoLocationPoint EPICENTER = new GeoLocationPoint(
		42.302, -71.0519);

	protected static final String FIELD =
		"ddm__keyword__20733__geolocation_en_US";

}