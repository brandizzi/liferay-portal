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
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchEngineHelper;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.search.test.util.IndexedFieldsFixture;
import com.liferay.portal.search.test.util.IndexerFixture;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.users.admin.test.util.search.UserSearchFixture;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Luan Maoski
 * @author Lucas Marques
 */
@RunWith(Arquillian.class)
public class KBArticleIndexerIndexedFieldsTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		setUpUserSearchFixture();

		setUpIndexedFieldsFixture();

		setUpKBArticleFixture();

		setUpKBArticleIndexerFixture();
	}

	@Test
	public void testIndexedFields() throws Exception {
		String url = "https://www.liferay.com/pt/home";

		KBArticle kbArticle = kbArticleFixture.createKBArticle(url);

		String searchTerm = kbArticle.getDescription();

		Document document = kbArticleIndexerFixture.searchOnlyOne(searchTerm);

		indexedFieldsFixture.postProcessDocument(document);

		Map<String, String> expected = _expectedFieldValues(kbArticle);

		FieldValuesAssert.assertFieldValues(expected, document, searchTerm);
	}

	protected void setUpIndexedFieldsFixture() {
		indexedFieldsFixture = new IndexedFieldsFixture(
			resourcePermissionLocalService, searchEngineHelper);
	}

	protected void setUpKBArticleFixture() throws PortalException {
		kbArticleFixture = new KBArticleFixture(
			_group, TestPropsValues.getUser());

		_kbArticles = kbArticleFixture.getKbArticles();
	}

	protected void setUpKBArticleIndexerFixture() {
		kbArticleIndexerFixture = new IndexerFixture<>(KBArticle.class);
	}

	protected void setUpUserSearchFixture() throws Exception {
		userSearchFixture = new UserSearchFixture();

		userSearchFixture.setUp();

		_group = userSearchFixture.addGroup();

		_groups = userSearchFixture.getGroups();

		_user = TestPropsValues.getUser();

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

	private Map<String, String> _expectedFieldValues(KBArticle kbArticle)
		throws Exception {

		Map<String, String> map = new HashMap<>();

		map.put(Field.COMPANY_ID, String.valueOf(kbArticle.getCompanyId()));
		map.put(Field.ENTRY_CLASS_NAME, KBArticle.class.getName());
		map.put(
			Field.ENTRY_CLASS_PK,
			String.valueOf(kbArticle.getRootResourcePrimKey()));
		map.put(Field.FOLDER_ID, String.valueOf(kbArticle.getKbFolderId()));
		map.put(Field.GROUP_ID, String.valueOf(kbArticle.getGroupId()));
		map.put(
			Field.ROOT_ENTRY_CLASS_PK,
			String.valueOf(kbArticle.getRootResourcePrimKey()));
		map.put(Field.SCOPE_GROUP_ID, String.valueOf(kbArticle.getGroupId()));
		map.put(Field.STAGING_GROUP, String.valueOf(_group.isStagingGroup()));
		map.put(Field.STATUS, String.valueOf(kbArticle.getStatus()));
		map.put(Field.USER_ID, String.valueOf(kbArticle.getUserId()));
		map.put(Field.USER_NAME, StringUtil.lowerCase(kbArticle.getUserName()));

		map.put(
			"parentMessageId",
			String.valueOf(kbArticle.getParentResourcePrimKey()));
		map.put(
			"title" + _SORTABLE, StringUtil.lowerCase(kbArticle.getTitle()));
		map.put("titleKeyword", StringUtil.lowerCase(kbArticle.getTitle()));
		map.put("viewCount", "0");
		map.put("viewCount_sortable", "0");
		map.put("visible", "true");

		indexedFieldsFixture.populatePriority("0.0", map);
		indexedFieldsFixture.populateUID(
			KBArticle.class.getName(), kbArticle.getRootResourcePrimKey(), map);

		_populateDates(kbArticle, map);
		_populateLocalizedTitles(kbArticle, map);
		_populateLocalizedValues(kbArticle, map);
		_populateRoles(kbArticle, map);

		return map;
	}

	private void _populateDates(KBArticle kbArticle, Map<String, String> map) {
		indexedFieldsFixture.populateDate(
			Field.CREATE_DATE, kbArticle.getCreateDate(), map);
		indexedFieldsFixture.populateDate(
			Field.MODIFIED_DATE, kbArticle.getModifiedDate(), map);
		indexedFieldsFixture.populateDate(Field.PUBLISH_DATE, new Date(0), map);
		indexedFieldsFixture.populateExpirationDateWithForever(map);
	}

	private void _populateLocalizedTitles(
		KBArticle kbArticle, Map<String, String> map) {

		String title = StringUtil.lowerCase(kbArticle.getTitle());

		map.put("localized_title", title);

		for (Locale locale : LanguageUtil.getAvailableLocales()) {
			String languageId = LocaleUtil.toLanguageId(locale);

			String key = "localized_title_" + languageId;

			map.put(key, title);
			map.put(key.concat(_SORTABLE), title);
		}
	}

	private void _populateLocalizedValues(
			KBArticle kbArticle, Map<String, String> map)
		throws PortalException {

		map.put(Field.CONTENT, kbArticle.getContent());
		map.put(Field.DESCRIPTION, kbArticle.getDescription());
		map.put(Field.TITLE, kbArticle.getTitle());

		for (Locale locale :
				LanguageUtil.getAvailableLocales(kbArticle.getGroupId())) {

			String languageId = LocaleUtil.toLanguageId(locale);

			map.put(
				LocalizationUtil.getLocalizedName(
					Field.DESCRIPTION, languageId),
				kbArticle.getDescription());
			map.put(
				LocalizationUtil.getLocalizedName(Field.CONTENT, languageId),
				kbArticle.getContent());
			map.put(
				LocalizationUtil.getLocalizedName(Field.TITLE, languageId),
				kbArticle.getTitle());
		}
	}

	private void _populateRoles(KBArticle kbArticle, Map<String, String> map)
		throws Exception {

		indexedFieldsFixture.populateRoleIdFields(
			kbArticle.getCompanyId(), KBArticle.class.getName(),
			kbArticle.getRootResourcePrimKey(), kbArticle.getGroupId(), null,
			map);
	}

	private static final String _SORTABLE =
		StringPool.UNDERLINE + Field.SORTABLE_FIELD_SUFFIX;

	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups;

	@DeleteAfterTestRun
	private List<KBArticle> _kbArticles;

	private User _user;

	@DeleteAfterTestRun
	private List<User> _users;

}