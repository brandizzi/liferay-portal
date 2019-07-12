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

package com.liferay.company.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.test.rule.SybaseDump;
import com.liferay.portal.test.rule.SybaseDumpTransactionLog;
import com.liferay.users.admin.test.util.search.DummyPermissionChecker;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.runtime.ServiceComponentRuntime;
import org.osgi.service.component.runtime.dto.ComponentDescriptionDTO;

/**
 * @author Vagner B.C
 */
@RunWith(Arquillian.class)
@SybaseDumpTransactionLog(dumpBefore = {SybaseDump.CLASS, SybaseDump.METHOD})
public class DefaultDDLStructuresPortalInstanceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() {
		_indexer = IndexerRegistryUtil.getIndexer(DDMStructure.class);

		PermissionThreadLocal.setPermissionChecker(
			new DummyPermissionChecker() {

				@Override
				public long getCompanyId() {
					return _company.getCompanyId();
				}

				@Override
				public boolean hasPermission(
					Group group, String name, long primKey, String actionId) {

					return true;
				}

				@Override
				public boolean hasPermission(
					Group group, String name, String primKey, String actionId) {

					return true;
				}

				@Override
				public boolean hasPermission(
					long groupId, String name, long primKey, String actionId) {

					return true;
				}

				@Override
				public boolean hasPermission(
					long groupId, String name, String primKey,
					String actionId) {

					return true;
				}

				@Override
				public boolean isCompanyAdmin(long companyId) {
					return true;
				}

			});
	}

	@After
	public void tearDown() throws Exception {
		CompanyLocalServiceUtil.deleteCompany(_company.getCompanyId());
	}

	@Test
	public void testDefaultDDLStructuresPortalInstance() throws Exception {
		_company = addCompany();

		User user = _company.getDefaultUser();

		SearchContext searchContext = new SearchContext();

		searchContext.setCompanyId(_company.getCompanyId());
		searchContext.setKeywords(user.getFullName());
		searchContext.setUserId(user.getUserId());

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setSelectedFieldNames(StringPool.STAR);

		Hits hits = _indexer.search(searchContext);

		Assert.assertEquals("Must have 9 documents", 9, hits.getLength());
	}

	@Test
	public void testDefaultDDLStructuresPortalInstanceDDMStructureModelContributorProblem()
		throws Exception {

		_disableModelContributor();

		_company = addCompany();

		_enableModelContributor();

		User user = _company.getDefaultUser();

		SearchContext searchContext = new SearchContext();

		searchContext.setCompanyId(_company.getCompanyId());
		searchContext.setKeywords(user.getFullName());
		searchContext.setUserId(user.getUserId());
		searchContext.setAttribute(
			"entryClassName", DDMStructure.class.getName());

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setSelectedFieldNames(StringPool.STAR);

		Hits hits = _indexer.search(searchContext);

		_assertNoOneDDMStructureIndexed(hits);
	}

	@Test
	public void testDefaultDDLStructuresPortalInstanceDefaultIndexerProblem()
		throws Exception {

		_unregisterIndexer();

		_company = addCompany();

		_registerIndexer();

		User user = _company.getDefaultUser();

		SearchContext searchContext = new SearchContext();

		searchContext.setCompanyId(_company.getCompanyId());
		searchContext.setKeywords(user.getFullName());
		searchContext.setUserId(user.getUserId());
		searchContext.setAttribute(
			"entryClassName", DDMStructure.class.getName());

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setSelectedFieldNames(StringPool.STAR);

		Hits hits = _indexer.search(searchContext);

		Assert.assertEquals("Must have 0 documents", 0, hits.getLength());
	}

	protected Company addCompany() throws Exception {
		String webId = RandomTestUtil.randomString() + "test.com";

		return CompanyLocalServiceUtil.addCompany(
			webId, webId, "test.com", false, 0, true);
	}

	private static void _disableModelContributor() {
		Bundle bundle = FrameworkUtil.getBundle(DDMStructureLocalService.class);

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceReference<DDMStructureLocalService> serviceReference =
			bundleContext.getServiceReference(DDMStructureLocalService.class);

		bundle = serviceReference.getBundle();

		_componentDescriptionDTO =
			_serviceComponentRuntime.getComponentDescriptionDTO(
				bundle,
				"com.liferay.dynamic.data.mapping.internal.search.spi.model." +
					"index.contributor.DDMStructureModelDocumentContributor");

		_enabled = _serviceComponentRuntime.isComponentEnabled(
			_componentDescriptionDTO);

		if (_enabled) {
			_serviceComponentRuntime.disableComponent(_componentDescriptionDTO);
		}
	}

	private static void _enableModelContributor() {
		if (_enabled) {
			_serviceComponentRuntime.enableComponent(_componentDescriptionDTO);
		}
	}

	private static void _registerIndexer() {
		IndexerRegistryUtil.register(_indexer);
	}

	private static void _unregisterIndexer() {
		IndexerRegistryUtil.unregister(_indexer);
	}

	private void _assertNoOneDDMStructureIndexed(Hits hits) {
		Stream<Document> stream = Arrays.stream(hits.getDocs());

		stream.forEach(
			document -> {
				Map<String, String> map = _getFieldValues(
					document, name -> name.startsWith("entryClassName"));

				map.forEach(
					(key, value) -> Assert.assertFalse(
						value.contains("DDMStructure")));
			});
	}

	private Map<String, String> _getFieldValues(
		Document document, Predicate<String> predicate) {

		Map<String, Field> fieldsMap = document.getFields();

		Set<Map.Entry<String, Field>> entrySet = fieldsMap.entrySet();

		Stream<Map.Entry<String, Field>> stream = entrySet.stream();

		if (predicate != null) {
			stream = stream.filter(entry -> predicate.test(entry.getKey()));
		}

		return stream.collect(
			Collectors.toMap(
				Map.Entry::getKey,
				entry -> {
					Field field = entry.getValue();

					String[] values = field.getValues();

					if (values == null) {
						return null;
					}

					if (values.length == 1) {
						return values[0];
					}

					return String.valueOf(Arrays.asList(values));
				}));
	}

	private static ComponentDescriptionDTO _componentDescriptionDTO;
	private static boolean _enabled;
	private static Indexer<DDMStructure> _indexer;

	@Inject
	private static ServiceComponentRuntime _serviceComponentRuntime;

	private Company _company;

}