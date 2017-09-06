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

package com.liferay.portal.search.web.internal.page;

import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.service.LayoutPrototypeLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.AggregateResourceBundleLoader;
import com.liferay.portal.kernel.util.DefaultLayoutPrototypesUtil;
import com.liferay.portal.kernel.util.ResourceBundleLoader;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.language.LanguageResources;
import com.liferay.portal.search.web.page.SearchLayoutPrototypeCustomizer;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author André de Oliveira
 * @author Lino Alves
 */
@Component(immediate = true, service = PortalInstanceLifecycleListener.class)
public class AddLayoutPrototypePortalInstanceLifecycleListener
	extends BasePortalInstanceLifecycleListener {

	@Override
	public void portalInstanceRegistered(Company company) throws Exception {
		long defaultUserId = userLocalService.getDefaultUserId(
			company.getCompanyId());

		List<LayoutPrototype> layoutPrototypes =
			layoutPrototypeLocalService.search(
				company.getCompanyId(), null, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		addSearchPage(company.getCompanyId(), defaultUserId, layoutPrototypes);
	}

	protected void addSearchPage(
			long companyId, long defaultUserId,
			List<LayoutPrototype> layoutPrototypes)
		throws Exception {

		ResourceBundleLoader resourceBundleLoader =
			new AggregateResourceBundleLoader(
				ResourceBundleUtil.getResourceBundleLoader(
					"content.Language", getClassLoader()),
				LanguageResources.RESOURCE_BUNDLE_LOADER);

		Map<Locale, String> nameMap = ResourceBundleUtil.getLocalizationMap(
			resourceBundleLoader, "layout-prototype-search-title");
		Map<Locale, String> descriptionMap =
			ResourceBundleUtil.getLocalizationMap(
				resourceBundleLoader, "layout-prototype-search-description");

		Layout layout = DefaultLayoutPrototypesUtil.addLayoutPrototype(
			companyId, defaultUserId, nameMap, descriptionMap, "1_2_columns_i",
			layoutPrototypes);

		if (layout == null) {
			return;
		}

		searchLayoutPrototypeCustomizer.customize(layout);
	}

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED, unbind = "-")
	protected void setModuleServiceLifecycle(
		ModuleServiceLifecycle moduleServiceLifecycle) {
	}

	@Reference
	protected LayoutPrototypeLocalService layoutPrototypeLocalService;

	@Reference(
		cardinality = ReferenceCardinality.MANDATORY,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	protected volatile SearchLayoutPrototypeCustomizer
		searchLayoutPrototypeCustomizer;

	@Reference
	protected UserLocalService userLocalService;

}