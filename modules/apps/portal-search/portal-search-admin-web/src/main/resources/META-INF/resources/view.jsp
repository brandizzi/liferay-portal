<%--
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
--%>

<%@ include file="/init.jsp" %>

<%
String tabs1 = ParamUtil.getString(request, "tabs1", "index-actions");
%>

<clay:navigation-bar
	inverted="<%= true %>"
	navigationItems="<%=
		new JSPNavigationItemList(pageContext) {
			{
				add(
					navigationItem -> {
						navigationItem.setActive(tabs1.equals("index-actions"));
						navigationItem.setHref(renderResponse.createRenderURL(), "tabs1", "index-actions");
						navigationItem.setLabel(LanguageUtil.get(request, "index-actions"));
					});

				add(
					navigationItem -> {
						navigationItem.setActive(tabs1.equals("field-mappings"));
						navigationItem.setHref(renderResponse.createRenderURL(), "tabs1", "field-mappings");
						navigationItem.setLabel(LanguageUtil.get(request, "field-mappings"));
					});
			}
		}
	%>"
/>

<c:choose>
	<c:when test='<%= tabs1.equals("index-actions") %>'>
		<liferay-util:include page="/index_actions.jsp" servletContext="<%= application %>" />
	</c:when>
	<c:when test='<%= tabs1.equals("field-mappings") %>'>
		<liferay-util:include page="/field_mappings.jsp" servletContext="<%= application %>" />
	</c:when>
</c:choose>