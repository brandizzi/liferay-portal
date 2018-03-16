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

package com.liferay.portal.search.web.internal.suggestions.display;

import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.search.web.internal.suggestions.QuerySuggestionFactory;
import com.liferay.portal.util.HtmlImpl;
import com.liferay.portal.util.HttpImpl;
import com.liferay.portal.util.PortalImpl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Adam Brandizzi
 */
public class QuerySuggestionFactoryTest {

	@BeforeClass
	public static void setUpClass() {
		new HtmlUtil().setHtml(new HtmlImpl());
		new HttpUtil().setHttp(new HttpImpl());
		new PortalUtil().setPortal(new PortalImpl());
	}

	@Test
	public void testGetRelatedSuggestions() {
		QuerySuggestionFactory querySuggestionFactory =
			new QuerySuggestionFactory(
				"http://localhost:8080/?q=a+b", "q", "a b", "a b",
				Arrays.asList("a C", "a b C"));

		List<QuerySuggestion> relatedSuggestions =
			querySuggestionFactory.getRelatedSuggestions();

		Assert.assertEquals(
			relatedSuggestions.toString(), 2, relatedSuggestions.size());

		assertFormattedQuery(
			"<span class=\"unchanged-keyword\">a</span> <span " +
				"class=\"changed-keyword\">C</span>",
			relatedSuggestions.get(0));
		assertSearchURL(
			"http://localhost:8080/?q=a+C", relatedSuggestions.get(0));

		assertFormattedQuery(
			"<span class=\"unchanged-keyword\">a</span> <span " +
				"class=\"unchanged-keyword\">b</span> <span " +
					"class=\"changed-keyword\">C</span>",
			relatedSuggestions.get(1));
		assertSearchURL(
			"http://localhost:8080/?q=a+b+C", relatedSuggestions.get(1));
	}

	@Test
	public void testGetRelatedSuggestionsWithEmptyList() {
		QuerySuggestionFactory querySuggestionFactory =
			new QuerySuggestionFactory(
				"http://localhost:8080/?q=a+b", "q", "a b", "a b",
				Collections.emptyList());

		List<QuerySuggestion> relatedSuggestions =
			querySuggestionFactory.getRelatedSuggestions();

		Assert.assertTrue(relatedSuggestions.isEmpty());
	}

	@Test
	public void testGetRelatedSuggestionsWithKeywordsAsSuggestions() {
		QuerySuggestionFactory querySuggestionFactory =
			new QuerySuggestionFactory(
				"http://localhost:8080/?q=a+b", "q", "a b", "a b",
				Arrays.asList("a b", "a b C"));

		List<QuerySuggestion> relatedSuggestions =
			querySuggestionFactory.getRelatedSuggestions();

		Assert.assertEquals(
			relatedSuggestions.toString(), 1, relatedSuggestions.size());

		assertFormattedQuery(
			"<span class=\"unchanged-keyword\">a</span> <span " +
				"class=\"unchanged-keyword\">b</span> <span " +
					"class=\"changed-keyword\">C</span>",
			relatedSuggestions.get(0));
		assertSearchURL(
			"http://localhost:8080/?q=a+b+C", relatedSuggestions.get(0));
	}

	@Test
	public void testGetRelatedSuggestionsWithNullSuggestions() {
		QuerySuggestionFactory querySuggestionFactory =
			new QuerySuggestionFactory(
				"http://localhost:8080/?q=a+b", "q", "a b", "a b",
				Arrays.asList(null, "", "a b C"));

		List<QuerySuggestion> relatedSuggestions =
			querySuggestionFactory.getRelatedSuggestions();

		Assert.assertEquals(
			relatedSuggestions.toString(), 1, relatedSuggestions.size());

		assertFormattedQuery(
			"<span class=\"unchanged-keyword\">a</span> <span " +
				"class=\"unchanged-keyword\">b</span> <span " +
					"class=\"changed-keyword\">C</span>",
			relatedSuggestions.get(0));
		assertSearchURL(
			"http://localhost:8080/?q=a+b+C", relatedSuggestions.get(0));
	}

	@Test
	public void testGetSpellCheckSuggestion() {
		QuerySuggestionFactory querySuggestionFactory =
			new QuerySuggestionFactory(
				"http://localhost:8080/?q=a+b", "q", "a b", "a C",
				Collections.emptyList());

		QuerySuggestion spellCheckSuggestion =
			querySuggestionFactory.getSpellCheckSuggestion();

		assertFormattedQuery(
			"<span class=\"unchanged-keyword\">a</span> <span " +
				"class=\"changed-keyword\">C</span>",
			spellCheckSuggestion);
		assertSearchURL("http://localhost:8080/?q=a+C", spellCheckSuggestion);
	}

	@Test
	public void testGetSpellCheckSuggestionEqualsToKeywords() {
		QuerySuggestionFactory querySuggestionFactory =
			new QuerySuggestionFactory(
				"http://localhost:8080/?q=a+b", "q", "a b", "a b",
				Collections.emptyList());

		Assert.assertNull(querySuggestionFactory.getSpellCheckSuggestion());
	}

	@Test
	public void testGetSpellCheckSuggestionWithEmptySuggestions() {
		QuerySuggestionFactory querySuggestionFactory =
			new QuerySuggestionFactory(
				"http://localhost:8080/?q=a+b", "q", "a b", "",
				Collections.emptyList());

		Assert.assertNull(querySuggestionFactory.getSpellCheckSuggestion());
	}

	@Test
	public void testGetSpellCheckSuggestionWithNullSuggestions() {
		QuerySuggestionFactory querySuggestionFactory =
			new QuerySuggestionFactory(
				"http://localhost:8080/?q=a+b", "q", "a b", null,
				Collections.emptyList());

		Assert.assertNull(querySuggestionFactory.getSpellCheckSuggestion());
	}

	protected void assertFormattedQuery(
		String formattedQuery, QuerySuggestion querySuggestio) {

		Assert.assertEquals(formattedQuery, querySuggestio.getFormattedQuery());
	}

	protected void assertSearchURL(
		String searchURL, QuerySuggestion querySuggestion) {

		Assert.assertEquals(searchURL, querySuggestion.getSearchURL());
	}

}