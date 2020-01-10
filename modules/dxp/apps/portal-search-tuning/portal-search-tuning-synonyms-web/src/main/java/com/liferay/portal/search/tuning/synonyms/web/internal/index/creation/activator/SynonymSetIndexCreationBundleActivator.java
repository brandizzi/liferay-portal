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

package com.liferay.portal.search.tuning.synonyms.web.internal.index.creation.activator;

import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyService;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.SynonymSetIndexCreator;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.SynonymSetIndexReader;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.name.SynonymSetIndexNameBuilder;
import com.liferay.portal.search.tuning.synonyms.web.internal.synchronizer.FilterToIndexSynchronizer;

import java.util.List;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adam Brandizzi
 */
@Component(
	immediate = true, service = SynonymSetIndexCreationBundleActivator.class
)
public class SynonymSetIndexCreationBundleActivator {

	@Activate
	@Modified
	protected void activate() {
		List<Company> companies = _companyService.getCompanies();

		Stream<Company> stream = companies.stream();

		stream.map(
			Company::getCompanyId
		).map(
			_indexNameBuilder::getIndexName
		).filter(
			this::hasNoSynonymSetIndex
		).forEach(
			this::createSynonymSetIndex
		);
	}

	protected void createSynonymSetIndex(String companyIndexName) {
		_synonymSetIndexCreator.create(
			_synonymSetIndexNameBuilder.getSynonymSetIndexName(
				companyIndexName));

		_filterToIndexSynchronizer.copyToIndex(companyIndexName);
	}

	protected boolean hasNoSynonymSetIndex(String companyIndexName) {
		return !_synonymSetIndexReader.isExists(
			_synonymSetIndexNameBuilder.getSynonymSetIndexName(
				companyIndexName));
	}

	@Reference
	private CompanyService _companyService;

	@Reference
	private FilterToIndexSynchronizer _filterToIndexSynchronizer;

	@Reference
	private IndexNameBuilder _indexNameBuilder;

	@Reference
	private SynonymSetIndexCreator _synonymSetIndexCreator;

	@Reference
	private SynonymSetIndexNameBuilder _synonymSetIndexNameBuilder;

	@Reference
	private SynonymSetIndexReader _synonymSetIndexReader;

}