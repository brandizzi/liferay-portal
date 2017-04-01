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

package com.liferay.message.boards.web.internal.search;

import com.liferay.message.boards.kernel.model.MBMessage;
import com.liferay.portal.search.web.search.result.SearchResultImage;
import com.liferay.portal.search.web.search.result.SearchResultImageContributor;

import org.osgi.service.component.annotations.Component;

/**
 * @author André de Oliveira
 */
@Component(service = SearchResultImageContributor.class)
public class MBMessageSearchResultImageContributor
	implements SearchResultImageContributor {

	@Override
	public void contribute(SearchResultImage searchResultImage) {
		String className = searchResultImage.getClassName();

		if (className.equals(MBMessage.class.getName())) {
			searchResultImage.setIcon("message-boards");
		}
	}

}