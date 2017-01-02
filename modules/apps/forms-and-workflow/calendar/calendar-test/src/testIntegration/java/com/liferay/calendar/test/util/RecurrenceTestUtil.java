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

package com.liferay.calendar.test.util;

import com.liferay.calendar.recurrence.Frequency;
import com.liferay.calendar.recurrence.PositionalWeekday;
import com.liferay.calendar.recurrence.Recurrence;

import java.util.ArrayList;

/**
 * @author Adam Brandizzi
 */
public class RecurrenceTestUtil {

	public static Recurrence getDailyRecurrence() {
		return getDailyRecurrence(0, null);
	}

	public static Recurrence getDailyRecurrence(int count) {
		return getDailyRecurrence(count, null);
	}

	public static Recurrence getDailyRecurrence(
		int count, java.util.Calendar untilJCalendar) {

		return getRecurrence(Frequency.DAILY, count, untilJCalendar);
	}

	public static Recurrence getDailyRecurrence(
		java.util.Calendar untilJCalendar) {

		return getDailyRecurrence(0, untilJCalendar);
	}

	public static Recurrence getRecurrence(
		Frequency frequency, int count, java.util.Calendar untilJCalendar) {

		Recurrence recurrence = new Recurrence();

		recurrence.setFrequency(frequency);
		recurrence.setCount(count);
		recurrence.setPositionalWeekdays(new ArrayList<PositionalWeekday>());
		recurrence.setUntilJCalendar(untilJCalendar);

		return recurrence;
	}

}