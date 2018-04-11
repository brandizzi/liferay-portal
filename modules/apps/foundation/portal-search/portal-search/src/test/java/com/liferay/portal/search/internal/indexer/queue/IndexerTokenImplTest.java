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
import com.liferay.portal.search.spi.model.index.queue.IndexerToken;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Adam Brandizzi
 */
public class IndexerTokenImplTest {

	@Test
	public void testEquals() {
		long classPK = RandomTestUtil.randomLong();
		String className = RandomTestUtil.randomString();
		IndexerWriterMode indexerWriterMode = randomIndexerWriterMode();

		IndexerToken indexerRequest1 = new IndexerTokenImpl(
			indexerWriterMode, className, classPK);
		IndexerToken indexerRequest2 = new IndexerTokenImpl(
			indexerWriterMode, className, classPK);

		Assert.assertEquals(indexerRequest1, indexerRequest2);
	}

	@Test
	public void testEqualsByUid() {
		IndexerWriterMode indexerWriterMode = randomIndexerWriterMode();
		String uid = RandomTestUtil.randomString();

		IndexerToken indexerRequest1 = new IndexerTokenImpl(
			indexerWriterMode, uid);
		IndexerToken indexerRequest2 = new IndexerTokenImpl(
			indexerWriterMode, uid);

		Assert.assertEquals(indexerRequest1, indexerRequest2);
	}

	@Test
	public void testEqualsComparesClassName() {
		long classPK = RandomTestUtil.randomLong();
		IndexerWriterMode indexerWriterMode = randomIndexerWriterMode();

		IndexerToken indexerRequest1 = new IndexerTokenImpl(
			indexerWriterMode, RandomTestUtil.randomString(), classPK);
		IndexerToken indexerRequest2 = new IndexerTokenImpl(
			indexerWriterMode, RandomTestUtil.randomString(), classPK);

		Assert.assertNotEquals(indexerRequest1, indexerRequest2);
	}

	@Test
	public void testEqualsComparesClassPK() {
		String className = RandomTestUtil.randomString();
		IndexerWriterMode indexerWriterMode = randomIndexerWriterMode();

		IndexerToken indexerRequest1 = new IndexerTokenImpl(
			indexerWriterMode, className, RandomTestUtil.randomLong());
		IndexerToken indexerRequest2 = new IndexerTokenImpl(
			indexerWriterMode, className, RandomTestUtil.randomLong());

		Assert.assertNotEquals(indexerRequest1, indexerRequest2);
	}

	@Test
	public void testEqualsComparesIndexerWriterMode() {
		long classPK = RandomTestUtil.randomLong();
		String className = RandomTestUtil.randomString();

		IndexerToken indexerRequest1 = new IndexerTokenImpl(
			IndexerWriterMode.DELETE, className, classPK);
		IndexerToken indexerRequest2 = new IndexerTokenImpl(
			IndexerWriterMode.PARTIAL_UPDATE, className, classPK);

		Assert.assertNotEquals(indexerRequest1, indexerRequest2);
	}

	@Test
	public void testEqualsComparesUid() {
		IndexerWriterMode indexerWriterMode = randomIndexerWriterMode();

		IndexerToken indexerRequest1 = new IndexerTokenImpl(
			indexerWriterMode, RandomTestUtil.randomString());
		IndexerToken indexerRequest2 = new IndexerTokenImpl(
			indexerWriterMode, RandomTestUtil.randomString());

		Assert.assertNotEquals(indexerRequest1, indexerRequest2);
	}

	protected IndexerWriterMode randomIndexerWriterMode() {
		int randomIndex = RandomTestUtil.randomInt(
		0, _indexerWriterModes.length - 1);

		return _indexerWriterModes[randomIndex];
	}

	private final IndexerWriterMode[] _indexerWriterModes =
		IndexerWriterMode.values();

}