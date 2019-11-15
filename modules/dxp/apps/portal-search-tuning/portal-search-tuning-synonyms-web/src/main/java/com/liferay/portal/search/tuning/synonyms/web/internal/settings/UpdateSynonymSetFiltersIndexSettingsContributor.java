/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.portal.search.tuning.synonyms.web.internal.settings;

import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.search.spi.settings.IndexSettingsContributor;
import com.liferay.portal.search.spi.settings.IndexSettingsHelper;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.SynonymFilterNameHolder;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.SynonymSetFilterHelper;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.SynonymSetIndexReader;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adam Brandizzi
 */
@Component(immediate = true, service = IndexSettingsContributor.class)
public class UpdateSynonymSetFiltersIndexSettingsContributor
	implements IndexSettingsContributor {

	@Override
	public void populate(
		String indexName, IndexSettingsHelper indexSettingsHelper) {

		String[] synonyms = _synonymSetFilterHelper.getSynonyms(indexName);

		for (String filterName : _synonymFilterNameHolder.getNames()) {
			indexSettingsHelper.put(
				"analysis.filter." + filterName + ".synonyms",
				ListUtil.fromArray(synonyms));
		}
	}

	@Reference
	private SynonymFilterNameHolder _synonymFilterNameHolder;

	@Reference
	private SynonymSetFilterHelper _synonymSetFilterHelper;

	@Reference
	private SynonymSetIndexReader _synonymSetIndexReader;

}