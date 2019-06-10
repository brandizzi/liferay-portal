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

package com.liferay.portal.search.elasticsearch6.internal.connection;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequestBuilder;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.get.GetIndexResponse;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.ClusterAdminClient;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.elasticsearch.common.unit.TimeValue;
import org.junit.Assert;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceObjects;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.PortalInetSocketAddressEventListener;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.search.elasticsearch6.configuration.ElasticsearchConfiguration;
import com.liferay.portal.search.elasticsearch6.internal.cluster.ClusterExecutorClusterSettingsContext;
import com.liferay.portal.search.elasticsearch6.internal.cluster.ClusterSettingsContext;
import com.liferay.portal.search.elasticsearch6.internal.cluster.UnicastSettingsContributor;
import com.liferay.portal.search.elasticsearch6.internal.settings.BaseSettingsContributor;
import com.liferay.portal.search.elasticsearch6.settings.ClientSettingsHelper;
import com.liferay.portal.util.FileImpl;
import com.liferay.portal.util.PropsImpl;

/**
 * @author André de Oliveira
 */
public class ElasticsearchFixture implements ElasticsearchClientResolver {

	public ElasticsearchFixture(Class clazz) {
		this(getSimpleName(clazz));
	}

	public ElasticsearchFixture(String subdirName) {
		this(subdirName, Collections.<String, Object>emptyMap());
	}

	public ElasticsearchFixture(
		String subdirName,
		Map<String, Object> elasticsearchConfigurationProperties) {

		_elasticsearchConfigurationProperties =
			createElasticsearchConfigurationProperties(
				elasticsearchConfigurationProperties);

		_tmpDirName = "tmp/" + subdirName;
	}

	public void createNode() throws Exception {
		deleteTmpDir();

		_embeddedElasticsearchConnection = createElasticsearchConnection();

		ReflectionTestUtil.setFieldValue(
			_embeddedElasticsearchConnection, "_file", new FileImpl());
	}

	public void destroyNode() throws Exception {
		if (_embeddedElasticsearchConnection != null) {
			_embeddedElasticsearchConnection.close();
		}

		deleteTmpDir();
	}

	public AdminClient getAdminClient() {
		Client client = getClient();

		return client.admin();
	}

	@Override
	public Client getClient() {
		return _embeddedElasticsearchConnection.getClient();
	}

	public ClusterHealthResponse getClusterHealthResponse(
		HealthExpectations healthExpectations) {

		AdminClient adminClient = getAdminClient();

		ClusterAdminClient clusterAdminClient = adminClient.cluster();

		ClusterHealthRequestBuilder clusterHealthRequestBuilder =
			clusterAdminClient.prepareHealth();

		ClusterHealthRequest clusterHealthRequest =
			clusterHealthRequestBuilder.request();

		clusterHealthRequest.timeout(new TimeValue(10, TimeUnit.MINUTES));
		clusterHealthRequest.waitForActiveShards(
			healthExpectations.getActiveShards());
		clusterHealthRequest.waitForNodes(
			String.valueOf(healthExpectations.getNumberOfNodes()));
		clusterHealthRequest.waitForNoRelocatingShards(true);
		clusterHealthRequest.waitForStatus(healthExpectations.getStatus());

		ActionFuture<ClusterHealthResponse> healthActionFuture =
			clusterAdminClient.health(clusterHealthRequest);

		return healthActionFuture.actionGet();
	}

	public Map<String, Object> getElasticsearchConfigurationProperties() {
		return _elasticsearchConfigurationProperties;
	}

	public EmbeddedElasticsearchConnection
		getEmbeddedElasticsearchConnection() {

		return _embeddedElasticsearchConnection;
	}

	public GetIndexResponse getIndex(String... indices) {
		IndicesAdminClient indicesAdminClient = getIndicesAdminClient();

		GetIndexRequestBuilder getIndexRequestBuilder =
			indicesAdminClient.prepareGetIndex();

		getIndexRequestBuilder.addIndices(indices);

		return getIndexRequestBuilder.get();
	}

	public IndicesAdminClient getIndicesAdminClient() {
		AdminClient adminClient = getAdminClient();

		return adminClient.indices();
	}

