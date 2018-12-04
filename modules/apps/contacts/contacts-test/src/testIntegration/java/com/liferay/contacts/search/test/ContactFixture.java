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

import java.util.List;
import java.util.Locale;

/**
 * @author Lucas Marques de Paula
 */
public class ContactFixture {

	public ContactFixture(
		ContactLocalService contactLocalService, List<Contact> contacts,
		Group group, User user) {

		_contactLocalService = contactLocalService;
		_contacts = contacts;
		_group = group;
		_user = user;
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
			_user.getUserId(), StringPool.STAR, 1, emailAddress, firstName,
			middleName, lastName, 0, 0, RandomTestUtil.randomBoolean(),
			RandomTestUtil.randomInt(0, 11), RandomTestUtil.randomInt(1, 28),
			RandomTestUtil.randomInt(1970, 2018), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			jobTitle);

		_contacts.add(contact);

		return contact;
	}

	public ServiceContext getServiceContext() throws Exception {
		return ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), _user.getUserId());
	}

	public void setUp() throws Exception {
		CompanyThreadLocal.setCompanyId(TestPropsValues.getCompanyId());
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

	private final ContactLocalService _contactLocalService;
	private final List<Contact> _contacts;
	private final Group _group;
	private final User _user;

}