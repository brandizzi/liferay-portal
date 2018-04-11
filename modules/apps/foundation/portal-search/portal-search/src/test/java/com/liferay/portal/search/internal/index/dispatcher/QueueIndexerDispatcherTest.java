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

package com.liferay.portal.search.internal.index.dispatcher;

import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.search.internal.indexer.queue.IndexerQueueImpl;
import com.liferay.portal.search.internal.indexer.queue.IndexerTokenImpl;
import com.liferay.portal.search.spi.model.index.contributor.helper.IndexerWriterMode;
import com.liferay.portal.search.spi.model.index.queue.IndexerQueue;
import com.liferay.portal.search.spi.model.index.queue.IndexerToken;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Adam Brandizzi
 */
public class QueueIndexerDispatcherTest {

	@Test
	public void testDeleteDocumentByUidPutIndexerRequestInQueue()
		throws Exception {

		IndexerQueue indexerQueue = new IndexerQueueImpl();

		QueueIndexerDispatcher queueIndexerDispatcher =
			new QueueIndexerDispatcher(indexerQueue);

		long companyId = RandomTestUtil.randomLong();
		String uid = RandomTestUtil.randomString();

		queueIndexerDispatcher.deleteDocument(companyId, uid);

		assertIndexerToken(createDeleteIndexerToken(uid), indexerQueue);
	}

	@Test
	public void testDeleteDocumentPutIndexerRequestInQueue() throws Exception {
		IndexerQueue indexerQueue = new IndexerQueueImpl();

		QueueIndexerDispatcher queueIndexerDispatcher =
			new QueueIndexerDispatcher(indexerQueue);

		long classPK = RandomTestUtil.randomLong();
		long companyId = RandomTestUtil.randomLong();

		queueIndexerDispatcher.deleteDocument(
			companyId, createBaseModel(Example.class, classPK));

		assertIndexerToken(
			createIndexerToken(IndexerWriterMode.DELETE, classPK),
			indexerQueue);
	}

	@Test
	public void testUpdateDocumentPutIndexerRequestInQueue() throws Exception {
		IndexerQueue indexerQueue = new IndexerQueueImpl();

		QueueIndexerDispatcher queueIndexerDispatcher =
			new QueueIndexerDispatcher(indexerQueue);

		long companyId = RandomTestUtil.randomLong();
		long classPK = RandomTestUtil.randomLong();

		queueIndexerDispatcher.updateDocument(
			companyId, createBaseModel(Example.class, classPK));

		assertIndexerToken(
			createIndexerToken(IndexerWriterMode.UPDATE, classPK),
			indexerQueue);
	}

	@Test
	public void testUpdatePermissionFieldsPutIndexerRequestInQueue()
		throws Exception {

		IndexerQueue indexerQueue = new IndexerQueueImpl();

		QueueIndexerDispatcher queueIndexerDispatcher =
			new QueueIndexerDispatcher(indexerQueue);

		long companyId = RandomTestUtil.randomLong();
		long classPK = RandomTestUtil.randomLong();

		queueIndexerDispatcher.updatePermissionFields(
			companyId, createBaseModel(Example.class, classPK));

		assertIndexerToken(// TODO is this the best choice?
			createIndexerToken(IndexerWriterMode.UPDATE, classPK),
			indexerQueue);
	}

	protected void assertIndexerToken(
		IndexerToken expected, IndexerQueue indexerQueue) {

		List<IndexerToken> indexerTokens = indexerQueue.take(1);

		Assert.assertEquals(indexerTokens.toString(), 1, indexerTokens.size());

		Assert.assertEquals(expected, indexerTokens.get(0));
	}

	protected BaseModel<Example> createBaseModel(
		Class<?> baseModelClass, long classPK) {

		return new Example(baseModelClass, classPK);
	}

	protected IndexerToken createDeleteIndexerToken(String uid) {
		return new IndexerTokenImpl(IndexerWriterMode.DELETE, uid);
	}

	protected IndexerTokenImpl createIndexerToken(
		IndexerWriterMode indexerWriterMode, long classPK) {

		return new IndexerTokenImpl(
			indexerWriterMode, Example.class.getName(), classPK);
	}

	private class Example implements BaseModel<Example> {

		public Example(Class<?> baseModelClass, long classPK) {
			_baseModelClass = baseModelClass;
			_classPK = classPK;
		}

		@Override
		public Object clone() {
			throw new UnsupportedOperationException();
		}

		@Override
		public int compareTo(Example o) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ExpandoBridge getExpandoBridge() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Map<String, Object> getModelAttributes() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Class<?> getModelClass() {
			return _baseModelClass;
		}

		@Override
		public String getModelClassName() {
			return _baseModelClass.getName();
		}

		@Override
		public Serializable getPrimaryKeyObj() {
			return _classPK;
		}

		@Override
		public boolean isCachedModel() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isEntityCacheEnabled() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isEscapedModel() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isFinderCacheEnabled() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isNew() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void resetOriginalValues() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setCachedModel(boolean cachedModel) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setExpandoBridgeAttributes(BaseModel<?> baseModel) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setExpandoBridgeAttributes(ExpandoBridge expandoBridge) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setExpandoBridgeAttributes(ServiceContext serviceContext) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setModelAttributes(Map<String, Object> attributes) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setNew(boolean n) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setPrimaryKeyObj(Serializable primaryKeyObj) {
			throw new UnsupportedOperationException();
		}

		@Override
		public CacheModel<Example> toCacheModel() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Example toEscapedModel() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Example toUnescapedModel() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String toXmlString() {
			throw new UnsupportedOperationException();
		}

		private final Class<?> _baseModelClass;
		private final long _classPK;

	}

}