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

import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.search.internal.indexer.queue.IndexerTokenImpl;
import com.liferay.portal.search.spi.model.index.contributor.helper.IndexerWriterMode;
import com.liferay.portal.search.spi.model.index.dispatcher.IndexerDispatcher;
import com.liferay.portal.search.spi.model.index.queue.IndexerQueue;
import com.liferay.portal.search.spi.model.index.queue.IndexerToken;

/**
 * @author Adam Brandizzi
 */
public class QueueIndexerDispatcher implements IndexerDispatcher {

	public QueueIndexerDispatcher(IndexerQueue indexerQueue) {
		_indexerQueue = indexerQueue;
	}

	@Override
	public void deleteDocument(long companyId, BaseModel<?> baseModel)
		throws SearchException {

		IndexerToken indexerToken = new IndexerTokenImpl(
			IndexerWriterMode.DELETE, baseModel.getModelClassName(),
			baseModel.getPrimaryKeyObj());

		_indexerQueue.put(indexerToken);
	}

	@Override
	public void deleteDocument(long companyId, String uid)
		throws SearchException {

		IndexerToken indexerToken = new IndexerTokenImpl(
			IndexerWriterMode.DELETE, uid);

		_indexerQueue.put(indexerToken);
	}

	@Override
	public void updateDocument(long companyId, BaseModel<?> baseModel) {
		IndexerToken indexerToken = new IndexerTokenImpl(
			IndexerWriterMode.UPDATE, baseModel.getModelClassName(),
			baseModel.getPrimaryKeyObj());

		_indexerQueue.put(indexerToken);
	}

	@Override
	public void updatePermissionFields(long companyId, BaseModel<?> baseModel) {

		// TODO Auto-generated method stub

	}

	private final IndexerQueue _indexerQueue;

}