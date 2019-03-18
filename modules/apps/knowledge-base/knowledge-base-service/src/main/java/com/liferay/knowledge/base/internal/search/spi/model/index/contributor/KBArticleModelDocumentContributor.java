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

package com.liferay.knowledge.base.internal.search.spi.model.index.contributor;

import com.liferay.knowledge.base.constants.KBFolderConstants;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.model.KBFolder;
import com.liferay.knowledge.base.service.KBFolderLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;

import java.util.ArrayList;
import java.util.Collection;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luan Maoski
 */
@Component(
	immediate = true,
	property = "indexer.class.name=com.liferay.knowledge.base.model.KBArticle",
	service = ModelDocumentContributor.class
)
public class KBArticleModelDocumentContributor
	implements ModelDocumentContributor<KBArticle> {

	@Override
	public void contribute(Document document, KBArticle kbArticle) {
		document.addText(
			Field.CONTENT, HtmlUtil.extractText(kbArticle.getContent()));
		document.addText(Field.DESCRIPTION, kbArticle.getDescription());
		document.addText(Field.TITLE, kbArticle.getTitle());

		try {
			document.addKeyword("folderNames", getKBFolderNames(kbArticle));
		}
		catch (PortalException pe) {
			throw new SystemException(pe);
		}

		document.addKeyword("titleKeyword", kbArticle.getTitle(), true);
	}

	protected String[] getKBFolderNames(KBArticle kbArticle)
		throws PortalException {

		long kbFolderId = kbArticle.getKbFolderId();

		Collection<String> kbFolderNames = new ArrayList<>();

		while (kbFolderId != KBFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			KBFolder kbFolder = kbFolderLocalService.getKBFolder(kbFolderId);

			kbFolderNames.add(kbFolder.getName());

			kbFolderId = kbFolder.getParentKBFolderId();
		}

		return kbFolderNames.toArray(new String[kbFolderNames.size()]);
	}

	@Reference
	protected KBFolderLocalService kbFolderLocalService;

}