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
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.test.rule.SybaseDump;
import com.liferay.portal.test.rule.SybaseDumpTransactionLog;
import com.liferay.users.admin.test.util.search.DummyPermissionChecker;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Vagner B.C
 */
@RunWith(Arquillian.class)
@SybaseDumpTransactionLog(dumpBefore = {SybaseDump.CLASS, SybaseDump.METHOD})
public class DefaultDDLStructuresPortalInstanceDefaultIndexerProblemTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_indexer = IndexerRegistryUtil.getIndexer(DDMStructure.class);

		_setUpUnregisterIndexer();

		_company = addCompany();

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

		_setUpRegisterIndexer();
	}

	@Test
	public void test() throws PortalException {
		_setUpRegisterIndexer();

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

	private static void _setUpRegisterIndexer() {
		IndexerRegistryUtil.register(_indexer);
	}

	private static void _setUpUnregisterIndexer() {
		IndexerRegistryUtil.unregister(_indexer);
	}

	private static Indexer<DDMStructure> _indexer;

	private Company _company;

}