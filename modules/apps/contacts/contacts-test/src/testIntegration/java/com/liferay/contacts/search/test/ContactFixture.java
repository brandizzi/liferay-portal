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

package com.liferay.contacts.search.test;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ContactLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;

import java.util.List;
import java.util.Locale;

/**
 * @author Lucas Marques de Paula
 */
public class ContactFixture {

	public ContactFixture(
		ContactLocalService contactLocalService, List<User> users,
		List<Group> groups, List<Contact> contacts) {

		_contactLocalService = contactLocalService;
		_users = users;
		_groups = groups;
		_contacts = contacts;
	}

	public Contact addContact(String firstName) throws Exception {
		return addContact(
			RandomTestUtil.randomString(), firstName,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString());
	}

	public Contact addContact(
			String emailAddress, String firstName, String middleName,
			String lastName, String jobTitle)
		throws Exception {

		Contact contact = _contactLocalService.addContact(
			getUserId(), StringPool.STAR, 1, emailAddress, firstName,
			middleName, lastName, 0, 0, RandomTestUtil.randomBoolean(),
			RandomTestUtil.randomInt(0, 11), RandomTestUtil.randomInt(1, 28),
			RandomTestUtil.randomInt(1970, 2018), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			jobTitle);

		_contacts.add(contact);

		return contact;
	}

	public Group addGroup() throws Exception {
		Group group = GroupTestUtil.addGroup();

		_groups.add(group);

		return group;
	}

	public User addUser() throws Exception {
		User user = UserTestUtil.addUser(getGroupId());

		_users.add(user);

		return user;
	}

	public ServiceContext getServiceContext() throws Exception {
		return ServiceContextTestUtil.getServiceContext(
			getGroupId(), getUserId());
	}

	public void setGroup(Group group) {
		_group = group;
	}

	public void setUp() throws Exception {
		CompanyThreadLocal.setCompanyId(TestPropsValues.getCompanyId());
	}

	public void setUser(User user) {
		_user = user;
	}

	public void updateDisplaySettings(Locale locale) throws Exception {
		Group group = GroupTestUtil.updateDisplaySettings(
			_group.getGroupId(), null, locale);

		_group.setModelAttributes(group.getModelAttributes());
	}

	protected Contact addContact() throws Exception {
		return addContact(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString());
	}

	protected long getGroupId() throws Exception {
		return _group.getGroupId();
	}

	protected long getUserId() throws Exception {
		return _user.getUserId();
	}

	private final ContactLocalService _contactLocalService;
	private final List<Contact> _contacts;
	private Group _group;
	private final List<Group> _groups;
	private User _user;
	private final List<User> _users;

}