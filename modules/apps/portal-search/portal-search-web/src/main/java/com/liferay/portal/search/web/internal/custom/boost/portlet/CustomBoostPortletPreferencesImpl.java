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

package com.liferay.portal.search.web.internal.custom.boost.portlet;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.search.web.internal.util.PortletPreferencesHelper;

import java.util.Optional;

import javax.portlet.PortletPreferences;

/**
 * @author Igor Nazar
 * @author Luan Maoski
 */
public class CustomBoostPortletPreferencesImpl
	implements CustomBoostPortletPreferences {

	public CustomBoostPortletPreferencesImpl(
		Optional<PortletPreferences> portletPreferencesOptional) {

		_portletPreferencesHelper = new PortletPreferencesHelper(
			portletPreferencesOptional);
	}

	@Override
	public Optional<String> getBoostFieldOptional() {
		return _portletPreferencesHelper.getString(
			CustomBoostPortletPreferences.PREFERENCE_KEY_BOOST_FIELD);
	}

	@Override
	public String getBoostFieldString() {
		Optional<String> optional = getBoostFieldOptional();

		return optional.orElse(StringPool.BLANK);
	}

	@Override
	public String getBoostIncrement() {
		Optional<String> optional = getBoostIncrementOptional();

		return optional.orElse(StringPool.BLANK);
	}

	@Override
	public Optional<String> getBoostIncrementOptional() {
		return _portletPreferencesHelper.getString(
			CustomBoostPortletPreferences.PREFERENCE_KEY_BOOST_INCREMENT);
	}

	@Override
	public String getBoostValues() {
		Optional<String> optional = getBoostValuesOptional();

		return optional.orElse(StringPool.BLANK);
	}

	@Override
	public Optional<String> getBoostValuesOptional() {
		return _portletPreferencesHelper.getString(
			CustomBoostPortletPreferences.PREFERENCE_KEY_BOOST_VALUES);
	}

	@Override
	public String getCustomHeading() {
		Optional<String> optional = getCustomHeadingOptional();

		return optional.orElse(StringPool.BLANK);
	}

	@Override
	public Optional<String> getCustomHeadingOptional() {
		return _portletPreferencesHelper.getString(
			CustomBoostPortletPreferences.PREFERENCE_KEY_CUSTOM_HEADING);
	}

	@Override
	public String getCustomParameterName() {
		Optional<String> optional = getCustomParameterNameOptional();

		return optional.orElse(StringPool.BLANK);
	}

	@Override
	public Optional<String> getCustomParameterNameOptional() {
		return _portletPreferencesHelper.getString(
			CustomBoostPortletPreferences.PREFERENCE_KEY_CUSTOM_PARAMETER_NAME);
	}

	@Override
	public boolean isInvisible() {
		return _portletPreferencesHelper.getBoolean(
			CustomBoostPortletPreferences.PREFERENCE_KEY_IS_INVISIBLE, true);
	}

	private final PortletPreferencesHelper _portletPreferencesHelper;

}