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

package com.liferay.portal.search.ranking.web.internal.index;

import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.search.document.Document;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;

/**
 * @author André de Oliveira
 */
@Component(service = DocumentToRankingTranslator.class)
public class DocumentToRankingTranslatorImpl
	implements DocumentToRankingTranslator {

	@Override
	public Ranking translate(Document document, String uid) {
		Ranking ranking = new Ranking();

		ranking.setAliases(
			ArrayUtil.toStringArray(document.getStrings("aliases")));
		ranking.setHiddenIds(document.getStrings("hidden_documents"));
		ranking.setIndex(document.getString("index"));
		ranking.setPins(_getPins(document));
		ranking.setQueryString(
			document.getString(SearchTuningFields.QUERY_STRING));
		ranking.setStatus(GetterUtil.getInteger(document.getInteger("status")));
		ranking.setUid(uid);

		return ranking;
	}

	private List<Ranking.Pin> _getPins(Document document) {
		List<?> values = document.getValues(SearchTuningFields.PINS);

		if (ListUtil.isEmpty(values)) {
			return Collections.emptyList();
		}

		List<Map<String, String>> maps = (List<Map<String, String>>)values.get(
			0);

		Stream<Map<String, String>> stream = maps.stream();

		Stream<Ranking.Pin> pinStream = stream.map(this::_toPin);

		return pinStream.collect(Collectors.toList());
	}

	private Ranking.Pin _toPin(Map<String, String> map) {
		return new Ranking.Pin(
			GetterUtil.getInteger(map.get("position")), map.get("uid"));
	}

}