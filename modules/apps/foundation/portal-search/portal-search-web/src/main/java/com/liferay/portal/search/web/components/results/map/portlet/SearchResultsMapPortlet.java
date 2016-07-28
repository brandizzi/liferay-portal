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

package com.liferay.portal.search.web.components.results.map.portlet;

import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.search.summary.SummaryBuilderFactory;
import com.liferay.portal.search.web.internal.display.context.PortletURLFactory;
import com.liferay.portal.search.web.internal.display.context.PortletURLFactoryImpl;
import com.liferay.portal.search.web.internal.result.display.builder.SearchResultSummaryDisplayBuilder;
import com.liferay.portal.search.web.internal.result.display.context.SearchResultSummaryDisplayContext;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchRequest;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchResponse;
import com.liferay.portal.search.web.search.result.SearchResultImageContributor;

import java.io.IOException;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

/**
 * @author André de Oliveira
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=" +
			SearchResultsMapPortletKeys.CSS_CLASS_WRAPPER,
		"com.liferay.portlet.display-category=category.search-poc",
		"com.liferay.portlet.header-portlet-css=/components/results/map/css/main.css",
		"com.liferay.portlet.icon=/icons/search.png",
		"com.liferay.portlet.instanceable=true",
		"com.liferay.portlet.layout-cacheable=true",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.restore-current-view=false",
		"com.liferay.portlet.use-default-template=true",
		"javax.portlet.display-name=" +
			SearchResultsMapPortletKeys.DISPLAY_NAME,
		"javax.portlet.expiration-cache=0",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=" +
			SearchResultsMapPortletKeys.VIEW_TEMPLATE,
		"javax.portlet.name=" + SearchResultsMapPortletKeys.PORTLET_NAME,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=guest,power-user,user",
		"javax.portlet.supports.mime-type=text/html"
	},
	service = Portlet.class
)
public class SearchResultsMapPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		PortletSharedSearchResponse portletSharedSearchResponse =
			portletSharedSearchRequest.search(renderRequest);

		renderRequest.setAttribute(
			SearchResultsMapDisplayContext.ATTRIBUTE,
			buildDisplayContext(
				portletSharedSearchResponse, renderRequest, renderResponse));

		super.render(renderRequest, renderResponse);
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC
	)
	protected void addSearchResultImageContributor(
		SearchResultImageContributor searchResultImageContributor) {

		_searchResultImageContributors.add(searchResultImageContributor);
	}

	protected SearchResultsMapDisplayContext buildDisplayContext(
			PortletSharedSearchResponse portletSharedSearchResponse,
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		Optional<String> keywordsOptional =
			portletSharedSearchResponse.getKeywords();

		String keywords = keywordsOptional.orElse(StringPool.BLANK);

		String mapMarkersJSON = buildMapMarkers(
			portletSharedSearchResponse, renderRequest);

		SearchResultsMapDisplayContext searchResultsMapDisplayContext =
			new SearchResultsMapDisplayContext(
				portletSharedSearchResponse, keywords, mapMarkersJSON);

		searchResultsMapDisplayContext.setSearchResultsSummariesHolder(
			buildSummaries(
				portletSharedSearchResponse, renderRequest, renderResponse));

		return searchResultsMapDisplayContext;
	}

	protected String buildMapMarkers(
		PortletSharedSearchResponse portletSharedSearchResponse,
		RenderRequest renderRequest) {

		ThemeDisplay themeDisplay = portletSharedSearchResponse.getThemeDisplay(
			renderRequest);

		Locale locale = themeDisplay.getLocale();

		MapMarkersExtendedBuilder mapMarkersExtendedBuilder =
			new MapMarkersExtendedBuilder(locale, resourceActions);

		return mapMarkersExtendedBuilder.buildMapMarkersJSON(
			portletSharedSearchResponse.getDocuments());
	}

	protected SearchResultsSummariesHolder buildSummaries(
			PortletSharedSearchResponse portletSharedSearchResponse,
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			return doBuildSummaries(
				portletSharedSearchResponse, renderRequest, renderResponse);
		}
		catch (PortletException pe) {
			throw pe;
		}
		catch (RuntimeException re) {
			throw re;
		}
		catch (Exception e) {
			throw new PortletException(e);
		}
	}

	protected SearchResultsSummariesHolder doBuildSummaries(
			PortletSharedSearchResponse portletSharedSearchResponse,
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws Exception {

		SearchMapPortletPreferences searchMapPortletPreferences =
			new SearchMapPortletPreferencesImpl(
				portletSharedSearchResponse.getPortletPreferences(
					renderRequest));

		ThemeDisplay themeDisplay = portletSharedSearchResponse.getThemeDisplay(
			renderRequest);

		List<Document> documents = portletSharedSearchResponse.getDocuments();

		SearchResultsSummariesHolder searchResultsSummariesHolder =
			new SearchResultsSummariesHolder(documents.size());

		PortletURLFactory portletURLFactory = new PortletURLFactoryImpl(
			renderRequest, renderResponse);

		for (Document document : documents) {
			SearchResultSummaryDisplayContext summary = doBuildSummary(
				document, portletSharedSearchResponse, renderRequest,
				renderResponse, themeDisplay, portletURLFactory,
				searchMapPortletPreferences);

			searchResultsSummariesHolder.put(document, summary);
		}

		return searchResultsSummariesHolder;
	}

	protected SearchResultSummaryDisplayContext doBuildSummary(
			Document document,
			PortletSharedSearchResponse portletSharedSearchResponse,
			RenderRequest renderRequest, RenderResponse renderResponse,
			ThemeDisplay themeDisplay, PortletURLFactory portletURLFactory,
			SearchMapPortletPreferences searchResultsPortletPreferences)
		throws Exception {

		SearchResultSummaryDisplayBuilder searchResultSummaryDisplayBuilder =
			new SearchResultSummaryDisplayBuilder();

		PortletURL portletURL = portletURLFactory.getPortletURL();

		searchResultSummaryDisplayBuilder.setAssetEntryLocalService(
			assetEntryLocalService);
		searchResultSummaryDisplayBuilder.setCurrentURL(portletURL.toString());
		searchResultSummaryDisplayBuilder.setDocument(document);
		searchResultSummaryDisplayBuilder.setHighlightEnabled(
			searchResultsPortletPreferences.isHighlightEnabled());
		searchResultSummaryDisplayBuilder.setImageRequested(true);
		searchResultSummaryDisplayBuilder.setLanguage(language);
		searchResultSummaryDisplayBuilder.setLocale(themeDisplay.getLocale());
		searchResultSummaryDisplayBuilder.setPortletURLFactory(
			portletURLFactory);
		searchResultSummaryDisplayBuilder.setRenderRequest(renderRequest);
		searchResultSummaryDisplayBuilder.setRenderResponse(renderResponse);
		searchResultSummaryDisplayBuilder.setRequest(
			getHttpServletRequest(renderRequest));
		searchResultSummaryDisplayBuilder.setResourceActions(resourceActions);
		searchResultSummaryDisplayBuilder.
			setSearchResultImageContributorsStream(
				_searchResultImageContributors.stream());
		searchResultSummaryDisplayBuilder.setSummaryBuilderFactory(
			summaryBuilderFactory);
		searchResultSummaryDisplayBuilder.setThemeDisplay(themeDisplay);

		return searchResultSummaryDisplayBuilder.build();
	}

	protected HttpServletRequest getHttpServletRequest(
		RenderRequest renderRequest) {

		LiferayPortletRequest liferayPortletRequest =
			(LiferayPortletRequest)renderRequest;

		return liferayPortletRequest.getHttpServletRequest();
	}

	protected void removeSearchResultImageContributor(
		SearchResultImageContributor searchResultImageContributor) {

		_searchResultImageContributors.remove(searchResultImageContributor);
	}

	@Reference
	protected AssetEntryLocalService assetEntryLocalService;

	@Reference
	protected Language language;

	@Reference
	protected PortletSharedSearchRequest portletSharedSearchRequest;

	@Reference
	protected ResourceActions resourceActions;

	@Reference
	protected SummaryBuilderFactory summaryBuilderFactory;

	private final Set<SearchResultImageContributor>
		_searchResultImageContributors = new HashSet<>();

}