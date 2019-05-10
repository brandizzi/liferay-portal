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

package com.liferay.portal.search.ranking.web.internal.portlet.action;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.document.GetDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.GetDocumentResponse;
import com.liferay.portal.search.filter.ComplexQueryPartBuilderFactory;
import com.liferay.portal.search.query.IdsQuery;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.ranking.web.internal.constants.SearchTuningPortletKeys;
import com.liferay.portal.search.ranking.web.internal.index.Ranking;
import com.liferay.portal.search.ranking.web.internal.index.RankingIndexReader;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;

import java.io.IOException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bryan Engler
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + SearchTuningPortletKeys.SEARCH_TUNING,
		"mvc.command.name=/results_ranking/get_results"
	},
	service = MVCResourceCommand.class
)
public class RankingMVCResourceCommand implements MVCResourceCommand {

	@Override
	public boolean serveResource(
		ResourceRequest resourceRequest, ResourceResponse resourceResponse) {

		try {
			String cmd = ParamUtil.getString(resourceRequest, Constants.CMD);

			if (cmd.equals("getVisibleResults")) {
				_writeVisibleDocumentsJSON(resourceRequest, resourceResponse);
			}
			else if (cmd.equals("getHiddenResults")) {
				_writeHiddenDocumentsJSON(resourceRequest, resourceResponse);
			}

			return false;
		}
		catch (RuntimeException re) {
			re.printStackTrace();

			throw re;
		}
	}

	protected void addHiddenIdsQuery(
		Collection<String> ids, SearchRequestBuilder searchRequestBuilder) {

		searchRequestBuilder.addComplexQueryPart(
			complexQueryPartBuilderFactory.builder(
			).query(
				_getIdsQuery(ids)
			).occur(
				"must_not"
			).build());
	}

	protected void addPinnedIdsQuery(
		Collection<String> ids, SearchRequestBuilder searchRequestBuilder) {

		searchRequestBuilder.addComplexQueryPart(
			complexQueryPartBuilderFactory.builder(
			).boost(
				1000F
			).query(
				_getIdsQuery(ids)
			).occur(
				"should"
			).build());
	}

	protected Document getDocument(String index, String id, String type) {
		GetDocumentRequest getDocumentRequest = new GetDocumentRequest(
			index, id);

		getDocumentRequest.setType(type);
		getDocumentRequest.setFetchSourceInclude("*");

		GetDocumentResponse getDocumentResponse = searchEngineAdapter.execute(
			getDocumentRequest);

		if (!getDocumentResponse.isExists()) {
			return null;
		}

		return getDocumentResponse.getDocument();
	}

	protected SearchRequestBuilder getSearchRequestBuilder(
		ResourceRequest resourceRequest) {

		return searchRequestBuilderFactory.builder(
		).from(
			ParamUtil.getInteger(resourceRequest, "from")
		).queryString(
			ParamUtil.getString(resourceRequest, "keywords")
		).size(
			ParamUtil.getInteger(resourceRequest, "size", 10)
		).withSearchContext(
			searchContext -> searchContext.setCompanyId(
				ParamUtil.getLong(resourceRequest, "companyId"))
		);
	}

	protected String getTitle(Document document) {
		String title = document.getString(Field.TITLE + "_en_US");

		if (!Validator.isBlank(title)) {
			return title;
		}

		return document.getString(Field.TITLE);
	}

	protected void populateHiddenDocuments(
		JSONArray jsonArray, Ranking ranking) {

		List<String> ids = ranking.getHiddenIds();

		ids.stream(
		).map(
			id -> getDocument(
				ranking.getIndex(), id,
				LiferayTypeMappingsConstants.LIFERAY_DOCUMENT_TYPE)
		).filter(
			document -> document != null
		).map(
			document -> _withHiddenIcon(_translate(document))
		).forEach(
			jsonArray::put
		);
	}

