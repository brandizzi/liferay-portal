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

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemList;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

/**
 * @author Adam Brandizzi
 */
public class SearchAdminDisplayBuilder {

	private RenderRequest _renderRequest;
	private RenderResponse _renderResponse;
	private Portal _portal;

	public SearchAdminDisplayBuilder(
		Portal portal, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_portal = portal;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
	}
	
	public SearchAdminDisplayContext build() {
		SearchAdminDisplayContext searchAdminDisplayContext =
			new SearchAdminDisplayContext();

		HttpServletRequest request = _portal.getHttpServletRequest(
			_renderRequest);
		String tab = ParamUtil.getString(
			_renderRequest, "tabs1", "index-actions");

		searchAdminDisplayContext.setNavigationItemList(
			new NavigationItemList() {
				{
					add(
						navigationItem -> {
							navigationItem.setActive(
								tab.equals("index-actions"));
							navigationItem.setHref(
								_renderResponse.createRenderURL(), "tabs1",
								"index-actions");
							navigationItem.setLabel(
								LanguageUtil.get(request, "index-actions"));
						});
	
					add(
						navigationItem -> {
							navigationItem.setActive(
								tab.equals("field-mappings"));
							navigationItem.setHref(
								_renderResponse.createRenderURL(), "tabs1",
								"field-mappings");
							navigationItem.setLabel(
								LanguageUtil.get(request, "field-mappings"));
						});
				}
			});
		searchAdminDisplayContext.setTab(tab);

		return searchAdminDisplayContext;
	}
}