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

package com.liferay.document.library.internal.search;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;
import com.liferay.portal.util.PrefsPropsUtil;

import java.io.IOException;
import java.io.InputStream;

import java.util.Optional;
import java.util.Scanner;

import org.osgi.service.component.annotations.Component;

/**
 * @author Michael C. Han
 */
@Component(
	immediate = true,
	property = "indexer.class.name=com.liferay.document.library.kernel.model.DLFileEntry",
	service = ModelDocumentContributor.class
)
public class DLFileEntryWorldCountModelDocumentContributor
	implements ModelDocumentContributor<DLFileEntry> {

	@Override
	public void contribute(Document document, DLFileEntry dlFileEntry) {
		if (_log.isDebugEnabled()) {
			_log.debug("Indexing document " + dlFileEntry);
		}

		Optional<InputStream> inputStreamOptional = getContentStreamOptional(
			dlFileEntry, isIndexContent(dlFileEntry));

		try {
			inputStreamOptional.ifPresent(
				is -> {
					try {
						document.addKeyword("wordCount", countWords(is));
					}
					catch (IOException ioe) {
						if (_log.isWarnEnabled()) {
							_log.warn("Unable to index content", ioe);
						}
					}
				});

				_log.debug(
					"Document " + dlFileEntry + " word count indexed" +
						" successfully");
		}
		catch (Exception e) {
			throw new SystemException(e);
		}
	}

	protected int countWords(InputStream is) throws IOException {
		int wordCount = 0;

		try(Scanner scanner = new Scanner(is)) {
			while (scanner.hasNext()) {
				wordCount++;
				System.err.println(scanner.next());
			}
		}

		return wordCount;
	}

	protected Optional<InputStream> getContentStreamOptional(
		DLFileEntry dlFileEntry, boolean indexContent) {

		InputStream is = null;

		if (indexContent) {
			try {
				DLFileVersion dlFileVersion = dlFileEntry.getFileVersion();

				is = dlFileVersion.getContentStream(false);
			}
			catch (Exception e) {
				if (_log.isDebugEnabled()) {
					_log.debug("Unable to retrieve document stream", e);
				}
			}
		}

		return Optional.ofNullable(is);
	}

	protected boolean isIndexContent(DLFileEntry dlFileEntry) {
		boolean indexContent = true;

		String[] ignoreExtensions = PrefsPropsUtil.getStringArray(
			PropsKeys.DL_FILE_INDEXING_IGNORE_EXTENSIONS, StringPool.COMMA);

		if (ArrayUtil.contains(
				ignoreExtensions,
				StringPool.PERIOD + dlFileEntry.getExtension())) {

			indexContent = false;
		}

		return indexContent;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DLFileEntryWorldCountModelDocumentContributor.class);

}