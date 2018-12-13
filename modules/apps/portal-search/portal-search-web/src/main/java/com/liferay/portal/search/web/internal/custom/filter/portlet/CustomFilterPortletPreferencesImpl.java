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

import com.liferay.petra.string.StringPool;
import com.liferay.portal.search.web.internal.util.PortletPreferencesHelper;

import java.util.Optional;

import javax.portlet.PortletPreferences;

/**
 * @author Igor Fabiano Nazar
 * @author Luan Maoski
 */
public class CustomFilterPortletPreferencesImpl
	implements CustomFilterPortletPreferences {

	public CustomFilterPortletPreferencesImpl(
		Optional<PortletPreferences> portletPreferencesOptional) {

		_portletPreferencesHelper = new PortletPreferencesHelper(
			portletPreferencesOptional);
	}

	@Override
	public String getCustomHeading() {
		Optional<String> optional = getCustomHeadingOptional();

		return optional.orElse(StringPool.BLANK);
	}

	@Override
	public Optional<String> getCustomHeadingOptional() {
		return _portletPreferencesHelper.getString(
			CustomFilterPortletPreferences.PREFERENCE_KEY_CUSTOM_HEADING);
	}

	@Override
	public String getCustomParameterName() {
		Optional<String> optional = getCustomParameterNameOptional();

		return optional.orElse(StringPool.BLANK);
	}

	@Override
	public Optional<String> getCustomParameterNameOptional() {
		return _portletPreferencesHelper.getString(
			CustomFilterPortletPreferences.
				PREFERENCE_KEY_CUSTOM_PARAMETER_NAME);
	}

	@Override
	public String getFilterField() {
		Optional<String> optional = getFilterFieldOptional();

		return optional.orElse(StringPool.BLANK);
	}

	@Override
	public Optional<String> getFilterFieldOptional() {
		return _portletPreferencesHelper.getString(
			CustomFilterPortletPreferences.PREFERENCE_KEY_FILTER_FIELD);
	}

	@Override
	public String getFilterValue() {
		Optional<String> optional = getFilterValueOptional();

		return optional.orElse(StringPool.BLANK);
	}

	@Override
	public Optional<String> getFilterValueOptional() {
		return _portletPreferencesHelper.getString(
			CustomFilterPortletPreferences.PREFERENCE_KEY_FILTER_VALUE);
	}

	@Override
	public boolean isInvisible() {
		return _portletPreferencesHelper.getBoolean(
			CustomFilterPortletPreferences.PREFERENCE_KEY_IS_INVISIBLE, true);
	}

	private final PortletPreferencesHelper _portletPreferencesHelper;

}