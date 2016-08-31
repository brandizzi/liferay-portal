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

package com.liferay.calendar.web.internal.display.context;

import com.liferay.calendar.model.Calendar;
import com.liferay.calendar.model.CalendarResource;
import com.liferay.calendar.service.CalendarLocalService;
import com.liferay.calendar.service.CalendarService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adam Brandizzi
 */
public class CalendarDisplayContext {

	public CalendarDisplayContext(
		GroupLocalService groupLocalService, CalendarService calendarService,
		CalendarLocalService calendarLocalService, ThemeDisplay themeDisplay) {

		_groupLocalService = groupLocalService;
		_calendarService = calendarService;
		_calendarLocalService = calendarLocalService;
		_themeDisplay = themeDisplay;
	}

	public List<Calendar> getOtherCalendars(long[] calendarIds)
		throws PortalException {

		List<Calendar> otherCalendars = new ArrayList<>();

		for (long calendarId : calendarIds) {
			Calendar calendar = _calendarService.fetchCalendar(calendarId);

			if (calendar == null) {
				continue;
			}

			CalendarResource calendarResource = calendar.getCalendarResource();

			if (!calendarResource.isActive()) {
				continue;
			}

			Group scopeGroup = _themeDisplay.getScopeGroup();
			Group calendarGroup = _groupLocalService.getGroup(
				calendar.getGroupId());

			if (calendarGroup.isStagingGroup() &&
				(!scopeGroup.isStagingGroup() ||
				 (scopeGroup.getGroupId() != calendarGroup.getGroupId()))) {

				calendar = _calendarLocalService.fetchCalendarByUuidAndGroupId(
					calendar.getUuid(), calendarGroup.getLiveGroupId());

				if (calendar == null) {
					continue;
				}
			}
			else if (scopeGroup.isStagingGroup() &&
					 (scopeGroup.getLiveGroupId() ==
						 calendarGroup.getGroupId())) {

				Group stagingGroup = calendarGroup.getStagingGroup();

				calendar = _calendarLocalService.fetchCalendarByUuidAndGroupId(
					calendar.getUuid(), stagingGroup.getGroupId());
			}

			otherCalendars.add(calendar);
		}

		return otherCalendars;
	}

	private final CalendarLocalService _calendarLocalService;
	private final CalendarService _calendarService;
	private final GroupLocalService _groupLocalService;
	private final ThemeDisplay _themeDisplay;

}