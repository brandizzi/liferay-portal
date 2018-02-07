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

package com.liferay.portal.search.web.internal.modified.facet.portlet;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.ModifiedFacetFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.web.internal.display.context.PortletRequestThemeDisplaySupplier;
import com.liferay.portal.search.web.internal.display.context.ThemeDisplaySupplier;
import com.liferay.portal.search.web.internal.modified.facet.builder.ModifiedFacetBuilder;
import com.liferay.portal.search.web.internal.modified.facet.builder.ModifiedFacetConfiguration;
import com.liferay.portal.search.web.internal.modified.facet.builder.ModifiedFacetConfigurationImpl;
import com.liferay.portal.search.web.internal.modified.facet.constants.ModifiedFacetPortletKeys;
import com.liferay.portal.search.web.internal.modified.facet.display.context.ModifiedFacetDisplayBuilder;
import com.liferay.portal.search.web.internal.modified.facet.display.context.ModifiedFacetDisplayContext;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchContributor;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchRequest;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchResponse;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchSettings;

import java.io.IOException;

import java.util.Optional;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lino Alves
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-modified-facet",
		"com.liferay.portlet.display-category=category.search",
		"com.liferay.portlet.icon=/icons/search.png",
		"com.liferay.portlet.instanceable=true",
		"com.liferay.portlet.layout-cacheable=true",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.restore-current-view=false",
		"com.liferay.portlet.use-default-template=true",
		"javax.portlet.display-name=Last Modified Facet",
		"javax.portlet.expiration-cache=0",
		"javax.portlet.init-param.template-path=/",
		"javax.portlet.init-param.view-template=/modified/facet/view.jsp",
		"javax.portlet.name=" + ModifiedFacetPortletKeys.MODIFIED_FACET,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=guest,power-user,user",
		"javax.portlet.supports.mime-type=text/html"
	},
	service = {Portlet.class, PortletSharedSearchContributor.class}
)
public class ModifiedFacetPortlet
	extends MVCPortlet implements PortletSharedSearchContributor {

	@Override
	public void contribute(
		PortletSharedSearchSettings portletSharedSearchSettings) {

		ModifiedFacetPortletPreferences modifiedFacetPortletPreferences =
			new ModifiedFacetPortletPreferencesImpl(
				portletSharedSearchSettings.getPortletPreferences());

		Facet facet = buildFacet(
			modifiedFacetPortletPreferences, portletSharedSearchSettings);

		portletSharedSearchSettings.addFacet(facet);
	}

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		PortletSharedSearchResponse portletSharedSearchResponse =
			portletSharedSearchRequest.search(renderRequest);

		ModifiedFacetDisplayContext modifiedSearchFacetDisplayContext =
			buildDisplayContext(portletSharedSearchResponse, renderRequest);

		renderRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT, modifiedSearchFacetDisplayContext);

		super.render(renderRequest, renderResponse);
	}

	protected ModifiedFacetDisplayContext buildDisplayContext(
		PortletSharedSearchResponse portletSharedSearchResponse,
		RenderRequest renderRequest) {

		Facet facet = portletSharedSearchResponse.getFacet(getFieldName());

		ModifiedFacetPortletPreferences modifiedFacetPortletPreferences =
			new ModifiedFacetPortletPreferencesImpl(
				portletSharedSearchResponse.getPortletPreferences(
					renderRequest));

		ModifiedFacetConfiguration modifiedFacetConfiguration =
			new ModifiedFacetConfigurationImpl(facet.getFacetConfiguration());

		JSONArray rangesJSONArray =
			modifiedFacetConfiguration.getRangesJSONArray();

		modifiedFacetPortletPreferences.updateRangeLabels(rangesJSONArray);

		String parameterName =
			modifiedFacetPortletPreferences.getParameterName();

		ThemeDisplay themeDisplay = getThemeDisplay(renderRequest);

		ModifiedFacetDisplayBuilder modifiedSearchFacetDisplayBuilder =
			new ModifiedFacetDisplayBuilder();

		modifiedSearchFacetDisplayBuilder.setFacet(facet);
		modifiedSearchFacetDisplayBuilder.setRangesJSONArray(rangesJSONArray);
		modifiedSearchFacetDisplayBuilder.setTimeZone(
			themeDisplay.getTimeZone());
		modifiedSearchFacetDisplayBuilder.setLocale(themeDisplay.getLocale());

		modifiedSearchFacetDisplayBuilder.setParameterName(parameterName);
		modifiedSearchFacetDisplayBuilder.setCurrentURL(
			portal.getCurrentURL(renderRequest));

		Optional<String[]> parameterValuesOptional =
			portletSharedSearchResponse.getParameterValues(
				parameterName, renderRequest);

		parameterValuesOptional.ifPresent(
			modifiedSearchFacetDisplayBuilder::setParameterValues);

		Optional<String> fromParameterValueOptional =
			portletSharedSearchResponse.getParameter(
				parameterName + "From", renderRequest);

		fromParameterValueOptional.ifPresent(
			modifiedSearchFacetDisplayBuilder::setFromParameterValue);

		Optional<String> toParameterValueOptional =
			portletSharedSearchResponse.getParameter(
				parameterName + "To", renderRequest);

		toParameterValueOptional.ifPresent(
			modifiedSearchFacetDisplayBuilder::setToParameterValue);

		return modifiedSearchFacetDisplayBuilder.build();
	}

	protected Facet buildFacet(
		ModifiedFacetPortletPreferences modifiedFacetPortletPreferences,
		PortletSharedSearchSettings portletSharedSearchSettings) {

		ModifiedFacetBuilder modifiedFacetBuilder = new ModifiedFacetBuilder(
			modifiedFacetFactory);

		modifiedFacetBuilder.setSearchContext(
			portletSharedSearchSettings.getSearchContext());

		setSelectedRanges(
			modifiedFacetPortletPreferences, portletSharedSearchSettings,
			modifiedFacetBuilder);

		return modifiedFacetBuilder.build();
	}

	protected String getFieldName() {
		Facet facet = modifiedFacetFactory.newInstance(new SearchContext());

		return facet.getFieldName();
	}

	protected Optional<String> getLiteralRange(
		PortletSharedSearchSettings portletSharedSearchSettings) {

		Optional<String[]> modifiedFromValuesOptional =
			portletSharedSearchSettings.getParameterValues("modifiedFrom");
		Optional<String[]> modifiedToValuesOptional =
			portletSharedSearchSettings.getParameterValues("modifiedTo");

		String[] modifiedFromValues = modifiedFromValuesOptional.orElse(
			_UNBOUND_RANGE_LIMIT);
		String[] modifiedToValues = modifiedToValuesOptional.orElse(
			_UNBOUND_RANGE_LIMIT);

		String modifiedFromValue = getModifiedFromValue(modifiedFromValues);
		String modifiedToValue = getModifiedToValue(modifiedToValues);

		if (isBoundRange(modifiedFromValue, modifiedToValue)) {
			return Optional.of(getRange(modifiedFromValue, modifiedToValue));
		}
		else {
			return Optional.empty();
		}
	}

	protected String getModifiedFromValue(String[] modifiedFromValues) {
		String modifiedFromValue = modifiedFromValues[0].replace("-", "");

		return modifiedFromValue + "000000";
	}

	protected String getModifiedToValue(String[] modifiedToValues) {
		String modifiedFromValue = modifiedToValues[0].replace("-", "");

		return modifiedFromValue + "999999";
	}

	protected Optional<String> getNamedRange(
		ModifiedFacetPortletPreferences modifiedFacetPortletPreferences,
		PortletSharedSearchSettings portletSharedSearchSettings) {

		Optional<String[]> modifiedValueOptional =
			portletSharedSearchSettings.getParameterValues(
				modifiedFacetPortletPreferences.getParameterName());

		return modifiedValueOptional.map(values -> values[0]);
	}

	protected ModifiedFacetPortletPreferencesImpl getPortletPreferences(
		RenderRequest renderRequest) {

		return new ModifiedFacetPortletPreferencesImpl(
			Optional.ofNullable(renderRequest.getPreferences()));
	}

	protected String getRange(
		String modifiedFromValue, String modifiedToValue) {

		return "[" + modifiedFromValue + " TO " + modifiedToValue + "]";
	}

	protected ThemeDisplay getThemeDisplay(RenderRequest renderRequest) {
		ThemeDisplaySupplier themeDisplaySupplier =
			new PortletRequestThemeDisplaySupplier(renderRequest);

		return themeDisplaySupplier.getThemeDisplay();
	}

	protected boolean isBoundRange(
		String modifiedFromValue, String modifiedToValue) {

		if (!_UNBOUND_RANGE_LIMIT[0].equals(modifiedFromValue)) {
			return true;
		}

		if (!_UNBOUND_RANGE_LIMIT[0].equals(modifiedToValue)) {
			return true;
		}

		return false;
	}

	protected Optional<String> or(
		Optional<String> range1, Optional<String> range2) {

		return range1.map(
			Optional::of
		).orElse(
			range2
		);
	}

	protected void setSelectedRanges(
		ModifiedFacetPortletPreferences modifiedFacetPortletPreferences,
		PortletSharedSearchSettings portletSharedSearchSettings,
		ModifiedFacetBuilder modifiedFacetBuilder) {

		Optional<String> namedRangeOptional = getNamedRange(
			modifiedFacetPortletPreferences, portletSharedSearchSettings);

		Optional<String> literalRangeOptional = getLiteralRange(
			portletSharedSearchSettings);

		Optional<String> rangeOptional = or(
			namedRangeOptional, literalRangeOptional);

		rangeOptional.ifPresent(modifiedFacetBuilder::setSelectedRanges);
	}

	@Reference
	protected ModifiedFacetFactory modifiedFacetFactory;

	@Reference
	protected Portal portal;

	@Reference
	protected PortletSharedSearchRequest portletSharedSearchRequest;

	private static final String[] _UNBOUND_RANGE_LIMIT = {"*"};

}