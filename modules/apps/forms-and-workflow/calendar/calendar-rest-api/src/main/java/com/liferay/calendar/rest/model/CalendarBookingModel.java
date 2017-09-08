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

package com.liferay.calendar.rest.model;

/**
 * @author Adam Brandizzi
 */
public interface CalendarBookingModel {

	public long getCalendarBookingId();

	public long getCalendarId();

	public String getDescription();

	public long getEndTime();

	public int getEndTimeDay();

	public int getEndTimeHour();

	public int getEndTimeMinute();

	public int getEndTimeMonth();

	public int getEndTimeYear();

	public long getFirstReminder();

	public String getFirstReminderType();

	public int getInstanceIndex();

	public String getLocation();

	public long getParentCalendarBookingId();

	public String getRecurrence();

	public long getRecurringCalendarBookingId();

	public long getSecondReminder();

	public String getSecondReminderType();

	public long getStartTime();

	public int getStartTimeDay();

	public int getStartTimeHour();

	public int getStartTimeMinute();

	public int getStartTimeMonth();

	public int getStartTimeYear();

	public int getStatus();

	public String getTitle();

	public boolean hasChildCalendarBookings();

	public boolean hasWorkflowInstanceLink();

	public boolean isAllDay();

}