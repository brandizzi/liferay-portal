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

package com.liferay.portal.store.gcs.test;

import com.google.auth.oauth2.ServiceAccountCredentials;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.AssumeTestRule;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder.HashMapDictionaryWrapper;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.store.test.util.BaseStoreTestCase;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.IOException;
import java.io.InputStream;

import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
public class GCSStoreTest extends BaseStoreTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new AssumeTestRule("assume"), new LiferayIntegrationTestRule());

	public static void assume() {
		String gcsStoreClassName = "com.liferay.portal.store.gcs.GCSStore";
		String dlStoreImpl = PropsUtil.get(PropsKeys.DL_STORE_IMPL);

		Assume.assumeTrue(
			StringBundler.concat(
				"Property \"", PropsKeys.DL_STORE_IMPL, "\" is not set to \"",
				gcsStoreClassName, "\""),
			dlStoreImpl.equals(gcsStoreClassName));
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
		_configuration = _configurationAdmin.getConfiguration(
			"com.liferay.portal.store.gcs.configuration.GCSStoreConfiguration",
			StringPool.QUESTION);

		ConfigurationTestUtil.saveConfiguration(
			_configuration, getGCSStoreConfigurationBuilder().build());
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		ConfigurationTestUtil.deleteConfiguration(_configuration);
	}

	@Test
	public void testActivate() throws Exception {
		_configuration = _configurationAdmin.getConfiguration(
			"com.liferay.portal.store.gcs.configuration.GCSStoreConfiguration",
			StringPool.QUESTION);

		String serviceAccountKey = readServiceAccountKey(
			"dependencies/service-account-key.json");

		ConfigurationTestUtil.saveConfiguration(
			_configuration,
			getGCSStoreConfigurationBuilder(
			).put(
				"serviceAccountKey", serviceAccountKey
			).build());

		Map<String, Object> properties = new HashMap<>();

		ReflectionTestUtil.invoke(
			_store, "activate", new Class<?>[] {properties.getClass()},
			properties);

		ServiceAccountCredentials serviceAccountCredentials =
			ReflectionTestUtil.getFieldValue(_store, "_googleCredentials");

		Assert.assertEquals(
			"1234567890", serviceAccountCredentials.getClientId());
	}

	protected static HashMapDictionaryWrapper<String, Object>
		getGCSStoreConfigurationBuilder() {

		return HashMapDictionaryBuilder.<String, Object>put(
			"aes256Key", ""
		).put(
			"bucketName", "test"
		).put(
			"initialRetryDelay", "400"
		).put(
			"initialRPCTimeout", "120000"
		).put(
			"maxRetryAttempts", "5"
		).put(
			"maxRetryDelay", "10000"
		).put(
			"maxRPCTimeout", "600000"
		).put(
			"retryDelayMultiplier", "1.5"
		).put(
			"retryJitter", "false"
		).put(
			"rpcTimeoutMultiplier", "1.0"
		).put(
			"serviceAccountKey", ""
		);
	}

	@Override
	protected Store getStore() {
		return _store;
	}

	protected String readServiceAccountKey(String path) throws IOException {
		Class<?> clazz = getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		InputStream inputStream = classLoader.getResourceAsStream(path);

		return StringUtil.read(inputStream);
	}

	private static Configuration _configuration;

	@Inject
	private static ConfigurationAdmin _configurationAdmin;

	@Inject(
		filter = "store.type=com.liferay.portal.store.gcs.GCSStore",
		type = Store.class
	)
	private Store _store;

}