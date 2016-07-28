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

package com.liferay.portal.search.web.components.results.map.portlet;

/**
 * @author Lino Alves
 */
public interface SearchMapPortletPreferences {

	public static final boolean HIGHLIGHT_ENABLED_DEFAULT_VALUE = true;

	public static final String HIGHLIGHT_ENABLED_PREFERENCE_KEY =
		"highlightEnabled";

	public boolean isHighlightEnabled();

}