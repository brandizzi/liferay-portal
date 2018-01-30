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

package com.liferay.portal.search.web.internal.model.listener;

import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutPrototypeLocalService;
import com.liferay.portal.kernel.util.AggregateResourceBundleLoader;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.language.LanguageResources;
import com.liferay.portal.search.web.internal.layout.prototype.SearchLayoutFactory;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adam Brandizzi
 */
@Component(immediate = true, service = ModelListener.class)
public class LayoutModelListener extends BaseModelListener<Layout> {

	@Override
	public void onAfterCreate(Layout layout) throws ModelListenerException {
		_searchLayoutFactory.createSearchLayout(layout.getGroup());
	}

	protected ClassLoader getClassLoader() {
		Class<?> clazz = getClass();

		return clazz.getClassLoader();
	}

	protected AggregateResourceBundleLoader getResourceBundleLoader() {
		return new AggregateResourceBundleLoader(
			ResourceBundleUtil.getResourceBundleLoader(
				"content.Language", getClassLoader()),
			LanguageResources.RESOURCE_BUNDLE_LOADER);
	}

	@Reference
	protected LayoutLocalService layoutLocalService;

	@Reference
	protected LayoutPrototypeLocalService layoutPrototypeLocalService;

	@Reference
	private SearchLayoutFactory _searchLayoutFactory;

}