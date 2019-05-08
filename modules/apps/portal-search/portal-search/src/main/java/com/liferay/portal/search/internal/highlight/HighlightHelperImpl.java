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

package com.liferay.portal.search.internal.highlight;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.highlight.HighlightHelper;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;

/**
 * @author Vagner B.C
 */
@Component(service = HighlightHelper.class)
public class HighlightHelperImpl implements HighlightHelper {

	public static final List<Locale> nonBoundaryLocales = Arrays.asList(
		LocaleUtil.JAPAN, LocaleUtil.JAPANESE, LocaleUtil.CHINA,
		LocaleUtil.CHINESE, LocaleUtil.SIMPLIFIED_CHINESE,
		LocaleUtil.TRADITIONAL_CHINESE);

	public void addSnippet(
		Document document, Set<String> queryTerms, String snippet,
		String snippetFieldName) {

		if (!snippet.equals(StringPool.BLANK)) {
			Matcher matcher = _pattern.matcher(snippet);

			while (matcher.find()) {
				queryTerms.add(matcher.group(1));
			}

			snippet = StringUtil.replace(
				snippet, HIGHLIGHT_TAG_OPEN, StringPool.BLANK);
			snippet = StringUtil.replace(
				snippet, HIGHLIGHT_TAG_CLOSE, StringPool.BLANK);
		}

		document.addText(
			Field.SNIPPET.concat(
				StringPool.UNDERLINE
			).concat(
				snippetFieldName
			),
			snippet);
	}

	public String highlight(String s, String[] queryTerms) {
		return highlight(s, queryTerms, HIGHLIGHTS[0], HIGHLIGHTS[1], null);
	}

	public String highlight(
		String s, String[] queryTerms, String highlight1, String highlight2,
		Locale locale) {

		if (Validator.isBlank(s) || ArrayUtil.isEmpty(queryTerms)) {
			return s;
		}

		StringBundler sb = new StringBundler(3 * queryTerms.length - 1);

		for (int i = 0; i < queryTerms.length; i++) {
			if (i != 0) {
				sb.append(StringPool.PIPE);
			}

			sb.append(Pattern.quote(queryTerms[i].trim()));

			if ((locale == null) || !nonBoundaryLocales.contains(locale)) {
				sb.append(_REGEXP_WORD_BOUNDARY);
			}
		}

		Pattern pattern = Pattern.compile(
			sb.toString(), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

		return _highlight(s, pattern, highlight1, highlight2);
	}

	private static String _highlight(
		String s, Pattern pattern, String highlight1, String highlight2) {

		Matcher matcher = pattern.matcher(s);

		if (!matcher.find()) {
			return s;
		}

		StringBuffer sb = new StringBuffer();

		do {
			matcher.appendReplacement(
				sb, highlight1 + matcher.group() + highlight2);
		}
		while (matcher.find());

		matcher.appendTail(sb);

		return sb.toString();
	}

	private static final String _REGEXP_WORD_BOUNDARY = "\\b";

	private static final Pattern _pattern = Pattern.compile(
		HIGHLIGHT_TAG_OPEN + "(.*?)" + HIGHLIGHT_TAG_CLOSE);

}