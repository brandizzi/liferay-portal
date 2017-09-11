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

package com.liferay.calendar.rest.internal.resource.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.calendar.model.Calendar;
import com.liferay.calendar.model.CalendarBooking;
import com.liferay.calendar.rest.model.CalendarBookingModel;
import com.liferay.calendar.rest.resource.CalendarBookingResource;
import com.liferay.calendar.test.util.CalendarBookingTestUtil;
import com.liferay.calendar.test.util.CalendarTestUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;

import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adam Brandizzi
 */
@RunWith(Arquillian.class)
public class CalendarBookingResourceTest {

	@Before
	public void setUp() throws Exception {
		Registry registry = RegistryUtil.getRegistry();

		_calendarBookingResource = registry.getService(
			CalendarBookingResource.class);

		_group = GroupTestUtil.addGroup();

		_user = TestPropsValues.getUser();

		_permissionChecker = PermissionThreadLocal.getPermissionChecker();

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(_user);

		PermissionThreadLocal.setPermissionChecker(permissionChecker);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), _user.getUserId());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);
	}

	@After
	public void tearDown() {
		PermissionThreadLocal.setPermissionChecker(_permissionChecker);

		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testGetCalendarBookings() throws Exception {
		Calendar calendar = CalendarTestUtil.addCalendar(_group);

		CalendarBooking calendarBooking =
			CalendarBookingTestUtil.addRegularCalendarBooking(calendar);

		Company company = CompanyLocalServiceUtil.fetchCompany(
			calendarBooking.getCompanyId());
		Locale locale = LocaleUtil.fromLanguageId(
			calendarBooking.getTitleCurrentLanguageId());

		List<CalendarBookingModel> calendarBookingModels =
			_calendarBookingResource.getCalendarBookings(
				company, locale, new long[] {calendarBooking.getCalendarId()},
				calendarBooking.getStartTime() - Time.DAY,
				calendarBooking.getEndTime() + Time.DAY,
				new int[] {calendarBooking.getStatus()}, 0);

		Assert.assertEquals(1, calendarBookingModels.size());

		assertCalendarBookingResult(
			calendarBooking, calendarBookingModels.get(0), locale);
	}

	protected void assertCalendarBookingResult(
			CalendarBooking calendarBooking,
			CalendarBookingModel calendarBookingModel, Locale locale)
		throws JSONException, PortalException {

		Assert.assertEquals(
			calendarBooking.isAllDay(), calendarBookingModel.isAllDay());
		Assert.assertEquals(
			calendarBooking.getCalendarBookingId(),
			calendarBookingModel.getCalendarBookingId());
		Assert.assertEquals(
			calendarBooking.getCalendarId(),
			calendarBookingModel.getCalendarId());
		Assert.assertEquals(
			calendarBooking.getDescription(locale),
			calendarBookingModel.getDescription());

		assertEndTimeEquals(calendarBooking, calendarBookingModel);

		Assert.assertEquals(
			calendarBooking.getFirstReminder(),
			calendarBookingModel.getFirstReminder());
		Assert.assertEquals(
			calendarBooking.getFirstReminderType(),
			calendarBookingModel.getFirstReminderType());
		Assert.assertFalse(calendarBookingModel.hasChildCalendarBookings());
		Assert.assertFalse(calendarBookingModel.hasWorkflowInstanceLink());
		Assert.assertEquals(
			calendarBooking.getInstanceIndex(),
			calendarBookingModel.getInstanceIndex());
		Assert.assertEquals(
			calendarBooking.getLocation(), calendarBookingModel.getLocation());
		Assert.assertEquals(
			calendarBooking.getParentCalendarBookingId(),
			calendarBookingModel.getParentCalendarBookingId());
		Assert.assertEquals(
			calendarBooking.getRecurrence(),
			calendarBookingModel.getRecurrence());
		Assert.assertEquals(
			calendarBooking.getRecurringCalendarBookingId(),
			calendarBookingModel.getRecurringCalendarBookingId());
		Assert.assertEquals(
			calendarBooking.getSecondReminder(),
			calendarBookingModel.getSecondReminder());
		Assert.assertEquals(
			calendarBooking.getSecondReminderType(),
			calendarBookingModel.getSecondReminderType());
		Assert.assertEquals(
			calendarBooking.getStatus(), calendarBookingModel.getStatus());

		assertStartTimeEquals(calendarBooking, calendarBookingModel);

		Assert.assertEquals(
			calendarBooking.getTitle(locale), calendarBookingModel.getTitle());
	}

	protected void assertEndTimeEquals(
		CalendarBooking calendarBooking,
		CalendarBookingModel calendarBookingModel) {

		Assert.assertEquals(
			calendarBooking.getEndTime(), calendarBookingModel.getEndTime());

		java.util.Calendar jCalendar = java.util.Calendar.getInstance();

		jCalendar.setTimeZone(calendarBooking.getTimeZone());
		jCalendar.setTimeInMillis(calendarBooking.getEndTime());

		Assert.assertEquals(
			jCalendar.get(java.util.Calendar.DATE),
			calendarBookingModel.getEndTimeDay());
		Assert.assertEquals(
			jCalendar.get(java.util.Calendar.HOUR_OF_DAY),
			calendarBookingModel.getEndTimeHour());
		Assert.assertEquals(
			jCalendar.get(java.util.Calendar.MINUTE),
			calendarBookingModel.getEndTimeMinute());
		Assert.assertEquals(
			jCalendar.get(java.util.Calendar.MONTH),
			calendarBookingModel.getEndTimeMonth());
		Assert.assertEquals(
			jCalendar.get(java.util.Calendar.YEAR),
			calendarBookingModel.getEndTimeYear());
	}

	protected void assertStartTimeEquals(
		CalendarBooking calendarBooking,
		CalendarBookingModel calendarBookingModel) {

		Assert.assertEquals(
			calendarBooking.getStartTime(),
			calendarBookingModel.getStartTime());

		java.util.Calendar jCalendar = java.util.Calendar.getInstance();

		jCalendar.setTimeZone(calendarBooking.getTimeZone());
		jCalendar.setTimeInMillis(calendarBooking.getStartTime());

		Assert.assertEquals(
			jCalendar.get(java.util.Calendar.DATE),
			calendarBookingModel.getStartTimeDay());
		Assert.assertEquals(
			jCalendar.get(java.util.Calendar.HOUR_OF_DAY),
			calendarBookingModel.getStartTimeHour());
		Assert.assertEquals(
			jCalendar.get(java.util.Calendar.MINUTE),
			calendarBookingModel.getStartTimeMinute());
		Assert.assertEquals(
			jCalendar.get(java.util.Calendar.MONTH),
			calendarBookingModel.getStartTimeMonth());
		Assert.assertEquals(
			jCalendar.get(java.util.Calendar.YEAR),
			calendarBookingModel.getStartTimeYear());
	}

	private CalendarBookingResource _calendarBookingResource;

	@DeleteAfterTestRun
	private Group _group;

	private PermissionChecker _permissionChecker;
	private ServiceContext _serviceContext;

	@DeleteAfterTestRun
	private User _user;

}