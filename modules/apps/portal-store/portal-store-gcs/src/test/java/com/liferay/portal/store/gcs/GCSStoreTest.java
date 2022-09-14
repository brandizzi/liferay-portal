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

package com.liferay.portal.store.gcs;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.store.gcs.configuration.GCSStoreConfiguration;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.ByteArrayInputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author Adam Brandizzi
 */
public class GCSStoreTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);

		_gcsStore = new GCSStore();

		ReflectionTestUtil.setFieldValue(
			_gcsStore, "_gcsStoreConfiguration", _gcsStoreConfiguration);
	}

	@Test
	public void testGoogleCredentials() throws Exception {
		byte[] serviceAccountKeyBytes = FileUtil.getBytes(
			getClass(), "dependencies/service-account-key.json");

		mockServiceAccountKey(new String(serviceAccountKeyBytes));

		GoogleCredentials googleCredentials = ReflectionTestUtil.invoke(
			_gcsStore, "_getGoogleCredentials", new Class<?>[0]);

		Assert.assertEquals(
			googleCredentials,
			ServiceAccountCredentials.fromStream(
				new ByteArrayInputStream(serviceAccountKeyBytes)));
	}

	@Test
	public void testGoogleCredentialsNull() throws Exception {
		mockServiceAccountKey(null);

		GoogleCredentials googleCredentials = ReflectionTestUtil.invoke(
			_gcsStore, "_getGoogleCredentials", new Class<?>[0]);

		Assert.assertEquals(
			googleCredentials,
			ServiceAccountCredentials.getApplicationDefault());
	}

	protected void mockServiceAccountKey(String serviceAccountKey) {
		Mockito.when(
			_gcsStoreConfiguration.serviceAccountKey()
		).thenReturn(
			serviceAccountKey
		);
	}

	private GCSStore _gcsStore;

	@Mock
	private GCSStoreConfiguration _gcsStoreConfiguration;

}