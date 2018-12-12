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

package com.liferay.portal.search.web.internal.custom.filter.portlet;

import java.util.Optional;

/**
 * @author Igor Fabiano Nazar
 * @author Luan Maoski
 */
public interface CustomFilterPortletPreferences {

	public static final String PREFERENCE_KEY_CUSTOM_HEADING = "customHeading";

	public static final String PREFERENCE_KEY_CUSTOM_PARAMETER_NAME =
		"customParameterName";

	public static final String PREFERENCE_KEY_FILTER_FIELD = "filterField";

	public static final String PREFERENCE_KEY_FILTER_VALUE = "filterValue";

	public static final String PREFERENCE_KEY_IS_INVISIBLE = "isInvisible";

	public String getCustomHeading();

	public Optional<String> getCustomHeadingOptional();

	public String getCustomParameterName();

	public Optional<String> getCustomParameterNameOptional();

	public String getFilterField();

	public Optional<String> getFilterFieldOptional();

	public String getFilterValue();

	public Optional<String> getFilterValueOptional();

	public boolean isInvisible();

}