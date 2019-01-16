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

package com.liferay.configuration.admin.internal.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.configuration.admin.web.internal.model.ConfigurationModel;
import com.liferay.configuration.admin.web.internal.search.FieldNames;
import com.liferay.configuration.admin.web.internal.util.ConfigurationEntryRetriever;
import com.liferay.configuration.admin.web.internal.util.ConfigurationModelRetriever;
import com.liferay.configuration.admin.web.internal.util.ResourceBundleLoaderProvider;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchEngineHelper;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.search.test.util.IndexedFieldsFixture;
import com.liferay.portal.search.test.util.IndexerFixture;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerTestRule;
import com.liferay.users.admin.test.util.search.UserSearchFixture;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Vagner B.C
 */
@RunWith(Arquillian.class)
public class ConfigurationModelIndexerIndexedFieldsTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		setUpUserSearchFixture();

		setUpConfigurationModelFixture();
		setUpConfigurationModelIndexerFixture();

		setUpIndexedFieldsFixture();

		defaultLocale = LocaleThreadLocal.getDefaultLocale();
	}

	@After
	public void tearDown() {
		LocaleThreadLocal.setDefaultLocale(defaultLocale);
	}

	@Test
	public void testIndexedFields() throws Exception {
		Locale locale = LocaleUtil.JAPAN;

		setTestLocale(locale);

		configurationModelFixture.updateDisplaySettings(locale);

		ConfigurationModel configurationModel =
			configurationModelFixture.getConfigurationModel();

		String searchTerm = "説明";

		Document document = configurationModelIndexerFixture.searchOnlyOne(
			searchTerm, locale, CompanyConstants.SYSTEM);

		indexedFieldsFixture.postProcessDocument(document);

		FieldValuesAssert.assertFieldValues(
			expectedFieldValues(configurationModel), document, searchTerm);
	}

	protected Map<String, String> expectedFieldValues(
			ConfigurationModel configurationModel)
		throws Exception {

		Map<String, String> map = new HashMap<>();

		map.put(Field.COMPANY_ID, String.valueOf(CompanyConstants.SYSTEM));

		map.put(
			FieldNames.CONFIGURATION_MODEL_FACTORY_PID,
			configurationModel.getFactoryPid());

		map.put(FieldNames.CONFIGURATION_MODEL_ID, configurationModel.getID());

		map.put(Field.ENTRY_CLASS_NAME, ConfigurationModel.class.getName());

		map.put(
			Field.UID,
			ConfigurationAdminPortletKeys.SYSTEM_SETTINGS + "_PORTLET_" +
				configurationModel.getID());

		configurationModelFixture.populateConfigurationAttributes(
			configurationModel, map);

		configurationModelFixture.populateTranslationTitleAndDescription(
			configurationModel, map);

		return map;
	}

	protected void setTestLocale(Locale locale) throws Exception {
		configurationModelFixture.updateDisplaySettings(locale);
		LocaleThreadLocal.setDefaultLocale(locale);
	}

	protected void setUpConfigurationModelFixture() {
		configurationModelFixture = new ConfigurationModelFixture(
			group, configurationModelRetriever, configurationEntryRetriever,
			resourceBundleLoaderProvider);

		_configurationModels =
			configurationModelFixture.getConfigurationModels();
	}

	protected void setUpConfigurationModelIndexerFixture() {
		configurationModelIndexerFixture = new IndexerFixture<>(
			ConfigurationModel.class);
	}

	protected void setUpIndexedFieldsFixture() {
		indexedFieldsFixture = new IndexedFieldsFixture(
			resourcePermissionLocalService, searchEngineHelper);
	}

	protected void setUpUserSearchFixture() throws Exception {
		userSearchFixture = new UserSearchFixture();

		userSearchFixture.setUp();

		_groups = userSearchFixture.getGroups();
		_users = userSearchFixture.getUsers();

		group = userSearchFixture.addGroup();

		CompanyThreadLocal.setCompanyId(CompanyConstants.SYSTEM);
	}

	@Inject
	protected ConfigurationEntryRetriever configurationEntryRetriever;

	protected ConfigurationModelFixture configurationModelFixture;
	protected IndexerFixture<ConfigurationModel>
		configurationModelIndexerFixture;

	@Inject
	protected ConfigurationModelRetriever configurationModelRetriever;

	protected Locale defaultLocale;
	protected Group group;
	protected IndexedFieldsFixture indexedFieldsFixture;

	@Inject
	protected ResourceBundleLoaderProvider resourceBundleLoaderProvider;

	@Inject
	protected ResourcePermissionLocalService resourcePermissionLocalService;

	@Inject
	protected SearchEngineHelper searchEngineHelper;

	protected UserSearchFixture userSearchFixture;

	@DeleteAfterTestRun
	private Collection<ConfigurationModel> _configurationModels;

	@DeleteAfterTestRun
	private List<Group> _groups;

	@DeleteAfterTestRun
	private List<User> _users;

}