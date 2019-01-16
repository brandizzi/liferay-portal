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

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.osgi.util.service.OSGiServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.definitions.ExtendedAttributeDefinition;
import com.liferay.portal.configuration.metatype.definitions.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;

import java.io.InputStream;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Vagner B.C
 */
public class BaseConfigurationModelTest {

	@Before
	public void setUp() throws Exception {
		reflectionConfigurationModel();

		defaultLocale = LocaleThreadLocal.getDefaultLocale();
	}

	protected void reflectionConfigurationModel()
		throws ClassNotFoundException, NoSuchMethodException {

		bundle = FrameworkUtil.getBundle(ConfigurationModelIndexerTest.class);

		bundleContext = bundle.getBundleContext();

		Bundle configAdminWebBundle = getBundle(
			"com.liferay.configuration.admin.web");

		Class<?> configurationModelClass = configAdminWebBundle.loadClass(
			"com.liferay.configuration.admin.web.internal.model." +
				"ConfigurationModel");

		configurationModelConstructor = configurationModelClass.getConstructor(
			ExtendedObjectClassDefinition.class, Configuration.class,
			String.class, String.class, boolean.class);
	}

	@After
	public void tearDown() throws SearchException {
		cleanDocuments();

		LocaleThreadLocal.setDefaultLocale(defaultLocale);
	}

	protected void cleanDocuments() throws SearchException {
		for (Document document : documents) {
			deleteDocument(document);
		}

		documents.clear();
	}

	protected void reindex() throws SearchException {
		indexer.reindex(new String[] {String.valueOf(CompanyConstants.SYSTEM)});
	}

	protected void deleteDocument(Document doc) throws SearchException {
		indexWriterHelper.deleteDocument(
			indexer.getSearchEngineId(), CompanyConstants.SYSTEM, doc.getUID(),
			true);
	}

	protected void setTestLocale(Locale locale) {
		LocaleThreadLocal.setDefaultLocale(locale);
	}

	protected Object addCompanyFactoryConfiguration(String pid)
		throws Exception {

		Configuration configuration = OSGiServiceUtil.callService(
			bundleContext, ConfigurationAdmin.class,
			configurationAdmin -> configurationAdmin.createFactoryConfiguration(
				pid, StringPool.QUESTION));

		Map<String, String> extensionAttributes = new HashMap<>();

		extensionAttributes.put("factoryInstanceLabelAttribute", "companyId");
		extensionAttributes.put(
			"scope",
			com.liferay.portal.configuration.metatype.annotations.
				ExtendedObjectClassDefinition.Scope.SYSTEM.toString());

		ExtendedObjectClassDefinition extendedObjectClassDefinition =
			new BaseConfigurationModelTest.SimpleExtendedObjectClassDefinition(
				extensionAttributes, pid);

		Object configurationModel = configurationModelConstructor.newInstance(
			extendedObjectClassDefinition, configuration,
			bundle.getSymbolicName(), StringPool.QUESTION, false);

		Document document = indexer.getDocument(configurationModel);

		documents.add(document);

		indexWriterHelper.addDocument(
			indexer.getSearchEngineId(), CompanyConstants.SYSTEM, document,
			true);

		return configurationModel;
	}

	protected void assertSearchResults(String id) throws Exception {
		Hits hits = search(id);

		Assert.assertEquals(hits.toString(), 1, hits.getLength());
	}

	protected void assertSearchNoResults(String id) throws Exception {
		Hits hits = search(id);

		Assert.assertEquals(hits.toString(), 0, hits.getLength());
	}

	protected Hits search(String id) throws SearchException {
		SearchContext searchContext = new SearchContext();

		searchContext.setAndSearch(false);
		searchContext.setCompanyId(CompanyConstants.SYSTEM);
		searchContext.setKeywords(id);
		searchContext.setLocale(LocaleUtil.US);

		return indexer.search(searchContext);
	}

	protected Bundle getBundle(String bundleSymbolicName) {
		Bundle[] bundles = bundleContext.getBundles();

		for (Bundle bundle : bundles) {
			if (bundleSymbolicName.equals(bundle.getSymbolicName())) {
				return bundle;
			}
		}

		return null;
	}

