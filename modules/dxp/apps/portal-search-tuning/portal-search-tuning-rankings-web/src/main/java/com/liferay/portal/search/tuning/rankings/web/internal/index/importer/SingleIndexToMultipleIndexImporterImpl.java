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

package com.liferay.portal.search.tuning.rankings.web.internal.index.importer;

import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.document.BulkDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.BulkDocumentResponse;
import com.liferay.portal.search.engine.adapter.document.IndexDocumentRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.tuning.rankings.web.internal.index.RankingIndexCreator;
import com.liferay.portal.search.tuning.rankings.web.internal.index.RankingIndexDefinition;
import com.liferay.portal.search.tuning.rankings.web.internal.index.RankingIndexReader;
import com.liferay.portal.search.tuning.rankings.web.internal.index.RankingIndexUtil;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adam Brandizzi
 */
@Component(service = SingleIndexToMultipleIndexImporter.class)
public class SingleIndexToMultipleIndexImporterImpl
	implements SingleIndexToMultipleIndexImporter {

	@Override
	public void importRankings() {
		if (_rankingIndexReader.isExists(RankingIndexDefinition.INDEX_NAME)) {
			List<Document> documents = getDocuments(
				RankingIndexDefinition.INDEX_NAME);

			if (documents.isEmpty()) {
				_rankingIndexCreator.delete(RankingIndexDefinition.INDEX_NAME);

				return;
			}

			Boolean succeeded = groupDocumentByIndex(
				documents
			).entrySet(
			).stream(
			).map(
				entry -> addDocuments(entry.getKey(), entry.getValue())
			).reduce(
				true, Boolean::logicalAnd
			);

			if (succeeded) {
				_rankingIndexCreator.delete(RankingIndexDefinition.INDEX_NAME);
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

	protected boolean addDocuments(String indexName, List<Document> documents) {
		boolean successed = true;

		String rankingIndexName = RankingIndexUtil.getRankingIndexName(
			indexName);

		if (!_rankingIndexReader.isExists(rankingIndexName)) {
			_rankingIndexCreator.create(rankingIndexName);
		}

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

	@Reference
	private Queries _queries;

	@Reference
	private RankingIndexCreator _rankingIndexCreator;

	@Reference
	private RankingIndexReader _rankingIndexReader;

	@Reference
	private SearchEngineAdapter _searchEngineAdapter;

}