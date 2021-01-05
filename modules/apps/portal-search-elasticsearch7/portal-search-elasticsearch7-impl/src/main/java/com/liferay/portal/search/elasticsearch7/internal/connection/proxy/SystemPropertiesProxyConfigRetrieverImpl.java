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

package com.liferay.portal.search.elasticsearch7.internal.connection.proxy;

import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.SystemProperties;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adam Brandizzi
 */
@Component(service = SystemPropertiesProxyConfigRetriever.class)
public class SystemPropertiesProxyConfigRetrieverImpl
	implements SystemPropertiesProxyConfigRetriever {

	@Override
	public String getHost() {
		return SystemProperties.get("http.proxyHost");
	}

	@Override
	public int getPort() {
		return GetterUtil.getInteger(SystemProperties.get("http.port"));
	}

	@Override
	public boolean isConfigured() {
		return http.hasProxyConfig();
	}

	@Override
	public boolean shouldBeProxied(String networkHostAddress) {
		return !http.isNonProxyHost(networkHostAddress);
	}

	@Reference
	protected Http http;

}