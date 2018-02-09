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

import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.search.facet.config.FacetConfiguration;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.CalendarFactoryImpl;
import com.liferay.portal.util.DateFormatFactoryImpl;
import com.liferay.portal.util.HtmlImpl;
import com.liferay.portal.util.HttpImpl;

import java.util.List;

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
		MockitoAnnotations.initMocks(this);

		setUpCalendarFactoryUtil();
		setUpDateFormat();
		setUpHtmlUtil();
		setUpHttpUtil();
		setUpJSONFactoryUtil();
		setUpPortalUtil();

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
	public void testGetTermDisplayContextsHasNoFromAndToAttributes() {
		ModifiedFacetDisplayBuilder modifiedFacetDisplayBuilder =
			createDisplayBuilder();

		modifiedFacetDisplayBuilder.setCurrentURL(
			"/?modifiedFrom=2018-01-01&modifiedTo=2018-01-31");

		ModifiedFacetDisplayContext modifiedFaceDisplayContext =
			modifiedFacetDisplayBuilder.build();

		assertTermDisplayContextsDoNotHaveFromAndToParameters(
			modifiedFaceDisplayContext.getTermDisplayContexts());
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

	protected void addRangeJSONObject(
		JSONArray jsonArray, String label, String range) {

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		jsonObject.put("label", label);
		jsonObject.put("range", range);

		jsonArray.put(jsonObject);
	}

	protected void assertDoesNotHasParameter(String url, String name) {
		Assert.assertTrue(
			Validator.isNull(HttpUtil.getParameter(url, name, false)));
	}

	protected void assertHasParameter(String url, String name) {
		Assert.assertTrue(
			Validator.isNotNull(HttpUtil.getParameter(url, name, false)));
	}

	protected void assertTermDisplayContextsDoNotHaveFromAndToParameters(
		List<ModifiedFacetTermDisplayContext> termDisplayContexts) {

		for (ModifiedFacetTermDisplayContext termDisplayContext :
				termDisplayContexts) {

			String label = termDisplayContext.getLabel();

			if (label.equals("custom-range")) {
				continue;
			}

			String rangeURL = termDisplayContext.getRangeURL();

			assertHasParameter(rangeURL, "modified");
			assertDoesNotHasParameter(rangeURL, "modifiedFrom");
			assertDoesNotHasParameter(rangeURL, "modifiedTo");
		}
	}

	protected ModifiedFacetDisplayBuilder createDisplayBuilder() {
		ModifiedFacetDisplayBuilder modifiedFacetDisplayBuilder =
			new ModifiedFacetDisplayBuilder();

		modifiedFacetDisplayBuilder.setFacet(_facet);
		modifiedFacetDisplayBuilder.setRangesJSONArray(createRangesJSONArray());
		modifiedFacetDisplayBuilder.setLocale(LocaleUtil.getDefault());
		modifiedFacetDisplayBuilder.setTimeZone(TimeZoneUtil.getDefault());

		return modifiedFacetDisplayBuilder;
	}

	protected JSONArray createRangesJSONArray() {
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		addRangeJSONObject(
			jsonArray, "past-hour", "[20180215120000 TO 20180215140000]");
		addRangeJSONObject(
			jsonArray, "past-24-hours", "[20180214130000 TO 20180215140000]");
		addRangeJSONObject(
			jsonArray, "past-week", "[20180208130000 TO 20180215140000]");
		addRangeJSONObject(
			jsonArray, "past-month", "[20180115130000 TO 20180215140000]");
		addRangeJSONObject(
			jsonArray, "past-year", "[20170215130000 TO 20180215140000]");

		return jsonArray;
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

	protected void setUpHtmlUtil() {
		HtmlUtil htmlUtil = new HtmlUtil();

		htmlUtil.setHtml(new HtmlImpl());
	}

	protected void setUpHttpUtil() {
		HttpUtil httpUtil = new HttpUtil();

		httpUtil.setHttp(new HttpImpl());
	}

	protected void setUpJSONFactoryUtil() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());
	}

	protected void setUpPortalUtil() {
		Mockito.doAnswer(
			invocation -> new String[] {
				invocation.getArgumentAt(0, String.class), StringPool.BLANK
			}
		).when(
			portal
		).stripURLAnchor(
			Mockito.anyString(), Mockito.anyString()
		);

		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(portal);
	}

	@Mock
	protected Portal portal;

	@Mock
	private Facet _facet;

	@Mock
	private FacetCollector _facetCollector;

}