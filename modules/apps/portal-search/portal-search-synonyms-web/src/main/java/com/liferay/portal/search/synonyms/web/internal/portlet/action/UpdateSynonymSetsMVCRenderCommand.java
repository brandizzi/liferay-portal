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

package com.liferay.portal.search.synonyms.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.search.synonyms.web.internal.constants.SynonymsPortletKeys;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Filipe Oshiro
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + SynonymsPortletKeys.SYNONYMS,
		"mvc.command.name=updateSynonymsEntryRender"
	},
	service = MVCRenderCommand.class
)
public class UpdateSynonymSetsMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		String synonymSets = ParamUtil.getString(renderRequest, "synonymSets");

		if (synonymSets != null) {
			renderRequest.setAttribute("synonymSets", synonymSets);
		}

		return "/update_synonym_sets.jsp";
	}

	@Reference
	private Portal _portal;

}