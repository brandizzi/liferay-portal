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

import com.liferay.portal.kernel.service.CompanyService;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.document.BulkDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.IndexDocumentRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.tuning.rankings.web.internal.index.RankingFields;
import com.liferay.portal.search.tuning.rankings.web.internal.index.RankingIndexCreator;
import com.liferay.portal.search.tuning.rankings.web.internal.index.RankingIndexReader;
import com.liferay.portal.search.tuning.rankings.web.internal.index.name.RankingIndexName;
import com.liferay.portal.search.tuning.rankings.web.internal.index.name.RankingIndexNameBuilder;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Wade Cao
 * @author Adam Brandizzi
 */
@Component(service = SingleIndexToMultipleIndexImporter.class)
public class SingleIndexToMultipleIndexImporterImpl
	implements SingleIndexToMultipleIndexImporter {

	@Override
	public void importRankings(String companyIndexName) {
		if (!_rankingIndexReader.isExists(SINGLE_INDEX_NAME)) {
			return;
		}

		RankingIndexName rankingIndexName =
			_rankingIndexNameBuilder.getRankingIndexName(companyIndexName);

		if (!_rankingIndexReader.isExists(rankingIndexName)) {
			_rankingIndexCreator.create(rankingIndexName);
		}

		_rankingIndexCreator.create(rankingIndexName);

		List<Document> documents = getDocuments(
			SINGLE_INDEX_NAME, companyIndexName);

		if (documents.isEmpty()) {
			return;
		}

		addDocuments(rankingIndexName, documents);
	}

	protected void addDocuments(
		RankingIndexName rankingIndexName, List<Document> documents) {

		BulkDocumentRequest bulkDocumentRequest = new BulkDocumentRequest();

		documents.forEach(
			document -> {
				IndexDocumentRequest indexDocumentRequest =
					new IndexDocumentRequest(
						rankingIndexName.getIndexName(), document);

				bulkDocumentRequest.addBulkableDocumentRequest(
					indexDocumentRequest);
			});

		_searchEngineAdapter.execute(bulkDocumentRequest);
	}

	protected List<Document> getDocuments(
		RankingIndexName rankingIndexName, String companyIndexName) {

		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		searchSearchRequest.setIndexNames(rankingIndexName.getIndexName());
		searchSearchRequest.setQuery(
			_queries.match(RankingFields.INDEX, companyIndexName));
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

	protected static final RankingIndexName SINGLE_INDEX_NAME =
		new RankingIndexName() {

			@Override
			public String getIndexName() {
				return "liferay-search-tuning-rankings";
			}

		};

	@Reference
	private CompanyService _companyService;

	@Reference
	private IndexNameBuilder _indexNameBuilder;

	@Reference
	private Queries _queries;

	@Reference
	private RankingIndexCreator _rankingIndexCreator;

	@Reference
	private RankingIndexNameBuilder _rankingIndexNameBuilder;

	@Reference
	private RankingIndexReader _rankingIndexReader;

	@Reference
	private SearchEngineAdapter _searchEngineAdapter;

}