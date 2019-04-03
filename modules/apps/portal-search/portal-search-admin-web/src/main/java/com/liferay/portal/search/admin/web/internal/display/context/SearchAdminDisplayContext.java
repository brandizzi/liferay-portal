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
import javax.servlet.jsp.PageContext;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.JSPNavigationItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemList;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.ParamUtil;

/**
 * @author Adam Brandizzi
 */
public class SearchAdminDisplayContext {

	private RenderRequest _renderRequest;
	private PageContext _pageContext;
	private NavigationItemList _navigationItemList;
	private String _tab;

	public String getTab() {
		return _tab;
	}
	
	public NavigationItemList getNavigationItemList() {
		return _navigationItemList;
	}

	public void setNavigationItemList(
			NavigationItemList navigationItemList) {

		_navigationItemList = navigationItemList;
	}

	public void setTab(String tab) {
		_tab = tab;
	}
	
}
