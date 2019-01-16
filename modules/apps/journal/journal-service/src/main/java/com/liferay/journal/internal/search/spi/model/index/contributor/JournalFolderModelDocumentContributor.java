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

package com.liferay.journal.internal.search.spi.model.index.contributor;

import com.liferay.journal.model.JournalFolder;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;
import com.liferay.trash.TrashHelper;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Vagner B.C
 */
@Component(
	immediate = true,
	property = "indexer.class.name=com.liferay.journal.model.JournalFolder",
	service = ModelDocumentContributor.class
)
public class JournalFolderModelDocumentContributor
	implements ModelDocumentContributor<JournalFolder> {

	@Override
	public void contribute(Document document, JournalFolder journalFolder) {
		if (_log.isDebugEnabled()) {
			_log.debug("Indexing journalFolder " + journalFolder);
		}

		document.addText(Field.DESCRIPTION, journalFolder.getDescription());
		document.addKeyword(Field.FOLDER_ID, journalFolder.getParentFolderId());

		String title = journalFolder.getName();

		if (journalFolder.isInTrash()) {
			title = _trashHelper.getOriginalTitle(title);
		}

		document.addText(Field.TITLE, title);

		document.addKeyword(
			Field.TREE_PATH,
			StringUtil.split(journalFolder.getTreePath(), CharPool.SLASH));

		if (_log.isDebugEnabled()) {
			_log.debug("Document " + journalFolder + " indexed successfully");
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JournalFolderModelDocumentContributor.class);

	@Reference
	private TrashHelper _trashHelper;

}