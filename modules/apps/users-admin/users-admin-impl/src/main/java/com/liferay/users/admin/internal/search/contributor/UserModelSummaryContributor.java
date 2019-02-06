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

package com.liferay.users.admin.internal.search.contributor;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.search.spi.model.result.contributor.ModelSummaryContributor;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;

/**
 * @author Luan Maoski
 */
@Component(
	immediate = true,
	property = "indexer.class.name=com.liferay.portal.kernel.model.User",
	service = ModelSummaryContributor.class
)
public class UserModelSummaryContributor implements ModelSummaryContributor {

	@Override
	public Summary getSummary(
		Document document, Locale locale, String snippet) {

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Getting summary for user ", document.get(Field.USER_NAME),
					" (", document.get(Field.CLASS_PK), ")"));
		}

		String prefix = Field.SNIPPET + StringPool.UNDERLINE;

		String fullName = "fullName";

		String title = document.get(prefix + fullName, fullName);

		String content = document.get(prefix);

		return new Summary(title, content);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UserModelSummaryContributor.class);

}