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

package com.liferay.asset.internal.search.spi.model.index.contributor;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.portal.search.batch.BatchIndexingActionable;
import com.liferay.portal.search.spi.model.index.contributor.ModelIndexerWriterContributor;
import com.liferay.portal.search.spi.model.index.contributor.helper.ModelIndexerWriterDocumentHelper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Brian Wing Shun Chan
 * @author Julio Camarero
 * @author Vagner B.C
 * @author Leonardo Miyagi
 */
@Component(
	immediate = true,
	property = "indexer.class.name=com.liferay.asset.kernel.model.AssetEntry",
	service = ModelIndexerWriterContributor.class
)
public class AssetEntryModelIndexerWriterContributor
	implements ModelIndexerWriterContributor<AssetEntry> {

	@Override
	public void customize(
		BatchIndexingActionable batchIndexingActionable,
		ModelIndexerWriterDocumentHelper modelIndexerWriterDocumentHelper) {
	}

	@Override
	public BatchIndexingActionable getBatchIndexingActionable() {
		return null;
	}

	@Override
	public long getCompanyId(AssetEntry assetEntry) {
		return assetEntry.getCompanyId();
	}

}