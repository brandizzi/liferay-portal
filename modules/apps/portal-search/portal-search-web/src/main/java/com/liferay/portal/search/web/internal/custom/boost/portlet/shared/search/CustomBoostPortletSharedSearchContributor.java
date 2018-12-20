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

package com.liferay.portal.search.web.internal.custom.boost.portlet.shared.search;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.search.custom.relevance.CustomRelevance;
import com.liferay.portal.search.web.internal.custom.boost.constants.CustomBoostPortletKeys;
import com.liferay.portal.search.web.internal.custom.boost.portlet.CustomBoostPortletPreferences;
import com.liferay.portal.search.web.internal.custom.boost.portlet.CustomBoostPortletPreferencesImpl;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchContributor;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;

/**
 * @author Igor Nazar
 * @author Luan Maoski
 */
@Component(
	immediate = true,
	property = "javax.portlet.name=" + CustomBoostPortletKeys.CUSTOM_BOOST,
	service = PortletSharedSearchContributor.class
)
public class CustomBoostPortletSharedSearchContributor
	implements PortletSharedSearchContributor {

	@Override
	public void contribute(
		PortletSharedSearchSettings portletSharedSearchSettings) {

		CustomBoostPortletPreferences customBoostPortletPreferences =
			new CustomBoostPortletPreferencesImpl(
				portletSharedSearchSettings.getPortletPreferences());

		Optional<String> boostFieldOptional =
			customBoostPortletPreferences.getBoostFieldOptional();

		final String boostValueOptional = getParameterValue(
			portletSharedSearchSettings, customBoostPortletPreferences);

		String boostIncrement =
			customBoostPortletPreferences.getBoostIncrement();

		boostFieldOptional.ifPresent(
			boostField -> {
				buildBoost(
					boostField, boostValueOptional, boostIncrement,
					customBoostPortletPreferences, portletSharedSearchSettings);
			});
	}

	@SuppressWarnings("unchecked")
	protected void buildBoost(
		String boostField, String boostValue, String boostIncrement,
		CustomBoostPortletPreferences customBoostPortletPreferences,
		PortletSharedSearchSettings portletSharedSearchSettings) {

		SearchContext searchContext =
			portletSharedSearchSettings.getSearchContext();

		ArrayList<CustomRelevance> customBoostList =
			(ArrayList<CustomRelevance>)searchContext.getAttribute(
				"customBoostWidget");

		if (customBoostList == null) {
			customBoostList = new ArrayList<>();

			searchContext.setAttribute("customBoostWidget", customBoostList);
		}

		List<String> values = ListUtil.fromArray(boostValue.split(","));

		Float increment = Float.valueOf(boostIncrement);

		CustomRelevance customRelevance = new CustomRelevance(
			values, increment, boostField);

		customBoostList.add(customRelevance);
	}

	protected <T> void copy(Supplier<Optional<T>> from, Consumer<T> to) {
		Optional<T> optional = from.get();

		optional.ifPresent(to);
	}

	protected String getBoostField(
		CustomBoostPortletPreferences customBoostPortletPreferences,
		String portletId) {

		return customBoostPortletPreferences.getBoostFieldString() +
			StringPool.PERIOD + portletId;
	}

	protected Optional<String> getCustomParameterValue(
		PortletSharedSearchSettings portletSharedSearchSettings,
		CustomBoostPortletPreferences customBoostPortletPreferences) {

		String parameterName = getParameterName(customBoostPortletPreferences);

		Optional<String> parameterValue =
			portletSharedSearchSettings.getParameter(parameterName);

		return parameterValue;
	}

	protected String getParameterName(
		CustomBoostPortletPreferences customBoostPortletPreferences) {

		Optional<String> optional = Stream.of(
			customBoostPortletPreferences.getBoostFieldOptional()
		).filter(
			Optional::isPresent
		).map(
			Optional::get
		).findFirst();

		return optional.orElse("customfield");
	}

	protected String getParameterValue(
		PortletSharedSearchSettings portletSharedSearchSettings,
		CustomBoostPortletPreferences customBoostPortletPreferences) {

		Optional<String> parameterValue = getCustomParameterValue(
			portletSharedSearchSettings, customBoostPortletPreferences);

		Optional<String> optional = Stream.of(
			parameterValue,
			customBoostPortletPreferences.getBoostValuesOptional()
		).filter(
			Optional::isPresent
		).map(
			Optional::get
		).findFirst();

		return optional.orElse("");
	}

}