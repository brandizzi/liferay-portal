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

import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.util.CalendarFactoryImpl;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Adam Brandizzi
 */
public class ModifiedFacetCalendarDisplayBuilderTest {

	@Before
	public void setUp() throws Exception {
		setUpCalendarFactoryUtil();
	}

	@Test
	public void testDoNotBreakWithoutSettingValues() {
		ModifiedFacetCalendarDisplayBuilder
			modifiedFacetCalendarDisplayBuilder = createDisplayBuilder();

		ModifiedFacetCalendarDisplayContext modifiedFaceCalendarDisplayContext =
			modifiedFacetCalendarDisplayBuilder.build();

		Assert.assertNotNull(modifiedFaceCalendarDisplayContext);
	}

	@Test
	public void testGetRangeFromCurrentDay() {
		TimeZone timeZone = TimeZoneUtil.getDefault();

		ModifiedFacetCalendarDisplayBuilder
			modifiedFacetCalendarDisplayBuilder = createDisplayBuilder(
				timeZone);

		ModifiedFacetCalendarDisplayContext modifiedFaceCalendarDisplayContext =
			modifiedFacetCalendarDisplayBuilder.build();

		LocalDate today = LocalDate.now();

		LocalDate yesterday = today.minus(1, ChronoUnit.DAYS);

		assertFromDateValues(
			yesterday.getYear(), yesterday.getMonth(),
			yesterday.getDayOfMonth(),
			modifiedFaceCalendarDisplayContext);

		assertToDateValues(
			today.getYear(), today.getMonth(),
			today.getDayOfMonth(),
			modifiedFaceCalendarDisplayContext);
	}

	@Test
	public void testGetRangeFromLimitAttributes() {
		ModifiedFacetCalendarDisplayBuilder
			modifiedFacetCalendarDisplayBuilder = createDisplayBuilder();

		modifiedFacetCalendarDisplayBuilder.setFrom("2018-01-31");
		modifiedFacetCalendarDisplayBuilder.setTo("2018-02-28");

		ModifiedFacetCalendarDisplayContext modifiedFaceCalendarDisplayContext =
			modifiedFacetCalendarDisplayBuilder.build();

		assertFromDateValues(
			2018, Month.JANUARY, 31, modifiedFaceCalendarDisplayContext);
		assertToDateValues(
			2018, Month.FEBRUARY, 28, modifiedFaceCalendarDisplayContext);
	}

	@Test
	public void testGetRangeFromLimitAttributesWithWestwardTimeZone() {
		Optional<TimeZone> timeZoneOptional = findWestwardTimeZoneOptional(
			TimeZone.getDefault());

		timeZoneOptional.ifPresent(
			timeZone -> {
				ModifiedFacetCalendarDisplayBuilder
					modifiedFacetCalendarDisplayBuilder = createDisplayBuilder(
						timeZone);

				modifiedFacetCalendarDisplayBuilder.setFrom("2018-01-31");
				modifiedFacetCalendarDisplayBuilder.setTo("2018-02-28");

				ModifiedFacetCalendarDisplayContext
					modifiedFaceCalendarDisplayContext =
						modifiedFacetCalendarDisplayBuilder.build();

				assertFromDateValues(
					2018, Month.JANUARY, 31,
					modifiedFaceCalendarDisplayContext);
				assertToDateValues(
					2018, Month.FEBRUARY, 28,
					modifiedFaceCalendarDisplayContext);
			});
	}

	protected void assertFromDateValues(
		int year, Month month, int dayOfMonth,
		ModifiedFacetCalendarDisplayContext
			modifiedFaceCalendarDisplayContext) {

		Assert.assertEquals(
			year, modifiedFaceCalendarDisplayContext.getFromYearValue());
		Assert.assertEquals(
			month.getValue() - 1,
			modifiedFaceCalendarDisplayContext.getFromMonthValue());
		Assert.assertEquals(
			dayOfMonth, modifiedFaceCalendarDisplayContext.getFromDayValue());
	}

	protected void assertToDateValues(
		int year, Month month, int dayOfMonth,
		ModifiedFacetCalendarDisplayContext
			modifiedFaceCalendarDisplayContext) {

		Assert.assertEquals(
			year, modifiedFaceCalendarDisplayContext.getToYearValue());
		Assert.assertEquals(
			month.getValue() - 1,
			modifiedFaceCalendarDisplayContext.getToMonthValue());
		Assert.assertEquals(
			dayOfMonth, modifiedFaceCalendarDisplayContext.getToDayValue());
	}

	protected ModifiedFacetCalendarDisplayBuilder createDisplayBuilder() {
		return createDisplayBuilder(TimeZoneUtil.getDefault());
	}

	protected ModifiedFacetCalendarDisplayBuilder createDisplayBuilder(
		TimeZone timeZone) {

		ModifiedFacetCalendarDisplayBuilder
			modifiedFacetCalendarDisplayBuilder =
				new ModifiedFacetCalendarDisplayBuilder();

		modifiedFacetCalendarDisplayBuilder.setLocale(LocaleUtil.getDefault());
		modifiedFacetCalendarDisplayBuilder.setTimeZone(timeZone);

		return modifiedFacetCalendarDisplayBuilder;
	}

	protected Optional<TimeZone> findWestwardTimeZoneOptional(
		TimeZone timeZone) {

		String[] availableIDs = TimeZone.getAvailableIDs(
			(int)(timeZone.getRawOffset() - Time.HOUR));

		if (availableIDs.length == 0) {
			return Optional.empty();
		}

		return Optional.of(TimeZoneUtil.getTimeZone(availableIDs[0]));
	}

	protected void setUpCalendarFactoryUtil() {
		CalendarFactoryUtil calendarFactoryUtil = new CalendarFactoryUtil();

		calendarFactoryUtil.setCalendarFactory(new CalendarFactoryImpl());
	}

}