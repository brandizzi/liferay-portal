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

import com.liferay.calendar.rest.internal.helper.CalendarBookingHelper;
import com.liferay.calendar.rest.model.CalendarBookingModel;
import com.liferay.calendar.rest.resource.CalendarBookingResource;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.Company;

import java.util.List;
import java.util.Locale;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adam Brandizzi
 */
@Component(immediate = true, service = CalendarBookingResource.class)
@Path("/calendar-booking")
public class CalendarBookingResourceImpl implements CalendarBookingResource {

	@GET
	@Override
	@Path("/")
	@Produces("application/json")
	public List<CalendarBookingModel> getCalendarBookings(
		@Context Company company, @Context Locale locale,
		@QueryParam("calendarIds") long[] calendarIds,
		@QueryParam("startTime") long startTime,
		@QueryParam("endTime") long endTime,
		@QueryParam("statuses") int[] statuses,
		@QueryParam("eventsperPage") int eventsPerPage) {

		try {
			return _calendarBookingHelper.search(
				company.getCompanyId(), calendarIds, startTime, endTime,
				statuses, eventsPerPage, locale);
		}
		catch (PortalException pe) {
			throw new SystemException(pe);
		}
	}

	@Reference
	private CalendarBookingHelper _calendarBookingHelper;

}