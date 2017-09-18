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

import com.liferay.calendar.model.CalendarBooking;
import com.liferay.calendar.rest.internal.conversor.CalendarBookingConverter;
import com.liferay.calendar.rest.internal.helper.CalendarBookingHelper;
import com.liferay.calendar.rest.model.CalendarBookingModel;
import com.liferay.calendar.rest.resource.CalendarBookingResource;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.Company;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
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
	@Path("/search")
	@Produces("application/json")
	public List<CalendarBookingModel> getCalendarBookings(
		@Context Company company, @Context Locale locale,
		@QueryParam("calendarIds") long[] calendarIds,
		@QueryParam("startTime") String startTime,
		@QueryParam("endTime") String endTime,
		@QueryParam("statuses") int[] statuses,
		@QueryParam("eventsperPage") int eventsPerPage) {

		try {
			DateFormat df = new SimpleDateFormat(
				"yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");

			Date startDate = df.parse(startTime);
			Date endDate = df.parse(endTime);

			List<CalendarBooking> calendarBookings =
				_calendarBookingHelper.search(
					company.getCompanyId(), calendarIds, startDate.getTime(),
					endDate.getTime(), statuses, eventsPerPage);

			return _calendarBookingConverter.toCalendarBookingModels(
				calendarBookings, locale);
		}
		catch (ParseException | PortalException e) {
			throw new SystemException(e);
		}
	}

	@Reference
	private CalendarBookingConverter _calendarBookingConverter;

	@Reference
	private CalendarBookingHelper _calendarBookingHelper;

}