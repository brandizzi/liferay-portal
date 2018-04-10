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
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.search.index.UpdateDocumentIndexWriter;
import com.liferay.portal.search.indexer.IndexerDocumentBuilder;
import com.liferay.portal.search.permission.SearchPermissionIndexWriter;
import com.liferay.portal.search.spi.model.index.dispatcher.IndexerDispatcher;
import com.liferay.portal.search.spi.model.registrar.ModelSearchSettings;

/**
 * @author Adam Brandizzi
 */
public class ImmediateIndexerDispatcher implements IndexerDispatcher {

	public ImmediateIndexerDispatcher(
		IndexerDocumentBuilder indexerDocumentBuilder,
		IndexWriterHelper indexWriterHelper,
		ModelSearchSettings modelSearchSettings,
		SearchPermissionIndexWriter searchPermissionIndexWriter,
		UpdateDocumentIndexWriter updateDocumentIndexWriter) {

		_indexerDocumentBuilder = indexerDocumentBuilder;
		_indexWriterHelper = indexWriterHelper;
		_modelSearchSettings = modelSearchSettings;
		_searchPermissionIndexWriter = searchPermissionIndexWriter;
		_updateDocumentIndexWriter = updateDocumentIndexWriter;
	}

	@Override
	public void deleteDocument(long companyId, BaseModel<?> baseModel)
		throws SearchException {

		deleteDocument(
			companyId, _indexerDocumentBuilder.getDocumentUID(baseModel));
	}

	@Override
	public void deleteDocument(long companyId, String uid)
		throws SearchException {

		_indexWriterHelper.deleteDocument(
			_modelSearchSettings.getSearchEngineId(), companyId, uid,
			_modelSearchSettings.isCommitImmediately());
	}

	@Override
	public void updateDocument(long companyId, BaseModel<?> baseModel) {
		Document document = _indexerDocumentBuilder.getDocument(baseModel);

		_updateDocumentIndexWriter.updateDocument(
			_modelSearchSettings.getSearchEngineId(), companyId, document,
			_modelSearchSettings.isCommitImmediately());
	}

	@Override
	public void updatePermissionFields(long companyId, BaseModel<?> baseModel) {
		_searchPermissionIndexWriter.updatePermissionFields(
			baseModel, companyId, _modelSearchSettings.getSearchEngineId(),
			_modelSearchSettings.isCommitImmediately());
	}

	private final IndexerDocumentBuilder _indexerDocumentBuilder;
	private final IndexWriterHelper _indexWriterHelper;
	private final ModelSearchSettings _modelSearchSettings;
	private final SearchPermissionIndexWriter _searchPermissionIndexWriter;
	private final UpdateDocumentIndexWriter _updateDocumentIndexWriter;

}