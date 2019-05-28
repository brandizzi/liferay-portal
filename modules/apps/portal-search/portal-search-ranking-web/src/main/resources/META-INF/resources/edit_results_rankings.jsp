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

<%@ page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.StringUtil" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portal.kernel.workflow.WorkflowConstants" %><%@
page import="com.liferay.portal.search.ranking.web.internal.constants.SearchTuningPortletKeys" %>

<%@ page import="javax.portlet.PortletURL" %>

<liferay-frontend:defineObjects />

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
String redirect = ParamUtil.getString(request, "redirect");

if (Validator.isNull(redirect)) {
	PortletURL portletURL = renderResponse.createRenderURL();

	redirect = portletURL.toString();
}

String resultsRankingsRootElementId = renderResponse.getNamespace() + "-results-rankings-root";

String resultsRankingUid = ParamUtil.getString(request, "resultsRankingUid");
String keywords = ParamUtil.getString(request, "keywords");
String companyId = ParamUtil.getString(request, "companyId");
String[] aliases = StringUtil.split(ParamUtil.getString(request, "aliases"), StringPool.COMMA_AND_SPACE);
%>

<portlet:actionURL name="/results_ranking/edit" var="addResultsRankingEntryURL" />

<aui:form action="<%= addResultsRankingEntryURL %>" name="editResultsRankingsFm" onSubmit="event.preventDefault();">
	<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
	<aui:input name="companyId" type="hidden" value="<%= companyId %>" />
	<aui:input name="keywords" type="hidden" value="<%= keywords %>" />
	<aui:input name="resultsRankingUid" type="hidden" value="<%= resultsRankingUid %>" />
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />

	<div id="<%= resultsRankingsRootElementId %>">
		<div class="loading-animation-container">
			<span aria-hidden="true" class="loading-animation"></span>
		</div>
	</div>
</aui:form>

<liferay-portlet:resourceURL id="/results_ranking/get_results" portletName="<%= SearchTuningPortletKeys.SEARCH_TUNING %>" var="resultsRankingResourceURL">
	<portlet:param name="resultsRankingUid" value="<%= resultsRankingUid %>" />
	<portlet:param name="companyId" value="<%= companyId %>" />
	<portlet:param name="<%= Constants.CMD %>" value="getVisibleResults" />
</liferay-portlet:resourceURL>

<liferay-portlet:resourceURL id="/results_ranking/get_results" portletName="<%= SearchTuningPortletKeys.SEARCH_TUNING %>" var="hiddenResultsRankingResourceURL">
	<portlet:param name="resultsRankingUid" value="<%= resultsRankingUid %>" />
	<portlet:param name="<%= Constants.CMD %>" value="getHiddenResults" />
</liferay-portlet:resourceURL>

<aui:script require='<%= npmResolvedPackageName + "/js/index.es as ResultsRankings" %>'>
	ResultsRankings.default(
		'<%= resultsRankingsRootElementId %>',
		{
			cancelUrl: '<%= HtmlUtil.escape(redirect) %>',
			fetchDocumentsHiddenUrl: '<%= hiddenResultsRankingResourceURL %>',
			fetchDocumentsUrl: '<%= resultsRankingResourceURL %>',
			formName: '<portlet:namespace />editResultsRankingsFm',
			initialAliases: <%= (aliases.length > 0) ? "['" + StringUtil.merge(aliases, "','") + "']" : "[]" %>,
			searchTerm: '<%= HtmlUtil.escape(keywords) %>'
		},
		{
			companyId: '<%= themeDisplay.getCompanyId() %>',
			constants: {
				WORKFLOW_ACTION_PUBLISH: '<%= WorkflowConstants.ACTION_PUBLISH %>',
				WORKFLOW_ACTION_SAVE_DRAFT: '<%= WorkflowConstants.ACTION_SAVE_DRAFT %>'
			},
			namespace: '<portlet:namespace />',
			spritemap: '<%= themeDisplay.getPathThemeImages() + "/lexicon/icons.svg" %>'
		}
	);
</aui:script>