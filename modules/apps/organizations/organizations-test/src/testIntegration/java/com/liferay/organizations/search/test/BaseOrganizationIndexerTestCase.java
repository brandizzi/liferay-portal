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

package com.liferay.organizations.search.test;

import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.search.SearchEngineHelper;
import com.liferay.portal.kernel.service.CountryService;
import com.liferay.portal.kernel.service.OrganizationService;
import com.liferay.portal.kernel.service.RegionService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.search.test.util.IndexedFieldsFixture;
import com.liferay.portal.search.test.util.IndexerFixture;
import com.liferay.portal.test.rule.Inject;

import java.util.List;

/**
 * @author Igor Fabiano Nazar
 * @author Luan Maoski
 */
public abstract class BaseOrganizationIndexerTestCase {

	public void setUp() throws Exception {
		organizationFixture = createOrganizationFixture();

		organizationFixture.setUp();

		_organizations = organizationFixture.getOrganizations();

		organizationIndexerFixture = createOrganizationIndexerFixture();
		indexedFieldsFixture = createIndexedFieldsFixture();
	}

	protected IndexedFieldsFixture createIndexedFieldsFixture() {
		return new IndexedFieldsFixture(
			resourcePermissionLocalService, searchEngineHelper);
	}

	protected OrganizationFixture createOrganizationFixture() {
		return new OrganizationFixture(
			organizationService, countryService, regionService);
	}

	protected IndexerFixture<Organization> createOrganizationIndexerFixture() {
		return new IndexerFixture<>(Organization.class);
	}

	protected void setGroup(Group group) {
		organizationFixture.setGroup(group);
	}

	@Inject
	protected CountryService countryService;

	protected IndexedFieldsFixture indexedFieldsFixture;
	protected OrganizationFixture organizationFixture;
	protected IndexerFixture<Organization> organizationIndexerFixture;

	@Inject
	protected OrganizationService organizationService;

	@Inject
	protected RegionService regionService;

	@Inject
	protected ResourcePermissionLocalService resourcePermissionLocalService;

	@Inject
	protected SearchEngineHelper searchEngineHelper;

	@DeleteAfterTestRun
	private List<Organization> _organizations;

}