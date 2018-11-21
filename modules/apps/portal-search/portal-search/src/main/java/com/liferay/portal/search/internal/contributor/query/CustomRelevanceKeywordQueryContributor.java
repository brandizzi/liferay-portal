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
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.configuration.CustomRelevanceConfiguration;
import com.liferay.portal.search.query.QueryHelper;
import com.liferay.portal.search.spi.model.query.contributor.KeywordQueryContributor;
import com.liferay.portal.search.spi.model.query.contributor.helper.KeywordQueryContributorHelper;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

		Set<String> boosterValues = _getBoosterValues();

		if (!Validator.isBlank(_field) && !boosterValues.isEmpty()) {
			queryHelper.addBoosterTerm(
				booleanQuery, _field, boosterValues, _boostIncrement);
		}
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		CustomRelevanceConfiguration customRelevanceConfiguration =
			ConfigurableUtil.createConfigurable(
				CustomRelevanceConfiguration.class, properties);

		_field = customRelevanceConfiguration.field();

		_boosterValues = SetUtil.fromArray(
			customRelevanceConfiguration.boosterValues());

		_boostIncrement = customRelevanceConfiguration.boostIncrement();
	}

	@Reference
	protected QueryHelper queryHelper;

	private Set<String> _getBoosterValues() {
		Stream<String> stream = _boosterValues.stream();

		return stream.filter(
			boosterValue -> !Validator.isBlank(boosterValue)
		).collect(
			Collectors.toSet()
		);
	}

	private Set<String> _boosterValues;
	private float _boostIncrement;
	private String _field;

}