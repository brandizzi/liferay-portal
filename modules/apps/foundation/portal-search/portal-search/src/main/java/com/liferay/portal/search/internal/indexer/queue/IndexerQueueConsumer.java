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

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.search.index.UpdateDocumentIndexWriter;
import com.liferay.portal.search.indexer.BaseModelRetriever;
import com.liferay.portal.search.indexer.IndexerDocumentBuilder;
import com.liferay.portal.search.internal.indexer.token.IndexerOperation;
import com.liferay.portal.search.internal.indexer.token.IndexerToken;
import com.liferay.portal.search.permission.SearchPermissionIndexWriter;

import java.util.List;
import java.util.Optional;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adam Brandizzi
 */
@Component
public class IndexerQueueConsumer {

	public void run() {
		List<IndexerToken> indexerTokens = _indexerQueue.take(1);

		execute(indexerTokens.get(0));
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_indexerDocumentBuilderServiceTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext, IndexerDocumentBuilder.class,
				"indexer.class.name");
	}

	@Deactivate
	protected void deactivate() {
		_indexerDocumentBuilderServiceTrackerMap.close();
	}

	protected void execute(IndexerToken indexerToken) {
		IndexerOperation indexerOperation = indexerToken.getIndexerOperation();

		if (indexerOperation == IndexerOperation.DELETE) {
			executeDelete(indexerToken);
		}
		else if (indexerOperation == IndexerOperation.REINDEX_COMPANY) {
			executeReindexCompany(indexerToken);
		}
		else if (indexerOperation == IndexerOperation.REINDEX_MODEL) {
			executeReindexModel(indexerToken);
		}
		else if (indexerOperation ==
					IndexerOperation.UPDATE_PERMISSION_FIELDS) {

			executeUpdatePermissionFields(indexerToken);
		}
		else {
			throw new UnsupportedOperationException(
				"IndexerWriter operation " + indexerOperation + " unknown");
		}
	}

	protected void executeDelete(IndexerToken indexerToken) {
		try {
			_indexWriterHelper.deleteDocument(
				indexerToken.getSearchEngineId(), indexerToken.getCompanyId(),
				indexerToken.getUid(), indexerToken.isCommitImmediately());
		}
		catch (SearchException e) {
			_log.error(
				"Failed to delete document (uid=" + indexerToken.getUid() +
					") from index.",
				e);
		}
	}

	protected void executeReindexCompany(IndexerToken indexerToken) {

		// TODO

		throw new UnsupportedOperationException(
			"Company reindex not implemented.");
	}

	protected void executeReindexModel(IndexerToken indexerToken) {
		Optional<BaseModel<?>> baseModelOptional = getBaseModelOptional(
			indexerToken);

		baseModelOptional.ifPresent(
			baseModel -> reindex(
				indexerToken.getSearchEngineId(), indexerToken.getCompanyId(),
				baseModel, indexerToken.isCommitImmediately()));
	}

	protected void executeUpdatePermissionFields(IndexerToken indexerToken) {
		Optional<BaseModel<?>> baseModelOptional = getBaseModelOptional(
			indexerToken);

		baseModelOptional.ifPresent(
			baseModel -> reindex(
				indexerToken.getSearchEngineId(), indexerToken.getCompanyId(),
				baseModel, indexerToken.isCommitImmediately()));
	}

	protected Optional<BaseModel<?>> getBaseModelOptional(
		IndexerToken indexerToken) {

		long classPK = GetterUtil.getLong(indexerToken.getPrimaryKeyObj());

		return _baseModelRetriever.fetchBaseModel(
			indexerToken.getClassName(), classPK);
	}

	protected Document getDocument(BaseModel<?> baseModel) {
		IndexerDocumentBuilder indexerDocumentBuilder =
			getIndexerDocumentBuilder(baseModel.getModelClassName());

		Document document = indexerDocumentBuilder.getDocument(baseModel);

		return document;
	}

	protected IndexerDocumentBuilder getIndexerDocumentBuilder(
		String className) {

		return _indexerDocumentBuilderServiceTrackerMap.getService(className);
	}

	protected void reindex(
		String searchEngineId, long companyId, BaseModel<?> baseModel,
		boolean commitImmediately) {

		Document document = getDocument(baseModel);

		_updateDocumentIndexWriter.updateDocument(
			searchEngineId, companyId, document, commitImmediately);
	}

	protected void updatePermissionFields(
		String searchEngineId, long companyId, BaseModel<?> baseModel,
		boolean commitImmediately) {

		_searchPermissionIndexWriter.updatePermissionFields(
			baseModel, companyId, searchEngineId, commitImmediately);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		IndexerQueueConsumer.class);

	@Reference
	private BaseModelRetriever _baseModelRetriever;

	private ServiceTrackerMap<String, IndexerDocumentBuilder>
		_indexerDocumentBuilderServiceTrackerMap;
	private IndexerQueue _indexerQueue;

	@Reference
	private IndexWriterHelper _indexWriterHelper;

	@Reference
	private SearchPermissionIndexWriter _searchPermissionIndexWriter;

	@Reference
	private UpdateDocumentIndexWriter _updateDocumentIndexWriter;

}