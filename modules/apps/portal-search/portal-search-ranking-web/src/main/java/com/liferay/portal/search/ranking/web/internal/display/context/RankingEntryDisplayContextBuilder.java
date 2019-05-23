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

package com.liferay.portal.search.ranking.web.internal.display.context;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.ranking.web.internal.index.Ranking;

import java.text.DateFormat;

import java.util.Date;
import java.util.List;

/**
 * @author Bryan Engler
 */
public class RankingEntryDisplayContextBuilder {

	public RankingEntryDisplayContextBuilder(
		String id, Document document, Ranking ranking) {

		_id = id;
		_document = document;
		_ranking = ranking;
	}

	public RankingEntryDisplayContext build() {
		RankingEntryDisplayContext rankingEntryDisplayContext =
			new RankingEntryDisplayContext();

		_setAliases(rankingEntryDisplayContext);
		_setDisplayDate(rankingEntryDisplayContext);
		_setHiddenResultsCount(rankingEntryDisplayContext);
		_setIndex(rankingEntryDisplayContext);
		_setKeywords(rankingEntryDisplayContext);
		_setModifiedDate(rankingEntryDisplayContext);
		_setPinnedResultsCount(rankingEntryDisplayContext);
		_setStatus(rankingEntryDisplayContext);
		_setUid(rankingEntryDisplayContext);

		return rankingEntryDisplayContext;
	}

	protected static String getSizeString(List<?> list) {
		return String.valueOf(list.size());
	}

	private Date _getDate(String name) {
		try {
			DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
				_INDEX_DATE_FORMAT_PATTERN);

			return dateFormat.parse(_document.getDate(name));
		}
		catch (Exception e) {
			return null;
		}
	}

	private void _setAliases(
		RankingEntryDisplayContext rankingEntryDisplayContext) {

		List<String> aliases = _ranking.getAliases();

		if (!aliases.isEmpty()) {
			rankingEntryDisplayContext.setAliases(
				StringUtil.merge(aliases, StringPool.COMMA_AND_SPACE));
		}
		else {
			rankingEntryDisplayContext.setAliases(StringPool.BLANK);
		}
	}

	private void _setDisplayDate(
		RankingEntryDisplayContext rankingEntryDisplayContext) {

		rankingEntryDisplayContext.setDisplayDate(_getDate(Field.DISPLAY_DATE));
	}

	private void _setHiddenResultsCount(
		RankingEntryDisplayContext rankingEntryDisplayContext) {

		rankingEntryDisplayContext.setHiddenResultsCount(
			getSizeString(_ranking.getHiddenIds()));
	}

	private void _setIndex(
		RankingEntryDisplayContext rankingEntryDisplayContext) {

		rankingEntryDisplayContext.setIndex(_ranking.getIndex());
	}

	private void _setKeywords(
		RankingEntryDisplayContext rankingEntryDisplayContext) {

		rankingEntryDisplayContext.setKeywords(_ranking.getQueryString());
	}

	private void _setModifiedDate(
		RankingEntryDisplayContext rankingEntryDisplayContext) {

		rankingEntryDisplayContext.setModifiedDate(
			_getDate(Field.MODIFIED_DATE));
	}

	private void _setPinnedResultsCount(
		RankingEntryDisplayContext rankingEntryDisplayContext) {

		rankingEntryDisplayContext.setPinnedResultsCount(
			getSizeString(_ranking.getPins()));
	}

	private void _setStatus(
		RankingEntryDisplayContext rankingEntryDisplayContext) {

		rankingEntryDisplayContext.setStatus(
			String.valueOf(_ranking.getStatus()));
	}

	private void _setUid(
		RankingEntryDisplayContext rankingEntryDisplayContext) {

		rankingEntryDisplayContext.setUid(_id);
	}

	private static final String _INDEX_DATE_FORMAT_PATTERN = PropsUtil.get(
		PropsKeys.INDEX_DATE_FORMAT_PATTERN);

	private final Document _document;
	private final String _id;
	private final Ranking _ranking;

}