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

package com.liferay.portal.search.web.internal.modified.facet.builder;

import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.ModifiedFacetFactory;
import com.liferay.portal.kernel.search.facet.util.RangeParserUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.util.CalendarFactoryImpl;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalQueries;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Adam Brandizzi
 */
public class ModifiedFacetBuilderTest {

	@Before
	public void setUp() {
		setUpCalendarFactoryUtil();
		setUpJSONFactoryUtil();
	}

	@Test
	public void testFacetGetModifiedValueFromRangeStartEnd() throws Exception {
		ModifiedFacetBuilder modifiedFacetBuilder =
			createModifiedFacetBuilder();

		modifiedFacetBuilder.setStartRange("20180131");
		modifiedFacetBuilder.setEndRange("20180228");

		Facet modifiedFacet = modifiedFacetBuilder.build();

		List<LocalDate> rangeLimits = getRangeLimits(modifiedFacet);

		assertSameDay(rangeLimits.get(0), parseLocalDateTime("20180131"));
		assertSameDay(rangeLimits.get(1), parseLocalDateTime("20180228"));
	}

	@Test
	public void testFacetGetModifiedValueFromSelectedRanges() throws Exception {
		ModifiedFacetBuilder modifiedFacetBuilder =
			createModifiedFacetBuilder();

		modifiedFacetBuilder.setSelectedRanges("past-24-hours");

		Facet modifiedFacet = modifiedFacetBuilder.build();

		List<LocalDate> rangeLimits = getRangeLimits(modifiedFacet);

		LocalDate today = LocalDate.now();

		assertSameDay(rangeLimits.get(0), today.minus(1, ChronoUnit.DAYS));
		assertSameDay(rangeLimits.get(1), today);
	}

	protected void assertSameDay(LocalDate expected, LocalDate actual) {
		Assert.assertEquals(expected, actual);
	}

	protected ModifiedFacetBuilder createModifiedFacetBuilder() {
		ModifiedFacetFactory modifiedFacetFactory = new ModifiedFacetFactory();

		ModifiedFacetBuilder modifiedFacetBuilder = new ModifiedFacetBuilder(
			modifiedFacetFactory);

		modifiedFacetBuilder.setSearchContext(new SearchContext());

		return modifiedFacetBuilder;
	}

	protected List<LocalDate> getRangeLimits(Facet modifiedFacet)
		throws ParseException {

		SearchContext searchContext = modifiedFacet.getSearchContext();

		String range = GetterUtil.getString(
			searchContext.getAttribute(modifiedFacet.getFieldName()));

		String[] dateStrings = RangeParserUtil.parserRange(range);

		LocalDate startDate = LocalDate.parse(
			dateStrings[0].substring(0, 8), dateTimeFormatter);

		LocalDate endDate = LocalDate.parse(
			dateStrings[1].substring(0, 8), dateTimeFormatter);

		return Arrays.asList(startDate, endDate);
	}

	protected LocalDate parseLocalDateTime(String dateString)
		throws ParseException {

		return LocalDate.parse(dateString, dateTimeFormatter);
	}

	protected void setUpCalendarFactoryUtil() {
		CalendarFactoryUtil calendarFactoryUtil = new CalendarFactoryUtil();

		calendarFactoryUtil.setCalendarFactory(new CalendarFactoryImpl());
	}

	protected void setUpJSONFactoryUtil() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());
	}

	protected DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(
		"yyyyMMdd");
}