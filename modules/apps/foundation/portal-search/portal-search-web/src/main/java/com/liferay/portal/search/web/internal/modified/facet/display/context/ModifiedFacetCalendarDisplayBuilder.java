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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.TimeZone;

import com.liferay.portal.kernel.util.Validator;

/**
 * @author André de Oliveira
 */
public class ModifiedFacetCalendarDisplayBuilder {

	public ModifiedFacetCalendarDisplayContext build() {
		if (_rangeString != null) {
			_setFromValues(_rangeString.substring(1, 9));
			_setToValues(_rangeString.substring(10, 18));
		}
		else if (Validator.isNotNull(_from) && Validator.isNotNull(_to)) {
			_setFromValues(_from.replace("-", ""));
			_setToValues(_to.replace("-", ""));
		}

		ModifiedFacetCalendarDisplayContext
			modifiedFacetCalendarDisplayContext =
				new ModifiedFacetCalendarDisplayContext();

		LocalDate from = _getFromLocalDate();
		LocalDate to = _getToLocalDate();

		boolean selected = false;

		if (Validator.isNotNull(_from) || Validator.isNotNull(_to)) {
			selected = true;
		}

		modifiedFacetCalendarDisplayContext.setSelected(selected);

		modifiedFacetCalendarDisplayContext.setFromDayValue(
			from.getDayOfMonth());

		DayOfWeek firstDayOfWeek = WeekFields.of(_locale).getFirstDayOfWeek();

		modifiedFacetCalendarDisplayContext.setFromFirstDayOfWeek(
			firstDayOfWeek.ordinal());
		modifiedFacetCalendarDisplayContext.setFromMonthValue(
			from.getMonth().getValue() - 1);
		modifiedFacetCalendarDisplayContext.setFromYearValue(
			from.getYear());

		modifiedFacetCalendarDisplayContext.setToDayValue(
			to.getDayOfMonth());
		modifiedFacetCalendarDisplayContext.setToFirstDayOfWeek(
			firstDayOfWeek.ordinal());
		modifiedFacetCalendarDisplayContext.setToMonthValue(
			to.getMonth().getValue() - 1);
		modifiedFacetCalendarDisplayContext.setToYearValue(
			to.getYear());

		boolean fromBeforeTo = false;

		if (from.isBefore(to)) {
			fromBeforeTo = true;
		}

		modifiedFacetCalendarDisplayContext.setFromBeforeTo(fromBeforeTo);

		return modifiedFacetCalendarDisplayContext;
	}

	public void setFrom(String from) {
		_from = from;
	}

	public void setFromDay(int fromDay) {
		_fromDay = fromDay;
	}

	public void setFromMonth(int fromMonth) {
		_fromMonth = fromMonth;
	}

	public void setFromYear(int fromYear) {
		_fromYear = fromYear;
	}

	public void setLocale(Locale locale) {
		_locale = locale;
	}

	public void setRangeString(String rangeString) {
		_rangeString = rangeString;
	}

	public void setTimeZone(TimeZone timeZone) {
		_timeZone = timeZone;
	}

	public void setTo(String to) {
		_to = to;
	}

	public void setToDay(int toDay) {
		_toDay = toDay;
	}

	public void setToMonth(int toMonth) {
		_toMonth = toMonth;
	}

	public void setToYear(int toYear) {
		_toYear = toYear;
	}

	protected int[] parseDate(String string) {
		int day = Integer.valueOf(string.substring(6, 8));
		int month = Integer.valueOf(string.substring(4, 6));
		int year = Integer.valueOf(string.substring(0, 4));

		return new int[] {day, month, year};
	}

	private LocalDate _getFromLocalDate() {
		if (Validator.isGregorianDate(_fromMonth, _fromDay, _fromYear)) {
			return LocalDate.of(
				_fromYear, _fromMonth, _fromDay);
		}
		else {
			return LocalDate.now().minus(1, ChronoUnit.HOURS);
		}
	}

	private LocalDate _getToLocalDate() {
		if (Validator.isGregorianDate(_toMonth, _toDay, _toYear)) {
			return LocalDate.of(_toYear, _toMonth, _toDay);
		}
		else {
			return LocalDate.now();
		}
	}

	private void _setFromValues(String dateString) {
		int[] from = parseDate(dateString);

		_fromDay = from[0];
		_fromMonth = from[1] - 1;
		_fromYear = from[2];
	}

	private void _setToValues(String dateString) {
		int[] to = parseDate(dateString);

		_toDay = to[0];
		_toMonth = to[1] - 1;
		_toYear = to[2];
	}

	private String _from;
	private int _fromDay;
	private int _fromMonth;
	private int _fromYear;
	private Locale _locale;
	private String _rangeString;
	private TimeZone _timeZone;
	private String _to;
	private int _toDay;
	private int _toMonth;
	private int _toYear;

}