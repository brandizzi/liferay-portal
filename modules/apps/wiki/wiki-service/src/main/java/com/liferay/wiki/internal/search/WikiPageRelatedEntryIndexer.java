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

package com.liferay.wiki.internal.search;

import com.liferay.portal.kernel.comment.Comment;
import com.liferay.portal.kernel.repository.capabilities.RelatedModelCapability;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.BaseRelatedEntryIndexer;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.RelatedEntryIndexer;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiPageLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luan Maoski
 */
@Component(
	immediate = true,
	property = "related.entry.indexer.class.name=com.liferay.wiki.model.WikiPage",
	service = RelatedEntryIndexer.class
)
public class WikiPageRelatedEntryIndexer extends BaseRelatedEntryIndexer {

	public void addRelatedEntryFields(Document document, Object obj)
		throws Exception {

		long classPK = 0;

		if (obj instanceof Comment) {
			Comment comment = (Comment)obj;

			classPK = comment.getClassPK();
		}
		else if (obj instanceof FileEntry) {
			FileEntry fileEntry = (FileEntry)obj;

			RelatedModelCapability relatedModelCapability =
				fileEntry.getRepositoryCapability(RelatedModelCapability.class);

			classPK = relatedModelCapability.getClassPK(fileEntry);
		}

		WikiPage page = null;

		try {
			page = wikiPageLocalService.getPage(classPK);
		}
		catch (Exception e) {
			return;
		}

		document.addKeyword(Field.NODE_ID, page.getNodeId());
	}

	public boolean isVisible(long classPK, int status) throws Exception {
		WikiPage page = wikiPageLocalService.getPage(classPK);

		if (((status != WorkflowConstants.STATUS_ANY) &&
			 (page.getStatus() == status)) ||
			(page.getStatus() != WorkflowConstants.STATUS_IN_TRASH)) {

			return true;
		}

		return false;
	}

	@Reference
	protected WikiPageLocalService wikiPageLocalService;

}