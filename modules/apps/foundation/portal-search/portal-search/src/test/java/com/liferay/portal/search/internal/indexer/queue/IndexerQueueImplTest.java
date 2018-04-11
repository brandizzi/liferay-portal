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

package com.liferay.portal.search.internal.indexer.queue;

import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.search.spi.model.index.contributor.helper.IndexerWriterMode;
import com.liferay.portal.search.spi.model.index.queue.IndexerQueue;
import com.liferay.portal.search.spi.model.index.queue.IndexerToken;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Adam Brandizzi
 */
public class IndexerQueueImplTest {

	@Test
	public void testDequeueEmpty() {
		IndexerQueue indexerQueue = new IndexerQueueImpl();

		List<IndexerToken> indexerTokens = indexerQueue.take(1);

		Assert.assertEquals(indexerTokens.toString(), 0, indexerTokens.size());
	}

	@Test
	public void testDequeueMoreThanAvailable() {
		IndexerQueue indexerQueue = new IndexerQueueImpl();

		IndexerToken indexerToken = new IndexerTokenImpl(
			IndexerWriterMode.UPDATE, RandomTestUtil.randomString(),
			RandomTestUtil.randomLong());

		indexerQueue.put(indexerToken);

		List<IndexerToken> indexerTokens = indexerQueue.take(2);

		Assert.assertEquals(indexerTokens.toString(), 1, indexerTokens.size());
		Assert.assertEquals(indexerToken, indexerTokens.get(0));
	}

	@Test
	public void testDequeueRequestedNumber() {
		IndexerQueue indexerQueue = new IndexerQueueImpl();

		IndexerToken indexerRequest1 = new IndexerTokenImpl(
			IndexerWriterMode.DELETE, RandomTestUtil.randomString(),
			RandomTestUtil.randomLong());
		IndexerToken indexerRequest2 = new IndexerTokenImpl(
			IndexerWriterMode.UPDATE, RandomTestUtil.randomString(),
			RandomTestUtil.randomLong());

		indexerQueue.put(indexerRequest1);
		indexerQueue.put(indexerRequest2);

		List<IndexerToken> indexerTokens = indexerQueue.take(1);

		Assert.assertEquals(indexerTokens.toString(), 1, indexerTokens.size());
		Assert.assertEquals(indexerRequest1, indexerTokens.get(0));
	}

	@Test
	public void testEnqueue() {
		IndexerQueue indexerQueue = new IndexerQueueImpl();

		IndexerToken indexerToken = new IndexerTokenImpl(
			IndexerWriterMode.UPDATE, RandomTestUtil.randomString(),
			RandomTestUtil.randomLong());

		indexerQueue.put(indexerToken);

		List<IndexerToken> indexerTokens = indexerQueue.take(1);

		Assert.assertEquals(indexerTokens.toString(), 1, indexerTokens.size());
		Assert.assertEquals(indexerToken, indexerTokens.get(0));
	}

}