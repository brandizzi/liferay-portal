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

package com.liferay.calendar.rest.internal.helper;

import com.liferay.calendar.model.CalendarResource;
import com.liferay.calendar.rest.internal.conversor.CalendarResourceConverter;
import com.liferay.calendar.rest.model.CalendarResourceModel;
import com.liferay.calendar.service.CalendarResourceService;
import com.liferay.portal.kernel.exception.PortalException;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adam Brandizzi
 */
@Component(immediate = true, service = CalendarResourceHelper.class)
public class CalendarResourceHelper {

	public CalendarResourceModel getCalendarResourceModel(
			long calendarResourceId, Locale locale)
		throws PortalException {

		CalendarResource calendarResource =
			calendarResourceService.getCalendarResource(calendarResourceId);

		return calendarResourceConverter.toCalendarResourceModel(
			calendarResource, locale);
	}

	@Reference
	protected CalendarResourceConverter calendarResourceConverter;

	@Reference
	protected CalendarResourceService calendarResourceService;

}