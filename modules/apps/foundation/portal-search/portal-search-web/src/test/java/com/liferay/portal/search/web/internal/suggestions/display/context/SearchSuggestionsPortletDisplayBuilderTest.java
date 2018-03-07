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

package com.liferay.portal.search.web.internal.suggestions.display.context;

import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.search.web.internal.suggestions.display.QuerySuggestion;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Adam Brandizzi
 */
public class SearchSuggestionsPortletDisplayBuilderTest {

	@Test
	public void testGetRelatedSuggestions() {
		_displayBuilder = new SearchSuggestionsPortletDisplayBuilder();

		List<QuerySuggestion> querySuggestions = createQuerySuggestions();

		_displayBuilder.setRelatedSuggestions(querySuggestions);

		_displayContext = _displayBuilder.build();

		List<QuerySuggestion> relatedSuggestions =
			_displayContext.getRelatedSuggestions();

		Assert.assertFalse(relatedSuggestions.isEmpty());

		assertSameQuerySuggestion(
			querySuggestions.get(0), relatedSuggestions.get(0));
	}

	@Test
	public void testGetRelatedSuggestionsEmptyByDefault() {
		_displayBuilder = new SearchSuggestionsPortletDisplayBuilder();

		_displayContext = _displayBuilder.build();

		List<QuerySuggestion> relatedSuggestions =
			_displayContext.getRelatedSuggestions();

		Assert.assertTrue(relatedSuggestions.isEmpty());
	}

	@Test
	public void testGetSpellCheckSuggestion() {
		_displayBuilder = new SearchSuggestionsPortletDisplayBuilder();

		QuerySuggestion querySuggestion = createQuerySuggestion();

		_displayBuilder.setSpellCheckSuggestion(querySuggestion);

		_displayContext = _displayBuilder.build();

		assertSameQuerySuggestion(
			querySuggestion, _displayContext.getSpellCheckSuggestion());
	}

	@Test
	public void testGetSpellCheckSuggestionsNullByDefault() {
		_displayBuilder = new SearchSuggestionsPortletDisplayBuilder();

		_displayContext = _displayBuilder.build();

		Assert.assertNull(_displayContext.getSpellCheckSuggestion());
	}

	@Test
	public void testHasRelatedSuggestionsFalseByDefault() {
		_displayBuilder = new SearchSuggestionsPortletDisplayBuilder();

		_displayContext = _displayBuilder.build();

		Assert.assertFalse(_displayContext.hasRelatedSuggestions());
	}

	@Test
	public void testHasRelatedSuggestionsFalseWithDisabledAndNonEmptyList() {
		_displayBuilder = new SearchSuggestionsPortletDisplayBuilder();

		setRelatedSuggestions(createQuerySuggestions(), false);

		_displayContext = _displayBuilder.build();

		Assert.assertFalse(_displayContext.hasRelatedSuggestions());
	}

	@Test
	public void testHasRelatedSuggestionsFalseWithEnabledAndEmptyList() {
		_displayBuilder = new SearchSuggestionsPortletDisplayBuilder();

		setRelatedSuggestions(Collections.emptyList(), true);

		_displayContext = _displayBuilder.build();

		Assert.assertFalse(_displayContext.hasRelatedSuggestions());
	}

	@Test
	public void testHasRelatedSuggestionsTrueWithEnabledAndNonEmptyList() {
		_displayBuilder = new SearchSuggestionsPortletDisplayBuilder();

		setRelatedSuggestions(createQuerySuggestions(), true);

		_displayContext = _displayBuilder.build();

		Assert.assertTrue(_displayContext.hasRelatedSuggestions());
	}

	@Test
	public void testHasSpellCheckSuggestionFalseByDefault() {
		_displayBuilder = new SearchSuggestionsPortletDisplayBuilder();

		_displayContext = _displayBuilder.build();

		Assert.assertFalse(_displayContext.hasSpellCheckSuggestion());
	}

