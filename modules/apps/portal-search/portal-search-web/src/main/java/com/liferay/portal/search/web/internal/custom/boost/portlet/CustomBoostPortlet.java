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

package com.liferay.portal.search.web.internal.custom.boost.portlet;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.web.internal.custom.boost.constants.CustomBoostPortletKeys;
import com.liferay.portal.search.web.internal.custom.boost.display.context.CustomBoostDisplayBuilder;
import com.liferay.portal.search.web.internal.custom.boost.display.context.CustomBoostDisplayContext;
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
 * @author Igor Nazar
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
		"javax.portlet.display-name=Custom Boost",
		"javax.portlet.expiration-cache=0",
		"javax.portlet.init-param.template-path=/META-INF/resources/",
		"javax.portlet.init-param.view-template=/custom/boost/view.jsp",
		"javax.portlet.name=" + CustomBoostPortletKeys.CUSTOM_BOOST,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=guest,power-user,user",
		"javax.portlet.supports.mime-type=text/html"
	},
	service = Portlet.class
)
public class CustomBoostPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		PortletSharedSearchResponse portletSharedSearchResponse =
			portletSharedSearchRequest.search(renderRequest);

		CustomBoostDisplayContext customBoostDisplayContext =
			buildDisplayContext(portletSharedSearchResponse, renderRequest);

		renderRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT, customBoostDisplayContext);

		if (customBoostDisplayContext.isInvisible()) {
			renderRequest.setAttribute(
				WebKeys.PORTLET_CONFIGURATOR_VISIBILITY, Boolean.TRUE);
		}

		super.render(renderRequest, renderResponse);
	}

	protected CustomBoostDisplayContext buildDisplayContext(
		PortletSharedSearchResponse portletSharedSearchResponse,
		RenderRequest renderRequest) {

		CustomBoostPortletPreferences customBoostPortletPreferences =
			new CustomBoostPortletPreferencesImpl(
				portletSharedSearchResponse.getPortletPreferences(
					renderRequest));

		CustomBoostDisplayBuilder customBoostDisplayBuilder =
			new CustomBoostDisplayBuilder();

		SearchOptionalUtil.copy(
			customBoostPortletPreferences::getCustomHeadingOptional,
			customBoostDisplayBuilder::setCustomDisplayCaption);

		customBoostDisplayBuilder.setIsInvisible(
			customBoostPortletPreferences.isInvisible());
		customBoostDisplayBuilder.setParameterValue(
			customBoostPortletPreferences.getBoostValues());

		String customDisplayCaption = getCustomDisplayCaption(
			customBoostPortletPreferences);

		customBoostDisplayBuilder.setCustomDisplayCaption(customDisplayCaption);

		String parameterName = getParameterName(customBoostPortletPreferences);

		customBoostDisplayBuilder.setParameterName(parameterName);

		SearchOptionalUtil.copy(
			() -> getParameterValuesOptional(
				parameterName, portletSharedSearchResponse, renderRequest),
			customBoostDisplayBuilder::setParameterValues);

		CustomBoostDisplayContext displayContext =
			customBoostDisplayBuilder.build();

		return displayContext;
	}

	protected String getCustomDisplayCaption(
		CustomBoostPortletPreferences customBoostPortletPreferences) {

		Optional<String> optional = Stream.of(
			customBoostPortletPreferences.getCustomHeadingOptional(),
			customBoostPortletPreferences.getBoostFieldOptional()
		).filter(
			Optional::isPresent
		).map(
			Optional::get
		).findFirst();

		return optional.orElse("Custom Boost");
	}

	protected String getParameterName(
		CustomBoostPortletPreferences customBoostPortletPreferences) {

		Optional<String> optional = Stream.of(
			customBoostPortletPreferences.getCustomParameterNameOptional(),
			customBoostPortletPreferences.getBoostFieldOptional()
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