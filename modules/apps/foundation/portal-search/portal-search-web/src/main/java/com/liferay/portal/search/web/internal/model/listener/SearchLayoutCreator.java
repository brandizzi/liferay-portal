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

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutPrototypeLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.transaction.TransactionCommitCallbackUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.ResourceBundleLoader;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.search.web.internal.configuration.SearchPageConfiguration;
import com.liferay.sites.kernel.util.SitesUtil;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author Adam Brandizzi
 */
public class SearchLayoutCreator {

	public SearchLayoutCreator(
		LayoutLocalService layoutLocalService,
		LayoutPrototypeLocalService layoutPrototypeLocalService,
		SearchPageConfiguration searchPageConfiguration,
		ResourceBundleLoader resourceBundleLoader) {

		_layoutLocalService = layoutLocalService;
		_layoutPrototypeLocalService = layoutPrototypeLocalService;
		_searchPageConfiguration = searchPageConfiguration;
		_resourceBundleLoader = resourceBundleLoader;
	}

	public void copyLayoutPrototype(Group group) {
		Optional<LayoutPrototype> layoutPrototypeOptional = findLayoutPrototype(
			group.getCompanyId());

		layoutPrototypeOptional.ifPresent(
			layoutPrototype -> registerCreateLayoutTransactionCallback(
				group, layoutPrototype));
	}

	protected void createLayout(Group group, LayoutPrototype layoutPrototype)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		Layout baseLayout = layoutPrototype.getLayout();

		serviceContext.setAttribute("layoutPrototypeLinkEnabled", Boolean.TRUE);

		serviceContext.setAttribute(
			"layoutPrototypeUuid", layoutPrototype.getUuid());

		Layout layout = _layoutLocalService.addLayout(
			group.getCreatorUserId(), group.getGroupId(), false,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID,
			layoutPrototype.getNameMap(), baseLayout.getTitleMap(),
			layoutPrototype.getDescriptionMap(), baseLayout.getKeywordsMap(),
			baseLayout.getRobotsMap(), LayoutConstants.TYPE_PORTLET,
			baseLayout.getTypeSettings(), baseLayout.isPrivateLayout(),
			getFriendlyURLMap(), serviceContext);

		// Force propagation from page template to page. See LPS-48430.

		SitesUtil.mergeLayoutPrototypeLayout(layout.getGroup(), layout);
	}

	protected Optional<LayoutPrototype> findLayoutPrototype(long companyId) {
		List<LayoutPrototype> layoutPrototypes =
			_layoutPrototypeLocalService.getLayoutPrototypes(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Map<Locale, String> nameMap = getLocalizationMap(
			"layout-prototype-search-title", _resourceBundleLoader);

		Stream<LayoutPrototype> stream = layoutPrototypes.stream();

		return stream.filter(
			layoutPrototype -> layoutPrototype.getCompanyId() == companyId
		).filter(
			layoutPrototype ->
				nameMap.equals(layoutPrototype.getNameMap())
		).findAny();
	}

	protected String getFriendlyURL() {
		return _searchPageConfiguration.searchPageFriendlyURL();
	}

	protected Map<Locale, String> getFriendlyURLMap() {
		return LocalizationUtil.getLocalizationMap(getFriendlyURL());
	}

	protected Map<Locale, String> getLocalizationMap(
		String key, ResourceBundleLoader resourceBundleLoader) {

		return ResourceBundleUtil.getLocalizationMap(resourceBundleLoader, key);
	}

	protected void registerCreateLayoutTransactionCallback(
		Group group, LayoutPrototype layoutPrototype) {

		TransactionCommitCallbackUtil.registerCallback(
			() -> {
				if (shouldCreateSite(group)) {
					createLayout(group, layoutPrototype);
				}

				return null;
			});
	}

	protected boolean shouldCreateSite(Group group) {
		if (!_searchPageConfiguration.enableSearchPageCreation()) {
			return false;
		}

		if (!group.isSite()) {
			return false;
		}

		Layout layout = _layoutLocalService.fetchLayoutByFriendlyURL(
			group.getGroupId(), false, getFriendlyURL());

		if (layout != null) {
			return false;
		}

		return true;
	}

	private final LayoutLocalService _layoutLocalService;
	private final LayoutPrototypeLocalService _layoutPrototypeLocalService;
	private final ResourceBundleLoader _resourceBundleLoader;
	private final SearchPageConfiguration _searchPageConfiguration;

}