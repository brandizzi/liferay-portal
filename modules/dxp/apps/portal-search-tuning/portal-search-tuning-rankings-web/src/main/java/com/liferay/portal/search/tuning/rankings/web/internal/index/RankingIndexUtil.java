/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.portal.search.tuning.rankings.web.internal.index;

import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.document.BulkDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.BulkDocumentResponse;
import com.liferay.portal.search.engine.adapter.document.IndexDocumentRequest;
import com.liferay.portal.search.engine.adapter.index.DeleteIndexRequest;
import com.liferay.portal.search.engine.adapter.index.DeleteIndexResponse;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.query.Queries;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Wade Cao
 */
@Component(immediate = true, service = RankingIndexUtil.class)
public class RankingIndexUtil {

	public static void createRankingIndex(String rankingIndexName) {
		_rankingIndexUtil.createRankingIndex1(rankingIndexName);
	}

	public static String getRankingIndexName(String companyIndexName) {
		return _rankingIndexUtil.getRankingIndexName1(companyIndexName);
	}

	public static void renameRankingIndexName() {
		if (_rankingIndexUtil.isIndicesExists(
				RankingIndexDefinition.INDEX_NAME)) {

			List<Document> documents = _rankingIndexUtil.getDocuments(
				RankingIndexDefinition.INDEX_NAME);

			if (documents.isEmpty()) {
				_rankingIndexUtil.deleteIndex(
					RankingIndexDefinition.INDEX_NAME);

				return;
			}

			Boolean succeeded = groupDocumentByIndex(
				documents
			).entrySet(
			).stream(
			).map(
				entry -> _rankingIndexUtil.addDocuments(
					entry.getKey(), entry.getValue())
			).reduce(
				true, Boolean::logicalAnd
			);

			if (succeeded) {
				_rankingIndexUtil.deleteIndex(
					RankingIndexDefinition.INDEX_NAME);
			}
		}
	}

	protected static Map<String, List<Document>> groupDocumentByIndex(
		List<Document> documents) {

		return documents.stream(
		).collect(
			Collectors.groupingBy(document -> document.getString("index"))
		);
	}

	@Activate
	protected void activate() {
		_rankingIndexUtil = this;
	}

	protected boolean addDocuments(String indexName, List<Document> documents) {
		boolean successed = true;

		String rankingIndexName = _rankingIndexUtil.getRankingIndexName1(
			indexName);

		createRankingIndex(rankingIndexName);

		BulkDocumentRequest bulkDocumentRequest = new BulkDocumentRequest();

		documents.forEach(
			document -> {
				IndexDocumentRequest indexDocumentRequest =
					new IndexDocumentRequest(rankingIndexName, document);

				bulkDocumentRequest.addBulkableDocumentRequest(
					indexDocumentRequest);
			});

		BulkDocumentResponse bulkDocumentResponse =
			_searchEngineAdapter.execute(bulkDocumentRequest);

		if (bulkDocumentResponse.hasErrors()) {
			successed = false;
		}

		return successed;
	}

	protected boolean createIndex(String indexName) {
		_rankingIndexCreator.create(indexName);

		return true;
	}

	protected void createRankingIndex1(String rankingIndexName) {
		if (isIndicesExists(rankingIndexName)) {
			return;
		}

		createIndex(rankingIndexName);
	}

	protected boolean deleteIndex(String... indexNames) {
		DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(
			indexNames);

		DeleteIndexResponse deleteIndexResponse = _searchEngineAdapter.execute(
			deleteIndexRequest);

		return deleteIndexResponse.isAcknowledged();
	}

	protected List<Document> getDocuments(String indexName) {
		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		searchSearchRequest.setIndexNames(indexName);
		searchSearchRequest.setQuery(_queries.matchAll());
		searchSearchRequest.setFetchSource(true);

		SearchSearchResponse searchSearchResponse =
			_searchEngineAdapter.execute(searchSearchRequest);

		SearchHits searchHits = searchSearchResponse.getSearchHits();

		List<SearchHit> searchHitsList = searchHits.getSearchHits();

		Stream<SearchHit> documentStream = searchHitsList.stream();

		return documentStream.map(
			SearchHit::getDocument
		).collect(
			Collectors.toList()
		);
	}

	protected String getRankingIndexName1(String companyIndexName) {
		return companyIndexName + "-" + RankingIndexDefinition.INDEX_NAME;
	}

	protected boolean isIndicesExists(String... indexNames) {
		return Stream.of(
			indexNames
		).map(
			_rankingIndexReader::isExists
		).reduce(
			true, Boolean::logicalAnd
		);
	}

	@Reference(unbind = "-")
	protected void setQueries(Queries queries) {
		_queries = queries;
	}

	@Reference(unbind = "-")
	protected void setSearchEngineAdapter(
		SearchEngineAdapter searchEngineAdapter) {

		_searchEngineAdapter = searchEngineAdapter;
	}

	private static RankingIndexUtil _rankingIndexUtil;

	private Queries _queries;

	@Reference
	private RankingIndexCreator _rankingIndexCreator;

	@Reference
	private RankingIndexReader _rankingIndexReader;

	private SearchEngineAdapter _searchEngineAdapter;

}