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

package com.liferay.wiki.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
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
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.search.test.util.IndexedFieldsFixture;
import com.liferay.portal.search.test.util.IndexerFixture;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerTestRule;
import com.liferay.users.admin.test.util.search.UserSearchFixture;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;

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
 */
@RunWith(Arquillian.class)
public class WikiPageIndexerIndexedFieldsTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		setUpUserSearchFixture();
		setUpIndexedFieldsFixture();
		setUpWikiPageIndexerFixture();
		setUpWikiFixture();
	}

	@Test
	public void testIndexedFields() throws Exception {
		WikiPage wikiPage = wikiPageFixture.createWikiPage();

		String searchTerm = wikiPage.getContent();

		Document document = wikiPageIndexerFixture.searchOnlyOne(searchTerm);

		indexedFieldsFixture.postProcessDocument(document);

		Map<String, String> expected = _expectedFieldValues(wikiPage);

		FieldValuesAssert.assertFieldValues(expected, document, searchTerm);
	}

	protected void setUpIndexedFieldsFixture() {
		indexedFieldsFixture = new IndexedFieldsFixture(
			resourcePermissionLocalService, searchEngineHelper);
	}

	protected void setUpUserSearchFixture() throws Exception {
		userSearchFixture = new UserSearchFixture();

		userSearchFixture.setUp();

		_group = userSearchFixture.addGroup();

		_groups = userSearchFixture.getGroups();

		_users = userSearchFixture.getUsers();
	}

	protected void setUpWikiFixture() {
		wikiPageFixture = new WikiFixture(_group);

		_wikiNodes = wikiPageFixture.getWikiNodes();
		_wikiPages = wikiPageFixture.getWikiPages();
	}

	protected void setUpWikiPageIndexerFixture() {
		wikiPageIndexerFixture = new IndexerFixture<>(WikiPage.class);
	}

	protected IndexedFieldsFixture indexedFieldsFixture;

	@Inject
	protected ResourcePermissionLocalService resourcePermissionLocalService;

	@Inject
	protected SearchEngineHelper searchEngineHelper;

	protected UserSearchFixture userSearchFixture;
	protected WikiFixture wikiPageFixture;
	protected IndexerFixture<WikiPage> wikiPageIndexerFixture;

	private Map<String, String> _expectedFieldValues(WikiPage wikiPage)
		throws Exception {

		Map<String, String> map = new HashMap<>();

		map.put(Field.COMPANY_ID, String.valueOf(wikiPage.getCompanyId()));
		map.put(Field.CONTENT, wikiPage.getContent());
		map.put(Field.ENTRY_CLASS_NAME, WikiPage.class.getName());
		map.put(
			Field.ENTRY_CLASS_PK,
			String.valueOf(wikiPage.getResourcePrimKey()));
		map.put(Field.GROUP_ID, String.valueOf(wikiPage.getGroupId()));
		map.put(Field.SCOPE_GROUP_ID, String.valueOf(wikiPage.getGroupId()));
		map.put(Field.STAGING_GROUP, String.valueOf(_group.isStagingGroup()));
		map.put(Field.TITLE, wikiPage.getTitle());
		map.put(
			Field.TITLE + "_sortable",
			StringUtil.lowerCase(wikiPage.getTitle()));
		map.put(Field.STATUS, String.valueOf(wikiPage.getStatus()));
		map.put(Field.USER_ID, String.valueOf(wikiPage.getUserId()));
		map.put(Field.USER_NAME, StringUtil.lowerCase(wikiPage.getUserName()));
		map.put(Field.USER_NAME, StringUtil.lowerCase(wikiPage.getUserName()));

		map.put("nodeId", String.valueOf(wikiPage.getNodeId()));
		map.put(
			"rootEntryClassPK", String.valueOf(wikiPage.getResourcePrimKey()));
		map.put("viewCount", "0");
		map.put("viewCount_sortable", "0");
		map.put("visible", "true");

		indexedFieldsFixture.populatePriority("0.0", map);
		indexedFieldsFixture.populateUID(
			WikiPage.class.getName(), wikiPage.getResourcePrimKey(), map);

		_populateDates(wikiPage, map);
		_populateRoles(wikiPage, map);
		_populateTitle(wikiPage, map);

		return map;
	}

	private void _populateDates(WikiPage wikiPage, Map<String, String> map) {
		indexedFieldsFixture.populateDate(
			Field.MODIFIED_DATE, wikiPage.getModifiedDate(), map);
		indexedFieldsFixture.populateDate(
			Field.CREATE_DATE, wikiPage.getCreateDate(), map);
		indexedFieldsFixture.populateDate(Field.PUBLISH_DATE, new Date(0), map);
		indexedFieldsFixture.populateExpirationDateWithForever(map);
	}

	private void _populateRoles(WikiPage wikiPage, Map<String, String> map)
		throws Exception {

		indexedFieldsFixture.populateRoleIdFields(
			wikiPage.getCompanyId(), WikiPage.class.getName(),
			wikiPage.getResourcePrimKey(), wikiPage.getGroupId(), null, map);
	}

	private void _populateTitle(WikiPage wikiPage, Map<String, String> map) {
		for (Locale locale :
				LanguageUtil.getAvailableLocales(wikiPage.getGroupId())) {

			String title = StringUtil.lowerCase(wikiPage.getTitle());

			String languageId = LocaleUtil.toLanguageId(locale);

			String key = "localized_title_" + languageId;

			map.put("localized_title", title);
			map.put(key, title);
			map.put(key.concat("_sortable"), title);
		}
	}

	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups;

	@DeleteAfterTestRun
	private List<User> _users;

	@DeleteAfterTestRun
	private List<WikiNode> _wikiNodes;

	@DeleteAfterTestRun
	private List<WikiPage> _wikiPages;

}