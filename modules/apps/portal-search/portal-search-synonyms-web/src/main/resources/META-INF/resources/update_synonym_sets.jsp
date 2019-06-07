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

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>

<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
String synonymSetsRootElementId = renderResponse.getNamespace() + "-synonym-sets-root";
String synonymSets = (String)request.getAttribute("synonymSets");

String redirect = ParamUtil.getString(request, "redirect");
portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(redirect);
%>

<portlet:actionURL name="updateSynonymsEntryAction" var="updateSynonymsEntryURL">
	<portlet:param name="mvcRenderCommandName" value="updateSynonymsEntryAction" />
</portlet:actionURL>

<liferay-frontend:edit-form
	action="<%= updateSynonymsEntryURL %>"
	name="synonymSetsForm"
>
	<aui:input name="synonymSetsInput" type="hidden" value="" />
	<aui:input name="originalSynonymSetsInput" type="hidden" value="" />

	<liferay-frontend:edit-form-body>
		<div id="<%= synonymSetsRootElementId %>"></div>
	</liferay-frontend:edit-form-body>
</liferay-frontend:edit-form>

<aui:script require='<%= npmResolvedPackageName + "/js/index-synonym-sets.es as SynonymSets" %>'>
	SynonymSets.default(
		'<%= synonymSetsRootElementId %>',
		{
			formName: '<%= renderResponse.getNamespace() + "synonymSetsForm" %>',
			inputName: '<%= renderResponse.getNamespace() + "synonymSetsInput" %>',
			originalInputName: '<%= renderResponse.getNamespace() + "originalSynonymSetsInput" %>',
			synonymSets: '<%= synonymSets %>'
		}
	);
</aui:script>