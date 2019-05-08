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

package com.liferay.portal.search.highlight;

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.kernel.search.Document;

import java.util.Locale;
import java.util.Set;

/**
 * @author Vagner B.C
 */
@ProviderType
public interface HighlightHelper {

	public static final String HIGHLIGHT_TAG_CLOSE = "</liferay-hl>";

	public static final String HIGHLIGHT_TAG_OPEN = "<liferay-hl>";

	public static final String[] HIGHLIGHTS = {
		"<span class=\"highlight\">", "</span>"
	};

	public void addSnippet(
		Document document, Set<String> queryTerms, String snippet,
		String snippetFieldName);

	public String highlight(String s, String[] queryTerms);

	public String highlight(
		String s, String[] queryTerms, String highlight1, String highlight2,
		Locale locale);

}