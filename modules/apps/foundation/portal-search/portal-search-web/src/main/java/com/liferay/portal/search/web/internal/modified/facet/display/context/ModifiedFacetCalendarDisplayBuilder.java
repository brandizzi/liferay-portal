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

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author André de Oliveira
 */
public class ModifiedFacetCalendarDisplayBuilder {

	public ModifiedFacetCalendarDisplayContext build() {
		int[] from = parseDate(_from);

		if (from != null) {
			_fromDay = from[0];
			_fromMonth = from[1];
			_fromYear = from[2];
		}

		int[] to = parseDate(_to);

		if (to != null) {
			_toDay = to[0];
			_toMonth = to[1];
			_toYear = to[2];
		}

		ModifiedFacetCalendarDisplayContext
			modifiedFacetCalendarDisplayContext =
				new ModifiedFacetCalendarDisplayContext();

		Date fromDate = getDate(_fromYear, _fromMonth, _fromDay);
		Date toDate = getDate(_toYear, _toMonth, _toDay);

		Calendar fromCalendar = CalendarFactoryUtil.getCalendar(
			_timeZone, _locale);

		if (Validator.isNotNull(fromDate)) {
			fromCalendar.setTime(fromDate);
		}
		else {
			fromCalendar.add(Calendar.DATE, -1);
		}

		Calendar toCalendar = CalendarFactoryUtil.getCalendar(
			_timeZone, _locale);

		if (Validator.isNotNull(toDate)) {
			toCalendar.setTime(toDate);
		}

		boolean selected = false;

		if (Validator.isNotNull(_from) || Validator.isNotNull(_to)) {
			selected = true;
		}

		modifiedFacetCalendarDisplayContext.setSelected(selected);

		modifiedFacetCalendarDisplayContext.setFromDayValue(
			fromCalendar.get(Calendar.DATE));
		modifiedFacetCalendarDisplayContext.setFromFirstDayOfWeek(
			fromCalendar.getFirstDayOfWeek() - 1);
		modifiedFacetCalendarDisplayContext.setFromMonthValue(
			fromCalendar.get(Calendar.MONTH));
		modifiedFacetCalendarDisplayContext.setFromYearValue(
			fromCalendar.get(Calendar.YEAR));

		modifiedFacetCalendarDisplayContext.setToDayValue(
			toCalendar.get(Calendar.DATE));
		modifiedFacetCalendarDisplayContext.setToFirstDayOfWeek(
			toCalendar.getFirstDayOfWeek() - 1);
		modifiedFacetCalendarDisplayContext.setToMonthValue(
			toCalendar.get(Calendar.MONTH));
		modifiedFacetCalendarDisplayContext.setToYearValue(
			toCalendar.get(Calendar.YEAR));

		boolean fromBeforeTo = false;

		if (fromCalendar.getTimeInMillis() < toCalendar.getTimeInMillis()) {
			fromBeforeTo = true;
		}

		modifiedFacetCalendarDisplayContext.setFromBeforeTo(fromBeforeTo);

		return modifiedFacetCalendarDisplayContext;
	}

	public Date getDate(int year, int month, int day) {
		try {
			return PortalUtil.getDate(month, day, year, _timeZone, null);
		}
		catch (PortalException pe) {
			throw new RuntimeException(pe);
		}
	}

	public void setFrom(String from) {
		_from = from;
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

	protected int[] parseDate(String string) {
		DateFormat format = new SimpleDateFormat("yyyy-mm-dd");

		try {
			Date date = format.parse(string);

			Calendar calendar = Calendar.getInstance();

			calendar.setTime(date);

			return new int[] {
				calendar.get(Calendar.DATE), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.YEAR)
			};
		}
		catch (Exception pe) {
			return null;
		}
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