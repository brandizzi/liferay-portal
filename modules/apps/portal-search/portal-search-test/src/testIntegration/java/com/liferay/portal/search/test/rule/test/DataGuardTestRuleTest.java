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

package com.liferay.portal.search.test.rule.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.users.admin.test.util.search.GroupSearchFixture;
import com.liferay.users.admin.test.util.search.OrganizationBlueprint;
import com.liferay.users.admin.test.util.search.OrganizationBlueprint.OrganizationBlueprintBuilder;
import com.liferay.users.admin.test.util.search.OrganizationSearchFixture;
import com.liferay.users.admin.test.util.search.UserBlueprint;
import com.liferay.users.admin.test.util.search.UserGroupSearchFixture;
import com.liferay.users.admin.test.util.search.UserSearchFixture;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adam Brandizzi
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class DataGuardTestRuleTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_groupSearchFixture = new GroupSearchFixture();
		_organizationSearchFixture = new OrganizationSearchFixture(
			_organizationLocalService);
		_userGroupSearchFixture = new UserGroupSearchFixture(
			_userGroupLocalService);

		_userSearchFixture = new UserSearchFixture(
			_userLocalService, _groupSearchFixture, _organizationSearchFixture,
			_userGroupSearchFixture);

		_company = CompanyTestUtil.addCompany();

		UserBlueprint.UserBlueprintBuilder userBlueprintBuilder =
			_userSearchFixture.getTestUserBlueprintBuilder();

		userBlueprintBuilder.companyId(_company.getCompanyId());

		_user = _userSearchFixture.addUser(userBlueprintBuilder);
	}

	@Test
	public void test() throws Exception {
		for (int i = 0; i < 100; i++) {
			OrganizationBlueprintBuilder organizationBlueprintBuilder =
				new OrganizationBlueprintBuilder();

			OrganizationBlueprint organizationBlueprint =
				organizationBlueprintBuilder.userId(
					_user.getUserId()
				).name(
					RandomTestUtil.randomString()
				).build();

			_organizationSearchFixture.addOrganization(organizationBlueprint);
		}
	}

	private Company _company;
	private GroupSearchFixture _groupSearchFixture;

	@Inject
	private OrganizationLocalService _organizationLocalService;

	private OrganizationSearchFixture _organizationSearchFixture;
	private User _user;

	@Inject
	private UserGroupLocalService _userGroupLocalService;

	private UserGroupSearchFixture _userGroupSearchFixture;

	@Inject
	private UserLocalService _userLocalService;

	private UserSearchFixture _userSearchFixture;

}