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

package com.liferay.dynamic.data.lists.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.lists.model.DDLRecord;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.lists.service.DDLRecordSetLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.test.util.FieldValuesAssert;
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
 * @author Vagner B.C
 */
@RunWith(Arquillian.class)
public class DDLRecordMultiLanguageSearchTest {

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

		setUpDDLRecordFixture();

		setUpIndexerFixture();

		defaultLocale = LocaleThreadLocal.getDefaultLocale();
	}

	@After
	public void tearDown() {
		LocaleThreadLocal.setDefaultLocale(defaultLocale);
	}

	@Test
	public void testChineseDescription() throws Exception {
		_testLocaleField(LocaleUtil.CHINA, _DESCRIPTION, "你好");
	}

	@Test
	public void testChineseName() throws Exception {
		_testLocaleField(LocaleUtil.CHINA, _NAME, "你好");
	}

	@Test
	public void testEnglishDescription() throws Exception {
		_testLocaleField(
			LocaleUtil.US, _DESCRIPTION,
			StringUtil.toLowerCase(RandomTestUtil.randomString()));
	}

	@Test
	public void testEnglishName() throws Exception {
		_testLocaleField(
			LocaleUtil.US, _NAME,
			StringUtil.toLowerCase(RandomTestUtil.randomString()));
	}

	@Test
	public void testJapaneseDescription() throws Exception {
		_testLocaleField(LocaleUtil.JAPAN, _DESCRIPTION, "新規");
	}

	@Test
	public void testJapaneseName() throws Exception {
		_testLocaleField(LocaleUtil.JAPAN, _NAME, "新規");
	}

	protected void assertFieldValues(
		String prefix, Locale locale, Map<String, String> map,
		String searchTerm) {

		Document document = ddlRecordIndexerFixture.searchOnlyOne(
			searchTerm, locale);

		FieldValuesAssert.assertFieldValues(map, prefix, document, searchTerm);
	}

	protected String getPrefix(String type, DDLRecord ddlRecord)
		throws PortalException {

		DDLRecordSet ddlRecordSet = ddlRecordFixture.getDDLRecordSet(
			ddlRecord.getRecordSetId());

		if (type == _NAME) {
			return _DDM_KEYWORD + ddlRecordSet.getDDMStructureId() +
				StringPool.UNDERLINE + StringPool.UNDERLINE + _NAME +
					StringPool.UNDERLINE;
		}

		return _DDM_TEXT + ddlRecordSet.getDDMStructureId() +
			StringPool.UNDERLINE + StringPool.UNDERLINE + _DESCRIPTION +
				StringPool.UNDERLINE;
	}

	protected Map<String, String> getResultMap(
		Locale locale, String searchTerm, String prefix) {

		return new HashMap<String, String>() {
			{
				put(prefix + LocaleUtil.toLanguageId(locale), searchTerm);
				put(
					prefix + LocaleUtil.toLanguageId(locale) + _STRING_SORTABLE,
					searchTerm);
			}
		};
	}

	protected void setTestLocale(Locale locale) throws Exception {
		ddlRecordFixture.updateDisplaySettings(locale);

		LocaleThreadLocal.setDefaultLocale(locale);
	}

	protected void setUpDDLRecordFixture() throws Exception {
		ddlRecordFixture = new DDLRecordFixture(
			ddlRecordSetLocalService, group, user);

		_ddlRecords = ddlRecordFixture.getDDLRecords();
	}

	protected void setUpIndexerFixture() {
		ddlRecordIndexerFixture = new IndexerFixture<>(DDLRecord.class);
	}

	protected void setUpUserSearchFixture() throws Exception {
		userSearchFixture = new UserSearchFixture();

		userSearchFixture.setUp();

		_groups = userSearchFixture.getGroups();

		_users = userSearchFixture.getUsers();

		group = userSearchFixture.addGroup();

		user = userSearchFixture.addUser(RandomTestUtil.randomString(), group);
	}

	protected DDLRecordFixture ddlRecordFixture;
	protected IndexerFixture<DDLRecord> ddlRecordIndexerFixture;

	@Inject
	protected DDLRecordSetLocalService ddlRecordSetLocalService;

	protected Locale defaultLocale;
	protected Group group;
	protected User user;
	protected UserSearchFixture userSearchFixture;

	private void _testLocaleField(
			Locale locale, String field, String searchTerm)
		throws Exception {

		setTestLocale(locale);

		DDLRecord ddlRecord = ddlRecordFixture.createDDLRecord(
			searchTerm, searchTerm, locale);

		ddlRecordIndexerFixture.searchOnlyOne(searchTerm, locale);

		String prefix = getPrefix(field, ddlRecord);

		Map<String, String> map = getResultMap(locale, searchTerm, prefix);

		assertFieldValues(prefix, locale, map, searchTerm);
	}

	private static final String _DDM_KEYWORD = "ddm__keyword__";

	private static final String _DDM_TEXT = "ddm__text__";

	private static final String _DESCRIPTION = "description";

	private static final String _NAME = "name";

	private static final String _STRING_SORTABLE = "_String_sortable";

	@DeleteAfterTestRun
	private List<DDLRecord> _ddlRecords;

	@DeleteAfterTestRun
	private List<Group> _groups;

	@DeleteAfterTestRun
	private List<User> _users;

}