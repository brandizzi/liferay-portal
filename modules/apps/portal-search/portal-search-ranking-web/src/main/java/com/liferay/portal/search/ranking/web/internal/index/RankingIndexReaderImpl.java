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

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.document.GetDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.GetDocumentResponse;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.MatchQuery;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.query.TermQuery;
import com.liferay.portal.search.query.TermsQuery;

import java.util.List;
import java.util.Optional;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bryan Engler
 */
@Component(service = RankingIndexReader.class)
public class RankingIndexReaderImpl implements RankingIndexReader {

	@Override
	public boolean exists(RankingCriteria rankingCriteria) {
		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		searchSearchRequest.setIndexNames(_getIndexName());
		searchSearchRequest.setQuery(_translate(rankingCriteria));
		searchSearchRequest.setSize(0);

		SearchSearchResponse searchSearchResponse =
			_searchEngineAdapter.execute(searchSearchRequest);

		if (searchSearchResponse.getCount() > 0) {
			return true;
		}

		return false;
	}

	@Override
	public Optional<Ranking> fetch(String uid) {
		return Optional.ofNullable(
			_getDocument(uid)
		).map(
			document -> _documentToRankingTranslator.translate(document, uid)
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

	private Document _getDocument(String uid) {
		GetDocumentRequest getDocumentRequest = new GetDocumentRequest(
			_getIndexName(), uid);

		getDocumentRequest.setFetchSourceInclude(StringPool.STAR);

		GetDocumentResponse getDocumentResponse = _searchEngineAdapter.execute(
			getDocumentRequest);

		if (getDocumentResponse.isExists()) {
			return getDocumentResponse.getDocument();
		}

		return null;
	}

	private String _getIndexName() {
		return SearchTuningIndexDefinition.INDEX_NAME;
	}

	private BooleanQuery _translate(RankingCriteria rankingCriteria) {
		BooleanQuery booleanQuery = _queries.booleanQuery();

		BooleanQuery keywordsBooleanQuery = _queries.booleanQuery();

		String keywords = rankingCriteria.getQueryString();

		if (!Validator.isBlank(keywords)) {
			TermQuery aliasesKeywordsTermQuery = _queries.term(
				"aliases", keywords);

			TermQuery keywordsKeywordsTermQuery = _queries.term(
				"keywords", keywords);

			keywordsBooleanQuery.addShouldQueryClauses(
				aliasesKeywordsTermQuery, keywordsKeywordsTermQuery);
		}

		List<String> aliases = rankingCriteria.getAliases();

		if (ListUtil.isNotEmpty(aliases)) {
			TermsQuery aliasesAliasesTermsQuery = _queries.terms("aliases");

			aliasesAliasesTermsQuery.addValues(aliases.toArray());

			TermsQuery keywordsAliasesTermsQuery = _queries.terms("keywords");

			keywordsAliasesTermsQuery.addValues(aliases.toArray());

			keywordsBooleanQuery.addShouldQueryClauses(
				aliasesAliasesTermsQuery, keywordsAliasesTermsQuery);
		}

		MatchQuery indexMatchQuery = _queries.match(
			"index", rankingCriteria.getIndex());

		booleanQuery.addMustQueryClauses(indexMatchQuery, keywordsBooleanQuery);

		if (rankingCriteria.getUid() != null) {
			booleanQuery.addMustNotQueryClauses(
				_queries.match("_id", rankingCriteria.getUid()));
		}

		return booleanQuery;
	}

	@Reference
	private DocumentToRankingTranslator _documentToRankingTranslator;

	private Queries _queries;
	private SearchEngineAdapter _searchEngineAdapter;

}