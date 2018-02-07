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

package com.liferay.portal.search.web.internal.modified.facet.date.range;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Adam Brandizzi
 */
public enum Label {

	PAST_HOUR("past-hour"), PAST_24_HOURS("past-24-hours"),
	PAST_WEEK("past-week"), PAST_MONTH("past-month"), PAST_YEAR("past-year");

	Label(String text) {
		_text = text;
	}

	public static List<String> getTexts() {
		Stream<Label> valuesStream = Stream.of(values());

		return valuesStream.map(
			Label::getText
		).collect(
			Collectors.toList()
		);
	}

	public static Optional<Label> of(String text) {
		Stream<Label> valuesStream = Stream.of(values());

		return valuesStream.filter(
			(label) -> label.getText().equals(text)
		).findAny();
	}

	public String getText() {
		return _text;
	}

	private String _text;

}