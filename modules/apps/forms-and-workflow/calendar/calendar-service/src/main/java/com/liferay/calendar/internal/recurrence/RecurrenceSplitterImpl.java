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

package com.liferay.calendar.internal.recurrence;

import com.google.ical.iter.RecurrenceIterator;
import com.google.ical.iter.RecurrenceIteratorFactory;
import com.google.ical.values.DateValue;
import com.google.ical.values.DateValueImpl;

import com.liferay.calendar.recurrence.Recurrence;
import com.liferay.calendar.recurrence.RecurrenceSerializer;

import java.text.ParseException;

import java.util.Calendar;

/**
 * @author Adam Brandizzi
 */
public class RecurrenceSplitterImpl implements RecurrenceSplitter {

	public RecurrenceSplitterImpl(
		Recurrence recurrence, Calendar startTimeJCalendar,
		Calendar splitTimeJCalendar) {

		Recurrence firstRecurrence = recurrence.clone();

		Recurrence secondRecurrence = recurrence.clone();

		_firstRecurrence = firstRecurrence;

		_secondRecurrence = secondRecurrence;
	}

	@Override
	public Recurrence getFirstRecurrence() {
		return _firstRecurrence;
	}

	@Override
	public Recurrence getSecondRecurrence() {
		return _secondRecurrence;
	}

	@Override
	public boolean isSplit() {
		if (_secondRecurrence != null) {
			return true;
		}

		return false;
	}

	private final Recurrence _firstRecurrence;
	private final Recurrence _secondRecurrence;

}