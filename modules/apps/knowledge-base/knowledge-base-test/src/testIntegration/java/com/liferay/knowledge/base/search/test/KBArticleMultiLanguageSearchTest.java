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

package com.liferay.knowledge.base.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchEngineHelper;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.search.test.util.IndexedFieldsFixture;
import com.liferay.portal.search.test.util.IndexerFixture;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.users.admin.test.util.search.UserSearchFixture;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Luan Maoski
 */
@RunWith(Arquillian.class)
public class KBArticleMultiLanguageSearchTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		setUpKBArticleIndexerFixture();

		_defaultLocale = LocaleThreadLocal.getDefaultLocale();
	}

	@After
	public void tearDown() {
		LocaleThreadLocal.setDefaultLocale(_defaultLocale);
	}

	@Test
	public void testChineseSubject() throws Exception {
		_testLocaleKeywords(LocaleUtil.CHINA, "你好");
	}

	@Test
	public void testEnglishSubject() throws Exception {
		_testLocaleKeywords(LocaleUtil.US, "firstName");
	}

	@Test
	public void testJapaneseSubject() throws Exception {
		_testLocaleKeywords(LocaleUtil.JAPAN, "東京");
	}

	protected void assertFieldValues(
		String prefix, Locale locale, Map<String, String> map,
		String searchTerm) {

		Document document = kbArticleIndexerFixture.searchOnlyOne(
			_user.getUserId(), searchTerm, locale);

		FieldValuesAssert.assertFieldValues(map, prefix, document, searchTerm);
	}

	protected void setTestLocale(Locale locale) throws Exception {
		kbArticleFixture.updateDisplaySettings(locale);

		LocaleThreadLocal.setDefaultLocale(locale);
	}

	protected void setUpIndexedFieldsFixture() {
		indexedFieldsFixture = new IndexedFieldsFixture(
			resourcePermissionLocalService, searchEngineHelper);
	}

	protected void setUpKBArticleFixture() {
		kbArticleFixture = new KBArticleFixture(_group, _user);

		_kbArticles = kbArticleFixture.getKbArticles();
	}

	protected void setUpKBArticleIndexerFixture() {
		kbArticleIndexerFixture = new IndexerFixture<>(KBArticle.class);
	}

	protected void setUpUserSearchFixture(
			String firstName, String lastName, Locale locale)
		throws Exception {

		userSearchFixture = new UserSearchFixture();

		userSearchFixture.setUp();

		_group = userSearchFixture.addGroup();

		_groups = userSearchFixture.getGroups();

		_user = userSearchFixture.addUser(
			RandomTestUtil.randomString(), firstName, lastName, locale, _group);

		_users = userSearchFixture.getUsers();
	}

	protected IndexedFieldsFixture indexedFieldsFixture;
	protected KBArticleFixture kbArticleFixture;
	protected IndexerFixture<KBArticle> kbArticleIndexerFixture;

	@Inject
	protected ResourcePermissionLocalService resourcePermissionLocalService;

	@Inject
	protected SearchEngineHelper searchEngineHelper;

	protected UserSearchFixture userSearchFixture;

	private Map<String, String> _getResultMap(KBArticle kbArticle) {
		return new HashMap<String, String>() {
			{
				put(
					Field.ENTRY_CLASS_PK,
					String.valueOf(kbArticle.getResourcePrimKey()));
			}
		};
	}

	private void _testLocaleKeywords(Locale locale, String keywords)
		throws Exception {

		setUpUserSearchFixture(keywords, _LAST_NAME, locale);

		setUpKBArticleFixture();

		setTestLocale(locale);

		String url = "https://www.liferay.com/pt/home";

		KBArticle kbArticle = kbArticleFixture.createKBArticle(url);

		assertFieldValues(
			Field.ENTRY_CLASS_PK, locale, _getResultMap(kbArticle), keywords);
	}

	private static final String _LAST_NAME = "lastName";

	private Locale _defaultLocale;
	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups;

	@DeleteAfterTestRun
	private List<KBArticle> _kbArticles;

	private User _user;

	@DeleteAfterTestRun
	private List<User> _users;

}