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
taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.portal.search.ranking.web.internal.constants.ResultsRankingPortletKeys" %><%@
page import="com.liferay.portal.search.ranking.web.internal.display.context.SynonymSetsPortletDisplayContext" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<%
SynonymSetsPortletDisplayContext synonymSetsPortletDisplayContext = (SynonymSetsPortletDisplayContext)request.getAttribute(ResultsRankingPortletKeys.SYNONYM_SETS_DISPLAY_CONTEXT);
%>

<clay:management-toolbar
	actionDropdownItems="<%= synonymSetsPortletDisplayContext.getActionDropdownItems() %>"
	clearResultsURL="<%= synonymSetsPortletDisplayContext.getClearResultsURL() %>"
	componentId="synonymSetsEntriesManagementToolbar"
	creationMenu="<%= synonymSetsPortletDisplayContext.getCreationMenu() %>"
	disabled="<%= synonymSetsPortletDisplayContext.isDisabledManagementBar() %>"
	filterDropdownItems="<%= synonymSetsPortletDisplayContext.getFilterItemsDropdownItems() %>"
	itemsTotal="<%= synonymSetsPortletDisplayContext.getTotalItems() %>"
	searchActionURL="<%= synonymSetsPortletDisplayContext.getSearchActionURL() %>"
	searchContainerId="synonymSetsEntries"
	searchFormName="searchFm"
	selectable="<%= true %>"
	showCreationMenu="<%= synonymSetsPortletDisplayContext.isShowCreationMenu() %>"
	sortingOrder="<%= synonymSetsPortletDisplayContext.getOrderByType() %>"
	sortingURL="<%= synonymSetsPortletDisplayContext.getSortingURL() %>"
/>

<aui:form cssClass="container-fluid-1280" method="post" name="SynonymSetsEntriesFm">
	<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />

	<liferay-ui:search-container
		id="synonymSetsEntries"
		searchContainer="<%= synonymSetsPortletDisplayContext.getSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.portal.search.ranking.web.internal.display.context.SynonymSetsEntryDisplayContext"
			keyProperty="synonymSetsEntryId"
			modelVar="synonymSetsEntry"
		>
			<portlet:renderURL var="rowURL">
				<portlet:param name="mvcRenderCommandName" value="updateSynonymsEntryRender" />
				<portlet:param name="redirect" value="<%= currentURL %>" />
				<portlet:param name="synonymSets" value="<%= synonymSetsEntry.getSynonyms() %>" />
			</portlet:renderURL>

			<liferay-ui:search-container-column-text
				colspan="<%= 2 %>"
				cssClass="table-cell-expand table-title"
			>
				<h2 class="h5">
					<aui:a href="<%= rowURL %>">
						<%= synonymSetsEntry.getSynonyms() %>
					</aui:a>
				</h2>

				<span class="text-default">
					<span class="label label-success text-uppercase">
						<liferay-ui:message key="approved" />
					</span>
				</span>
			</liferay-ui:search-container-column-text>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</aui:form>