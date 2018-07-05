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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Adam Brandizzi
 */
public class IndexerTokenFactoryTest {

	@Before
	public void setUp() {
		_indexerTokenFactory = new IndexerTokenFactoryImpl();
	}

	@Test
	public void testCreateDeleteIndexerToken() {
		long companyId = RandomTestUtil.randomLong();
		String searchEngineId = RandomTestUtil.randomString();
		String uid = RandomTestUtil.randomString();
		boolean commitImmediately = RandomTestUtil.randomBoolean();

		IndexerToken indexerToken =
			_indexerTokenFactory.createDeleteIndexerToken(
				searchEngineId, companyId, uid, commitImmediately);

		Assert.assertEquals(
			IndexerOperation.DELETE, indexerToken.getIndexerOperation());
		Assert.assertEquals(searchEngineId, indexerToken.getSearchEngineId());
		Assert.assertEquals(companyId, indexerToken.getCompanyId());
		Assert.assertEquals(uid, indexerToken.getUid());
		Assert.assertEquals(
			commitImmediately, indexerToken.isCommitImmediately());
	}

	@Test
	public void testCreateReindexCompanyIndexerToken() {
		String searchEngineId = RandomTestUtil.randomString();
		String className = RandomTestUtil.randomString();
		long companyId = RandomTestUtil.randomLong();
		boolean commitImmediately = RandomTestUtil.randomBoolean();

		IndexerToken indexerToken =
			_indexerTokenFactory.createReindexCompanyIndexerToken(
				searchEngineId, companyId, className, commitImmediately);

		Assert.assertEquals(
			IndexerOperation.REINDEX_COMPANY,
			indexerToken.getIndexerOperation());
		Assert.assertEquals(searchEngineId, indexerToken.getSearchEngineId());
		Assert.assertEquals(className, indexerToken.getClassName());
		Assert.assertEquals(companyId, indexerToken.getCompanyId());
		Assert.assertEquals(
			commitImmediately, indexerToken.isCommitImmediately());
	}

	@Test
	public void testCreateReindexModeIndexerToken() {
		long companyId = RandomTestUtil.randomLong();
		String searchEngineId = RandomTestUtil.randomString();
		String className = RandomTestUtil.randomString();
		long primaryKeyObj = RandomTestUtil.randomLong();
		boolean commitImmediately = RandomTestUtil.randomBoolean();

		IndexerToken indexerToken =
			_indexerTokenFactory.createReindexModelIndexerToken(
				searchEngineId, companyId, className, primaryKeyObj,
				commitImmediately);

		Assert.assertEquals(
			IndexerOperation.REINDEX_MODEL, indexerToken.getIndexerOperation());
		Assert.assertEquals(searchEngineId, indexerToken.getSearchEngineId());
		Assert.assertEquals(companyId, indexerToken.getCompanyId());
		Assert.assertEquals(className, indexerToken.getClassName());
		Assert.assertEquals(primaryKeyObj, indexerToken.getPrimaryKeyObj());
		Assert.assertEquals(
			commitImmediately, indexerToken.isCommitImmediately());
	}

	@Test
	public void testCreateUpdatePermissionFieldsIndexerToken() {
		long companyId = RandomTestUtil.randomLong();
		String searchEngineId = RandomTestUtil.randomString();
		String className = RandomTestUtil.randomString();
		long primaryKeyObj = RandomTestUtil.randomLong();
		boolean commitImmediately = RandomTestUtil.randomBoolean();

		IndexerToken indexerToken =
			_indexerTokenFactory.createUpdatePermissionFieldsIndexerToken(
				searchEngineId, companyId, className, primaryKeyObj,
				commitImmediately);

		Assert.assertEquals(
			IndexerOperation.UPDATE_PERMISSION_FIELDS,
			indexerToken.getIndexerOperation());
		Assert.assertEquals(searchEngineId, indexerToken.getSearchEngineId());
		Assert.assertEquals(companyId, indexerToken.getCompanyId());
		Assert.assertEquals(className, indexerToken.getClassName());
		Assert.assertEquals(primaryKeyObj, indexerToken.getPrimaryKeyObj());
		Assert.assertEquals(
			commitImmediately, indexerToken.isCommitImmediately());
	}

	private IndexerTokenFactory _indexerTokenFactory;

}