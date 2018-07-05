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
import com.liferay.portal.search.internal.indexer.token.IndexerToken;
import com.liferay.portal.search.internal.indexer.token.IndexerTokenFactory;
import com.liferay.portal.search.internal.indexer.token.IndexerTokenFactoryImpl;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Adam Brandizzi
 */
public class IndexerQueueTest {

	@Before
	public void setUp() {
		_indexerTokenFactory = new IndexerTokenFactoryImpl();
	}

	@Test
	public void testIsEmpty() {
		IndexerQueue indexerQueue = new IndexerQueue();

		Assert.assertTrue(indexerQueue.isEmpty());
	}

	@Test
	public void testIsNotEmpty() {
		IndexerQueue indexerQueue = new IndexerQueue();

		indexerQueue.put(createIndexerToken());

		Assert.assertFalse(indexerQueue.isEmpty());
	}

	@Test
	public void testPut() {
		IndexerQueue indexerQueue = new IndexerQueue();

		IndexerToken indexerToken = createIndexerToken();

		indexerQueue.put(indexerToken);

		List<IndexerToken> indexerTokens = indexerQueue.take(1);

		assertIndexerTokensListContainsOneToken(indexerTokens, indexerToken);
	}

	@Test
	public void testRemove() {
		IndexerQueue indexerQueue = new IndexerQueue();

		IndexerToken indexerToken = createIndexerToken();

		indexerQueue.put(indexerToken);

		List<IndexerToken> indexerTokens = indexerQueue.take(1);

		indexerQueue.remove(indexerTokens);

		indexerTokens = indexerQueue.take(1);

		Assert.assertTrue(indexerTokens.isEmpty());
	}

	@Test
	public void testTakeEmpty() {
		IndexerQueue indexerQueue = new IndexerQueue();

		List<IndexerToken> indexerTokens = indexerQueue.take(1);

		assertIndexerTokensListIsEmpty(indexerTokens);
	}

	@Test
	public void testTakeMoreThanAvailable() {
		IndexerQueue indexerQueue = new IndexerQueue();

		IndexerToken indexerToken = createIndexerToken();

		indexerQueue.put(indexerToken);

		List<IndexerToken> indexerTokens = indexerQueue.take(2);

		assertIndexerTokensListContainsOneToken(indexerTokens, indexerToken);
	}

	@Test
	public void testTakeRequestedNumber() {
		IndexerQueue indexerQueue = new IndexerQueue();

		IndexerToken indexerRequest1 = createIndexerToken();
		IndexerToken indexerRequest2 = createIndexerToken();

		indexerQueue.put(indexerRequest1);
		indexerQueue.put(indexerRequest2);

		List<IndexerToken> indexerTokens = indexerQueue.take(1);

		assertIndexerTokensListContainsOneToken(indexerTokens, indexerRequest1);
	}

	protected void assertIndexerTokensListContainsOneToken(
		List<IndexerToken> indexerTokens, IndexerToken indexerToken) {

		Assert.assertEquals(indexerTokens.toString(), 1, indexerTokens.size());
		Assert.assertEquals(indexerToken, indexerTokens.get(0));
	}

	protected void assertIndexerTokensListIsEmpty(
		List<IndexerToken> indexerTokens) {

		Assert.assertEquals(indexerTokens.toString(), 0, indexerTokens.size());
	}

	protected IndexerToken createIndexerToken() {
		return _indexerTokenFactory.createReindexModelIndexerToken(
			RandomTestUtil.randomString(), RandomTestUtil.randomLong(),
			RandomTestUtil.randomString(), RandomTestUtil.randomLong(),
			RandomTestUtil.randomBoolean());
	}

	private IndexerTokenFactory _indexerTokenFactory;

}