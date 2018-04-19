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

import java.io.Serializable;

/**
 * @author Adam Brandizzi
 */
public class IndexerTokenFactoryImpl implements IndexerTokenFactory {

	@Override
	public IndexerToken createDeleteIndexerToken(
		String searchEngineId, long companyId, String uid,
		boolean commitImmediately) {

		return createIndexerToken(
			searchEngineId, IndexerOperation.DELETE, companyId, null, null, uid,
			commitImmediately);
	}

	@Override
	public IndexerToken createIndexerToken(
		String searchEngineId, IndexerOperation indexerOperation,
		long companyId, String className, Serializable primaryKeyObj,
		String uid, boolean commitImmediately) {

		IndexerTokenImpl indexerToken = new IndexerTokenImpl();

		indexerToken.setSearchEngineId(searchEngineId);
		indexerToken.setIndexerOperation(indexerOperation);
		indexerToken.setCompanyId(companyId);
		indexerToken.setClassName(className);
		indexerToken.setPrimaryKeyObj(primaryKeyObj);
		indexerToken.setUid(uid);
		indexerToken.setCommitImmediately(commitImmediately);

		return indexerToken;
	}

	@Override
	public IndexerToken createReindexCompanyIndexerToken(
		String searchEngineId, long companyId, String className,
		boolean commitImmediately) {

		return createIndexerToken(
			searchEngineId, IndexerOperation.REINDEX_COMPANY, companyId,
			className, null, null, commitImmediately);
	}

	@Override
	public IndexerToken createReindexModelIndexerToken(
		String searchEngineId, long companyId, String className,
		Serializable primaryKeyObj, boolean commitImmediately) {

		return createIndexerToken(
			searchEngineId, IndexerOperation.REINDEX_MODEL, companyId,
			className, primaryKeyObj, null, commitImmediately);
	}

	@Override
	public IndexerToken createUpdatePermissionFieldsIndexerToken(
		String searchEngineId, long companyId, String className,
		Serializable primaryKeyObj, boolean commitImmediately) {

		return createIndexerToken(
			searchEngineId, IndexerOperation.UPDATE_PERMISSION_FIELDS,
			companyId, className, primaryKeyObj, null, commitImmediately);
	}

}