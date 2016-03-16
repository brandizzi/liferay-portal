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

package com.liferay.calendar.util.test;

import com.google.ical.values.DateValue;
import com.google.ical.values.DateValueImpl;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.calendar.model.CalendarBooking;
import com.liferay.calendar.model.impl.CalendarBookingImpl;
import com.liferay.calendar.util.CalendarBookingIterator;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.text.ParseException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adam Brandizzi
 */
@RunWith(Arquillian.class)
public class CalendarBookingIteratorTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testRecurrenceIsNull() throws ParseException {
		Calendar calendar = Calendar.getInstance();

		CalendarBooking calendarBooking = new MockCalendarBooking();

		calendarBooking.setStartTime(calendar.getTimeInMillis());
		calendarBooking.setRecurrence(null);
		calendarBooking.setMasterRecurrence(null);

		List<CalendarBooking> calendarBookings = new ArrayList<>();

		calendarBookings.add(calendarBooking);

		CalendarBookingIterator calendarBookingIterator =
			new CalendarBookingIterator(calendarBookings);

		int count = 0;

		while (calendarBookingIterator.hasNext()) {
			calendarBookingIterator.next();

			count++;
		}

		Assert.assertEquals(1, count);
	}

	@Test
	public void testRecurrenceStartsMondayRepeatsMonday()
		throws ParseException {

		Calendar calendar = Calendar.getInstance();

		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

		CalendarBooking calendarBooking = new MockCalendarBooking();

		calendarBooking.setStartTime(calendar.getTimeInMillis());
		calendarBooking.setRecurrence(
			"RRULE:FREQ=WEEKLY;COUNT=2;INTERVAL=1;BYDAY=MO");
		calendarBooking.setMasterRecurrence(
			"RRULE:FREQ=WEEKLY;COUNT=2;INTERVAL=1;BYDAY=MO");

		List<CalendarBooking> calendarBookings = new ArrayList<>();

		calendarBookings.add(calendarBooking);

		CalendarBookingIterator calendarBookingIterator =
			new CalendarBookingIterator(calendarBookings);

		int count = 0;

		while (calendarBookingIterator.hasNext()) {
			calendarBookingIterator.next();

			count++;
		}

		Assert.assertEquals(2, count);
	}

	@Test
	public void testRecurrenceStartsMondayRepeatsWednesday()
		throws ParseException {

		Calendar calendar = Calendar.getInstance();

		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

		CalendarBooking calendarBooking = new MockCalendarBooking();

		calendarBooking.setStartTime(calendar.getTimeInMillis());
		calendarBooking.setRecurrence(
			"RRULE:FREQ=WEEKLY;COUNT=2;INTERVAL=1;BYDAY=WE");
		calendarBooking.setMasterRecurrence(
			"RRULE:FREQ=WEEKLY;COUNT=2;INTERVAL=1;BYDAY=WE");

		List<CalendarBooking> calendarBookings = new ArrayList<>();

		calendarBookings.add(calendarBooking);

		CalendarBookingIterator calendarBookingIterator =
			new CalendarBookingIterator(calendarBookings);

		int count = 0;

		while (calendarBookingIterator.hasNext()) {
			calendarBookingIterator.next();

			count++;
		}

		Assert.assertEquals(2, count);
	}

	@Test
	public void testRecurrenceWithMultipleBookings() throws ParseException {
		Calendar calendar = Calendar.getInstance();

		calendar.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);

		Calendar exceptionCalendar = (Calendar)calendar.clone();

		exceptionCalendar.add(Calendar.WEEK_OF_YEAR, 2);

		CalendarBooking calendarBooking = new MockCalendarBooking();

		CalendarBooking exceptionCalendarBooking = new MockCalendarBooking();

		long calendarBookingId = RandomTestUtil.randomLong();

		long exceptionCalendarBookingId = calendarBookingId + 1;

		calendarBooking.setCalendarBookingId(calendarBookingId);
		exceptionCalendarBooking.setCalendarBookingId(
			exceptionCalendarBookingId);

		calendarBooking.setRecurringCalendarBookingId(calendarBookingId);
		exceptionCalendarBooking.setRecurringCalendarBookingId(
			calendarBookingId);

		calendarBooking.setStartTime(calendar.getTimeInMillis());
		exceptionCalendarBooking.setStartTime(
			exceptionCalendar.getTimeInMillis());

		calendarBooking.setMasterRecurrence(
			"RRULE:FREQ=WEEKLY;INTERVAL=1;BYDAY=WE");
		exceptionCalendarBooking.setMasterRecurrence(
			"RRULE:FREQ=WEEKLY;INTERVAL=1;BYDAY=WE");

		calendarBooking.setRecurrence(
			"RRULE:FREQ=WEEKLY;INTERVAL=1;BYDAY=WE\n"+
				"EXDATE;TZID=\"UTC\";VALUE=DATE:" +
					toDateString(exceptionCalendar));
		exceptionCalendarBooking.setRecurrence(null);

		List<CalendarBooking> calendarBookings = new ArrayList<>();

		calendarBookings.add(calendarBooking);
		calendarBookings.add(exceptionCalendarBooking);

		CalendarBookingIterator calendarBookingIterator =
			new CalendarBookingIterator(calendarBookings);

		CalendarBooking testCalendarBooking = null;

		for (int i = 0; i < 3; i++) {
			testCalendarBooking = calendarBookingIterator.next();
		}

		Assert.assertEquals(
			exceptionCalendarBookingId,
			testCalendarBooking.getCalendarBookingId());

		testCalendarBooking = calendarBookingIterator.next();

		Assert.assertEquals(
			calendarBookingId, testCalendarBooking.getCalendarBookingId());
	}

	protected static String toDateString(Calendar calendar) {
		DateValue dateValue = new DateValueImpl(
			calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
			calendar.get(Calendar.DATE));

		return dateValue.toString();
	}

	protected class MockCalendarBooking extends CalendarBookingImpl {

		@Override
		public TimeZone getTimeZone() {
			return TimeZoneUtil.getTimeZone(StringPool.UTC);
		}

	}

}