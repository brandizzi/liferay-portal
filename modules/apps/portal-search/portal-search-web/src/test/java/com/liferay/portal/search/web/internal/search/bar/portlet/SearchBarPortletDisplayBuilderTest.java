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

package com.liferay.portal.search.web.internal.search.bar.portlet;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.search.web.internal.display.context.SearchScopePreference;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author Adam Brandizzi
 */
public class SearchBarPortletDisplayBuilderTest {

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		setUpHttp();
		setUpThemeDisplay();
	}

	@Test
	public void testGetSearchURLWithoutSearchLayoutAvailable() {
		SearchBarPortletDisplayBuilder searchBarPortletDisplayBuilder =
			createSearchBarPortletDisplayBuilder();

		searchBarPortletDisplayBuilder.setSearchLayoutAvailable(false);
		searchBarPortletDisplayBuilder.setSearchLayoutURL("/web/guest/search");

		SearchBarPortletDisplayContext searchBarPortletDisplayContext =
			searchBarPortletDisplayBuilder.build();

		Assert.assertEquals(
			"/web/guest/home", searchBarPortletDisplayContext.getSearchURL());
	}

	@Test
	public void testGetSearchURLWithSearchLayoutAvailable() {
		SearchBarPortletDisplayBuilder searchBarPortletDisplayBuilder =
			createSearchBarPortletDisplayBuilder();

		searchBarPortletDisplayBuilder.setSearchLayoutAvailable(true);
		searchBarPortletDisplayBuilder.setSearchLayoutURL("/web/guest/search");

		SearchBarPortletDisplayContext searchBarPortletDisplayContext =
			searchBarPortletDisplayBuilder.build();

		Assert.assertEquals(
			"/web/guest/search", searchBarPortletDisplayContext.getSearchURL());
	}

	@Test
	public void testIsDestinationConfiguredHasDestinationNoPage() {
		SearchBarPortletDisplayBuilder searchBarPortletDisplayBuilder =
			createSearchBarPortletDisplayBuilder();

		searchBarPortletDisplayBuilder.setDestination("/search");
		searchBarPortletDisplayBuilder.setSearchLayoutAvailable(false);

		SearchBarPortletDisplayContext searchBarPortletDisplayContext =
			searchBarPortletDisplayBuilder.build();

		Assert.assertTrue(
			searchBarPortletDisplayContext.isDestinationConfigured());
	}

	@Test
	public void testIsDestinationConfiguredHasDestinationPage() {
		SearchBarPortletDisplayBuilder searchBarPortletDisplayBuilder =
			createSearchBarPortletDisplayBuilder();

		searchBarPortletDisplayBuilder.setDestination("/search");
		searchBarPortletDisplayBuilder.setSearchLayoutAvailable(true);

		SearchBarPortletDisplayContext searchBarPortletDisplayContext =
			searchBarPortletDisplayBuilder.build();

		Assert.assertFalse(
			searchBarPortletDisplayContext.isDestinationConfigured());
	}

	@Test
	public void testIsDestinationConfiguredHasEmptyDestination() {
		SearchBarPortletDisplayBuilder searchBarPortletDisplayBuilder =
			createSearchBarPortletDisplayBuilder();

		searchBarPortletDisplayBuilder.setDestination(StringPool.BLANK);

		SearchBarPortletDisplayContext searchBarPortletDisplayContext =
			searchBarPortletDisplayBuilder.build();

		Assert.assertFalse(
			searchBarPortletDisplayContext.isDestinationConfigured());
	}

	@Test
	public void testIsDestinationConfiguredHasNullDestination() {
		SearchBarPortletDisplayBuilder searchBarPortletDisplayBuilder =
			createSearchBarPortletDisplayBuilder();

		searchBarPortletDisplayBuilder.setDestination(null);

		SearchBarPortletDisplayContext searchBarPortletDisplayContext =
			searchBarPortletDisplayBuilder.build();

		Assert.assertFalse(
			searchBarPortletDisplayContext.isDestinationConfigured());
	}

	protected SearchBarPortletDisplayBuilder
		createSearchBarPortletDisplayBuilder() {

		SearchBarPortletDisplayBuilder displayBuilder =
			new SearchBarPortletDisplayBuilder(_http);

		displayBuilder.setSearchScopePreference(
			SearchScopePreference.EVERYTHING);
		displayBuilder.setThemeDisplay(_themeDisplay);

		return displayBuilder;
	}

	protected void setUpHttp() {
		Mockito.doAnswer(
			invocation -> {
				String url = invocation.getArgumentAt(0, String.class);

				url = url.replaceAll("https?://", "");

				int slashIndex = url.indexOf(StringPool.SLASH);
				int questionIndex = url.indexOf(StringPool.QUESTION);

				if (questionIndex > 0) {
					return url.substring(slashIndex, questionIndex);
				}

				return url.substring(slashIndex);
			}
		).when(
			_http
		).getPath(
			Mockito.anyString()
		);
	}

	protected void setUpThemeDisplay() {
		Mockito.when(
			_themeDisplay.getURLCurrent()
		).thenReturn(
			"http://example.com/web/guest/home?param=arg"
		);

		Mockito.when(
			_themeDisplay.getScopeGroup()
		).thenReturn(
			_group
		);
	}

	@Mock
	private Group _group;

	@Mock
	private Http _http;

	@Mock
	private ThemeDisplay _themeDisplay;

}