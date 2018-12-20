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
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>

<%@ page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.search.web.internal.custom.boost.portlet.CustomBoostPortletPreferences" %><%@
page import="com.liferay.portal.search.web.internal.custom.boost.portlet.CustomBoostPortletPreferencesImpl" %><%@
page import="com.liferay.portal.search.web.internal.util.PortletPreferencesJspUtil" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
CustomBoostPortletPreferences customBoostPortletPreferences = new CustomBoostPortletPreferencesImpl(java.util.Optional.of(portletPreferences));
%>

<liferay-portlet:actionURL portletConfiguration="<%= true %>" var="configurationActionURL" />

<liferay-portlet:renderURL portletConfiguration="<%= true %>" var="configurationRenderURL" />

<liferay-frontend:edit-form
	action="<%= configurationActionURL %>"
	method="post"
	name="fm"
>
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
	<aui:input name="redirect" type="hidden" value="<%= configurationRenderURL %>" />

	<liferay-frontend:edit-form-body>
		<liferay-frontend:fieldset-group>
			<aui:input helpMessage="boost-field-help" label="boost-field" name="<%= PortletPreferencesJspUtil.getInputName(CustomBoostPortletPreferences.PREFERENCE_KEY_BOOST_FIELD) %>" value="<%= customBoostPortletPreferences.getBoostFieldString() %>" />

			<aui:input helpMessage="boost-heading-help" label="boost-heading" name="<%= PortletPreferencesJspUtil.getInputName(CustomBoostPortletPreferences.PREFERENCE_KEY_CUSTOM_HEADING) %>" value="<%= customBoostPortletPreferences.getCustomHeading() %>" />

			<aui:input helpMessage="boost-parameter-name-help" label="boost-parameter-name" name="<%= PortletPreferencesJspUtil.getInputName(CustomBoostPortletPreferences.PREFERENCE_KEY_CUSTOM_PARAMETER_NAME) %>" value="<%= customBoostPortletPreferences.getCustomParameterName() %>" />

			<aui:input helpMessage="boost-values-help" label="boost-values" name="<%= PortletPreferencesJspUtil.getInputName(CustomBoostPortletPreferences.PREFERENCE_KEY_BOOST_VALUES) %>" value="<%= customBoostPortletPreferences.getBoostValues() %>" />

			<aui:input helpMessage="boost-increment-help" label="boost-increment" name="<%= PortletPreferencesJspUtil.getInputName(CustomBoostPortletPreferences.PREFERENCE_KEY_BOOST_INCREMENT) %>" required="<%= true %>" value="<%= customBoostPortletPreferences.getBoostIncrement() %>">
				<aui:validator name="number" />
				<aui:validator name="range">[1,1000]</aui:validator>
			</aui:input>

			<aui:input helpMessage="boost-invisible-help" label="boost-invisible" name="<%= PortletPreferencesJspUtil.getInputName(CustomBoostPortletPreferences.PREFERENCE_KEY_IS_INVISIBLE) %>" type="checkbox" value="<%= customBoostPortletPreferences.isInvisible() %>" />
		</liferay-frontend:fieldset-group>
	</liferay-frontend:edit-form-body>

	<liferay-frontend:edit-form-footer>
		<aui:button type="submit" />

		<aui:button type="cancel" />
	</liferay-frontend:edit-form-footer>
</liferay-frontend:edit-form>