	public void setClusterSettingsContext(
		ClusterSettingsContext clusterSettingsContext) {

		_clusterSettingsContext = clusterSettingsContext;
	}

	public void setUp() throws Exception {
		createNode();
	}

	public void tearDown() throws Exception {
		destroyNode();
	}

	public void waitForElasticsearchToStart() {
		getClusterHealthResponse(
			new HealthExpectations() {
				{
					setActivePrimaryShards(0);
					setActiveShards(0);
					setNumberOfDataNodes(1);
					setNumberOfNodes(1);
					setStatus(ClusterHealthStatus.GREEN);
					setUnassignedShards(0);
				}
			});
	}

	protected static String getSimpleName(Class clazz) {
		while (clazz.isAnonymousClass()) {
			clazz = clazz.getEnclosingClass();
		}

		return clazz.getSimpleName();
	}

	protected void addClusterLoggingThresholdContributor(
		EmbeddedElasticsearchConnection embeddedElasticsearchConnection) {

		embeddedElasticsearchConnection.addSettingsContributor(
			new BaseSettingsContributor(0) {

				@Override
				public void populate(
					ClientSettingsHelper clientSettingsHelper) {

					clientSettingsHelper.put(
						"cluster.service.slow_task_logging_threshold", "600s");
				}

			});
	}

	protected void addDiskThresholdSettingsContributor(
		EmbeddedElasticsearchConnection embeddedElasticsearchConnection) {

		embeddedElasticsearchConnection.addSettingsContributor(
			new BaseSettingsContributor(0) {

				@Override
				public void populate(
					ClientSettingsHelper clientSettingsHelper) {

					clientSettingsHelper.put(
						"cluster.routing.allocation.disk.threshold_enabled",
						"false");
				}

			});
	}

	protected void addUnicastSettingsContributor(
		EmbeddedElasticsearchConnection embeddedElasticsearchConnection) {

		if (_clusterSettingsContext == null) {
			return;
		}

		UnicastSettingsContributor unicastSettingsContributor =
			new UnicastSettingsContributor() {
				{
					setClusterSettingsContext(_clusterSettingsContext);

					activate(_elasticsearchConfigurationProperties);
				}
			};

		embeddedElasticsearchConnection.addSettingsContributor(
			unicastSettingsContributor);
	}

	protected Map<String, Object> createElasticsearchConfigurationProperties(
		Map<String, Object> elasticsearchConfigurationProperties) {

		Map<String, Object> map = new HashMap<>();

		map.put("configurationPid", ElasticsearchConfiguration.class.getName());
		map.put("httpCORSAllowOrigin", "*");
		map.put("logExceptionsOnly", false);

		map.putAll(elasticsearchConfigurationProperties);

		return map;
	}

	protected EmbeddedElasticsearchConnection createElasticsearchConnection() {
		EmbeddedElasticsearchConnection embeddedElasticsearchConnection =
			new EmbeddedElasticsearchConnection();

		addClusterLoggingThresholdContributor(embeddedElasticsearchConnection);
		addDiskThresholdSettingsContributor(embeddedElasticsearchConnection);
		addUnicastSettingsContributor(embeddedElasticsearchConnection);

		Props props = new PropsImpl() {
			
			public String get(String key) {
				if (key.equals(PropsKeys.LIFERAY_HOME)) {
					return _tmpDirName;
				}

				return super.get(key);
			};
		};


		ClusterSettingsContext clusterSettingsContext = _clusterSettingsContext;

		if (clusterSettingsContext == null) {
			clusterSettingsContext =
				new ClusterExecutorClusterSettingsContext();
		}

		embeddedElasticsearchConnection.clusterSettingsContext =
			clusterSettingsContext;

		embeddedElasticsearchConnection.props = props;

		BundleContext bundleContext = new MockBundleContext() {
			
			@Override
			public File getDataFile(String fileName) {
				if (fileName.equals(
						EmbeddedElasticsearchConnection.JNA_TMP_DIR)) {

					new File(
						SystemProperties.get(SystemProperties.TMP_DIR) + "/" +
							EmbeddedElasticsearchConnection.JNA_TMP_DIR);
				}
				
				return super.getDataFile(fileName);
			}

		};

		embeddedElasticsearchConnection.activate(
			bundleContext, _elasticsearchConfigurationProperties);

		embeddedElasticsearchConnection.connect();

		return embeddedElasticsearchConnection;
	}

