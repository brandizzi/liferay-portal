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

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;
import com.liferay.trash.TrashHelper;
import com.liferay.wiki.engine.WikiEngineRenderer;
import com.liferay.wiki.exception.PageContentException;
import com.liferay.wiki.exception.WikiFormatException;
import com.liferay.wiki.model.WikiPage;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Luan Maoski
 */
@Component(
	immediate = true,
	property = "indexer.class.name=com.liferay.wiki.model.WikiPage",
	service = ModelDocumentContributor.class
)
public class WikiPageModelDocumentContributor
	implements ModelDocumentContributor<WikiPage> {

	@Override
	public void contribute(Document document, WikiPage wikiPage) {
		try {
			String content;

			try {
				content = HtmlUtil.extractText(
					_wikiEngineRenderer.convert(wikiPage, null, null, null));

				document.addText(Field.CONTENT, content);
			}
			catch (PageContentException pce) {
				throw new SystemException(pce);
			}
		}
		catch (WikiFormatException wfe) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to get wiki engine for " + wikiPage.getFormat());
			}
		}

		document.addKeyword(Field.NODE_ID, wikiPage.getNodeId());

		String title = wikiPage.getTitle();

		if (wikiPage.isInTrash()) {
			title = _trashHelper.getOriginalTitle(title);
		}

		document.addText(Field.TITLE, title);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		WikiPageModelDocumentContributor.class);

	@Reference
	private TrashHelper _trashHelper;

	@Reference
	private WikiEngineRenderer _wikiEngineRenderer;

}