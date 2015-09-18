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

package com.liferay.calendar.util;

import com.google.ical.iter.RecurrenceIterator;
import com.google.ical.iter.RecurrenceIteratorFactory;
import com.google.ical.util.TimeUtils;
import com.google.ical.values.DateValue;
import com.google.ical.values.DateValueImpl;

import com.liferay.calendar.model.CalendarBooking;
import com.liferay.calendar.recurrence.Frequency;
import com.liferay.calendar.recurrence.PositionalWeekday;
import com.liferay.calendar.recurrence.Recurrence;
import com.liferay.calendar.recurrence.RecurrenceSerializer;
import com.liferay.calendar.recurrence.Weekday;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;

import java.text.ParseException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author Marcellus Tavares
 */
public class RecurrenceUtil {

	public static List<CalendarBooking> expandCalendarBooking(
		CalendarBooking calendarBooking, long startTime, long endTime,
		int maxSize) {

		List<CalendarBooking> expandedCalendarBookings = new ArrayList<>();

		String recurrence = calendarBooking.getRecurrence();

		Recurrence recurrenceObj = RecurrenceSerializer.deserialize(recurrence);

		List<PositionalWeekday> positionalWeekdays =
			recurrenceObj.getPositionalWeekdays();

		boolean isEmpty = positionalWeekdays.isEmpty();

		boolean isInWeekdays = true;

		try {
			CalendarBookingIterator calendarBookingIterator =
				new CalendarBookingIterator(calendarBooking);

			while (calendarBookingIterator.hasNext()) {
				CalendarBooking newCalendarBooking =
					calendarBookingIterator.next();

				if (newCalendarBooking.getEndTime() < startTime) {
					continue;
				}

				if (newCalendarBooking.getStartTime() > endTime) {
					break;
				}

				if (!isEmpty) {
					isInWeekdays = isInWeekdays(
						positionalWeekdays, newCalendarBooking, recurrenceObj);
				}

				if (!isInWeekdays) {
					continue;
				}

				expandedCalendarBookings.add(newCalendarBooking);

				if ((maxSize > 0) &&
					(expandedCalendarBookings.size() >= maxSize)) {

					break;
				}
			}
		}
		catch (ParseException pe) {
			_log.error("Unable to parse data ", pe);
		}

		return expandedCalendarBookings;
	}

	public static List<CalendarBooking> expandCalendarBookings(
		List<CalendarBooking> calendarBookings, long startTime, long endTime) {

		return expandCalendarBookings(calendarBookings, startTime, endTime, 0);
	}

	public static List<CalendarBooking> expandCalendarBookings(
		List<CalendarBooking> calendarBookings, long startTime, long endTime,
		int maxSize) {

		List<CalendarBooking> expandedCalendarBookings = new ArrayList<>();

		for (CalendarBooking calendarBooking : calendarBookings) {
			List<CalendarBooking> expandedCalendarBooking =
				expandCalendarBooking(
					calendarBooking, startTime, endTime, maxSize);

			expandedCalendarBookings.addAll(expandedCalendarBooking);
		}

		return expandedCalendarBookings;
	}

	public static CalendarBooking getCalendarBookingInstance(
		CalendarBooking calendarBooking, int instanceIndex) {

		try {
			CalendarBookingIterator calendarBookingIterator =
				new CalendarBookingIterator(calendarBooking);

			while (calendarBookingIterator.hasNext()) {
				CalendarBooking calendarBookingInstance =
					calendarBookingIterator.next();

				if (calendarBookingInstance.getInstanceIndex() ==
						instanceIndex) {

					return calendarBookingInstance;
				}
			}
		}
		catch (ParseException pe) {
			_log.error("Unable to parse data ", pe);
		}

		return null;
	}

	public static int getIndexOfInstance(
		String recurrence, long recurrenceStartTime, long instanceStartTime) {

		int count = 0;

		DateValue instanceDateValue = _toDateValue(instanceStartTime);

		try {
			RecurrenceIterator recurrenceIterator =
				RecurrenceIteratorFactory.createRecurrenceIterator(
					recurrence, _toDateValue(recurrenceStartTime),
					TimeUtils.utcTimezone());

			while (recurrenceIterator.hasNext()) {
				DateValue dateValue = recurrenceIterator.next();

				if (dateValue.compareTo(instanceDateValue) >= 0) {
					break;
				}

				count++;
			}
		}
		catch (ParseException e) {
			_log.error("Unable to parse data ", e);
		}

		return count;
	}

	public static boolean isInWeekdays(
		List<PositionalWeekday> positionalWeekdays,
		CalendarBooking calendarBooking, Recurrence recurrence) {

		Calendar startTimeJCalendar = JCalendarUtil.getJCalendar(
			calendarBooking.getStartTime());

		Weekday startTimeWeekday = Weekday.getWeekday(startTimeJCalendar);

		List<Weekday> weekdays = new ArrayList<Weekday>();

		for (PositionalWeekday positionalWeekday : positionalWeekdays) {
			weekdays.add(positionalWeekday.getWeekday());
		}

		int position = startTimeJCalendar.get(Calendar.DAY_OF_WEEK_IN_MONTH);

		PositionalWeekday positionalWeekday = new PositionalWeekday(
			startTimeWeekday, position);

		Frequency frequency = recurrence.getFrequency();

		boolean isInWeekdays = true;

		if (Validator.equals(frequency, Frequency.WEEKLY)) {
			if (!weekdays.contains(startTimeWeekday)) {
				isInWeekdays = false;
			}
		}
		else if (Validator.equals(frequency, Frequency.YEARLY) ||
				Validator.equals(frequency, Frequency.MONTHLY)) {

			if (!positionalWeekdays.contains(positionalWeekday)) {
				isInWeekdays = false;
			}
		}

		return isInWeekdays;
	}

	private static DateValue _toDateValue(long time) {
		Calendar jCalendar = JCalendarUtil.getJCalendar(time);

		return new DateValueImpl(
			jCalendar.get(Calendar.YEAR), jCalendar.get(Calendar.MONTH) + 1,
			jCalendar.get(Calendar.DAY_OF_MONTH));
	}

	private static final Log _log = LogFactoryUtil.getLog(RecurrenceUtil.class);

}