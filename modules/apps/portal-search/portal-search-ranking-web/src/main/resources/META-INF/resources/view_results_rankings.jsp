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
ResultsRankingPortletDisplayContext resultsRankingPortletDisplayContext = (ResultsRankingPortletDisplayContext)request.getAttribute(ResultsRankingPortletKeys.RESULTS_RANKING_DISPLAY_CONTEXT);
%>

<clay:management-toolbar
	actionDropdownItems="<%= resultsRankingPortletDisplayContext.getActionDropdownItems() %>"
	clearResultsURL="<%= resultsRankingPortletDisplayContext.getClearResultsURL() %>"
	componentId="resultsRankingEntriesManagementToolbar"
	creationMenu="<%= resultsRankingPortletDisplayContext.getCreationMenu() %>"
	disabled="<%= resultsRankingPortletDisplayContext.isDisabledManagementBar() %>"
	filterDropdownItems="<%= resultsRankingPortletDisplayContext.getFilterItemsDropdownItems() %>"
	itemsTotal="<%= resultsRankingPortletDisplayContext.getTotalItems() %>"
	searchActionURL="<%= resultsRankingPortletDisplayContext.getSearchActionURL() %>"
	searchContainerId="resultsRankingEntries"
	searchFormName="searchFm"
	selectable="<%= true %>"
	showCreationMenu="<%= resultsRankingPortletDisplayContext.isShowCreationMenu() %>"
	sortingOrder="<%= resultsRankingPortletDisplayContext.getOrderByType() %>"
	sortingURL="<%= resultsRankingPortletDisplayContext.getSortingURL() %>"
/>

<portlet:actionURL name="deleteResultsRankingsEntry" var="deleteResultsRankingsEntryURL">
	<portlet:param name="redirect" value="<%= currentURL %>" />
</portlet:actionURL>

<aui:form action="<%= deleteResultsRankingsEntryURL %>" cssClass="container-fluid-1280" method="post" name="resultsRankingEntriesFm">
	<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />

	<liferay-ui:search-container
		id="resultsRankingEntries"
		searchContainer="<%= resultsRankingPortletDisplayContext.getSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.portal.kernel.search.Document"
			keyProperty="resultsRankingsEntryId"
			modelVar="resultsRankingsEntry"
		>
			<portlet:renderURL var="rowURL">
				<portlet:param name="mvcRenderCommandName" value="editResultsRankingEntry" />
				<portlet:param name="redirect" value="<%= currentURL %>" />
				<portlet:param name="ResultsRankingsEntryId" value="0" />
			</portlet:renderURL>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand table-title"
				href="<%= rowURL %>"
				name="name"
				value="Test Title"
			/>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand-smallest table-cell-minw-150"
				name="pinned"
				value="0"
			/>

			<liferay-ui:search-container-column-date
				cssClass="table-cell-expand-smallest table-cell-minw-150 table-cell-ws-nowrap"
				name="modified-date"
				value="Test Date"
			/>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</aui:form>