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

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.SearchEngineHelper;
import com.liferay.portal.kernel.service.ContactLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.search.test.util.IndexedFieldsFixture;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerTestRule;

import java.util.ArrayList;
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
 * @author Lucas Marques de Paula
 */
@RunWith(Arquillian.class)
public class ContactIndexerIndexedFieldsTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		setUpContactFixture();

		setUpContactSearchFixture();
	}

	@Test
	public void testIndexedFields() throws Exception {
		Locale locale = LocaleUtil.US;

		contactFixture.updateDisplaySettings(locale);

		Contact contact = contactFixture.addContact();

		assertSearchContact(locale, contact, contact.getFullName());
	}

	protected void assertSearchContact(
			Locale locale, Contact contact, String searchTerm)
		throws Exception {

		Document document = contactIndexerFixture.searchOnlyOne(
			searchTerm, locale);

		indexedFieldsFixture.postProcessDocument(document);

		Map<String, String> expected = expectedFieldValues(contact);

		FieldValuesAssert.assertFieldValues(expected, document, searchTerm);
	}

	protected Map<String, String> expectedFieldValues(Contact contact)
		throws Exception {

		Map<String, String> map = new HashMap<>();

		indexedFieldsFixture.populateUID(
			contact.getModelClassName(), contact.getContactId(), map);
		indexedFieldsFixture.populateDate(
			Field.CREATE_DATE, contact.getCreateDate(), map);
		indexedFieldsFixture.populateDate(
			Field.MODIFIED_DATE, contact.getModifiedDate(), map);

		map.put(Field.CLASS_NAME_ID, String.valueOf(contact.getClassNameId()));
		map.put(Field.CLASS_PK, String.valueOf(contact.getClassPK()));
		map.put(Field.ENTRY_CLASS_PK, String.valueOf(contact.getPrimaryKey()));
		map.put(Field.ENTRY_CLASS_NAME, contact.getModelClassName());

		map.put(Field.COMPANY_ID, String.valueOf(contact.getCompanyId()));
		map.put(Field.USER_ID, String.valueOf(contact.getUserId()));
		map.put(Field.USER_NAME, contact.getFullName());

		map.put("emailAddress", contact.getEmailAddress());

		map.put("firstName", contact.getFirstName());
		map.put(
			"firstName_sortable",
			StringUtil.toLowerCase(contact.getFirstName()));

		map.put("middleName", contact.getMiddleName());

		map.put("lastName", contact.getLastName());
		map.put(
			"lastName_sortable", StringUtil.toLowerCase(contact.getLastName()));

		map.put("fullName", contact.getFullName());

		map.put("jobTitle", contact.getJobTitle());
		map.put(
			"jobTitle_sortable", StringUtil.toLowerCase(contact.getJobTitle()));

		return map;
	}

	protected void setUpContactFixture() throws Exception {
		contactFixture = new ContactFixture(
			contactLocalService, _users, _groups, _contacts);

		contactFixture.setUp();

		contactFixture.setGroup(contactFixture.addGroup());

		user = contactFixture.addUser();

		contactFixture.setUser(user);
	}

	protected void setUpContactSearchFixture() {
		indexedFieldsFixture = new IndexedFieldsFixture(
			resourcePermissionLocalService, searchEngineHelper);

		Indexer<?> indexer = indexerRegistry.getIndexer(Contact.class);

		contactIndexerFixture = new ContactIndexerFixture(indexer);

		contactIndexerFixture.setUser(user);
	}

	protected ContactFixture contactFixture;
	protected ContactIndexerFixture contactIndexerFixture;

	@Inject
	protected ContactLocalService contactLocalService;

	protected IndexedFieldsFixture indexedFieldsFixture;

	@Inject
	protected IndexerRegistry indexerRegistry;

	@Inject
	protected ResourcePermissionLocalService resourcePermissionLocalService;

	@Inject
	protected SearchEngineHelper searchEngineHelper;

	protected User user;

	@DeleteAfterTestRun
	private final List<Contact> _contacts = new ArrayList<>(1);

	@DeleteAfterTestRun
	private final List<Group> _groups = new ArrayList<>(1);

	@DeleteAfterTestRun
	private final List<User> _users = new ArrayList<>(1);

}