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

package com.liferay.portal.search.elasticsearch7.internal.sidecar;

import com.liferay.petra.process.ProcessExecutor;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.search.elasticsearch7.configuration.ElasticsearchConfiguration;
import com.liferay.portal.search.elasticsearch7.internal.cluster.ClusterSettingsContext;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchInstancePaths;
import com.liferay.portal.search.elasticsearch7.settings.SettingsContributor;

import java.util.Collection;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author Adam Brandizzi
 */
public class SidecarTest {

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testSidecar() {
		Sidecar sidecar = createSidecar();

		Assert.assertNotNull(sidecar.getHttpPort());
	}

	@Test
	public void testSidecarWithHttpPort() {
		Sidecar sidecar = createSidecarWithHttpPort("9900-9999");

		Assert.assertEquals("9900-9999", sidecar.getHttpPort());
	}

	@Test
	public void testSidecarWithNotEmbeddedHttpPort() {
		int embeddedHttpPort = RandomTestUtil.randomInt(0, 65535);

		Mockito.when(
			elasticsearchConfiguration.embeddedHttpPort()
		).thenReturn(
			embeddedHttpPort
		);

		Sidecar sidecar = createSidecar();

		Assert.assertNotEquals(embeddedHttpPort, sidecar.getHttpPort());
	}

	@Test
	public void testSidecarWithSidecarHttpPort() {
		Mockito.when(
			elasticsearchConfiguration.sidecarHttpPort()
		).thenReturn(
			"9876-5432"
		);

		Sidecar sidecar = createSidecar();

		Assert.assertEquals("9876-5432", sidecar.getHttpPort());
	}

	protected Sidecar createSidecar() {
		return createSidecarWithHttpPort(null);
	}

	protected Sidecar createSidecarWithHttpPort(String httpPort) {
		return new Sidecar(
			clusterSettingsContext, elasticsearchConfiguration,
			elasticsearchInstancePaths, httpPort, processExecutor,
			processExecutorPaths, settingsContributors);
	}

	@Mock
	protected ClusterSettingsContext clusterSettingsContext;

	@Mock
	protected ElasticsearchConfiguration elasticsearchConfiguration;

	@Mock
	protected ElasticsearchInstancePaths elasticsearchInstancePaths;

	@Mock
	protected ProcessExecutor processExecutor;

	@Mock
	protected ProcessExecutorPaths processExecutorPaths;

	protected Collection<SettingsContributor> settingsContributors =
		Collections.emptyList();

}