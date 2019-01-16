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

import com.liferay.configuration.admin.category.ConfigurationCategory;
import com.liferay.configuration.admin.web.internal.model.ConfigurationModel;
import com.liferay.configuration.admin.web.internal.search.FieldNames;
import com.liferay.configuration.admin.web.internal.util.ConfigurationEntryRetriever;
import com.liferay.configuration.admin.web.internal.util.ConfigurationModelRetriever;
import com.liferay.configuration.admin.web.internal.util.ResourceBundleLoaderProvider;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ResourceBundleLoader;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Stream;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.ObjectClassDefinition;

/**
 * @author Vagner B.C
 */
public class ConfigurationModelFixture {

	public ConfigurationModelFixture(
		Group group, ConfigurationModelRetriever configurationModelRetriever,
		ConfigurationEntryRetriever configurationEntryRetriever,
		ResourceBundleLoaderProvider resourceBundleLoaderProvider) {

		_group = group;
		_configurationModelRetriever = configurationModelRetriever;
		_configurationEntryRetriever = configurationEntryRetriever;
		_resourceBundleLoaderProvider = resourceBundleLoaderProvider;
	}

	public Collection<ConfigurationModel> getConfigurationModels() {
		return _configurationModels;
	}

	public void updateDisplaySettings(Locale locale) throws Exception {
		Group group = GroupTestUtil.updateDisplaySettings(
			_group.getGroupId(), null, locale);

		_group.setModelAttributes(group.getModelAttributes());
	}

	public ConfigurationModel getConfigurationModel() {
		Bundle bundle = FrameworkUtil.getBundle(
			ConfigurationModelFixture.class);

		Map<String, ConfigurationModel> configurationModels =
			_configurationModelRetriever.getConfigurationModels(bundle);

		_configurationModels = configurationModels.values();

		Stream<ConfigurationModel> stream = _configurationModels.stream();

		Optional<ConfigurationModel> optional = stream.findFirst();

		return optional.get();
	}

	public void populateConfigurationAttributes(
		ConfigurationModel configurationModel, Map<String, String> map) {

		AttributeDefinition[] requiredAttributeDefinitions =
			configurationModel.getAttributeDefinitions(
				ObjectClassDefinition.ALL);

		List<String> attributeNames = new ArrayList<>(
			requiredAttributeDefinitions.length);

		List<String> attributeDescriptions = new ArrayList<>(
			requiredAttributeDefinitions.length);

		for (AttributeDefinition attributeDefinition :
				requiredAttributeDefinitions) {

			String name = attributeDefinition.getName();
			String description = attributeDefinition.getDescription();

			if (name != null) {
				attributeNames.add(name);
			}

			if (description != null) {
				attributeDescriptions.add(description);
			}
		}

		if (!attributeNames.isEmpty()) {
			map.put(
				FieldNames.CONFIGURATION_MODEL_ATTRIBUTE_NAME,
				attributeNames.toString());
		}

		if (!attributeDescriptions.isEmpty()) {
			map.put(
				FieldNames.CONFIGURATION_MODEL_ATTRIBUTE_DESCRIPTION,
				attributeDescriptions.toString());
		}
	}

	public void populateTranslationTitleAndDescription(
		ConfigurationModel configurationModel, Map<String, String> map) {

		ResourceBundleLoader resourceBundleLoader =
			_resourceBundleLoaderProvider.getResourceBundleLoader(
				configurationModel.getBundleSymbolicName());

		List<TranslationHelper> translationHelpers = new ArrayList<>(3);

		ConfigurationCategory configurationCategory =
			_configurationEntryRetriever.getConfigurationCategory(
				configurationModel.getCategory());

		if (configurationCategory != null) {
			translationHelpers.add(
				new TranslationHelper(
					"category." + configurationModel.getCategory(),
					FieldNames.CONFIGURATION_CATEGORY));
		}

		translationHelpers.add(
			new TranslationHelper(
				configurationModel.getDescription(), Field.DESCRIPTION));
		translationHelpers.add(
			new TranslationHelper(configurationModel.getName(), Field.TITLE));

		ResourceBundle defaultResourceBundle =
			resourceBundleLoader.loadResourceBundle(LocaleUtil.getDefault());

		for (Locale locale : LanguageUtil.getAvailableLocales()) {
			ResourceBundle resourceBundle =
				resourceBundleLoader.loadResourceBundle(locale);

			for (TranslationHelper translationHelper : translationHelpers) {
				if (resourceBundle != null) {
					translationHelper.accept(resourceBundle, locale);
				}
				else if (defaultResourceBundle != null) {
					translationHelper.accept(defaultResourceBundle, locale);
				}
			}
		}

		for (TranslationHelper translationHelper : translationHelpers) {
			String name = translationHelper._name;

			translationHelper._values.forEach(
				(locale, s) -> {
					map.put(name + StringPool.UNDERLINE + locale.toString(), s);
				});
		}

		map.put(
			Field.DESCRIPTION,
			StringUtil.upperCaseFirstLetter(
				configurationModel.getDescription()));

		map.put(
			Field.TITLE,
			StringUtil.upperCaseFirstLetter(configurationModel.getName()));
	}

	private final Group _group;
	private final ConfigurationModelRetriever _configurationModelRetriever;
	private Collection<ConfigurationModel> _configurationModels =
		new ArrayList<>();
	private final ConfigurationEntryRetriever _configurationEntryRetriever;
	private final ResourceBundleLoaderProvider _resourceBundleLoaderProvider;

	@Meta.OCD(
		description = "description",
		id = "com.liferay.configuration.admin.internal.search.test.ConfigurationModelFixture",
		name = "name"
	)
	private interface TestConfiguration {

		@Meta.AD
		public String key1();

		@Meta.AD
		public String key2();

	}

	private static class TranslationHelper {

		public void accept(ResourceBundle resourceBundle, Locale locale) {
			String value = ResourceBundleUtil.getString(resourceBundle, _key);

			if (Validator.isNotNull(value)) {
				_values.put(locale, value);
			}
		}

		private TranslationHelper(String key, String name) {
			_key = GetterUtil.getString(key);
			_name = name;
		}

		private final String _key;
		private final String _name;
		private final Map<Locale, String> _values = new HashMap<>();

	}

}