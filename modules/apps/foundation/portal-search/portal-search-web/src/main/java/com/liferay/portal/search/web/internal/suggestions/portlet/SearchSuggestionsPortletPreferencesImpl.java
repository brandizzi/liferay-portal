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

package com.liferay.portal.search.web.internal.suggestions.portlet;

import com.liferay.portal.search.web.internal.util.PortletPreferencesHelper;

import java.util.Optional;

import javax.portlet.PortletPreferences;

/**
 * @author Adam Brandizzi
 */
public class SearchSuggestionsPortletPreferencesImpl
	implements SearchSuggestionsPortletPreferences {

	public SearchSuggestionsPortletPreferencesImpl(
		Optional<PortletPreferences> portletPreferencesOptional) {

		_portletPreferencesHelper = new PortletPreferencesHelper(
			portletPreferencesOptional);
	}

	@Override
	public int getQueryIndexingThreshold() {
		return _portletPreferencesHelper.getInteger(
			PREFERENCE_KEY_QUERY_INDEXING_THRESHOLD, 50);
	}

	@Override
	public int getRelatedSuggestionsDisplayThreshold() {
		return _portletPreferencesHelper.getInteger(
			PREFERENCE_KEY_RELATED_SUGGESTIONS_DISPLAY_THRESHOLD, 50);
	}

	@Override
	public int getRelatedSuggestionsMax() {
		return _portletPreferencesHelper.getInteger(
			PREFERENCE_KEY_RELATED_SUGGESTIONS_MAX, 10);
	}

	@Override
	public int getSpellCheckSuggestionDisplayThreshold() {
		return _portletPreferencesHelper.getInteger(
			PREFERENCE_KEY_SPELL_CHECK_SUGGESTION_DISPLAY_THRESHOLD, 50);
	}

	@Override
	public boolean isQueryIndexingEnabled() {
		return _portletPreferencesHelper.getBoolean(
			PREFERENCE_KEY_QUERY_INDEXING_ENABLED, false);
	}

	@Override
	public boolean isRelatedSuggestionsEnabled() {
		return _portletPreferencesHelper.getBoolean(
			PREFERENCE_KEY_RELATED_SUGGESTIONS_ENABLED, false);
	}

	@Override
	public boolean isSpellCheckSuggestionEnabled() {
		return _portletPreferencesHelper.getBoolean(
			PREFERENCE_KEY_SPELL_CHECK_SUGGESTION_ENABLED, false);
	}

	private final PortletPreferencesHelper _portletPreferencesHelper;

}