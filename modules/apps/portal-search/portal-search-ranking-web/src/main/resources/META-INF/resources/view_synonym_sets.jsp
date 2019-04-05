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
SynonymSetsDisplayContext synonymSetsDisplayContext = (SynonymSetsDisplayContext)request.getAttribute(SearchRankingPortletKeys.SYNONYM_SETS_DISPLAY_CONTEXT);
%>

<%-- FIXME: BEGIN MOCK CODE --%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@	page import="com.liferay.portal.search.ranking.web.internal.display.context.SynonymSetsMock" %>
<%@	page import="com.liferay.portal.kernel.dao.search.SearchContainer" %>
<%
	List results = new ArrayList();
	results.add(new SynonymSetsMock(101, "car, automobile", "APPROVED"));
	results.add(new SynonymSetsMock(201, "phone, cellphone", "DRAFT"));

	SearchContainer searchContainerMock = synonymSetsDisplayContext.getSearchContainer();
	searchContainerMock.setResults(results);
%>
<%-- FIXME: END MOCK CODE --%>

<clay:management-toolbar
	actionDropdownItems="<%= synonymSetsDisplayContext.getActionDropdownItems() %>"
	clearResultsURL="<%= synonymSetsDisplayContext.getClearResultsURL() %>"
	componentId="synonymSetsEntriesManagementToolbar"
	creationMenu="<%= synonymSetsDisplayContext.getCreationMenu() %>"
	disabled="<%= synonymSetsDisplayContext.isDisabledManagementBar() %>"
	filterDropdownItems="<%= synonymSetsDisplayContext.getFilterItemsDropdownItems() %>"
	itemsTotal="<%= synonymSetsDisplayContext.getTotalItems() %>"
	searchActionURL="<%= synonymSetsDisplayContext.getSearchActionURL() %>"
	searchContainerId="synonymSetsEntries"
	searchFormName="searchFm"
	selectable="<%= true %>"
	showCreationMenu="<%= synonymSetsDisplayContext.isShowCreationMenu() %>"
	sortingOrder="<%= synonymSetsDisplayContext.getOrderByType() %>"
	sortingURL="<%= synonymSetsDisplayContext.getSortingURL() %>"
/>

<aui:form cssClass="container-fluid-1280" method="post" name="SynonymSetsEntriesFm">
	<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />

	<liferay-ui:search-container
		id="synonymSetsEntries"
		searchContainer="<%= synonymSetsDisplayContext.getSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.portal.search.ranking.web.internal.display.context.SynonymSetsMock"
			keyProperty="synonymSetsEntryId"
			modelVar="synonymSetsEntry"
		>
			<portlet:renderURL var="rowURL">
				<portlet:param name="mvcRenderCommandName" value="editSynonymSetsEntry" />
				<portlet:param name="redirect" value="<%= currentURL %>" />
				<portlet:param name="synonymSetsEntryId" value="<%= String.valueOf(synonymSetsEntry.getId()) %>" />
			</portlet:renderURL>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand table-title"
				colspan="<%= 2 %>"
			>
				<h2 class="h5">
					<aui:a href="<%= rowURL %>">
						<%= synonymSetsEntry.getSynonyms() %>
					</aui:a>
				</h2>

				<span class="text-default">
					<c:choose>
						<c:when test="<%= synonymSetsEntry.isApproved() %>">
							<span class="label label-success text-uppercase">
								<liferay-ui:message key="approved" />
							</span>
						</c:when>
						<c:otherwise>
							<span class="label label-secondary text-uppercase">
								<liferay-ui:message key="draft" />
							</span>
						</c:otherwise>
					</c:choose>
				</span>
			</liferay-ui:search-container-column-text>

		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</aui:form>