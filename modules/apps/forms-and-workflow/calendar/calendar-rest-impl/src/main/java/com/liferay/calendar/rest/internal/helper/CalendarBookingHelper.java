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

import com.liferay.calendar.model.CalendarBooking;
import com.liferay.calendar.service.CalendarBookingService;
import com.liferay.calendar.util.comparator.CalendarBookingStartTimeComparator;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.ArrayUtil;

import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adam Brandizzi
 */
@Component(immediate = true, service = CalendarBookingHelper.class)
public class CalendarBookingHelper {

	public List<CalendarBooking> search(
			long companyId, long[] calendarIds, long startTime, long endTime,
			int[] statuses, int eventsPerPage)
		throws PortalException {

		List<CalendarBooking> calendarBookings =
			Collections.<CalendarBooking>emptyList();

		if (!ArrayUtil.isEmpty(calendarIds)) {
			calendarBookings = calendarBookingService.search(
				companyId, new long[0], calendarIds, new long[0], -1, null,
				startTime, endTime, true, statuses, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS,
				new CalendarBookingStartTimeComparator(true));

			if ((eventsPerPage > 0) &&
				(eventsPerPage < calendarBookings.size())) {

				calendarBookings = calendarBookings.subList(0, eventsPerPage);
			}
		}

		return calendarBookings;
	}

	@Reference
	protected CalendarBookingService calendarBookingService;

}