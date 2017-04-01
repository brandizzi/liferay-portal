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

package com.liferay.blogs.web.internal.search;

import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.web.search.result.SearchResultImage;
import com.liferay.portal.search.web.search.result.SearchResultImageContributor;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author André de Oliveira
 */
@Component(service = SearchResultImageContributor.class)
public class BlogsEntrySearchResultImageContributor
	implements SearchResultImageContributor {

	@Override
	public void contribute(SearchResultImage searchResultImage) {
		String className = searchResultImage.getClassName();

		if (className.equals(BlogsEntry.class.getName())) {
			long classPK = searchResultImage.getClassPK();

			BlogsEntry blogsEntry = getBlogsEntry(classPK);

			contribute(searchResultImage, blogsEntry);
		}
	}

	protected void contribute(
		SearchResultImage searchResultImage, BlogsEntry blogsEntry) {

		String thumbnail = StringUtil.trim(blogsEntry.getCoverImageURL());

		if (!Validator.isBlank(thumbnail)) {
			searchResultImage.setThumbnail(thumbnail);
		}
		else {
			searchResultImage.setIcon("blogs");
		}
	}

	protected BlogsEntry getBlogsEntry(long classPK) {
		try {
			return blogsEntryLocalService.getEntry(classPK);
		}
		catch (PortalException pe) {
			throw new RuntimeException(pe);
		}
	}

	@Reference
	protected BlogsEntryLocalService blogsEntryLocalService;

}