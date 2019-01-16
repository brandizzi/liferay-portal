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

package com.liferay.journal.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.journal.model.JournalFolder;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
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
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.search.test.util.IndexedFieldsFixture;
import com.liferay.portal.search.test.util.IndexerFixture;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.users.admin.test.util.search.UserSearchFixture;

import java.util.ArrayList;
import java.util.Arrays;
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
 * @author Vagner B.C
 */
@RunWith(Arquillian.class)
public class JournalFolderIndexerIndexedFieldsTest {

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
		setUpJournalFolderIndexerFixture();
		setUpJournalFolderFixture();
	}

	@Test
	public void testIndexedFields() throws Exception {
		JournalFolder journalFolder =
			journalFolderFixture.createJournalFolder();

		String searchTerm = journalFolder.getName();

		Document document = journalFolderIndexerFixture.searchOnlyOne(
			searchTerm);

		indexedFieldsFixture.postProcessDocument(document);

		FieldValuesAssert.assertFieldValues(
			_expectedFieldValues(journalFolder), document, searchTerm);
	}

	protected void setUpIndexedFieldsFixture() {
		indexedFieldsFixture = new IndexedFieldsFixture(
			resourcePermissionLocalService, searchEngineHelper);
	}

	protected void setUpJournalFolderFixture() {
		journalFolderFixture = new JournalFolderFixture(group);

		_journalFolders = journalFolderFixture.getJournalFolders();
	}

	protected void setUpJournalFolderIndexerFixture() {
		journalFolderIndexerFixture = new IndexerFixture<>(JournalFolder.class);
	}

	protected void setUpUserSearchFixture() throws Exception {
		userSearchFixture = new UserSearchFixture();

		userSearchFixture.setUp();

		_groups = userSearchFixture.getGroups();
		_users = userSearchFixture.getUsers();

		group = userSearchFixture.addGroup();
	}

	protected Group group;
	protected IndexedFieldsFixture indexedFieldsFixture;
	protected JournalFolderFixture journalFolderFixture;
	protected IndexerFixture<JournalFolder> journalFolderIndexerFixture;

	@Inject
	protected ResourcePermissionLocalService resourcePermissionLocalService;

	@Inject
	protected SearchEngineHelper searchEngineHelper;

	protected UserSearchFixture userSearchFixture;

	private Map<String, String> _expectedFieldValues(
			JournalFolder journalFolder)
		throws Exception {

		Map<String, String> map = new HashMap<>();

		map.put(Field.COMPANY_ID, String.valueOf(journalFolder.getCompanyId()));
		map.put(Field.ENTRY_CLASS_NAME, JournalFolder.class.getName());
		map.put(
			Field.ENTRY_CLASS_PK,
			String.valueOf(journalFolder.getPrimaryKey()));
		map.put(
			Field.FOLDER_ID, String.valueOf(journalFolder.getParentFolderId()));
		map.put(Field.GROUP_ID, String.valueOf(journalFolder.getGroupId()));
		map.put(
			Field.SCOPE_GROUP_ID, String.valueOf(journalFolder.getGroupId()));
		map.put(Field.STAGING_GROUP, "false");
		map.put(Field.STATUS, String.valueOf(journalFolder.getStatus()));
		map.put(Field.USER_ID, String.valueOf(journalFolder.getUserId()));
		map.put(
			Field.USER_NAME,
			StringUtil.toLowerCase(journalFolder.getUserName()));

		map.put("viewCount", "0");
		map.put("viewCount" + _SORTABLE, "0");
		map.put("visible", "true");

		_populateDates(journalFolder, map);
		_populateLocalizedTitles(journalFolder, map);
		_populateRoles(journalFolder, map);
		_populateTitleAndDescription(journalFolder, map);
		_populateTreePath(journalFolder, map);

		indexedFieldsFixture.populatePriority("0.0", map);
		indexedFieldsFixture.populateUID(
			JournalFolder.class.getName(), journalFolder.getPrimaryKey(), map);

		return map;
	}

	private void _populateDates(
		JournalFolder journalFolder, Map<String, String> map) {

		indexedFieldsFixture.populateDate(
			Field.CREATE_DATE, journalFolder.getCreateDate(), map);
		indexedFieldsFixture.populateDate(
			Field.MODIFIED_DATE, journalFolder.getModifiedDate(), map);
		indexedFieldsFixture.populateDate(
			Field.PUBLISH_DATE, journalFolder.getCreateDate(), map);
		indexedFieldsFixture.populateExpirationDateWithForever(map);
	}

	private void _populateLocalizedTitles(
		JournalFolder journalFolder, Map<String, String> map) {

		String title = StringUtil.lowerCase(journalFolder.getName());

		map.put("localized_title", title);

		for (Locale locale : LanguageUtil.getAvailableLocales()) {
			String languageId = LocaleUtil.toLanguageId(locale);

			String key = "localized_title_" + languageId;

			map.put(key, title);
			map.put(key.concat(_SORTABLE), title);
		}
	}

	private void _populateRoles(
			JournalFolder journalFolder, Map<String, String> map)
		throws Exception {

		indexedFieldsFixture.populateRoleIdFields(
			journalFolder.getCompanyId(), JournalFolder.class.getName(),
			journalFolder.getPrimaryKey(), journalFolder.getGroupId(), null,
			map);
	}

	private void _populateTitleAndDescription(
		JournalFolder journalFolder, Map<String, String> map) {

		for (Locale locale :
				LanguageUtil.getAvailableLocales(journalFolder.getGroupId())) {

			String languageId = LocaleUtil.toLanguageId(locale);

			map.put(
				LocalizationUtil.getLocalizedName(
					Field.DESCRIPTION, languageId),
				journalFolder.getDescription());

			map.put(
				LocalizationUtil.getLocalizedName(Field.TITLE, languageId),
				journalFolder.getName());
		}
	}

	private void _populateTreePath(
		JournalFolder journalFolder, Map<String, String> map) {

		List<String> treePathValues = new ArrayList<>(
			Arrays.asList(
				StringUtil.split(journalFolder.getTreePath(), CharPool.SLASH)));

		if (treePathValues.size() == 1) {
			map.put(Field.TREE_PATH, treePathValues.get(0));
		}
		else if (treePathValues.size() > 1) {
			map.put(Field.TREE_PATH, treePathValues.toString());
		}
	}

	private static final String _SORTABLE =
		StringPool.UNDERLINE + Field.SORTABLE_FIELD_SUFFIX;

	@DeleteAfterTestRun
	private List<Group> _groups;

	@DeleteAfterTestRun
	private List<JournalFolder> _journalFolders;

	@DeleteAfterTestRun
	private List<User> _users;

}