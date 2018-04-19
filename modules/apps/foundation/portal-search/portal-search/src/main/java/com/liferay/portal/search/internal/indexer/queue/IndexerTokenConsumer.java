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

import java.io.Serializable;
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
public class IndexerTokenConsumer {

	public void execute(IndexerToken indexerToken) {
		IndexerOperation indexerOperation = indexerToken.getIndexerOperation();

		if (indexerOperation == IndexerOperation.DELETE) {
			executeDelete(
				indexerToken.getSearchEngineId(), indexerToken.getCompanyId(),
				indexerToken.getUid(), indexerToken.isCommitImmediately());
		}
		else if (indexerOperation == IndexerOperation.REINDEX_COMPANY) {
			executeReindexCompany(
				indexerToken.getSearchEngineId(), indexerToken.getCompanyId(),
				indexerToken.getClassName(),
				indexerToken.isCommitImmediately());
		}
		else if (indexerOperation == IndexerOperation.REINDEX_MODEL) {
			executeReindexModel(
				indexerToken.getSearchEngineId(), indexerToken.getCompanyId(),
				indexerToken.getClassName(), indexerToken.getPrimaryKeyObj(),
				indexerToken.isCommitImmediately());
		}
		else if (indexerOperation ==
					IndexerOperation.UPDATE_PERMISSION_FIELDS) {

			executeUpdatePermissionFields(
				indexerToken.getSearchEngineId(), indexerToken.getCompanyId(),
				indexerToken.getClassName(), indexerToken.getPrimaryKeyObj(),
				indexerToken.isCommitImmediately());
		}
		else {
			throw new UnsupportedOperationException(
				"IndexerWriter operation " + indexerOperation + " unknown");
		}
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

	protected void executeDelete(
		String searchEngineId, long companyId, String uid,
		boolean commitImmediately) {

		try {
			_indexWriterHelper.deleteDocument(
				searchEngineId, companyId, uid, commitImmediately);
		}
		catch (SearchException se) {
			_log.error(
				"Failed to delete document (uid=" + uid + ") from index.", se);
		}
	}

	protected void executeReindexCompany(
		String searchEngineId, long companyId, String className,
		boolean commitImmediately) {

		// TODO

		throw new UnsupportedOperationException(
			"Company reindex not implemented.");
	}

	protected void executeReindexModel(
		String searchEngineId, long companyId, String className,
		Serializable primaryKeyObj, boolean commitImmediately) {

		long classPK = GetterUtil.getLong(primaryKeyObj);

		Optional<BaseModel<?>> baseModelOptional = getBaseModelOptional(
			className, classPK);

		baseModelOptional.ifPresent(
			baseModel -> reindex(
				searchEngineId, companyId, baseModel, commitImmediately));
	}

	protected void executeUpdatePermissionFields(
		String searchEngineId, long companyId, String className,
		Serializable primaryKeyObj, boolean commitImmediately) {

		long classPK = GetterUtil.getLong(primaryKeyObj);

		Optional<BaseModel<?>> baseModelOptional = getBaseModelOptional(
			className, classPK);

		baseModelOptional.ifPresent(
			baseModel -> reindex(
					searchEngineId, companyId, baseModel, commitImmediately));
	}

	protected Optional<BaseModel<?>> getBaseModelOptional(
			String className, long classPK) {

		return _baseModelRetriever.fetchBaseModel(className, classPK);
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
		IndexerTokenConsumer.class);

	@Reference
	private BaseModelRetriever _baseModelRetriever;

	private ServiceTrackerMap<String, IndexerDocumentBuilder>
		_indexerDocumentBuilderServiceTrackerMap;

	@Reference
	private IndexWriterHelper _indexWriterHelper;

	@Reference
	private SearchPermissionIndexWriter _searchPermissionIndexWriter;

	@Reference
	private UpdateDocumentIndexWriter _updateDocumentIndexWriter;

}