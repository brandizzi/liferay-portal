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
import com.liferay.calendar.model.CalendarResource;
import com.liferay.calendar.rest.model.CalendarResourceModel;
import com.liferay.calendar.rest.resource.CalendarResourceResource;
import com.liferay.calendar.test.util.CalendarResourceTestUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;

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
public class CalendarResourceResourceTest {

	@Before
	public void setUp() throws Exception {
		Registry registry = RegistryUtil.getRegistry();

		_calendarResourceResource = registry.getService(
			CalendarResourceResource.class);

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
	public void testWebServiceInvocation() throws Exception {
		CalendarResource calendarResource =
			CalendarResourceTestUtil.addCalendarResource(
				_group, _serviceContext);

		Locale locale = LocaleUtil.fromLanguageId(
			calendarResource.getNameCurrentLanguageId());

		CalendarResourceModel calendarResourceModel =
			_calendarResourceResource.getCalendarResource(
				locale, calendarResource.getCalendarResourceId());

		assertCalendarResourceResult(
			calendarResource, calendarResourceModel, locale);
	}

	protected void assertCalendarResourceResult(
			CalendarResource calendarResource,
			CalendarResourceModel calendarResourceModel, Locale locale)
		throws JSONException, PortalException {

		Assert.assertEquals(
			calendarResource.getCalendarResourceId(),
			calendarResourceModel.getCalendarResourceId());
		Assert.assertEquals(
			calendarResource.getClassNameId(),
			calendarResourceModel.getClassNameId());
		Assert.assertEquals(
			calendarResource.getClassPK(), calendarResourceModel.getClassPK());
		Assert.assertEquals(
			calendarResource.getClassUuid(),
			calendarResourceModel.getClassUuId());
		Assert.assertEquals(
			calendarResource.getGroupId(), calendarResourceModel.getGroupId());
		Assert.assertEquals(
			calendarResource.getName(locale), calendarResourceModel.getName());
		Assert.assertEquals(
			calendarResource.getUserId(), calendarResourceModel.getUserId());
	}

	private CalendarResourceResource _calendarResourceResource;

	@DeleteAfterTestRun
	private Group _group;

	private PermissionChecker _permissionChecker;
	private ServiceContext _serviceContext;

	@DeleteAfterTestRun
	private User _user;

}