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

package com.liferay.portal.search.internal.indexer.token;

import com.liferay.portal.kernel.test.util.RandomTestUtil;

import java.io.Serializable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Adam Brandizzi
 */
public class IndexerTokenTest {

	@Before
	public void setUp() {
		_indexerTokenFactory = new IndexerTokenFactoryImpl();
	}

	@Test
	public void testEquals() {
		String className = RandomTestUtil.randomString();
		boolean commitImmediately = RandomTestUtil.randomBoolean();
		long companyId = RandomTestUtil.randomLong();
		IndexerOperation indexerOperation = randomIndexerOperation();
		Serializable primaryKeyObj = RandomTestUtil.randomLong();
		String searchEngineId = RandomTestUtil.randomString();
		String uid = RandomTestUtil.randomString();

		IndexerToken indexerRequest1 = _indexerTokenFactory.createIndexerToken(
			searchEngineId, indexerOperation, companyId, className,
			primaryKeyObj, uid, commitImmediately);
		IndexerToken indexerRequest2 = _indexerTokenFactory.createIndexerToken(
			searchEngineId, indexerOperation, companyId, className,
			primaryKeyObj, uid, commitImmediately);

		Assert.assertEquals(indexerRequest1, indexerRequest2);
	}

	@Test
	public void testEqualsComparesClassName() {
		boolean commitImmediately = RandomTestUtil.randomBoolean();
		long companyId = RandomTestUtil.randomLong();
		IndexerOperation indexerOperation = randomIndexerOperation();
		Serializable primaryKeyObj = RandomTestUtil.randomLong();
		String searchEngineId = RandomTestUtil.randomString();
		String uid = RandomTestUtil.randomString();

		IndexerToken indexerRequest1 = _indexerTokenFactory.createIndexerToken(
			searchEngineId, indexerOperation, companyId,
			RandomTestUtil.randomString(), primaryKeyObj, uid,
			commitImmediately);
		IndexerToken indexerRequest2 = _indexerTokenFactory.createIndexerToken(
			searchEngineId, indexerOperation, companyId,
			RandomTestUtil.randomString(), primaryKeyObj, uid,
			commitImmediately);

		Assert.assertNotEquals(indexerRequest1, indexerRequest2);
	}

	@Test
	public void testEqualsComparesCommitImmediately() {
		String className = RandomTestUtil.randomString();
		long companyId = RandomTestUtil.randomLong();
		IndexerOperation indexerOperation = randomIndexerOperation();
		Serializable primaryKeyObj = RandomTestUtil.randomLong();
		String searchEngineId = RandomTestUtil.randomString();
		String uid = RandomTestUtil.randomString();

		IndexerToken indexerRequest1 = _indexerTokenFactory.createIndexerToken(
			searchEngineId, indexerOperation, companyId, className,
			primaryKeyObj, uid, true);
		IndexerToken indexerRequest2 = _indexerTokenFactory.createIndexerToken(
			searchEngineId, indexerOperation, companyId, className,
			primaryKeyObj, uid, false);

		Assert.assertNotEquals(indexerRequest1, indexerRequest2);
	}

	@Test
	public void testEqualsComparesCompanyId() {
		String className = RandomTestUtil.randomString();
		boolean commitImmediately = RandomTestUtil.randomBoolean();
		IndexerOperation indexerOperation = randomIndexerOperation();
		Serializable primaryKeyObj = RandomTestUtil.randomLong();
		String searchEngineId = RandomTestUtil.randomString();
		String uid = RandomTestUtil.randomString();

		IndexerToken indexerRequest1 = _indexerTokenFactory.createIndexerToken(
			searchEngineId, indexerOperation, RandomTestUtil.randomLong(),
			className, primaryKeyObj, uid, commitImmediately);
		IndexerToken indexerRequest2 = _indexerTokenFactory.createIndexerToken(
			searchEngineId, indexerOperation, RandomTestUtil.randomLong(),
			className, primaryKeyObj, uid, commitImmediately);

		Assert.assertNotEquals(indexerRequest1, indexerRequest2);
	}

