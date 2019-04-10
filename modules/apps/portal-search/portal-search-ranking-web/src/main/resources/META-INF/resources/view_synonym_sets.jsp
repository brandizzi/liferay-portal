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
page import="com.liferay.portal.search.ranking.web.internal.display.context.SynonymSetsDisplayContext" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<%
SynonymSetsDisplayContext synonymSetsDisplayContext = (SynonymSetsDisplayContext)request.getAttribute(ResultsRankingPortletKeys.SYNONYM_SETS_DISPLAY_CONTEXT);
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

<aui:form cssClass="container-fluid-1280" method="post" name="synonymSetsEntriesFm">
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

			<liferay-ui:search-container-column-text>
				<clay:dropdown-actions
					dropdownItems="<%= synonymSetsDisplayContext.getActionDropdownItems() %>"
				/>
			</liferay-ui:search-container-column-text>

		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</aui:form>