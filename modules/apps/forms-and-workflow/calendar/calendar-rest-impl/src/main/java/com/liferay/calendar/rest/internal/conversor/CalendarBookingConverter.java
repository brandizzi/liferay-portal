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

package com.liferay.calendar.rest.internal.conversor;

import com.liferay.calendar.model.CalendarBooking;
import com.liferay.calendar.rest.internal.model.CalendarBookingModelImpl;
import com.liferay.calendar.rest.model.CalendarBookingModel;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;

/**
 * @author Adam Brandizzi
 */
@Component(immediate = true, service = CalendarBookingConverter.class)
public class CalendarBookingConverter {

	public CalendarBookingModel toCalendarBookingModel(
		CalendarBooking calendarBooking, Locale locale) {

		boolean hasChildCalendarBookings = false;
		boolean hasWorkflowInstanceLink = false;
		Calendar endTimeJCalendar = CalendarFactoryUtil.getCalendar(
			calendarBooking.getEndTime(), calendarBooking.getTimeZone());
		Calendar startTimeJCalendar = CalendarFactoryUtil.getCalendar(
			calendarBooking.getStartTime(), calendarBooking.getTimeZone());

		return new CalendarBookingModelImpl(
			calendarBooking.isAllDay(), calendarBooking.getCalendarBookingId(),
			calendarBooking.getCalendarId(),
			calendarBooking.getDescription(locale),
			calendarBooking.getEndTime(),
			endTimeJCalendar.get(Calendar.DAY_OF_MONTH),
			endTimeJCalendar.get(Calendar.HOUR_OF_DAY),
			endTimeJCalendar.get(Calendar.MINUTE),
			endTimeJCalendar.get(Calendar.MONTH),
			endTimeJCalendar.get(Calendar.YEAR),
			calendarBooking.getFirstReminder(),
			calendarBooking.getFirstReminderType(), hasChildCalendarBookings,
			hasWorkflowInstanceLink, calendarBooking.getInstanceIndex(),
			calendarBooking.getLocation(),
			calendarBooking.getParentCalendarBookingId(),
			calendarBooking.getRecurrence(),
			calendarBooking.getRecurringCalendarBookingId(),
			calendarBooking.getSecondReminder(),
			calendarBooking.getSecondReminderType(),
			calendarBooking.getStatus(), calendarBooking.getStartTime(),
			startTimeJCalendar.get(Calendar.DAY_OF_MONTH),
			startTimeJCalendar.get(Calendar.HOUR_OF_DAY),
			startTimeJCalendar.get(Calendar.MINUTE),
			startTimeJCalendar.get(Calendar.MONTH),
			startTimeJCalendar.get(Calendar.YEAR),
			calendarBooking.getTitle(locale));
	}

	public List<CalendarBookingModel> toCalendarBookingModels(
		List<CalendarBooking> calendarBookings, Locale locale) {

		Stream<CalendarBooking> stream = calendarBookings.stream();

		return stream.map(
			cb -> toCalendarBookingModel(cb, locale)
		).collect(
			Collectors.toList()
		);
	}

}