	@Test
	public void testEqualsComparesIndexerOperation() {
		String className = RandomTestUtil.randomString();
		boolean commitImmediately = RandomTestUtil.randomBoolean();
		long companyId = RandomTestUtil.randomLong();
		Serializable primaryKeyObj = RandomTestUtil.randomLong();
		String searchEngineId = RandomTestUtil.randomString();
		String uid = RandomTestUtil.randomString();

		IndexerToken indexerRequest1 = _indexerTokenFactory.createIndexerToken(
			searchEngineId, IndexerOperation.DELETE, companyId, className,
			primaryKeyObj, uid, commitImmediately);
		IndexerToken indexerRequest2 = _indexerTokenFactory.createIndexerToken(
			searchEngineId, IndexerOperation.REINDEX_MODEL, companyId,
			className, primaryKeyObj, uid, commitImmediately);

		Assert.assertNotEquals(indexerRequest1, indexerRequest2);
	}

	@Test
	public void testEqualsComparesPrimaryKeyObj() {
		String className = RandomTestUtil.randomString();
		boolean commitImmediately = RandomTestUtil.randomBoolean();
		long companyId = RandomTestUtil.randomLong();
		IndexerOperation indexerOperation = randomIndexerOperation();
		String searchEngineId = RandomTestUtil.randomString();
		String uid = RandomTestUtil.randomString();

		IndexerToken indexerRequest1 = _indexerTokenFactory.createIndexerToken(
			searchEngineId, indexerOperation, companyId, className,
			(Serializable)RandomTestUtil.randomLong(), uid, commitImmediately);
		IndexerToken indexerRequest2 = _indexerTokenFactory.createIndexerToken(
			searchEngineId, indexerOperation, companyId, className,
			(Serializable)RandomTestUtil.randomLong(), uid, commitImmediately);

		Assert.assertNotEquals(indexerRequest1, indexerRequest2);
	}

	@Test
	public void testEqualsComparesSearchEngineId() {
		String className = RandomTestUtil.randomString();
		boolean commitImmediately = RandomTestUtil.randomBoolean();
		long companyId = RandomTestUtil.randomLong();
		IndexerOperation indexerOperation = randomIndexerOperation();
		Serializable primaryKeyObj = RandomTestUtil.randomLong();
		String uid = RandomTestUtil.randomString();

		IndexerToken indexerRequest1 = _indexerTokenFactory.createIndexerToken(
			RandomTestUtil.randomString(), indexerOperation, companyId,
			className, primaryKeyObj, uid, commitImmediately);
		IndexerToken indexerRequest2 = _indexerTokenFactory.createIndexerToken(
			RandomTestUtil.randomString(), indexerOperation, companyId,
			className, primaryKeyObj, uid, commitImmediately);

		Assert.assertNotEquals(indexerRequest1, indexerRequest2);
	}

	@Test
	public void testEqualsComparesUid() {
		String className = RandomTestUtil.randomString();
		boolean commitImmediately = RandomTestUtil.randomBoolean();
		long companyId = RandomTestUtil.randomLong();
		IndexerOperation indexerOperation = randomIndexerOperation();
		Serializable primaryKeyObj = RandomTestUtil.randomLong();
		String searchEngineId = RandomTestUtil.randomString();

		IndexerToken indexerRequest1 = _indexerTokenFactory.createIndexerToken(
			searchEngineId, indexerOperation, companyId, className,
			primaryKeyObj, RandomTestUtil.randomString(), commitImmediately);
		IndexerToken indexerRequest2 = _indexerTokenFactory.createIndexerToken(
			searchEngineId, indexerOperation, companyId, className,
			primaryKeyObj, RandomTestUtil.randomString(), commitImmediately);

		Assert.assertNotEquals(indexerRequest1, indexerRequest2);
	}

	protected IndexerOperation randomIndexerOperation() {
		int randomIndex = RandomTestUtil.randomInt(
			0, _indexerOperations.length - 1);

		return _indexerOperations[randomIndex];
	}

	private final IndexerOperation[] _indexerOperations =
		IndexerOperation.values();
	private IndexerTokenFactory _indexerTokenFactory;

}