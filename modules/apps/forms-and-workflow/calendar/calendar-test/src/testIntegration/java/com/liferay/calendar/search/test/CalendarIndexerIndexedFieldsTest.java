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

package com.liferay.calendar.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.calendar.model.Calendar;
import com.liferay.calendar.model.CalendarResource;
import com.liferay.calendar.search.CalendarIndexer;
import com.liferay.calendar.test.util.FieldValuesAssert;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.settings.LocalizedValuesMap;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerTestRule;

import java.text.DateFormat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Wade Cao
 */
@RunWith(Arquillian.class)
@Sync
public class CalendarIndexerIndexedFieldsTest
	extends BaseCalendarIndexerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		super.setUp();

		_group = GroupTestUtil.addGroup();

		_indexer = new CalendarIndexer();
	}

	@Test
	public void testIndexedFields() throws Exception {

		//calendar name
		String originalName = "entity title";
		String translatedName = "entitas neve";

		//calendar description
		String originalDescription = "calendar description";
		String translatedDescription = "descripción del calendario";

		//add Calendar
		Calendar calendar = addCalendar(
			new LocalizedValuesMap() {
				{
					put(LocaleUtil.US, originalName);
					put(LocaleUtil.HUNGARY, translatedName);
				}
			},
			new LocalizedValuesMap() {
				{
					put(LocaleUtil.US, originalDescription);
					put(LocaleUtil.HUNGARY, translatedDescription);
				}
			});

		Map<String, String> mapStrings = _getFieldsString(
			originalName, translatedName, originalDescription,
			translatedDescription, calendar);

		String searchTerm = "nev";

		Document document = search(searchTerm, LocaleUtil.HUNGARY);

		FieldValuesAssert.assertFieldValues(mapStrings, document, searchTerm);
	}

	@Test
	public void testIndexedFieldsMissingWhenDescriptionIsEmpty()
		throws Exception {

		//calendar name
		String originalName = "entity title";
		String translatedName = "título da entidade";

		//add Calendar
		Calendar calendar = addCalendar(
			new LocalizedValuesMap() {
				{
					put(LocaleUtil.US, originalName);
					put(LocaleUtil.BRAZIL, translatedName);
				}
			},
			new LocalizedValuesMap() {
			});

		long calendarId = calendar.getCalendarId();

		String searchTerm = String.valueOf(calendarId);

		Document document = search(searchTerm, LocaleUtil.BRAZIL);

		Assert.assertNull(document);
	}

	@Override
	protected Group getGroup() {
		return _group;
	}

	@Override
	protected Indexer<?> getIndexer() {
		return _indexer;
	}

	@SuppressWarnings("serial")
	private Map<String, String> _getFieldsString(
		String originalName, String translatedName, String originalDescription,
		String translatedDescription, Calendar calendar) throws Exception {

		DateFormat df = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyyMMddHHmmss");

		CalendarResource clendarResource = calendar.getCalendarResource();

		Map<String, String> mapStrings = new HashMap<String, String>() {
			{
				put(Field.NAME, originalName);
				put(Field.NAME + "_en_US", originalName);
				put(Field.NAME + "_hu_HU", translatedName);
				put(Field.DESCRIPTION, originalDescription);
				put(Field.DESCRIPTION + "_en_US", originalDescription);
				put(Field.DESCRIPTION + "_hu_HU", translatedDescription);
				put(
					Field.ENTRY_CLASS_PK,
					String.valueOf(calendar.getCalendarId()));
				put("calendarId", String.valueOf(calendar.getCalendarId()));
				put(Field.COMPANY_ID, String.valueOf(calendar.getCompanyId()));
				put(Field.STAGING_GROUP, "false");
				put(Field.USER_ID, String.valueOf(calendar.getUserId()));
				put(
					Field.USER_NAME,
					StringUtil.toLowerCase(calendar.getUserName()));
				put(
					"resourceName",
					StringUtil.toLowerCase(
						clendarResource.getName(LocaleUtil.US, true)));
				put(
					"resourceName_en_US",
					StringUtil.toLowerCase(
						clendarResource.getName(
							calendar.getDefaultLanguageId())));
				put(Field.DEFAULT_LANGUAGE_ID, calendar.getDefaultLanguageId());
				put(Field.ENTRY_CLASS_NAME, calendar.getModelClassName());
				put(Field.CREATE_DATE, df.format(calendar.getCreateDate()));
				put(
					"createDate_sortable",
					String.valueOf(calendar.getModifiedDate().getTime()));
				put(Field.MODIFIED_DATE, df.format(calendar.getCreateDate()));
				put(
					"modified_sortable",
					String.valueOf(calendar.getModifiedDate().getTime()));
				put(Field.GROUP_ID, String.valueOf(calendar.getGroupId()));
				put(
					Field.SCOPE_GROUP_ID,
					String.valueOf(calendar.getGroupId()));
			}
		};

		_serUID(mapStrings, calendar);
		_setRoleId(mapStrings);
		_setGroupRoleId(mapStrings);

		return mapStrings;
	}

	private void _serUID(Map<String, String> mapStrings, Calendar calendar) {
		String uid =
			calendar.getModelClassName() + "_PORTLET_" +
				calendar.getCalendarId();

		mapStrings.put(Field.UID, uid);
	}

	private void _setGroupRoleId(Map<String, String> mapStrings)
		throws PortalException {

		Role role = RoleLocalServiceUtil.getDefaultGroupRole(
			_group.getGroupId());

		String groupRoleId =
			_group.getGroupId() + StringPool.DASH + role.getRoleId();

		mapStrings.put(Field.GROUP_ROLE_ID, groupRoleId);
	}

	private void _setRoleId(Map<String, String> mapStrings) {
		Role role = RoleLocalServiceUtil.fetchRole(
			_group.getCompanyId(), "Guest");

		mapStrings.put(Field.ROLE_ID, String.valueOf(role.getRoleId()));
	}

	@DeleteAfterTestRun
	private Group _group;

	private Indexer<?> _indexer;

}