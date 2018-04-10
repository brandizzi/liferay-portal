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

import com.liferay.portal.kernel.configuration.Filter;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.model.WorkflowedModel;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.search.batch.BatchIndexingActionable;
import com.liferay.portal.search.index.IndexStatusManager;
import com.liferay.portal.search.indexer.BaseModelRetriever;
import com.liferay.portal.search.indexer.IndexerDocumentBuilder;
import com.liferay.portal.search.indexer.IndexerWriter;
import com.liferay.portal.search.spi.model.index.contributor.ModelIndexerWriterContributor;
import com.liferay.portal.search.spi.model.index.contributor.helper.IndexerWriterMode;
import com.liferay.portal.search.spi.model.index.contributor.helper.ModelIndexerWriterDocumentHelper;
import com.liferay.portal.search.spi.model.index.dispatcher.IndexerDispatcher;
import com.liferay.portal.search.spi.model.registrar.ModelSearchSettings;

import java.util.Collection;
import java.util.Optional;

/**
 * @author Michael C. Han
 */
public class IndexerWriterImpl<T extends BaseModel<?>>
	implements IndexerWriter<T> {

	public IndexerWriterImpl(
		IndexerDispatcher indexerDispatcher,
		ModelSearchSettings modelSearchSettings,
		BaseModelRetriever baseModelRetriever,
		ModelIndexerWriterContributor<T> modelIndexerWriterContributor,
		IndexerDocumentBuilder indexerDocumentBuilder,
		IndexStatusManager indexStatusManager, Props props) {

		_indexerDispatcher = indexerDispatcher;
		_modelSearchSettings = modelSearchSettings;
		_baseModelRetriever = baseModelRetriever;
		_modelIndexerWriterContributor = modelIndexerWriterContributor;
		_indexerDocumentBuilder = indexerDocumentBuilder;
		_indexStatusManager = indexStatusManager;
		_props = props;
	}

	@Override
	public void delete(long companyId, String uid) {
		if (!isEnabled()) {
			return;
		}

		try {
			_indexerDispatcher.deleteDocument(companyId, uid);
		}
		catch (SearchException se) {
			throw new RuntimeException(se);
		}
	}

	@Override
	public void delete(T baseModel) {
		if (baseModel == null) {
			return;
		}

		long companyId = _modelIndexerWriterContributor.getCompanyId(baseModel);

		if (!isEnabled()) {
			return;
		}

		try {
			_indexerDispatcher.deleteDocument(companyId, baseModel);
		}
		catch (SearchException se) {
			throw new RuntimeException(se);
		}
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
		if (_indexerEnabled == null) {
			String indexerEnabled = _props.get(
				PropsKeys.INDEXER_ENABLED,
				new Filter(_modelSearchSettings.getClassName()));

			_indexerEnabled = GetterUtil.getBoolean(indexerEnabled, true);

			return _indexerEnabled;
		}

		if (_indexStatusManager.isIndexReadOnly() ||
			_indexStatusManager.isIndexReadOnly(
				_modelSearchSettings.getClassName()) ||
			!_indexerEnabled) {

			return false;
		}

		return true;
	}

	@Override
	public void reindex(Collection<T> baseModels) {
		if (!isEnabled()) {
			return;
		}

		if ((baseModels == null) || baseModels.isEmpty()) {
			return;
		}

		for (T baseModel : baseModels) {
			reindex(baseModel);
		}
	}

	@Override
	public void reindex(long classPK) {
		if (!isEnabled()) {
			return;
		}

		if (classPK <= 0) {
			return;
		}

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

		if (ArrayUtil.isEmpty(ids)) {
			return;
		}

		long companyThreadLocalCompanyId = CompanyThreadLocal.getCompanyId();

		try {
			for (String id : ids) {
				long companyId = GetterUtil.getLong(id);

				CompanyThreadLocal.setCompanyId(companyId);

				BatchIndexingActionable batchIndexingActionable =
					getBatchIndexingActionable();

				batchIndexingActionable.setCompanyId(companyId);

				_modelIndexerWriterContributor.customize(
					batchIndexingActionable,
					new ModelIndexerWriterDocumentHelper() {

						@Override
						public Document getDocument(BaseModel baseModel) {
							return _indexerDocumentBuilder.getDocument(
								baseModel);
						}

					});

				try {
					batchIndexingActionable.performActions();
				}
				catch (Exception pe) {
					if (_log.isWarnEnabled()) {
						StringBundler sb = new StringBundler(4);

						sb.append("Error reindexing all ");
						sb.append(_modelSearchSettings.getClassName());
						sb.append(" for company: ");
						sb.append(companyId);

						_log.warn(sb.toString(), pe);
					}
				}
			}
		}
		finally {
			CompanyThreadLocal.setCompanyId(companyThreadLocalCompanyId);
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

			_indexerDispatcher.updateDocument(
				_modelIndexerWriterContributor.getCompanyId(baseModel),
				baseModel);
		}
		else if (indexerWriterMode == IndexerWriterMode.DELETE) {
			delete(baseModel);
		}
		else if (indexerWriterMode == IndexerWriterMode.SKIP) {
			if (_log.isDebugEnabled()) {
				_log.debug("Skipping model " + baseModel);
			}
		}

		_modelIndexerWriterContributor.modelIndexed(baseModel);
	}

	@Override
	public void setEnabled(boolean enabled) {
		_indexerEnabled = enabled;
	}

	@Override
	public void updatePermissionFields(T baseModel) {
		_indexerDispatcher.updatePermissionFields(
			_modelIndexerWriterContributor.getCompanyId(baseModel), baseModel);
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
		IndexerWriterImpl.class);

	private final BaseModelRetriever _baseModelRetriever;
	private final IndexerDispatcher _indexerDispatcher;
	private final IndexerDocumentBuilder _indexerDocumentBuilder;
	private Boolean _indexerEnabled;
	private final IndexStatusManager _indexStatusManager;
	private final ModelIndexerWriterContributor<T>
		_modelIndexerWriterContributor;
	private final ModelSearchSettings _modelSearchSettings;
	private final Props _props;

}