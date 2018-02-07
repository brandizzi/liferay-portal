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

import com.liferay.portal.kernel.search.facet.util.RangeParserUtil;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.web.internal.modified.facet.date.range.Label;

import java.text.DateFormat;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author André de Oliveira
 */
public class DateRangeFactory {

	public String getRangeString(String range) {
		return Label.of(range).flatMap(
			(label) -> Optional.of(_normalizeDates(label)) 
		)
		.orElseGet(
			() -> _getLiteralRange(range)
		);
	}

	private String _getLiteralRange(String range) {
		String[] parsedRange = RangeParserUtil.parserRange(range);

		if ((parsedRange[0] != null) || (parsedRange[1]) != null) {
			return range;
		}
		
		return "";
	}

	private static String _normalizeDates(Label label) {
		return _normalizeDates(_ranges.get(label));
	}
	
	private static String _normalizeDates(String rangeString) {
		Calendar now = Calendar.getInstance();

		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MINUTE, 0);

		Calendar pastHour = (Calendar)now.clone();

		pastHour.set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY) - 1);

		Calendar past24Hours = (Calendar)now.clone();

		past24Hours.set(
			Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR) - 1);

		Calendar pastWeek = (Calendar)now.clone();

		pastWeek.set(Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR) - 7);

		Calendar pastMonth = (Calendar)now.clone();

		pastMonth.set(Calendar.MONTH, now.get(Calendar.MONTH) - 1);

		Calendar pastYear = (Calendar)now.clone();

		pastYear.set(Calendar.YEAR, now.get(Calendar.YEAR) - 1);

		now.set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY) + 1);

		DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyyMMddHHmmss");

		rangeString = StringUtil.replace(
			rangeString,
			new String[] {
				"past-hour", "past-24-hours", "past-week", "past-month",
				"past-year", "*"
			},
			new String[] {
				dateFormat.format(pastHour.getTime()),
				dateFormat.format(past24Hours.getTime()),
				dateFormat.format(pastWeek.getTime()),
				dateFormat.format(pastMonth.getTime()),
				dateFormat.format(pastYear.getTime()),
				dateFormat.format(now.getTime())
			});

		return rangeString;
	}

	private static final Map<Label, String> _ranges =
		new LinkedHashMap<Label, String>() {
			{
				put(Label.PAST_HOUR, "[past-hour TO *]");
				put(Label.PAST_24_HOURS, "[past-24-hours TO *]");
				put(Label.PAST_WEEK, "[past-week TO *]");
				put(Label.PAST_MONTH, "[past-month TO *]");
				put(Label.PAST_YEAR, "[past-year TO *]");
			}
		};

}