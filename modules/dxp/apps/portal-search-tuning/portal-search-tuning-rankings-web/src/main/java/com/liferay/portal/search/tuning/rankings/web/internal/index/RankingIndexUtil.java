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

	@Activate
	protected void activate() {
		_rankingIndexUtil = this;
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

	private static RankingIndexUtil _rankingIndexUtil;

	@Reference
	private RankingIndexCreator _rankingIndexCreator;

	@Reference
	private RankingIndexReader _rankingIndexReader;

}