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

package com.liferay.wiki.internal.search.spi.model.index.contributor;

import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.batch.BatchIndexingActionable;
import com.liferay.portal.search.batch.DynamicQueryBatchIndexingActionableFactory;
import com.liferay.portal.search.spi.model.index.contributor.ModelIndexerWriterContributor;
import com.liferay.portal.search.spi.model.index.contributor.helper.IndexerWriterMode;
import com.liferay.portal.search.spi.model.index.contributor.helper.ModelIndexerWriterDocumentHelper;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.service.WikiNodeLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luan Maoski
 */
@Component(
	immediate = true,
	property = "indexer.class.name=com.liferay.wiki.model.WikiNode",
	service = ModelIndexerWriterContributor.class
)
public class WikiNodeModelIndexWriterContributor
	implements ModelIndexerWriterContributor<WikiNode> {

	@Override
	public void customize(
		BatchIndexingActionable batchIndexingActionable,
		ModelIndexerWriterDocumentHelper modelIndexerWriterDocumentHelper) {

		batchIndexingActionable.setAddCriteriaMethod(
			dynamicQuery -> {
				Property property = PropertyFactoryUtil.forName("status");

				dynamicQuery.add(
					property.eq(WorkflowConstants.STATUS_IN_TRASH));
			});

		batchIndexingActionable.setPerformActionMethod(
			(WikiNode wikiNode) -> {
				if (!wikiNode.isInTrash()) {
					return;
				}

				Document document =
					modelIndexerWriterDocumentHelper.getDocument(wikiNode);

				batchIndexingActionable.addDocuments(document);
			});
	}

	@Override
	public BatchIndexingActionable getBatchIndexingActionable() {
		return dynamicQueryBatchIndexingActionableFactory.
			getBatchIndexingActionable(
				wikiNodeLocalService.getIndexableActionableDynamicQuery());
	}

	@Override
	public long getCompanyId(WikiNode wikiNode) {
		return wikiNode.getCompanyId();
	}

	@Override
	public IndexerWriterMode getIndexerWriterMode(WikiNode wikiNode) {
		if (wikiNode.isInTrash()) {
			return IndexerWriterMode.UPDATE;
		}

		return IndexerWriterMode.DELETE;
	}

	@Reference
	protected DynamicQueryBatchIndexingActionableFactory
		dynamicQueryBatchIndexingActionableFactory;

	@Reference
	protected WikiNodeLocalService wikiNodeLocalService;

}