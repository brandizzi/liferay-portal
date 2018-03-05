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

package com.liferay.portal.search.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adam Brandizzi
 */
@RunWith(Arquillian.class)
public class GroupModelListenerTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testCreateSearchLayoutForSiteGroup() throws Exception {
		_group = GroupTestUtil.addGroup();

		assertSearchLayout(_group);
	}

	@Test
	public void testDoNotCreateLayoutForOrganizationGroup() throws Exception {
		_organization = OrganizationTestUtil.addOrganization();

		Group group = _organization.getGroup();

		assertNoSearchLayout(group);
	}

	@Test
	public void testDoNotCreateLayoutForUserGroup() throws Exception {
		_user = UserTestUtil.addUser();

		Group group = _user.getGroup();

		assertNoSearchLayout(group);
	}

	protected void assertNoSearchLayout(Group group) {
		Layout layout = layoutLocalService.fetchLayoutByFriendlyURL(
			group.getGroupId(), false, "/search");

		Assert.assertNull(layout);
	}

	protected void assertSearchLayout(Group group) {
		Layout layout = layoutLocalService.fetchLayoutByFriendlyURL(
			group.getGroupId(), false, "/search");

		Assert.assertNotNull(layout);
	}

	@Inject
	protected LayoutLocalService layoutLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private Organization _organization;

	@DeleteAfterTestRun
	private User _user;

}