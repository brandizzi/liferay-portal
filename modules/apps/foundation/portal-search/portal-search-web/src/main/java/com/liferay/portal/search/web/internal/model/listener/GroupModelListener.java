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
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutPrototypeLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.transaction.TransactionCommitCallbackUtil;
import com.liferay.portal.kernel.util.AggregateResourceBundleLoader;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.ResourceBundleLoader;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.language.LanguageResources;
import com.liferay.portal.search.web.internal.layout.prototype.DefaultSearchLayoutPrototypeCustomizer;
import com.liferay.portal.search.web.layout.prototype.SearchLayoutPrototypeCustomizer;
import com.liferay.sites.kernel.util.SitesUtil;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Adam Brandizzi
 */
@Component(immediate = true, service = ModelListener.class)
public class GroupModelListener extends BaseModelListener<Group> {

	@Override
	public void onAfterCreate(Group group) throws ModelListenerException {
		if (group.isSite()) {
			copyLayoutPrototype(group, group.getCompanyId());
		}
	}

	protected void copyLayoutPrototype(Group group, long companyId) {
		Optional<LayoutPrototype> layoutPrototypeOptional = findLayoutPrototype(
			companyId);

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

		Layout layout = layoutLocalService.addLayout(
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
			layoutPrototypeLocalService.getLayoutPrototypes(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Map<Locale, String> nameMap = getLocalizationMap(
			"layout-prototype-search-title", getResourceBundleLoader());

		Stream<LayoutPrototype> stream = layoutPrototypes.stream();

		return stream.filter(
			layoutPrototype -> layoutPrototype.getCompanyId() == companyId
		).filter(
			layoutPrototype ->
				nameMap.equals(layoutPrototype.getNameMap())
		).findAny();
	}

	protected ClassLoader getClassLoader() {
		Class<?> clazz = getClass();

		return clazz.getClassLoader();
	}

	protected Map<Locale, String> getFriendlyURLMap() {
		return LocalizationUtil.getLocalizationMap("/search");
	}

	protected String getLayoutTemplateId() {
		if (searchLayoutPrototypeCustomizer != null) {
			return searchLayoutPrototypeCustomizer.getLayoutTemplateId();
		}

		return _defaultSearchLayoutPrototypeCustomizer.getLayoutTemplateId();
	}

	protected Map<Locale, String> getLocalizationMap(
		String key, ResourceBundleLoader resourceBundleLoader) {

		return ResourceBundleUtil.getLocalizationMap(resourceBundleLoader, key);
	}

	protected AggregateResourceBundleLoader getResourceBundleLoader() {
		return new AggregateResourceBundleLoader(
			ResourceBundleUtil.getResourceBundleLoader(
				"content.Language", getClassLoader()),
			LanguageResources.RESOURCE_BUNDLE_LOADER);
	}

	protected void registerCreateLayoutTransactionCallback(
		Group group, LayoutPrototype layoutPrototype) {

		TransactionCommitCallbackUtil.registerCallback(
			() -> {
				createLayout(group, layoutPrototype);

				return null;
			});
	}

	@Reference
	protected LayoutLocalService layoutLocalService;

	@Reference
	protected LayoutPrototypeLocalService layoutPrototypeLocalService;

	@Reference(
		cardinality = ReferenceCardinality.OPTIONAL,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	protected volatile SearchLayoutPrototypeCustomizer
		searchLayoutPrototypeCustomizer;

	private static final Log _log = LogFactoryUtil.getLog(
		GroupModelListener.class);

	private final SearchLayoutPrototypeCustomizer
		_defaultSearchLayoutPrototypeCustomizer =
			new DefaultSearchLayoutPrototypeCustomizer();

}