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

package com.liferay.portal.search.internal.indexer;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.model.WorkflowedModel;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.search.batch.BatchIndexingActionable;
import com.liferay.portal.search.indexer.BaseModelRetriever;
import com.liferay.portal.search.indexer.IndexerDocumentBuilder;
import com.liferay.portal.search.indexer.IndexerWriter;
import com.liferay.portal.search.internal.indexer.queue.IndexerQueue;
import com.liferay.portal.search.internal.indexer.token.IndexerToken;
import com.liferay.portal.search.internal.indexer.token.IndexerTokenFactory;
import com.liferay.portal.search.spi.model.index.contributor.ModelIndexerWriterContributor;
import com.liferay.portal.search.spi.model.index.contributor.helper.IndexerWriterMode;
import com.liferay.portal.search.spi.model.registrar.ModelSearchSettings;

import java.util.Collection;
import java.util.Optional;

/**
 * @author Adam Brandizzi
 */
public class QueueIndexerWriter<T extends BaseModel<?>>
	implements IndexerWriter<T> {

	public QueueIndexerWriter(
		BaseModelRetriever baseModelRetriever,
		IndexerTokenFactory indexerTokenFactory, IndexerQueue indexerQueue,
		ModelSearchSettings modelSearchSettings,
		ModelIndexerWriterContributor<T> modelIndexerWriterContributor,
		IndexerDocumentBuilder indexerDocumentBuilder, boolean enabled) {

		_baseModelRetriever = baseModelRetriever;
		_indexerTokenFactory = indexerTokenFactory;
		_indexerQueue = indexerQueue;
		_modelSearchSettings = modelSearchSettings;
		_modelIndexerWriterContributor = modelIndexerWriterContributor;
		_indexerDocumentBuilder = indexerDocumentBuilder;
		_enabled = enabled;
	}

	@Override
	public void delete(long companyId, String uid) {
		if (!isEnabled()) {
			return;
		}

		_indexerQueue.put(createDeleteIndexerToken(companyId, uid));
	}

	@Override
	public void delete(T baseModel) {
		long companyId = _modelIndexerWriterContributor.getCompanyId(baseModel);

		String uid = _indexerDocumentBuilder.getDocumentUID(baseModel);

		delete(companyId, uid);
	}

	@Override
	public BatchIndexingActionable getBatchIndexingActionable() {
		BatchIndexingActionable batchIndexingActionable =
			_modelIndexerWriterContributor.getBatchIndexingActionable();

		batchIndexingActionable.setSearchEngineId(
			_modelSearchSettings.getSearchEngineId());

		return batchIndexingActionable;
	}

	@Override
	public boolean isEnabled() {
		return _enabled;
	}

	@Override
	public void reindex(Collection<T> baseModels) {
		for (T baseModel : baseModels) {
			reindex(baseModel);
		}
	}

	@Override
	public void reindex(long classPK) {
		Optional<BaseModel<?>> baseModelOptional =
			_baseModelRetriever.fetchBaseModel(
				_modelSearchSettings.getClassName(), classPK);

		baseModelOptional.ifPresent(baseModel -> reindex((T)baseModel));
	}

	@Override
	public void reindex(String[] ids) {
		if (!isEnabled()) {
			return;
		}

		for (String id : ids) {
			long companyId = GetterUtil.getLong(id);

			IndexerToken indexerToken = createReindexCompanyIndexerToken(
				companyId);

			_indexerQueue.put(indexerToken);
		}
	}

	@Override
	public void reindex(T baseModel) {
		if (!isEnabled()) {
			return;
		}

		if (baseModel == null) {
			return;
		}

		IndexerWriterMode indexerWriterMode = _getIndexerWriterMode(baseModel);

		if ((indexerWriterMode == IndexerWriterMode.UPDATE) ||
			(indexerWriterMode == IndexerWriterMode.PARTIAL_UPDATE)) {

			_indexerQueue.put(createReindexModelIndexerToken(baseModel));
		}
		else if (indexerWriterMode == IndexerWriterMode.DELETE) {
			delete(baseModel);
		}
		else if (indexerWriterMode == IndexerWriterMode.SKIP) {
			if (_log.isDebugEnabled()) {
				_log.debug("Skipping model " + baseModel);
			}
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		_enabled = enabled;
	}

	@Override
	public void updatePermissionFields(T baseModel) {
		_indexerQueue.put(createUpdatePermissionFieldsIndexerToken(baseModel));
	}

	protected IndexerToken createDeleteIndexerToken(
		long companyId, String uid) {

		return _indexerTokenFactory.createDeleteIndexerToken(
			_modelSearchSettings.getSearchEngineId(), companyId, uid,
			_modelSearchSettings.isCommitImmediately());
	}

	protected IndexerToken createReindexCompanyIndexerToken(long companyId) {
		return _indexerTokenFactory.createReindexCompanyIndexerToken(
			_modelSearchSettings.getSearchEngineId(), companyId,
			_modelSearchSettings.getClassName(),
			_modelSearchSettings.isCommitImmediately());
	}

	protected IndexerToken createReindexModelIndexerToken(T baseModel) {
		return _indexerTokenFactory.createReindexModelIndexerToken(
			_modelSearchSettings.getSearchEngineId(),
			_modelIndexerWriterContributor.getCompanyId(baseModel),
			baseModel.getModelClassName(), baseModel.getPrimaryKeyObj(),
			_modelSearchSettings.isCommitImmediately());
	}

	protected IndexerToken createUpdatePermissionFieldsIndexerToken(
		T baseModel) {

		return _indexerTokenFactory.createUpdatePermissionFieldsIndexerToken(
			_modelSearchSettings.getSearchEngineId(),
			_modelIndexerWriterContributor.getCompanyId(baseModel),
			baseModel.getModelClassName(), baseModel.getPrimaryKeyObj(),
			_modelSearchSettings.isCommitImmediately());
	}

	private IndexerWriterMode _getIndexerWriterMode(T baseModel) {
		IndexerWriterMode indexerWriterMode =
			_modelIndexerWriterContributor.getIndexerWriterMode(baseModel);

		if (indexerWriterMode != null) {
			return indexerWriterMode;
		}

		if ((baseModel instanceof WorkflowedModel) &&
			(baseModel instanceof TrashedModel)) {

			TrashedModel trashedModel = (TrashedModel)baseModel;
			WorkflowedModel workflowedModel = (WorkflowedModel)baseModel;

			if (!workflowedModel.isApproved() && !trashedModel.isInTrash()) {
				return IndexerWriterMode.SKIP;
			}
		}

		return IndexerWriterMode.UPDATE;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		QueueIndexerWriter.class);

	private final BaseModelRetriever _baseModelRetriever;
	private boolean _enabled;
	private final IndexerDocumentBuilder _indexerDocumentBuilder;
	private final IndexerQueue _indexerQueue;
	private final IndexerTokenFactory _indexerTokenFactory;
	private final ModelIndexerWriterContributor<T>
		_modelIndexerWriterContributor;
	private final ModelSearchSettings _modelSearchSettings;

}