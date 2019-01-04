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
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
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
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.search.test.util.IndexedFieldsFixture;
import com.liferay.portal.search.test.util.IndexerFixture;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.users.admin.test.util.search.UserSearchFixture;

import java.util.HashMap;
import java.util.List;
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
public class DDLRecordSetIndexerIndexedFieldsTest {

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

		setUpDDLRecordSetFixture();

		setUpIndexedFieldsFixture();

		setUpIndexerFixture();
	}

	@Test
	public void testIndexedFields() throws Exception {
		DDLRecordSet ddlRecordSet = ddlRecordSetFixture.createDDLRecordSet();

		String searchTerm = user.getFullName();

		Document document = ddlRecordSetIndexerFixture.searchOnlyOne(
			searchTerm);

		indexedFieldsFixture.postProcessDocument(document);

		FieldValuesAssert.assertFieldValues(
			_expectedFieldValues(ddlRecordSet), document, searchTerm);
	}

	protected void setUpDDLRecordSetFixture() throws Exception {
		ddlRecordSetFixture = new DDLRecordSetFixture(group, user);

		_ddlRecordSets = ddlRecordSetFixture.getDDLRecordSets();
	}

	protected void setUpIndexedFieldsFixture() {
		indexedFieldsFixture = new IndexedFieldsFixture(
			resourcePermissionLocalService, searchEngineHelper);
	}

	protected void setUpIndexerFixture() {
		ddlRecordSetIndexerFixture = new IndexerFixture<>(DDLRecordSet.class);
	}

	protected void setUpUserSearchFixture() throws Exception {
		userSearchFixture = new UserSearchFixture();

		userSearchFixture.setUp();

		_groups = userSearchFixture.getGroups();

		_users = userSearchFixture.getUsers();

		group = userSearchFixture.addGroup();

		user = userSearchFixture.addUser(RandomTestUtil.randomString(), group);
	}

	protected DDLRecordSetFixture ddlRecordSetFixture;
	protected IndexerFixture<DDLRecordSet> ddlRecordSetIndexerFixture;
	protected Group group;
	protected IndexedFieldsFixture indexedFieldsFixture;

	@Inject
	protected ResourcePermissionLocalService resourcePermissionLocalService;

	@Inject
	protected SearchEngineHelper searchEngineHelper;

	protected User user;
	protected UserSearchFixture userSearchFixture;

	private Map<String, String> _expectedFieldValues(DDLRecordSet ddlRecordSet)
		throws Exception {

		Map<String, String> map = new HashMap<>();

		map.put(Field.COMPANY_ID, String.valueOf(ddlRecordSet.getCompanyId()));

		map.put(Field.ENTRY_CLASS_NAME, DDLRecordSet.class.getName());

		map.put(
			Field.ENTRY_CLASS_PK,
			String.valueOf(ddlRecordSet.getRecordSetId()));

		map.put(Field.GROUP_ID, String.valueOf(ddlRecordSet.getGroupId()));

		map.put(
			Field.SCOPE_GROUP_ID, String.valueOf(ddlRecordSet.getGroupId()));

		map.put(Field.STAGING_GROUP, String.valueOf(group.isStagingGroup()));

		map.put(Field.USER_ID, String.valueOf(ddlRecordSet.getUserId()));

		map.put(
			Field.USER_NAME, StringUtil.lowerCase(ddlRecordSet.getUserName()));

		indexedFieldsFixture.populateUID(
			DDLRecordSet.class.getName(), ddlRecordSet.getRecordSetId(), map);
		_populateDates(ddlRecordSet, map);
		_populateRoles(ddlRecordSet, map);

		return map;
	}

	private void _populateDates(
		DDLRecordSet ddlRecordSet, Map<String, String> map) {

		indexedFieldsFixture.populateDate(
			Field.CREATE_DATE, ddlRecordSet.getCreateDate(), map);
		indexedFieldsFixture.populateDate(
			Field.MODIFIED_DATE, ddlRecordSet.getModifiedDate(), map);
	}

	private void _populateRoles(
			DDLRecordSet ddlRecordSet, Map<String, String> map)
		throws Exception {

		indexedFieldsFixture.populateRoleIdFields(
			ddlRecordSet.getCompanyId(), DDLRecordSet.class.getName(),
			ddlRecordSet.getRecordSetId(), ddlRecordSet.getGroupId(), null,
			map);
	}

	@DeleteAfterTestRun
	private List<DDLRecordSet> _ddlRecordSets;

	@DeleteAfterTestRun
	private List<Group> _groups;

	@DeleteAfterTestRun
	private List<User> _users;

}