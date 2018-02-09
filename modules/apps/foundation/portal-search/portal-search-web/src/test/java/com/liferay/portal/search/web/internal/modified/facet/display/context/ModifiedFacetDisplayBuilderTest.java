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

package com.liferay.portal.search.web.internal.modified.facet.display.context;

import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.search.facet.config.FacetConfiguration;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.util.CalendarFactoryImpl;
import com.liferay.portal.util.DateFormatFactoryImpl;
import com.liferay.portal.util.HttpImpl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author Adam Brandizzi
 */
public class ModifiedFacetDisplayBuilderTest {

	@Before
	public void setUp() throws Exception {
		setUpCalendarFactoryUtil();
		setUpDateFormat();
		setUpHttpUtil();
		setUpJSONFactoryUtil();

		MockitoAnnotations.initMocks(this);

		Mockito.doReturn(
			_facetCollector
		).when(
			_facet
		).getFacetCollector();

		Mockito.doReturn(
			getFacetConfiguration()
		).when(
			_facet
		).getFacetConfiguration();
	}

	@Test
	public void testIsNothingSelected() {
		ModifiedFacetDisplayBuilder modifiedFacetDisplayBuilder =
			createDisplayBuilder();

		ModifiedFacetDisplayContext modifiedFaceDisplayContext =
			modifiedFacetDisplayBuilder.build();

		Assert.assertTrue(modifiedFaceDisplayContext.isNothingSelected());
	}

	@Test
	public void testIsNothingSelectedWithFromAndToAttributes() {
		ModifiedFacetDisplayBuilder modifiedFacetDisplayBuilder =
			createDisplayBuilder();

		modifiedFacetDisplayBuilder.setFromParameterValue("2018-01-01");
		modifiedFacetDisplayBuilder.setToParameterValue("2018-01-31");

		ModifiedFacetDisplayContext modifiedFaceDisplayContext =
			modifiedFacetDisplayBuilder.build();

		Assert.assertFalse(modifiedFaceDisplayContext.isNothingSelected());
	}

	@Test
	public void testIsNothingSelectedWithSelectedRange() {
		ModifiedFacetDisplayBuilder modifiedFacetDisplayBuilder =
			createDisplayBuilder();

		modifiedFacetDisplayBuilder.setParameterValues("past-24-hours");

		ModifiedFacetDisplayContext modifiedFaceDisplayContext =
			modifiedFacetDisplayBuilder.build();

		Assert.assertFalse(modifiedFaceDisplayContext.isNothingSelected());
	}

	protected ModifiedFacetDisplayBuilder createDisplayBuilder() {
		ModifiedFacetDisplayBuilder modifiedFacetDisplayBuilder =
			new ModifiedFacetDisplayBuilder();

		modifiedFacetDisplayBuilder.setFacet(_facet);
		modifiedFacetDisplayBuilder.setRangesJSONArray(
			JSONFactoryUtil.createJSONArray());
		modifiedFacetDisplayBuilder.setLocale(LocaleUtil.getDefault());
		modifiedFacetDisplayBuilder.setTimeZone(TimeZoneUtil.getDefault());

		return modifiedFacetDisplayBuilder;
	}

	protected FacetConfiguration getFacetConfiguration() {
		FacetConfiguration facetConfiguration = new FacetConfiguration();

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		facetConfiguration.setDataJSONObject(jsonObject);

		return facetConfiguration;
	}

	protected void setUpCalendarFactoryUtil() {
		CalendarFactoryUtil calendarFactoryUtil = new CalendarFactoryUtil();

		calendarFactoryUtil.setCalendarFactory(new CalendarFactoryImpl());
	}

	protected void setUpDateFormat() {
		DateFormatFactoryUtil dateFormatFactoryUtil =
			new DateFormatFactoryUtil();

		dateFormatFactoryUtil.setDateFormatFactory(new DateFormatFactoryImpl());
	}

	protected void setUpHttpUtil() {
		HttpUtil httpUtil = new HttpUtil();

		httpUtil.setHttp(new HttpImpl());
	}

	protected void setUpJSONFactoryUtil() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());
	}

	@Mock
	private Facet _facet;

	@Mock
	private FacetCollector _facetCollector;

}