	protected void deleteTmpDir() throws Exception {
		FileUtils.deleteDirectory(new File(_tmpDirName));
	}

	private ClusterSettingsContext _clusterSettingsContext;
	private final Map<String, Object> _elasticsearchConfigurationProperties;
	private EmbeddedElasticsearchConnection _embeddedElasticsearchConnection;
	private final String _tmpDirName;

	private static class MockBundleContext implements BundleContext {

		@Override
		public void addBundleListener(BundleListener bundleListener) {
		}

		@Override
		public void addFrameworkListener(FrameworkListener frameworkListener) {
		}

		@Override
		public void addServiceListener(ServiceListener serviceListener) {
		}

		@Override
		public void addServiceListener(
			ServiceListener serviceListener, String serviceName) {
		}

		@Override
		public Filter createFilter(String filterString) {
			return null;
		}

		@Override
		public ServiceReference<?>[] getAllServiceReferences(
			String serviceName, String filterString) {

			return null;
		}

		@Override
		public Bundle getBundle() {
			return null;
		}

		@Override
		public Bundle getBundle(long bundleId) {
			return null;
		}

		@Override
		public Bundle getBundle(String bundleName) {
			return null;
		}

		@Override
		public Bundle[] getBundles() {
			return null;
		}

		@Override
		public File getDataFile(String fileName) {
			return null;
		}

		@Override
		public String getProperty(String key) {
			return StringPool.BLANK;
		}

		@Override
		public <S> S getService(ServiceReference<S> serviceReference) {
			return null;
		}

		@Override
		public <S> ServiceObjects<S> getServiceObjects(
			ServiceReference<S> serviceReference) {

			return null;
		}

		@Override
		public <S> ServiceReference<S> getServiceReference(Class<S> clazz) {
			return null;
		}

		@Override
		public ServiceReference<?> getServiceReference(String serviceName) {
			return null;
		}

		@Override
		public <S> Collection<ServiceReference<S>> getServiceReferences(
			Class<S> clazz, String serviceName) {

			return null;
		}

		@Override
		public ServiceReference<?>[] getServiceReferences(
			String serviceName, String filterString) {

			return null;
		}

		@Override
		public Bundle installBundle(String bundleName) {
			return null;
		}

		@Override
		public Bundle installBundle(
			String bundleName, InputStream inputStream) {

			return null;
		}

		@Override
		public <S> ServiceRegistration<S> registerService(
			Class<S> clazz, S object, Dictionary<String, ?> dictionary) {

			Assert.assertEquals(
				PortalInetSocketAddressEventListener.class, clazz);
			Assert.assertNotNull(object);
			Assert.assertTrue(dictionary.isEmpty());

			return new ServiceRegistration<S>() {

				@Override
				public ServiceReference<S> getReference() {
					return null;
				}

				@Override
				public void setProperties(Dictionary<String, ?> dictionary) {
				}

				@Override
				public void unregister() {
				}

			};
		}

		@Override
		public <S> ServiceRegistration<S> registerService(
			Class<S> clazz, ServiceFactory<S> serviceFactory,
			Dictionary<String, ?> dictionary) {

			return null;
		}

		@Override
		public ServiceRegistration<?> registerService(
			String classNames, Object object,
			Dictionary<String, ?> dictionary) {

			return null;
		}

		@Override
		public ServiceRegistration<?> registerService(
			String[] classNames, Object object,
			Dictionary<String, ?> dictionary) {

			return null;
		}

		@Override
		public void removeBundleListener(BundleListener bundleListener) {
		}

		@Override
		public void removeFrameworkListener(
			FrameworkListener frameworkListener) {
		}

		@Override
		public void removeServiceListener(ServiceListener serviceListener) {
		}

		@Override
		public boolean ungetService(ServiceReference<?> serviceReference) {
			return true;
		}

	}

	
	
}