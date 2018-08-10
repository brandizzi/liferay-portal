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

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ListTypeConstants;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CountryService;
import com.liferay.portal.kernel.service.OrganizationService;
import com.liferay.portal.kernel.service.RegionService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.service.test.ServiceTestUtil;

import java.io.File;
import java.io.Serializable;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author Igor Fabiano Nazar
 * @author Luan Maoski
 */
public class OrganizationFixture {

	public OrganizationFixture(
		OrganizationService organizationService, CountryService countryService,
		RegionService regionService, List<Group> groups,
		List<Organization> organizatons) {

		_organizationService = organizationService;
		_countryService = countryService;
		_regionService = regionService;
		_groups = groups;
		_organizatons = organizatons;
	}

	public Group addGroup() throws Exception {
		Group group = GroupTestUtil.addGroup();

		_groups.add(group);

		return group;
	}

	public Organization createAnOrganization(
			String organizationName, String countryName, String regionName)
		throws Exception, PortalException {

		return createAnOrganization(
			organizationName, countryName, regionName, null);
	}

	public Organization createAnOrganization(
			String organizationName, String countryName, String regionName,
			Map<String, Serializable> expando)
		throws Exception, PortalException {

		Country country = _countryService.getCountryByName(countryName);

		Region region = _getRegion(regionName, country);

		long parentOrganizationId =
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID;
		String organizatioType = OrganizationConstants.TYPE_ORGANIZATION;
		long regionId = region.getRegionId();
		long countryId = country.getCountryId();
		long statusId = ListTypeConstants.ORGANIZATION_STATUS_DEFAULT;
		String comments = "";
		boolean site = false;

		ServiceContext serviceContext = getServiceContext();

		if (expando != null) {
			serviceContext.setExpandoBridgeAttributes(expando);
		}

		try {
			Organization organization = _organizationService.addOrganization(
				parentOrganizationId, organizationName, organizatioType,
				regionId, countryId, statusId, comments, site, serviceContext);

			_organizatons.add(organization);

			return organization;
		}
		catch (PortalException e)
		{
			throw new RuntimeException(e);
		}
	}

	public ServiceContext getServiceContext() throws Exception {
		return ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), getUserId());
	}

	public void setGroup(Group group) {
		_group = group;
	}

	public void setUp() throws Exception {
		ServiceTestUtil.setUser(TestPropsValues.getUser());

		CompanyThreadLocal.setCompanyId(TestPropsValues.getCompanyId());
	}

	public void updateDisplaySettings(Locale locale) throws Exception {
		Group group = GroupTestUtil.updateDisplaySettings(
			_group.getGroupId(), null, locale);

		_group.setModelAttributes(group.getModelAttributes());
	}

	protected String getContentType(String fileName) {
		return MimeTypesUtil.getContentType((File)null, fileName);
	}

	protected long getUserId() throws Exception {
		return TestPropsValues.getUserId();
	}

	private Region _getRegion(String regionName, Country country) {
		List<Region> regions = _regionService.getRegions(
			country.getCountryId());

		Stream<Region> regionStream = regions.stream();

		Optional<Region> regionOptional = regionStream.filter(
			line -> {
				return StringUtil.equalsIgnoreCase(regionName, line.getName());
			}).findFirst();

		Region region = regionOptional.get();

		return region;
	}

	private final CountryService _countryService;
	private Group _group;
	private final List<Group> _groups;
	private final OrganizationService _organizationService;
	private final List<Organization> _organizatons;
	private final RegionService _regionService;

}