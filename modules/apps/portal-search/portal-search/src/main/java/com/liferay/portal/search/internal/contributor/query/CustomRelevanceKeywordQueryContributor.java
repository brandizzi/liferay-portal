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

package com.liferay.portal.search.internal.contributor.query;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.search.configuration.CustomRelevanceConfiguration;
import com.liferay.portal.search.internal.configuration.CustomRelevance;
import com.liferay.portal.search.internal.configuration.CustomRelevanceFactory;
import com.liferay.portal.search.query.QueryHelper;
import com.liferay.portal.search.spi.model.query.contributor.KeywordQueryContributor;
import com.liferay.portal.search.spi.model.query.contributor.helper.KeywordQueryContributorHelper;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adam Brandizzi
 */
@Component(
	configurationPid = "com.liferay.portal.search.configuration.CustomRelevanceConfiguration",
	immediate = true, service = KeywordQueryContributor.class
)
public class CustomRelevanceKeywordQueryContributor
	implements KeywordQueryContributor {

	@Override
	public void contribute(
		String keywords, BooleanQuery booleanQuery,
		KeywordQueryContributorHelper keywordQueryContributorHelper) {

		for (CustomRelevance customRelevance : customRelevances) {
			queryHelper.addBoosterTerm(
				booleanQuery, customRelevance.getField(),
				customRelevance.getBoosterValues(),
				customRelevance.getBoostIncrement());
		}
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		CustomRelevanceConfiguration customRelevanceConfiguration =
			ConfigurableUtil.createConfigurable(
				CustomRelevanceConfiguration.class, properties);

		customRelevances = customRelevanceFactory.getCustomRelevances(
			customRelevanceConfiguration.boostings());
	}

	protected CustomRelevanceFactory customRelevanceFactory =
		new CustomRelevanceFactory();
	protected List<CustomRelevance> customRelevances;

	@Reference
	protected QueryHelper queryHelper;

}