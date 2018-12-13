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

package com.liferay.portal.search.web.internal.custom.filter.portlet;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.web.internal.custom.facet.portlet.CustomFacetPortletPreferences;
import com.liferay.portal.search.web.internal.custom.filter.constants.CustomFilterPortletKeys;
import com.liferay.portal.search.web.internal.custom.filter.display.context.CustomFilterDisplayBuilder;
import com.liferay.portal.search.web.internal.custom.filter.display.context.CustomFilterDisplayContext;
import com.liferay.portal.search.web.internal.util.SearchOptionalUtil;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchRequest;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchResponse;

import java.io.IOException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Igor Fabiano Nazar
 * @author Luan Maoski
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-custom-facet",
		"com.liferay.portlet.display-category=category.search",
		"com.liferay.portlet.icon=/icons/search.png",
		"com.liferay.portlet.instanceable=true",
		"com.liferay.portlet.layout-cacheable=true",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.restore-current-view=false",
		"com.liferay.portlet.use-default-template=true",
		"javax.portlet.display-name=Custom Filter",
		"javax.portlet.expiration-cache=0",
		"javax.portlet.init-param.template-path=/META-INF/resources/",
		"javax.portlet.init-param.view-template=/custom/filter/view.jsp",
		"javax.portlet.name=" + CustomFilterPortletKeys.CUSTOM_FILTER,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=guest,power-user,user",
		"javax.portlet.supports.mime-type=text/html"
	},
	service = Portlet.class
)
public class CustomFilterPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		PortletSharedSearchResponse portletSharedSearchResponse =
			portletSharedSearchRequest.search(renderRequest);

		CustomFilterDisplayContext customFilterDisplayContext =
			buildDisplayContext(portletSharedSearchResponse, renderRequest);

		renderRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT, customFilterDisplayContext);

		if (customFilterDisplayContext.isInvisible()) {
			renderRequest.setAttribute(
				WebKeys.PORTLET_CONFIGURATOR_VISIBILITY, Boolean.TRUE);
		}

		super.render(renderRequest, renderResponse);
	}

	protected CustomFilterDisplayContext buildDisplayContext(
		PortletSharedSearchResponse portletSharedSearchResponse,
		RenderRequest renderRequest) {

		CustomFilterPortletPreferences customFilterPortletPreferences =
			new CustomFilterPortletPreferencesImpl(
				portletSharedSearchResponse.getPortletPreferences(
					renderRequest));

		CustomFilterDisplayBuilder customFilterDisplayBuilder =
			new CustomFilterDisplayBuilder();

		SearchOptionalUtil.copy(
			customFilterPortletPreferences::getCustomHeadingOptional,
			customFilterDisplayBuilder::setCustomDisplayCaption);

		customFilterDisplayBuilder.setIsInvisible(
			customFilterPortletPreferences.isInvisible());
		customFilterDisplayBuilder.setParameterValue(
			customFilterPortletPreferences.getFilterValue());

		String customDisplayCaption = getCustomDisplayCaption(
			customFilterPortletPreferences);

		customFilterDisplayBuilder.setCustomDisplayCaption(
			customDisplayCaption);

		String parameterName = getParameterName(customFilterPortletPreferences);

		customFilterDisplayBuilder.setParameterName(parameterName);

		SearchOptionalUtil.copy(
			() -> getParameterValuesOptional(
				parameterName, portletSharedSearchResponse, renderRequest),
			customFilterDisplayBuilder::setParameterValues);

		CustomFilterDisplayContext displayContext =
			customFilterDisplayBuilder.build();

		return displayContext;
	}

	protected String getAggregationName(
		CustomFacetPortletPreferences customFacetPortletPreferences,
		String portletId) {

		return customFacetPortletPreferences.getAggregationFieldString() +
			StringPool.PERIOD + portletId;
	}

	protected String getCustomDisplayCaption(
		CustomFilterPortletPreferences customFilterPortletPreferences) {

		Optional<String> optional = Stream.of(
			customFilterPortletPreferences.getCustomHeadingOptional(),
			customFilterPortletPreferences.getFilterFieldOptional()
		).filter(
			Optional::isPresent
		).map(
			Optional::get
		).findFirst();

		return optional.orElse("Custom Filter");
	}

	protected String getParameterName(
		CustomFilterPortletPreferences customFilterPortletPreferences) {

		Optional<String> optional = Stream.of(
			customFilterPortletPreferences.getCustomParameterNameOptional(),
			customFilterPortletPreferences.getFilterFieldOptional()
		).filter(
			Optional::isPresent
		).map(
			Optional::get
		).findFirst();

		return optional.orElse("customfield");
	}

	protected Optional<List<String>> getParameterValuesOptional(
		String parameterName,
		PortletSharedSearchResponse portletSharedSearchResponse,
		RenderRequest renderRequest) {

		Optional<String[]> optional =
			portletSharedSearchResponse.getParameterValues(
				parameterName, renderRequest);

		return optional.map(Arrays::asList);
	}

	protected String getPortletId(RenderRequest renderRequest) {
		return _portal.getPortletId(renderRequest);
	}

	@Reference
	protected PortletSharedSearchRequest portletSharedSearchRequest;

	@Reference
	private Portal _portal;

}