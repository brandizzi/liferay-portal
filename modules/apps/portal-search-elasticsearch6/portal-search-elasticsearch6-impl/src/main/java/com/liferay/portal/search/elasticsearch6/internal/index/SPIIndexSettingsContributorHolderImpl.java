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

package com.liferay.portal.search.elasticsearch6.internal.index;

import com.liferay.portal.search.spi.settings.IndexSettingsContributor;
import com.liferay.portal.search.spi.settings.IndexSettingsHelper;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.elasticsearch.common.settings.Settings;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Adam Brandizzi
 */
@Component(immediate = true, service = SPIIndexSettingsContributorHolder.class)
public class SPIIndexSettingsContributorHolderImpl
	implements SPIIndexSettingsContributorHolder {

	@Override
	public void contributeAll(
		String indexName, final Settings.Builder builder) {

		IndexSettingsHelper spiIndexSettingsHelper = new IndexSettingsHelper() {

			@Override
			public void put(String setting, List<String> values) {
				builder.putList(setting, values);
			}

			@Override
			public void put(String setting, String value) {
				builder.put(setting, value);
			}

		};

		for (IndexSettingsContributor spiIndexSettingsContributor :
				_indexSettingsContributors) {

			spiIndexSettingsContributor.populate(
				indexName, spiIndexSettingsHelper);
		}
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	protected void addSPIIndexSettingsContributor(
		IndexSettingsContributor indexSettingsContributor) {

		_indexSettingsContributors.add(indexSettingsContributor);
	}

	protected void removeSPIIndexSettingsContributor(
		IndexSettingsContributor indexSettingsContributor) {

		_indexSettingsContributors.remove(indexSettingsContributor);
	}

	private final Set<IndexSettingsContributor> _indexSettingsContributors =
		new ConcurrentSkipListSet<>();

}