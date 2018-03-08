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

<%@ page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.search.web.internal.suggestions.portlet.SearchSuggestionsPortletPreferences" %><%@
page import="com.liferay.portal.search.web.internal.suggestions.portlet.SearchSuggestionsPortletPreferencesImpl" %><%@
page import="com.liferay.portal.search.web.internal.util.PortletPreferencesJspUtil" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<portlet:defineObjects />

<%
SearchSuggestionsPortletPreferences searchSuggestionsPortletPreferences = new SearchSuggestionsPortletPreferencesImpl(java.util.Optional.ofNullable(portletPreferences));
%>

<liferay-portlet:actionURL portletConfiguration="<%= true %>" var="configurationActionURL" />

<liferay-portlet:renderURL portletConfiguration="<%= true %>" var="configurationRenderURL" />

<aui:form action="<%= configurationActionURL %>" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
	<aui:input name="redirect" type="hidden" value="<%= configurationRenderURL %>" />

	<div class="portlet-configuration-body-content">
		<div class="container-fluid-1280">
			<aui:input helpMessage="collated-spell-check-result-enabled-help" id="spellCheckSuggestionEnabled" label="display-did-you-mean-if-the-number-of-search-results-does-not-meet-the-threshold" name="<%= PortletPreferencesJspUtil.getInputName(SearchSuggestionsPortletPreferences.PREFERENCE_KEY_SPELL_CHECK_SUGGESTION_ENABLED) %>" type="checkbox" value="<%= searchSuggestionsPortletPreferences.isSpellCheckSuggestionEnabled() %>" />

			<div class="options-container <%= !searchSuggestionsPortletPreferences.isSpellCheckSuggestionEnabled() ? "hide" : StringPool.BLANK %>" id="<portlet:namespace />spellCheckSuggestionOptionsContainer">
				<liferay-ui:toggle-area
					align="none"
					defaultShowContent="<%= searchSuggestionsPortletPreferences.isSpellCheckSuggestionEnabled() %>"
					hideMessage='<%= "&laquo; " + LanguageUtil.get(request, "hide-options") %>'
					showMessage='<%= LanguageUtil.get(request, "show-options") + " &raquo;" %>'
				>
					<aui:input disabled="<%= !searchSuggestionsPortletPreferences.isSpellCheckSuggestionEnabled() %>" helpMessage="collated-spell-check-result-display-threshold-help" label="threshold-for-displaying-did-you-mean" name="<%= PortletPreferencesJspUtil.getInputName(SearchSuggestionsPortletPreferences.PREFERENCE_KEY_SPELL_CHECK_SUGGESTION_DISPLAY_THRESHOLD) %>" size="10" type="text" value="<%= searchSuggestionsPortletPreferences.getSpellCheckSuggestionDisplayThreshold() %>" />
				</liferay-ui:toggle-area>
			</div>

			<aui:input helpMessage="query-suggestions-enabled-help" id="relatedSuggestionsEnabled" label="display-related-queries" name="<%= PortletPreferencesJspUtil.getInputName(SearchSuggestionsPortletPreferences.PREFERENCE_KEY_RELATED_SUGGESTIONS_ENABLED) %>" type="checkbox" value="<%= searchSuggestionsPortletPreferences.isRelatedSuggestionsEnabled() %>" />

			<div class="options-container <%= !searchSuggestionsPortletPreferences.isRelatedSuggestionsEnabled() ? "hide" : StringPool.BLANK %>" id="<portlet:namespace />relatedSuggestionsOptionsContainer">
				<liferay-ui:toggle-area
					align="none"
					defaultShowContent="<%= searchSuggestionsPortletPreferences.isRelatedSuggestionsEnabled() %>"
					hideMessage='<%= "&laquo; " + LanguageUtil.get(request, "hide-options") %>'
					id="toggle_id_search_configuration_query_suggestions"
					showMessage='<%= LanguageUtil.get(request, "show-options") + " &raquo;" %>'
				>
					<aui:input disabled="<%= !searchSuggestionsPortletPreferences.isRelatedSuggestionsEnabled() %>" label="maximum-number-of-related-queries" name="<%= PortletPreferencesJspUtil.getInputName(SearchSuggestionsPortletPreferences.PREFERENCE_KEY_RELATED_SUGGESTIONS_MAX) %>" size="10" type="text" value="<%= searchSuggestionsPortletPreferences.getRelatedSuggestionsMax() %>" />

					<aui:input disabled="<%= !searchSuggestionsPortletPreferences.isRelatedSuggestionsEnabled() %>" helpMessage="query-suggestions-display-threshold-help" label="threshold-for-displaying-related-queries" name="<%= PortletPreferencesJspUtil.getInputName(SearchSuggestionsPortletPreferences.PREFERENCE_KEY_RELATED_SUGGESTIONS_DISPLAY_THRESHOLD) %>" size="10" type="text" value="<%= searchSuggestionsPortletPreferences.getRelatedSuggestionsDisplayThreshold() %>" />
				</liferay-ui:toggle-area>
			</div>

			<aui:input helpMessage="query-indexing-enabled-help" label="add-new-related-queries-based-on-successful-queries" name="<%= PortletPreferencesJspUtil.getInputName(SearchSuggestionsPortletPreferences.PREFERENCE_KEY_QUERY_INDEXING_ENABLED) %>" type="checkbox" value="<%= searchSuggestionsPortletPreferences.isQueryIndexingEnabled() %>" />

			<div class="options-container <%= !searchSuggestionsPortletPreferences.isQueryIndexingEnabled() ? "hide" : StringPool.BLANK %>" id="<portlet:namespace />queryIndexingOptionsContainer">
				<liferay-ui:toggle-area
					align="none"
					defaultShowContent="<%= searchSuggestionsPortletPreferences.isQueryIndexingEnabled() %>"
					hideMessage='<%= "&laquo; " + LanguageUtil.get(request, "hide-options") %>'
					id="toggle_id_search_configuration_query_indexing"
					showMessage='<%= LanguageUtil.get(request, "show-options") + " &raquo;" %>'
				>
					<aui:input disabled="<%= !searchSuggestionsPortletPreferences.isQueryIndexingEnabled() %>" helpMessage="query-indexing-threshold-help" label="query-indexing-threshold" name="<%= PortletPreferencesJspUtil.getInputName(SearchSuggestionsPortletPreferences.PREFERENCE_KEY_QUERY_INDEXING_THRESHOLD) %>" size="10" type="text" value="<%= searchSuggestionsPortletPreferences.getQueryIndexingThreshold() %>" />
				</liferay-ui:toggle-area>
			</div>
		</div>
	</div>

	<aui:button-row>
		<aui:button type="submit" />
	</aui:button-row>
</aui:form>

<aui:script>
	Liferay.Util.toggleBoxes('<portlet:namespace />queryIndexingEnabled', '<portlet:namespace />queryIndexingOptionsContainer');
	Liferay.Util.toggleBoxes('<portlet:namespace />relatedSuggestionsEnabled', '<portlet:namespace />relatedSuggestionsOptionsContainer');
	Liferay.Util.toggleBoxes('<portlet:namespace />spellCheckSuggestionEnabled', '<portlet:namespace />spellCheckSuggestionOptionsContainer');
</aui:script>