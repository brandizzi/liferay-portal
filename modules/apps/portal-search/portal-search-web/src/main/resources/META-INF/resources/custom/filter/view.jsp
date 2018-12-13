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

<%@ page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.StringUtil" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.portal.search.web.internal.custom.filter.display.context.CustomFilterDisplayContext" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<portlet:defineObjects />

<style>
	.facet-checkbox-label {
		display: block;
	}

	.facet-term-selected {
		font-weight: 600;
	}

	.facet-term-unselected {
		font-weight: 400;
	}
</style>

<%
CustomFilterDisplayContext customFilterDisplayContext = (CustomFilterDisplayContext)java.util.Objects.requireNonNull(request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT));
%>

<liferay-ui:panel-container
	extended="<%= true %>"
	id='<%= renderResponse.getNamespace() + "facetCustomPanelContainer" %>'
	markupView="lexicon"
	persistState="<%= true %>"
>
	<c:if test="<%= !customFilterDisplayContext.isInvisible() %>">
		<liferay-ui:panel
			collapsible="<%= true %>"
			cssClass="search-facet"
			id='<%= renderResponse.getNamespace() + "facetCustomPanel" %>'
			markupView="lexicon"
			persistState="<%= true %>"
			title="<%= customFilterDisplayContext.getDisplayCaption() %>"
		>
			<aui:form method="post" name="customFilterForm">
				<aui:input cssClass="filter-parameter-name" name="filter-parameter-name" type="hidden" value="<%= customFilterDisplayContext.getParameterName() %>" />

				<div class="input-group search-bar search-bar-simple">
					<div class="input-group-item search-bar-keywords-input-wrapper">
						<input class="filter-keyword form-control input-group-inset input-group-inset-after search-bar-keywords-input" id="<portlet:namespace /><%= StringUtil.randomId() %>" name="<%= HtmlUtil.escapeAttribute(customFilterDisplayContext.getParameterValue()) %>" placeholder="<%= LanguageUtil.get(request, "search-...") %>" title="<%= LanguageUtil.get(request, "search") %>" type="text" value="<%= HtmlUtil.escapeAttribute(customFilterDisplayContext.getParameterValue()) %>" />

						<div class="input-group-inset-item input-group-inset-item-after search-bar-search-button-wrapper">
							<clay:button
								ariaLabel='<%= LanguageUtil.get(request, "submit") %>'
								elementClasses="search-bar-search-button"
								icon="search"
								id="submitCustomFilterForm"
								style="unstyled"
								type="submit"
							/>
						</div>
					</div>
				</div>
			</aui:form>
		</liferay-ui:panel>

		<aui:script use="liferay-custom-search-bar">
			new Liferay.Search.CustomSearchBar(A.one('#<portlet:namespace/>customFilterForm'));
		</aui:script>
	</c:if>
</liferay-ui:panel-container>