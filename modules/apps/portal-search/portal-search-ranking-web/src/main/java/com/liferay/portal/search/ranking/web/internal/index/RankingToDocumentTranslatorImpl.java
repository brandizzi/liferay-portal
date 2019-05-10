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
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.document.DocumentBuilderFactory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author André de Oliveira
 */
@Component(service = RankingToDocumentTranslator.class)
public class RankingToDocumentTranslatorImpl
	implements RankingToDocumentTranslator {

	@Override
	public Document translate(Ranking ranking) {
		return _documentBuilderFactory.builder(
		).setStrings(
			"aliases", ArrayUtil.toStringArray(ranking.getAliases())
		).setStrings(
			"hidden_documents", ArrayUtil.toStringArray(ranking.getHiddenIds())
		).setString(
			"index", ranking.getIndex()
		).setValue(
			SearchTuningFields.PINS, _toMaps(ranking.getPins())
		).setString(
			SearchTuningFields.QUERY_STRING, ranking.getQueryString()
		).setString(
			"uid", ranking.getUid()
		).build();
	}

	protected String[] getAliases(Ranking ranking) {
		List<String> aliases = ranking.getAliases();

		if (ListUtil.isNotEmpty(aliases)) {
			return ArrayUtil.toStringArray(aliases);
		}

		return null;
	}

	protected List<String> getHiddenDocuments(Ranking ranking) {
		if (ListUtil.isNotEmpty(ranking.getHiddenIds())) {
			return ranking.getHiddenIds();
		}

		return null;
	}

	@Reference(unbind = "-")
	protected void setDocumentBuilderFactory(
		DocumentBuilderFactory documentBuilderFactory) {

		_documentBuilderFactory = documentBuilderFactory;
	}

	private List<Map<String, String>> _toMaps(List<Ranking.Pin> pins) {
		return pins.stream(
		).map(
			pin -> new LinkedHashMap<String, String>() {
				{
					put("position", String.valueOf(pin.getPosition()));
					put("uid", pin.getId());
				}
			}
		).collect(
			Collectors.toList()
		);
	}

	private DocumentBuilderFactory _documentBuilderFactory;

}