	@Test
	public void testHasSpellCheckSuggestionsFalseWithDisabledAndSuggestion() {
		_displayBuilder = new SearchSuggestionsPortletDisplayBuilder();

		setSpellCheckSuggestions(createQuerySuggestion(), false);

		_displayContext = _displayBuilder.build();

		Assert.assertFalse(_displayContext.hasSpellCheckSuggestion());
	}

	@Test
	public void testHasSpellCheckSuggestionsFalseWithEnabledAndNull() {
		_displayBuilder = new SearchSuggestionsPortletDisplayBuilder();

		setSpellCheckSuggestions(null, true);

		_displayContext = _displayBuilder.build();

		Assert.assertFalse(_displayContext.hasSpellCheckSuggestion());
	}

	@Test
	public void testHasSpellCheckSuggestionsTrueWithEnabledAndNonEmptyList() {
		_displayBuilder = new SearchSuggestionsPortletDisplayBuilder();

		setSpellCheckSuggestions(createQuerySuggestion(), true);

		_displayContext = _displayBuilder.build();

		Assert.assertTrue(_displayContext.hasSpellCheckSuggestion());
	}

	@Test
	public void testIsRelatedSuggestions() {
		_displayBuilder = new SearchSuggestionsPortletDisplayBuilder();

		_displayBuilder.setRelatedSuggestionsEnabled(true);

		_displayContext = _displayBuilder.build();

		Assert.assertTrue(_displayContext.isRelatedSuggestionsEnabled());
	}

	@Test
	public void testIsRelatedSuggestionsFalseByDefault() {
		_displayBuilder = new SearchSuggestionsPortletDisplayBuilder();

		_displayContext = _displayBuilder.build();

		Assert.assertFalse(_displayContext.isRelatedSuggestionsEnabled());
	}

	@Test
	public void testIsSpellCheckSuggestion() {
		_displayBuilder = new SearchSuggestionsPortletDisplayBuilder();

		_displayBuilder.setSpellCheckSuggestionEnabled(true);

		_displayContext = _displayBuilder.build();

		Assert.assertTrue(_displayContext.isSpellCheckSuggestionEnabled());
	}

	@Test
	public void testIsSpellCheckSuggestionFalseByDefault() {
		_displayBuilder = new SearchSuggestionsPortletDisplayBuilder();

		_displayContext = _displayBuilder.build();

		Assert.assertFalse(_displayContext.isSpellCheckSuggestionEnabled());
	}

	protected void assertSameQuerySuggestion(
		QuerySuggestion suggestion1, QuerySuggestion suggestion2) {

		Assert.assertNotNull(suggestion1);
		Assert.assertNotNull(suggestion2);

		Assert.assertEquals(
			suggestion1.getFormattedQuery(), suggestion2.getFormattedQuery());
		Assert.assertEquals(
			suggestion1.getSearchURL(), suggestion2.getSearchURL());
	}

	protected QuerySuggestion createQuerySuggestion() {
		QuerySuggestion querySuggstion = new QuerySuggestion(
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		return querySuggstion;
	}

	protected List<QuerySuggestion> createQuerySuggestions() {
		return Arrays.asList(createQuerySuggestion());
	}

	protected void setRelatedSuggestions(
		List<QuerySuggestion> relatedSuggestions,
		boolean relatedSuggestionsEnabled) {

		_displayBuilder.setRelatedSuggestions(relatedSuggestions);
		_displayBuilder.setRelatedSuggestionsEnabled(relatedSuggestionsEnabled);
	}

	protected void setSpellCheckSuggestions(
		QuerySuggestion spellCheckSuggestion,
		boolean spellCheckSuggestionsEnabled) {

		_displayBuilder.setSpellCheckSuggestion(spellCheckSuggestion);
		_displayBuilder.setSpellCheckSuggestionEnabled(
			spellCheckSuggestionsEnabled);
	}

	private SearchSuggestionsPortletDisplayBuilder _displayBuilder;
	private SearchSuggestionsPortletDisplayContext _displayContext;

}