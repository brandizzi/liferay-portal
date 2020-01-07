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

package com.liferay.portal.search.internal;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Cristina Rodríguez Yrezábal
 * @author Mariano Álvaro Sáiz
 */
@Component(immediate = true, service = AssetRendererFactoryLookup.class)
public class AssetRendererFactoryLookupImpl
	implements AssetRendererFactoryLookup {

	public AssetRendererFactory<?> getAssetRendererFactoryByClassName(
		String className) {

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				className);

		if ((assetRendererFactory != null) || !_isEnabled() ||
			_isInitialized(className)) {

			return assetRendererFactory;
		}

		_waitAssetRendererFactoryLoaded(className);

		_initializedAssetRenderFactories.add(className);

		return AssetRendererFactoryRegistryUtil.
			getAssetRendererFactoryByClassName(className);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		_serviceTracker = ServiceTrackerFactory.open(
			_bundleContext,
			(Class<AssetRendererFactory<?>>)
				(Class<?>)AssetRendererFactory.class,
			new AssetRendererFactoryServiceTrackerCustomizer());
	}

	@Deactivate
	protected void deactivate() {
		_serviceTracker.close();

		_serviceTracker = null;
	}

	private static boolean _isEnabled() {
		if (_INDEX_ON_STARTUP && (_INDEX_ON_STARTUP_DELAY > 0)) {
			return true;
		}

		return false;
	}

	private boolean _isInitialized(String className) {
		return _initializedAssetRenderFactories.contains(className);
	}

	private void _waitAssetRendererFactoryLoaded(String className) {
		CountDownLatch countDownLatch =
			_assetRenderFactoriesCountDownLatch.computeIfAbsent(
				className, key -> new CountDownLatch(1));

		try {
			countDownLatch.await(_INDEX_ON_STARTUP_DELAY, TimeUnit.SECONDS);
		}
		catch (InterruptedException ie) {
			if (_log.isInfoEnabled()) {
				_log.info("Interrupted while waiting to load factory", ie);
			}
		}
	}

	private static final boolean _INDEX_ON_STARTUP = GetterUtil.getBoolean(
		PropsUtil.get(PropsKeys.INDEX_ON_STARTUP));

	private static final long _INDEX_ON_STARTUP_DELAY = GetterUtil.getLong(
		PropsUtil.get(PropsKeys.INDEX_ON_STARTUP_DELAY));

	private static final Log _log = LogFactoryUtil.getLog(
		AssetRendererFactoryLookupImpl.class);

	private final Map<String, CountDownLatch>
		_assetRenderFactoriesCountDownLatch = new ConcurrentHashMap<>();
	private BundleContext _bundleContext;
	private final Set<String> _initializedAssetRenderFactories =
		ConcurrentHashMap.newKeySet();
	private ServiceTracker<AssetRendererFactory<?>, AssetRendererFactory<?>>
		_serviceTracker;

	private class AssetRendererFactoryServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<AssetRendererFactory<?>, AssetRendererFactory<?>> {

		@Override
		public AssetRendererFactory<?> addingService(
			ServiceReference<AssetRendererFactory<?>> serviceReference) {

			AssetRendererFactory<?> assetRendererFactory =
				_bundleContext.getService(serviceReference);

			_assetRenderFactoriesCountDownLatch.computeIfPresent(
				assetRendererFactory.getClassName(),
				(key, countDownLatch) -> {
					countDownLatch.countDown();

					return countDownLatch;
				});

			return assetRendererFactory;
		}

		@Override
		public void modifiedService(
			ServiceReference<AssetRendererFactory<?>> serviceReference,
			AssetRendererFactory<?> service) {
		}

		@Override
		public void removedService(
			ServiceReference<AssetRendererFactory<?>> serviceReference,
			AssetRendererFactory<?> service) {

			_bundleContext.ungetService(serviceReference);
		}

	}

}