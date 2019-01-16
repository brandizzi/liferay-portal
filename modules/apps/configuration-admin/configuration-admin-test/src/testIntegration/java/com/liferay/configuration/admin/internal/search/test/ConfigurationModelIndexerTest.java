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
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition.Scope;
import com.liferay.portal.configuration.metatype.definitions.ExtendedAttributeDefinition;
import com.liferay.portal.configuration.metatype.definitions.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pei-Jung Lan
 */
@RunWith(Arquillian.class)
public class ConfigurationModelIndexerTest extends BaseConfigurationModelTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@After
	public void tearDown() throws SearchException {
		super.tearDown();
	}

	@Test
	public void testLocalizedAttributes() throws Exception {
		Map<String, String> extensionAttributes = new HashMap<>();

		extensionAttributes.put("factoryInstanceLabelAttribute", "companyId");
		extensionAttributes.put("scope", Scope.COMPANY.toString());

		ExtendedAttributeDefinition[] extendedAttributeDefinitions = {
			new SimpleExtendedAttributeDefinition(
				"com.liferay.configuration.admin.web.test.attribute." +
					"description",
				"com.liferay.configuration.admin.web.test.attribute.name")
		};

		ExtendedObjectClassDefinition extendedObjectClassDefinition =
			new SimpleExtendedObjectClassDefinition(
				extendedAttributeDefinitions, extensionAttributes, PID);

		Object configurationModel = configurationModelConstructor.newInstance(
			extendedObjectClassDefinition, null,
			"com.liferay.configuration.admin.web", StringPool.QUESTION, true);

		Document document = indexer.getDocument(configurationModel);

		String[] attributeDescriptions = document.getValues(
			"configurationModelAttributeDescription_en_US");

		Assert.assertEquals(
			"Test_Attribute_Description", attributeDescriptions[0]);

		String[] attributeNames = document.getValues(
			"configurationModelAttributeName_en_US");

		Assert.assertEquals("Test_Attribute_Name", attributeNames[0]);
	}

	@Test
	public void testSearchAfterReindex() throws Exception {
		String pid = PID;

		addCompanyFactoryConfiguration(pid);

		assertSearchResults(pid);

		addCompanyFactoryConfiguration(pid);

		assertSearchResults(pid);
	}

}