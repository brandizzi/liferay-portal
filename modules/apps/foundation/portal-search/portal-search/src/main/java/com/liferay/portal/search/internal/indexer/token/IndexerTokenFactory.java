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
public interface IndexerTokenFactory {

	public IndexerToken createDeleteIndexerToken(
		String searchEngineId, long companyId, String uid,
		boolean commitImmediately);

	public IndexerToken createIndexerToken(
		String searchEngineId, IndexerOperation indexerOperation,
		long companyId, String className, Serializable primaryKeyObj,
		String uid, boolean commitImmediately);

	public IndexerToken createReindexCompanyIndexerToken(
		String searchEngineId, long companyId, String className,
		boolean commitImmediately);

	public IndexerToken createReindexModelIndexerToken(
		String searchEngineId, long companyId, String className,
		Serializable primaryKeyObj, boolean commitImmediately);

	public IndexerToken createUpdatePermissionFieldsIndexerToken(
		String searchEngineId, long companyId, String className,
		Serializable primaryKeyObj, boolean commitImmediately);

}