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

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.index.CreateIndexRequest;
import com.liferay.portal.search.engine.adapter.index.CreateIndexResponse;
import com.liferay.portal.search.engine.adapter.index.DeleteIndexRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Wade Cao
 * @author Adam Brandizzi
 */
@Component(service = RankingIndexCreator.class)
public class RankingIndexCreatorImpl implements RankingIndexCreator {

	@Override
	public void create(String rankingIndexName) {
		String mappingSource = StringUtil.read(
			getClass(), RankingIndexDefinition.INDEX_SETTINGS_RESOURCE_NAME);

		CreateIndexRequest createIndexRequest = new CreateIndexRequest(
			rankingIndexName);

		createIndexRequest.setSource(mappingSource);

		CreateIndexResponse createIndexResponse = _searchEngineAdapter.execute(
			createIndexRequest);
	}

	@Override
	public void delete(String rankingIndexName) {
		DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(
			rankingIndexName);

		_searchEngineAdapter.execute(deleteIndexRequest);
	}

	protected String readIndexSettings() {
		return StringUtil.read(
			getClass(), RankingIndexDefinition.INDEX_SETTINGS_RESOURCE_NAME);
	}

	@Reference
	private SearchEngineAdapter _searchEngineAdapter;

}