	protected void populateVisibleDocuments(
		ResourceRequest resourceRequest, JSONArray jsonArray, Ranking ranking) {

		List<String> hiddenIds = ranking.getHiddenIds();
		Set<String> pinnedIds = _getPinnedIds(ranking);

		SearchRequestBuilder searchRequestBuilder = getSearchRequestBuilder(
			resourceRequest);

		addHiddenIdsQuery(hiddenIds, searchRequestBuilder);
		addPinnedIdsQuery(pinnedIds, searchRequestBuilder);

		SearchResponse searchResponse = searcher.search(
			searchRequestBuilder.build());

		Stream<Document> stream = searchResponse.getDocumentsStream();

		stream.map(
			document -> _withPinnedIcon(
				pinnedIds.contains(document.getString(Field.UID)),
				_translate(document))
		).forEach(
			jsonArray::put
		);
	}

	@Reference
	protected ComplexQueryPartBuilderFactory complexQueryPartBuilderFactory;

	@Reference
	protected Queries queries;

	@Reference
	protected RankingIndexReader rankingIndexReader;

	@Reference
	protected SearchEngineAdapter searchEngineAdapter;

	@Reference
	protected Searcher searcher;

	@Reference
	protected SearchRequestBuilderFactory searchRequestBuilderFactory;

	private IdsQuery _getIdsQuery(Collection<String> ids) {
		IdsQuery idsQuery = queries.ids();

		idsQuery.addIds(ArrayUtil.toStringArray(ids));

		return idsQuery;
	}

	private Set<String> _getPinnedIds(Ranking ranking) {
		List<Ranking.Pin> pins = ranking.getPins();

		return pins.stream(
		).map(
			Ranking.Pin::getId
		).collect(
			Collectors.toSet()
		);
	}

	private JSONObject _translate(Document document) {
		return JSONUtil.put(
			"author", document.getString(Field.USER_NAME)
		).put(
			"clicks", document.getString("clicks")
		).put(
			"description", document.getString(Field.DESCRIPTION)
		).put(
			"id", document.getString(Field.UID)
		).put(
			"title", getTitle(document)
		).put(
			"type", document.getString(Field.ENTRY_CLASS_NAME)
		);
	}

	private JSONObject _withHiddenIcon(JSONObject jsonObject) {
		return jsonObject.put("hidden", true);
	}

	private JSONObject _withPinnedIcon(boolean pinned, JSONObject jsonObject) {
		if (pinned) {
			jsonObject.put("pinned", true);
		}

		return jsonObject;
	}

	private void _writeHiddenDocumentsJSON(
		ResourceRequest resourceRequest, ResourceResponse resourceResponse) {

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		String uid = ParamUtil.getString(resourceRequest, "resultsRankingUid");

		Optional<Ranking> optional = rankingIndexReader.fetch(uid);

		optional.ifPresent(
			ranking -> populateHiddenDocuments(jsonArray, optional.get()));

		JSONObject jsonObject = JSONUtil.put(
			"documents", jsonArray
		).put(
			"total", jsonArray.length()
		);

		_writeJSON(resourceRequest, resourceResponse, jsonObject);
	}

	private void _writeJSON(
		ResourceRequest resourceRequest, ResourceResponse resourceResponse,
		JSONObject jsonObject) {

		try {
			JSONPortletResponseUtil.writeJSON(
				resourceRequest, resourceResponse, jsonObject);
		}
		catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}

	private void _writeVisibleDocumentsJSON(
		ResourceRequest resourceRequest, ResourceResponse resourceResponse) {

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		Optional<Ranking> optional = rankingIndexReader.fetch(
			ParamUtil.getString(resourceRequest, "resultsRankingUid"));

		if (optional.isPresent()) {
			populateVisibleDocuments(
				resourceRequest, jsonArray, optional.get());
		}

		JSONObject jsonObject = JSONUtil.put(
			"documents", jsonArray
		).put(
			"total", jsonArray.length()
		);

		_writeJSON(resourceRequest, resourceResponse, jsonObject);
	}

}