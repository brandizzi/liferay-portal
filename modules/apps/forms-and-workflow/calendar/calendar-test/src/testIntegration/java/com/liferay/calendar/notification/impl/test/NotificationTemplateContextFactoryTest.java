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

package com.liferay.calendar.notification.impl.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.calendar.model.Calendar;
import com.liferay.calendar.model.CalendarBooking;
import com.liferay.calendar.notification.NotificationTemplateType;
import com.liferay.calendar.notification.NotificationType;
import com.liferay.calendar.notification.impl.NotificationTemplateContext;
import com.liferay.calendar.notification.impl.NotificationTemplateContextFactory;
import com.liferay.calendar.test.util.CalendarBookingTestUtil;
import com.liferay.calendar.test.util.CalendarTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.util.test.LayoutTestUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Inácio Nery
 */
@RunWith(Arquillian.class)
public class NotificationTemplateContextFactoryTest {

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
		_user = UserTestUtil.addUser();

		_layout = LayoutTestUtil.addLayout(_group);
	}

	@Test
	public void testEmailInviteShouldReturnValidEventURL() throws Exception {
		Calendar calendar = CalendarTestUtil.getDefaultCalendar(_group);

		Calendar invitedCalendar = CalendarTestUtil.addCalendar(_user);

		CalendarBooking invitedCalendarBooking =
			CalendarBookingTestUtil.addChildCalendarBooking(
				calendar, invitedCalendar);

		NotificationTemplateContext notificationTemplateContext =
			NotificationTemplateContextFactory.getInstance(
				NotificationType.EMAIL, NotificationTemplateType.INVITE,
				invitedCalendarBooking, _user);

		String calendarBookingURL = GetterUtil.getString(
			notificationTemplateContext.getAttribute("url"));

		String plidString = HttpUtil.getParameter(
			calendarBookingURL, "p_l_id", false);

		long plid = GetterUtil.getLong(plidString);

		Assert.assertEquals(_layout.getPlid(), plid);
	}

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private Layout _layout;

	@DeleteAfterTestRun
	private User _user;

}