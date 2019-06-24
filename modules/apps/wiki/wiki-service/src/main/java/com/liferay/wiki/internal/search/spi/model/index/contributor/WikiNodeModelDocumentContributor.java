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

import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;
import com.liferay.trash.TrashHelper;
import com.liferay.wiki.model.WikiNode;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luan Maoski
 */
@Component(
	immediate = true,
	property = "indexer.class.name=com.liferay.wiki.model.WikiNode",
	service = ModelDocumentContributor.class
)
public class WikiNodeModelDocumentContributor
	implements ModelDocumentContributor<WikiNode> {

	@Override
	public void contribute(Document document, WikiNode wikiNode) {
		document.addText(Field.DESCRIPTION, wikiNode.getDescription());

		String title = wikiNode.getName();

		if (wikiNode.isInTrash()) {
			title = _trashHelper.getOriginalTitle(title);
		}

		document.addText(Field.TITLE, title);
	}

	@Reference
	private TrashHelper _trashHelper;

}