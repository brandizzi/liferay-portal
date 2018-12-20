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

package com.liferay.portal.search.custom.relevance;

import java.util.List;

/**
 * @author Adam Brandizzi
 */
public class CustomRelevance {

	public CustomRelevance(
		List<String> boosterValues, float boostIncrement, String field) {

		_boosterValues = boosterValues;
		_boostIncrement = boostIncrement;
		_field = field;
	}

	public List<String> getBoosterValues() {
		return _boosterValues;
	}

	public float getBoostIncrement() {
		return _boostIncrement;
	}

	public String getField() {
		return _field;
	}

	private final List<String> _boosterValues;
	private final float _boostIncrement;
	private final String _field;

}