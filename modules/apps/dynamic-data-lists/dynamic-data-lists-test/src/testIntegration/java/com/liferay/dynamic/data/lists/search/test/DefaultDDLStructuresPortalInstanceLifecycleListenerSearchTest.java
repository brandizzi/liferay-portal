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
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.util.DefaultDDMStructureHelper;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.search.test.util.IndexerFixture;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.users.admin.test.util.search.UserSearchFixture;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Vagner B.C
 */
@RunWith(Arquillian.class)
public class DefaultDDLStructuresPortalInstanceLifecycleListenerSearchTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		setUpUserSearchFixture();

		setUpDDMStructureIndexerFixture();
	}

	@After
	public void tearDown() {
		ddmStructureIndexerFixture.deleteDocuments(_documents);
	}

	@Test
	public void testIndexedDefaultDDLStructures() throws Exception {
		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGuestPermissions(true);
		serviceContext.setAddGroupPermissions(true);
		serviceContext.setScopeGroupId(group.getGroupId());
		serviceContext.setUserId(user.getUserId());

		_defaultDDMStructureHelper.addDDMStructures(
			user.getUserId(), group.getGroupId(),
			_portal.getClassNameId(DDLRecordSet.class),
			DefaultDDLStructuresPortalInstanceLifecycleListenerSearchTest.class.
				getClassLoader(),
			"com/liferay/dynamic/data/lists/search" +
				"/default-dynamic-data-lists-structures.xml",
			serviceContext);

		_documents = ddmStructureIndexerFixture.search(
			user.getUserId(), user.getFullName(), LocaleUtil.US);

		Assert.assertEquals("Must have 6 documents", 6, _documents.length);
	}

	protected void setUpDDMStructureIndexerFixture() {
		ddmStructureIndexerFixture = new IndexerFixture<>(DDMStructure.class);
	}

	protected void setUpUserSearchFixture() throws Exception {
		userSearchFixture = new UserSearchFixture();

		userSearchFixture.setUp();

		_groups = userSearchFixture.getGroups();

		_users = userSearchFixture.getUsers();

		group = userSearchFixture.addGroup();

		user = userSearchFixture.addUser(RandomTestUtil.randomString(), group);
	}

	protected IndexerFixture<DDMStructure> ddmStructureIndexerFixture;
	protected Group group;
	protected User user;
	protected UserSearchFixture userSearchFixture;

	@Inject
	private DefaultDDMStructureHelper _defaultDDMStructureHelper;

	private Document[] _documents;

	@DeleteAfterTestRun
	private List<Group> _groups;

	@Inject
	private Portal _portal;

	@DeleteAfterTestRun
	private List<User> _users;

}