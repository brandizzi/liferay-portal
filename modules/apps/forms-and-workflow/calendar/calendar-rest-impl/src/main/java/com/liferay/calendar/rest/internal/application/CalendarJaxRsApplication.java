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

package com.liferay.calendar.rest.internal.application;

import com.liferay.calendar.rest.internal.context.provider.CompanyContextProvider;
import com.liferay.calendar.rest.internal.context.provider.LocaleContextProvider;
import com.liferay.calendar.rest.internal.context.provider.UserContextProvider;
import com.liferay.calendar.rest.internal.resource.CalendarResourceResource;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adam Brandizzi
 */
@ApplicationPath("/")
@Component(immediate = true, service = Application.class)
public class CalendarJaxRsApplication extends Application {

	@Override
	public Set<Object> getSingletons() {
		Set<Object> singletons = new HashSet<>();

		singletons.add(_companyContextProvider);
		singletons.add(_localeContextProvider);
		singletons.add(_userContextProvider);
		singletons.add(_calendarResourceResource);

		return singletons;
	}

	@Reference
	private CalendarResourceResource _calendarResourceResource;

	@Reference
	private CompanyContextProvider _companyContextProvider;

	@Reference
	private LocaleContextProvider _localeContextProvider;

	@Reference
	private UserContextProvider _userContextProvider;

}