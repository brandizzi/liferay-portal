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

package com.liferay.calendar.rest.internal.model;

import com.liferay.calendar.rest.model.CalendarResourceModel;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Adam Brandizzi
 */
@XmlRootElement
public class CalendarResourceModelImpl implements CalendarResourceModel {

	public CalendarResourceModelImpl() {
		_calendarResourceId = 0;
		_classNameId = 0;
		_classPK = 0;
		_classUuId = null;
		_code = null;
		_groupId = 0;
		_name = null;
		_userId = 0;
	}

	public CalendarResourceModelImpl(
		long groupId, long userId, long calendarResourceId, String name,
		String classUuId, long classNameId, long classPK, String code) {

		_groupId = groupId;
		_userId = userId;
		_calendarResourceId = calendarResourceId;
		_name = name;
		_classUuId = classUuId;
		_classNameId = classNameId;
		_classPK = classPK;
		_code = code;
	}

	@Override
	@XmlElement
	public long getCalendarResourceId() {
		return _calendarResourceId;
	}

	@Override
	@XmlElement
	public long getClassNameId() {
		return _classNameId;
	}

	@Override
	@XmlElement
	public long getClassPK() {
		return _classPK;
	}

	@Override
	@XmlElement
	public String getClassUuId() {
		return _classUuId;
	}

	@Override
	@XmlElement
	public String getCode() {
		return _code;
	}

	@Override
	@XmlElement
	public long getGroupId() {
		return _groupId;
	}

	@Override
	@XmlElement
	public String getName() {
		return _name;
	}

	@Override
	@XmlElement
	public long getUserId() {
		return _userId;
	}

	private final long _calendarResourceId;
	private final long _classNameId;
	private final long _classPK;
	private final String _classUuId;
	private final String _code;
	private final long _groupId;
	private final String _name;
	private final long _userId;

}