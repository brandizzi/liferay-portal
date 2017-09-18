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

package com.liferay.calendar.rest.internal.conversor;

import com.liferay.calendar.model.CalendarResource;
import com.liferay.calendar.rest.internal.model.CalendarResourceModelImpl;
import com.liferay.calendar.rest.model.CalendarResourceModel;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;

/**
 * @author Adam Brandizzi
 */
@Component(immediate = true, service = CalendarResourceConverter.class)
public class CalendarResourceConverter {

	public CalendarResourceModel toCalendarResourceModel(
		CalendarResource calendarResource, Locale locale) {

		return new CalendarResourceModelImpl(
			calendarResource.getGroupId(), calendarResource.getUserId(),
			calendarResource.getCalendarResourceId(),
			calendarResource.getName(locale), calendarResource.getClassUuid(),
			calendarResource.getClassNameId(), calendarResource.getClassPK(),
			calendarResource.getCode());
	}

}