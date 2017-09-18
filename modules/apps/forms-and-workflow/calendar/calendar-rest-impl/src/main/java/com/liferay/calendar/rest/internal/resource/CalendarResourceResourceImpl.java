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

package com.liferay.calendar.rest.internal.resource;

import com.liferay.calendar.rest.internal.helper.CalendarResourceHelper;
import com.liferay.calendar.rest.model.CalendarResourceModel;
import com.liferay.calendar.rest.resource.CalendarResourceResource;
import com.liferay.portal.kernel.exception.PortalException;

import java.util.Locale;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adam Brandizzi
 */
@Component(immediate = true, service = CalendarResourceResource.class)
@Path("/calendar-resource")
public class CalendarResourceResourceImpl implements CalendarResourceResource {

	@GET
	@Override
	@Path("/{calendarResourceId}")
	@Produces("application/json")
	public CalendarResourceModel getCalendarResource(
			@Context Locale locale,
			@PathParam("calendarResourceId") long calendarResourceId)
		throws PortalException {

		return _calendarResourceHelper.getCalendarResourceModel(
			calendarResourceId, locale);
	}

	@Reference
	private CalendarResourceHelper _calendarResourceHelper;

}