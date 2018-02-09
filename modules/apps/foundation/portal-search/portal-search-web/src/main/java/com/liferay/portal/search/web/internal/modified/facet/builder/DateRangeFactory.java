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

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author André de Oliveira
 */
public class DateRangeFactory {

	public Collection<String> getLabels() {
		return _ranges.keySet();
	}

	public String getRangeString(String label) {
		return _normalizeDates(_ranges.get(label));
	}

	public String getRangeString(String start, String end) {
		StringBundler sb = new StringBundler(5);

		sb.append("[");
		sb.append(_stripAndPad(start, "000000"));
		sb.append(" TO ");
		sb.append(_stripAndPad(end, "235959"));
		sb.append("]");

		return sb.toString();
	}

	private static String _normalizeDates(String rangeString) {
		LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);

		LocalDateTime pastHour = now.minus(1, ChronoUnit.HOURS);
		LocalDateTime past24Hours = now.minus(24, ChronoUnit.HOURS);
		LocalDateTime pastWeek = now.minus(1, ChronoUnit.WEEKS);
		LocalDateTime pastMonth = now.minus(1, ChronoUnit.MONTHS);
		LocalDateTime pastYear = now.minus(1, ChronoUnit.YEARS);

		now = now.plus(1, ChronoUnit.HOURS);

		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(
			"yyyyMMddHHmmss");

		rangeString = StringUtil.replace(
			rangeString,
			new String[] {
				"past-hour", "past-24-hours", "past-week", "past-month",
				"past-year", "*"
			},
			new String[] {
				dateTimeFormatter.format(pastHour),
				dateTimeFormatter.format(past24Hours),
				dateTimeFormatter.format(pastWeek),
				dateTimeFormatter.format(pastMonth),
				dateTimeFormatter.format(pastYear),
				dateTimeFormatter.format(now)
			});

		return rangeString;
	}

	private String _stripAndPad(String dateString, String pad) {
		dateString = dateString.replace("-", "");

		return dateString + pad;
	}

	private static final Map<String, String> _ranges =
		new LinkedHashMap<String, String>() {
			{
				put("past-hour", "[past-hour TO *]");
				put("past-24-hours", "[past-24-hours TO *]");
				put("past-week", "[past-week TO *]");
				put("past-month", "[past-month TO *]");
				put("past-year", "[past-year TO *]");
			}
		};

}