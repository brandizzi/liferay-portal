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

package com.liferay.portal.search.admin.web.internal.display.context;

import com.liferay.portal.kernel.search.SearchEngine;
import com.liferay.portal.kernel.search.SearchEngineHelper;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.web.information.SearchEngineInformation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

/**
 * @author Adam Brandizzi
 */
@Component(immediate = true, service = SearchAdminDisplayContextFactory.class)
public class SearchAdminDisplayContextFactoryImpl
	implements SearchAdminDisplayContextFactory {

	private static final String[] _SEARCH_ADMIN_TABS = { "general", "reindex" };

	@Override
	public SearchAdminDisplayContext create(
			RenderRequest renderRequest, RenderResponse renderResponse,
			PortletPreferences portletPreferences)
		throws PortletException {

		SearchEngineInformation searchEngineInformation = getSearchEngineInformation();

		return new SearchAdminDisplayContext(
			searchEngineInformation.getStatusString(), _SEARCH_ADMIN_TABS);
	}

	protected SearchEngineInformation getSearchEngineInformation() {
		String searchEngineId = _searchEngineHelper.getDefaultSearchEngineId();

		SearchEngine searchEngine = _searchEngineHelper.getSearchEngine(
			searchEngineId);

		String vendor = searchEngine.getVendor();

		if (Validator.isNull(vendor)) {
			vendor = "null";
		}

		return _searchEngineInformationMap.get(vendor);
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		unbind = "unregisterSearchEngineInformation"
	)
	protected void registerSearchEngineInformation(
		SearchEngineInformation searchEngineInformation,
		Map<String, Object> properties) {

		String vendor = MapUtil.getString(properties, "search.engine.impl");

		_searchEngineInformationMap.put(vendor, searchEngineInformation);
	}

	protected void unregisterSearchEngineInformation(
		SearchEngineInformation searchEngineInformation,
		Map<String, Object> properties) {

		String vendor = GetterUtil.getString(properties, "search.engine.impl");

		_searchEngineInformationMap.remove(vendor);
	}

	@Reference
	private SearchEngineHelper _searchEngineHelper;

	private final Map<String, SearchEngineInformation>
		_searchEngineInformationMap = new ConcurrentHashMap<>();

}