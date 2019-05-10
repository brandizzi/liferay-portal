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

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="com.liferay.frontend.taglib.clay.servlet.taglib.util.JSPNavigationItemList" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
String tabs = ParamUtil.getString(request, "tabs", "results-rankings");
%>

<clay:navigation-bar
	inverted="<%= true %>"
	navigationItems="<%=
		new JSPNavigationItemList(pageContext) {
			{
				add(
					navigationItem -> {
						navigationItem.setActive(tabs.equals("results-rankings"));
						navigationItem.setHref(renderResponse.createRenderURL(), "tabs", "results-rankings");
						navigationItem.setLabel(LanguageUtil.get(request, "results-rankings"));
					});
				add(
					navigationItem -> {
						navigationItem.setActive(tabs.equals("field-weights"));
						navigationItem.setHref(renderResponse.createRenderURL(), "tabs", "field-weights");
						navigationItem.setLabel(LanguageUtil.get(request, "field-weights"));
					});
				add(
					navigationItem -> {
						navigationItem.setActive(tabs.equals("synonym-sets"));
						navigationItem.setHref(renderResponse.createRenderURL(), "tabs", "synonym-sets");
						navigationItem.setLabel(LanguageUtil.get(request, "synonym-sets"));
					});
			}
		}
	%>"
/>

<c:choose>
	<c:when test='<%= tabs.equals("results-rankings") %>'>
		<liferay-util:include page="/view_results_rankings.jsp" servletContext="<%= application %>" />
	</c:when>
	<c:when test='<%= tabs.equals("field-weights") %>'>
		<liferay-util:include page="/field_mappings.jsp" servletContext="<%= application %>" />
	</c:when>
	<c:when test='<%= tabs.equals("synonym-sets") %>'>
		<liferay-util:include page="/add_synonym_sets.jsp" servletContext="<%= application %>" />
	</c:when>
</c:choose>