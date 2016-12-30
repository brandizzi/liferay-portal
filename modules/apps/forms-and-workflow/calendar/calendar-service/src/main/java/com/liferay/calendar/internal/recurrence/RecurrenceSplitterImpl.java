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

		try {
			if (recurrence.getCount() != 0) {
				_setCount(
					recurrence, firstRecurrence, secondRecurrence,
					startTimeJCalendar, splitTimeJCalendar);
			}
		}
		catch (SplitTimeOutsideRecurrenceException store) {
			firstRecurrence = recurrence.clone();

			secondRecurrence = null;
		}

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

	private RecurrenceIterator _getRecurrenceIterator(
		Recurrence recurrence, DateValue startTimeDateValue) {

		try {
			return RecurrenceIteratorFactory.createRecurrenceIterator(
				RecurrenceSerializer.serialize(recurrence), startTimeDateValue,
				recurrence.getTimeZone());
		}
		catch (ParseException pe) {
			throw new IllegalStateException(pe);
		}
	}

	private void _setCount(
			Recurrence recurrence, Recurrence firstRecurrence,
			Recurrence secondRecurrence, Calendar startTimeJCalendar,
			Calendar splitTimeJCalendar)
		throws SplitTimeOutsideRecurrenceException {

		DateValue startTimeDateValue = _toDateValue(startTimeJCalendar);

		DateValue splitTimeDateValue = _toDateValue(splitTimeJCalendar);

		RecurrenceIterator recurrenceIterator = _getRecurrenceIterator(
			recurrence, startTimeDateValue);

		DateValue dateValue = recurrenceIterator.next();

		int count = 0;

		while (recurrenceIterator.hasNext()) {
			if (dateValue.compareTo(splitTimeDateValue) >= 0) {
				break;
			}

			count++;
			dateValue = recurrenceIterator.next();
		}

		_validateCount(splitTimeDateValue, dateValue);

		firstRecurrence.setCount(count);

		secondRecurrence.setCount(recurrence.getCount() - count);
	}

	private DateValue _toDateValue(Calendar jCalendar) {
		return new DateValueImpl(
			jCalendar.get(Calendar.YEAR), jCalendar.get(Calendar.MONTH) + 1,
			jCalendar.get(Calendar.DAY_OF_MONTH));
	}

	private void _validateCount(
			DateValue splitTimeDateValue, DateValue dateValue)
		throws SplitTimeOutsideRecurrenceException {

		if (dateValue.compareTo(splitTimeDateValue) < 0) {
			throw new SplitTimeOutsideRecurrenceException(
				"There is no instance after split time");
		}
	}

	private final Recurrence _firstRecurrence;
	private final Recurrence _secondRecurrence;

	private static class SplitTimeOutsideRecurrenceException extends Exception {

		public SplitTimeOutsideRecurrenceException(String message) {
			super(message);
		}

	}

}