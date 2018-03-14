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

package com.liferay.portal.search.web.internal.suggestions.portlet;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.web.internal.portlet.shared.task.PortletSharedRequestHelper;
import com.liferay.portal.search.web.internal.suggestions.QuerySuggestionBuilder;
import com.liferay.portal.search.web.internal.suggestions.constants.SearchSuggestionsPortletKeys;
import com.liferay.portal.search.web.internal.suggestions.display.context.SearchSuggestionsPortletDisplayBuilder;
import com.liferay.portal.search.web.internal.suggestions.display.context.SearchSuggestionsPortletDisplayContext;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchContributor;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchRequest;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchResponse;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchSettings;
import com.liferay.portal.search.web.search.request.SearchSettings;

import java.io.IOException;

import java.util.Optional;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author André de Oliveira
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-search-bar",
		"com.liferay.portlet.display-category=category.search",
		"com.liferay.portlet.header-portlet-css=/search/suggestions/css/main.css",
		"com.liferay.portlet.icon=/icons/search.png",
		"com.liferay.portlet.instanceable=true",
		"com.liferay.portlet.layout-cacheable=true",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.restore-current-view=false",
		"com.liferay.portlet.use-default-template=true",
		"javax.portlet.display-name=Search Suggestions Portlet",
		"javax.portlet.expiration-cache=0",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/search/suggestions/view.jsp",
		"javax.portlet.name=" + SearchSuggestionsPortletKeys.SEARCH_SUGGESTIONS,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=guest,power-user,user",
		"javax.portlet.supports.mime-type=text/html"
	},
	service = {Portlet.class, PortletSharedSearchContributor.class}
)
public class SearchSuggestionsPortlet
	extends MVCPortlet implements PortletSharedSearchContributor {

	@Override
	public void contribute(
		PortletSharedSearchSettings portletSharedSearchSettings) {

		SearchSuggestionsPortletPreferences
			searchSuggestionsPortletPreferences =
				new SearchSuggestionsPortletPreferencesImpl(
					portletSharedSearchSettings.getPortletPreferences());

		setUpRelatedSuggestions(
			searchSuggestionsPortletPreferences, portletSharedSearchSettings);
		setUpSpellCheckSuggestion(
			searchSuggestionsPortletPreferences, portletSharedSearchSettings);
		setUpQueryIndexing(
			searchSuggestionsPortletPreferences, portletSharedSearchSettings);
	}

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		SearchSuggestionsPortletPreferences
			searchSuggestionsPortletPreferences =
				new SearchSuggestionsPortletPreferencesImpl(
					Optional.ofNullable(renderRequest.getPreferences()));

		PortletSharedSearchResponse portletSharedSearchResponse =
			portletSharedSearchRequest.search(renderRequest);

		SearchSuggestionsPortletDisplayContext
			searchSuggestionsPortletDisplayContext = buildDisplayContext(
				searchSuggestionsPortletPreferences,
				portletSharedSearchResponse, renderRequest);

		renderRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT,
			searchSuggestionsPortletDisplayContext);

		super.render(renderRequest, renderResponse);
	}

	protected SearchSuggestionsPortletDisplayContext buildDisplayContext(
		SearchSuggestionsPortletPreferences searchSuggestionsPortletPreferences,
		PortletSharedSearchResponse portletSharedSearchResponse,
		RenderRequest renderRequest) {

		SearchSuggestionsPortletDisplayBuilder
			searchSuggestionsPortletDisplayBuilder =
				new SearchSuggestionsPortletDisplayBuilder();
		Optional<String> keywordsOptional =
			portletSharedSearchResponse.getKeywords();

		SearchSettings searchSettings =
			portletSharedSearchResponse.getSearchSettings();

		Optional<String> keywordsParameterNameOptional =
			searchSettings.getKeywordsParameterName();

		String searchURL = portletSharedRequestHelper.getCompleteURL(
			renderRequest);

		keywordsParameterNameOptional.ifPresent(
			keywordsParameterName -> keywordsOptional.ifPresent(
				keywords -> setSuggestions(
					searchSuggestionsPortletDisplayBuilder, searchURL,
					keywordsParameterName, keywords,
					portletSharedSearchResponse)));

		searchSuggestionsPortletDisplayBuilder.setRelatedSuggestionsEnabled(
			searchSuggestionsPortletPreferences.isRelatedSuggestionsEnabled());
		searchSuggestionsPortletDisplayBuilder.setSpellCheckSuggestionEnabled(
			searchSuggestionsPortletPreferences.
				isSpellCheckSuggestionEnabled());

		return searchSuggestionsPortletDisplayBuilder.build();
	}

	protected void setSuggestions(
		SearchSuggestionsPortletDisplayBuilder
			searchSuggestionsPortletDisplayBuilder,
		String searchURL, String keywordsParameterName, String keywords,
		PortletSharedSearchResponse portletSharedSearchResponse) {

		QuerySuggestionBuilder querySuggestionBuilder =
			new QuerySuggestionBuilder();

		querySuggestionBuilder.setKeywords(keywords);
		querySuggestionBuilder.setParameterName(keywordsParameterName);
		querySuggestionBuilder.setRelatedSuggestionsStrings(
			portletSharedSearchResponse.getRelatedSuggestions());
		querySuggestionBuilder.setSearchURL(searchURL);
		querySuggestionBuilder.setSpellCheckSuggestionString(
			portletSharedSearchResponse.getSpellCheckSuggestion());

		searchSuggestionsPortletDisplayBuilder.setRelatedSuggestions(
			querySuggestionBuilder.getRelatedSuggestions());
		searchSuggestionsPortletDisplayBuilder.setSpellCheckSuggestion(
			querySuggestionBuilder.getSpellCheckSuggestion());
	}

	protected void setUpQueryIndexing(
		SearchSuggestionsPortletPreferences searchSuggestionsPortletPreferences,
		PortletSharedSearchSettings portletSharedSearchSettings) {

		QueryConfig queryConfig = portletSharedSearchSettings.getQueryConfig();

		queryConfig.setQueryIndexingEnabled(
			searchSuggestionsPortletPreferences.isQueryIndexingEnabled());
		queryConfig.setQueryIndexingThreshold(
			searchSuggestionsPortletPreferences.getQueryIndexingThreshold());
	}

	protected void setUpRelatedSuggestions(
		SearchSuggestionsPortletPreferences searchSuggestionsPortletPreferences,
		PortletSharedSearchSettings portletSharedSearchSettings) {

		QueryConfig queryConfig = portletSharedSearchSettings.getQueryConfig();

		queryConfig.setQuerySuggestionEnabled(
			searchSuggestionsPortletPreferences.isRelatedSuggestionsEnabled());
		queryConfig.setQuerySuggestionScoresThreshold(
			searchSuggestionsPortletPreferences.
				getRelatedSuggestionsDisplayThreshold());
		queryConfig.setQuerySuggestionsMax(
			searchSuggestionsPortletPreferences.getRelatedSuggestionsMax());
	}

	protected void setUpSpellCheckSuggestion(
		SearchSuggestionsPortletPreferences searchSuggestionsPortletPreferences,
		PortletSharedSearchSettings portletSharedSearchSettings) {

		QueryConfig queryConfig = portletSharedSearchSettings.getQueryConfig();

		queryConfig.setCollatedSpellCheckResultEnabled(
			searchSuggestionsPortletPreferences.
				isSpellCheckSuggestionEnabled());

		queryConfig.setCollatedSpellCheckResultScoresThreshold(
			searchSuggestionsPortletPreferences.
				getSpellCheckSuggestionDisplayThreshold());
	}

	@Reference
	protected Portal portal;

	@Reference
	protected PortletSharedRequestHelper portletSharedRequestHelper;

	@Reference
	protected PortletSharedSearchRequest portletSharedSearchRequest;

}