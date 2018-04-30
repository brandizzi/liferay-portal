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

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.search.batch.BatchIndexingActionable;
import com.liferay.portal.search.index.UpdateDocumentIndexWriter;
import com.liferay.portal.search.indexer.BaseModelRetriever;
import com.liferay.portal.search.indexer.IndexerDocumentBuilder;
import com.liferay.portal.search.permission.SearchPermissionIndexWriter;
import com.liferay.portal.search.spi.model.index.contributor.ModelIndexerWriterContributor;
import com.liferay.portal.search.spi.model.index.contributor.helper.ModelIndexerWriterDocumentHelper;

import java.util.Optional;

/**
 * @author Adam Brandizzi
 */
public class IndexerTokenConsumer<T extends BaseModel<?>> {

	public IndexerTokenConsumer(
		BaseModelRetriever baseModelRetriever,
		IndexerDocumentBuilder indexerDocumentBuilder,
		IndexWriterHelper indexWriterHelper,
		ModelIndexerWriterContributor<T> modelIndexerWriterContributor,
		SearchPermissionIndexWriter searchPermissionIndexWriter,
		UpdateDocumentIndexWriter updateDocumentIndexWriter) {

		_baseModelRetriever = baseModelRetriever;
		_indexerDocumentBuilder = indexerDocumentBuilder;
		_indexWriterHelper = indexWriterHelper;
		_modelIndexerWriterContributor = modelIndexerWriterContributor;
		_searchPermissionIndexWriter = searchPermissionIndexWriter;
		_updateDocumentIndexWriter = updateDocumentIndexWriter;
	}

	public void consume(IndexerToken indexerToken) {
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
		catch (SearchException se) {
			_log.error(
				"Failed to delete document (uid=" + indexerToken.getUid() +
					") from index.", se);
		}
	}

	protected void executeReindexCompany(IndexerToken indexerToken) {
		long companyThreadLocalCompanyId = CompanyThreadLocal.getCompanyId();

		try {
			long companyId = indexerToken.getCompanyId();

			CompanyThreadLocal.setCompanyId(companyId);

			BatchIndexingActionable batchIndexingActionable =
				_modelIndexerWriterContributor.getBatchIndexingActionable();

			batchIndexingActionable.setSearchEngineId(
				indexerToken.getSearchEngineId());

			batchIndexingActionable.setCompanyId(companyId);

			_modelIndexerWriterContributor.customize(
				batchIndexingActionable,
				new ModelIndexerWriterDocumentHelper() {

					@Override
					public Document getDocument(BaseModel baseModel) {
						return _indexerDocumentBuilder .getDocument(baseModel);
					}

				});

			try {
				batchIndexingActionable.performActions();
			}
			catch (Exception pe) {
				if (_log.isWarnEnabled()) {
					StringBundler sb = new StringBundler(4);

					sb.append("Error reindexing all ");
					sb.append(indexerToken.getClassName());
					sb.append(" for company: ");
					sb.append(companyId);

					_log.warn(sb.toString(), pe);
				}
			}
		}
		finally {
			CompanyThreadLocal.setCompanyId(companyThreadLocalCompanyId);
		}
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
		Document document = _indexerDocumentBuilder.getDocument(baseModel);

		return document;
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

	private final BaseModelRetriever _baseModelRetriever;
	private final IndexerDocumentBuilder _indexerDocumentBuilder;
	private final IndexWriterHelper _indexWriterHelper;
	private final ModelIndexerWriterContributor<T>
		_modelIndexerWriterContributor;
	private final SearchPermissionIndexWriter _searchPermissionIndexWriter;
	private final UpdateDocumentIndexWriter _updateDocumentIndexWriter;

}