	protected String getFactoryPid(Object configurationModel)
		throws IllegalAccessException, InvocationTargetException,
			   NoSuchMethodException {

		Class<?> configurationModelClass = configurationModel.getClass();

		Method getFactoryPid = configurationModelClass.getMethod(
			"getFactoryPid");

		Object invoke = getFactoryPid.invoke(configurationModel);

		return invoke.toString();
	}

	protected String getId(Object configurationModel)
		throws IllegalAccessException, InvocationTargetException,
			   NoSuchMethodException {

		Class<?> configurationModelClass = configurationModel.getClass();

		Method getID = configurationModelClass.getMethod("getID");

		Object invoke = getID.invoke(configurationModel);

		return invoke.toString();
	}

	protected static final String PID =
		"com.liferay.configuration.admin.internal.search.test." +
			"BaseConfigurationModelTest";

	protected Bundle bundle;
	protected BundleContext bundleContext;
	protected Constructor configurationModelConstructor;
	protected final List<Document> documents = new ArrayList<>();

	@Inject(filter = "component.name=*.ConfigurationModelIndexer")
	protected Indexer indexer;

	@Inject
	protected IndexWriterHelper indexWriterHelper;

	protected Locale defaultLocale;

	protected class SimpleExtendedAttributeDefinition
		implements ExtendedAttributeDefinition {

		public SimpleExtendedAttributeDefinition(
			String description, String name) {

			_description = description;
			_name = name;
		}

		@Override
		public int getCardinality() {
			return 0;
		}

		@Override
		public String[] getDefaultValue() {
			return new String[0];
		}

		@Override
		public String getDescription() {
			return _description;
		}

		@Override
		public Map<String, String> getExtensionAttributes(String uri) {
			return null;
		}

		@Override
		public Set<String> getExtensionUris() {
			return null;
		}

		@Override
		public String getID() {
			return StringPool.BLANK;
		}

		@Override
		public String getName() {
			return _name;
		}

		@Override
		public String[] getOptionLabels() {
			return new String[0];
		}

		@Override
		public String[] getOptionValues() {
			return new String[0];
		}

		@Override
		public int getType() {
			return 0;
		}

		@Override
		public String validate(String s) {
			return null;
		}

		private final String _description;
		private final String _name;

	}

	protected class SimpleExtendedObjectClassDefinition
		implements ExtendedObjectClassDefinition {

		public SimpleExtendedObjectClassDefinition(
			ExtendedAttributeDefinition[] extendedAttributeDefinitions,
			Map<String, String> extensionAttributes, String id) {

			_extendedAttributeDefinitions = extendedAttributeDefinitions;
			_extensionAttributes = extensionAttributes;
			_id = id;
		}

		public SimpleExtendedObjectClassDefinition(
			Map<String, String> extensionAttributes, String id) {

			this(new ExtendedAttributeDefinition[0], extensionAttributes, id);
		}

		@Override
		public ExtendedAttributeDefinition[] getAttributeDefinitions(
			int filter) {

			return _extendedAttributeDefinitions;
		}

		@Override
		public String getDescription() {
			return null;
		}

		@Override
		public Map<String, String> getExtensionAttributes(String uri) {
			return _extensionAttributes;
		}

		@Override
		public Set<String> getExtensionUris() {
			return null;
		}

		@Override
		public InputStream getIcon(int size) {
			return null;
		}

		@Override
		public String getID() {
			return _id;
		}

		@Override
		public String getName() {
			return null;
		}

		private final ExtendedAttributeDefinition[]
			_extendedAttributeDefinitions;
		private final Map<String, String> _extensionAttributes;
		private String _id;

	}

	@Meta.OCD(
		factory = true,
		id = "com.liferay.configuration.admin.internal.search.test.BaseConfigurationModelTest"
	)
	private interface TestConfiguration {

		@Meta.AD
		public String key1();

		@Meta.AD
		public String key2();

	}

}