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
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.util.CalendarFactoryImpl;
import com.liferay.portal.util.DateFormatFactoryImpl;

import java.text.DateFormat;
import java.text.ParseException;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
		setUpDateFormat();
		setUpJSONFactoryUtil();
	}

	@Test
	public void testFacetGetModifiedValueFromRangeStartEnd() throws Exception {
		ModifiedFacetBuilder modifiedFacetBuilder =
			createModifiedFacetBuilder();

		modifiedFacetBuilder.setStartRange("20180131");
		modifiedFacetBuilder.setEndRange("20180228");

		Facet modifiedFacet = modifiedFacetBuilder.build();

		List<Calendar> calendars = getRangeCalendars(modifiedFacet);

		Calendar startCalendar = parseCalendar("20180131");

		Calendar endCalendar = parseCalendar("20180228");

		assertSameDay(calendars.get(0), startCalendar);
		assertSameDay(calendars.get(1), endCalendar);
	}

	@Test
	public void testFacetGetModifiedValueFromSelectedRanges() throws Exception {
		ModifiedFacetBuilder modifiedFacetBuilder =
			createModifiedFacetBuilder();

		modifiedFacetBuilder.setSelectedRanges("past-24-hours");

		Facet modifiedFacet = modifiedFacetBuilder.build();

		List<Calendar> calendars = getRangeCalendars(modifiedFacet);

		Calendar today = CalendarFactoryUtil.getCalendar();

		assertSameDay(calendars.get(0), getYesterday(today));
		assertSameDay(calendars.get(1), today);
	}

	protected void assertSameDay(Calendar expected, Calendar actual) {
		String expectedDateString = _dateFormat.format(expected.getTime());
		String actualDateString = _dateFormat.format(actual.getTime());

		Assert.assertEquals(expectedDateString, actualDateString);
	}

	protected ModifiedFacetBuilder createModifiedFacetBuilder() {
		ModifiedFacetFactory modifiedFacetFactory = new ModifiedFacetFactory();

		ModifiedFacetBuilder modifiedFacetBuilder = new ModifiedFacetBuilder(
			modifiedFacetFactory);

		modifiedFacetBuilder.setSearchContext(new SearchContext());

		return modifiedFacetBuilder;
	}

	protected List<Calendar> getRangeCalendars(Facet modifiedFacet)
		throws ParseException {

		SearchContext searchContext = modifiedFacet.getSearchContext();

		String range = GetterUtil.getString(
			searchContext.getAttribute(modifiedFacet.getFieldName()));

		String[] dateStrings = RangeParserUtil.parserRange(range);

		List<Date> dates = Arrays.asList(
			_dateFormat.parse(dateStrings[0].substring(0, 8)),
			_dateFormat.parse(dateStrings[1].substring(0, 8)));

		Stream<Date> stream = dates.stream();

		return stream.map(
			date -> CalendarFactoryUtil.getCalendar(date.getTime())
		).collect(
			Collectors.toList()
		);
	}

	protected Calendar getYesterday(Calendar today) {
		Calendar yesterday = (Calendar)today.clone();

		yesterday.add(Calendar.DAY_OF_YEAR, -1);

		return yesterday;
	}

	protected Calendar parseCalendar(String calendarString)
		throws ParseException {

		Calendar calendar = Calendar.getInstance();

		calendar.setTime(_dateFormat.parse(calendarString));

		return calendar;
	}

	protected void setUpCalendarFactoryUtil() {
		CalendarFactoryUtil calendarFactoryUtil = new CalendarFactoryUtil();

		calendarFactoryUtil.setCalendarFactory(new CalendarFactoryImpl());
	}

	protected void setUpDateFormat() {
		DateFormatFactoryUtil dateFormatFactoryUtil =
			new DateFormatFactoryUtil();

		dateFormatFactoryUtil.setDateFormatFactory(new DateFormatFactoryImpl());

		_dateFormat = DateFormatFactoryUtil.getSimpleDateFormat("yyyyMMdd");
	}

	protected void setUpJSONFactoryUtil() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());
	}

	private DateFormat _dateFormat;

}