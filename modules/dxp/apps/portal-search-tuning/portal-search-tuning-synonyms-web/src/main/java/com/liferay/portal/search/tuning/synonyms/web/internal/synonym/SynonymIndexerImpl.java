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

package com.liferay.portal.search.tuning.synonyms.web.internal.synonym;

import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.index.CloseIndexRequest;
import com.liferay.portal.search.engine.adapter.index.GetIndexIndexRequest;
import com.liferay.portal.search.engine.adapter.index.GetIndexIndexResponse;
import com.liferay.portal.search.engine.adapter.index.OpenIndexRequest;
import com.liferay.portal.search.engine.adapter.index.UpdateIndexSettingsIndexRequest;
import com.liferay.portal.search.index.IndexNameBuilder;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adam Brandizzi
 */
@Component(immediate = true, service = SynonymIndexer.class)
public class SynonymIndexerImpl implements SynonymIndexer {

	@Override
	public String[] getSynonymSets(long companyId, String filterName) {
		return getSynonymSets(
			indexNameBuilder.getIndexName(companyId), filterName);
	}

	@Override
	public String[] getSynonymSets(String indexName, String filterName) {
		GetIndexIndexRequest getIndexIndexRequest = new GetIndexIndexRequest(
			indexName);

		GetIndexIndexResponse getIndexIndexResponse =
			searchEngineAdapter.execute(getIndexIndexRequest);

		Map<String, String> settings = getIndexIndexResponse.getSettings();

		JSONObject jsonObject;

		try {
			jsonObject = jsonFactory.createJSONObject(settings.get(indexName));
		}
		catch (JSONException jsone) {
			throw new RuntimeException(jsone);
		}

		return JSONUtil.toStringArray(
			jsonObject.getJSONArray(
				"index.analysis.filter." + filterName + ".synonyms"));
	}

	@Override
	public void updateSynonymSets(
		long companyId, String filterName, String[] synonymSets) {

		updateSynonymSets(
			indexNameBuilder.getIndexName(companyId), filterName, synonymSets);
	}

	@Override
	public void updateSynonymSets(
		String indexName, String filterName, String[] synonymSets) {

		Class<?> clazz = getClass();

		_log.error(clazz.getName() + ": closing index " + indexName);

		closeIndex(indexName);

		try {
			_log.error(clazz.getName() + ": updating setings on " + indexName);
			UpdateIndexSettingsIndexRequest updateIndexSettingsIndexRequest =
				new UpdateIndexSettingsIndexRequest(indexName);

			String settings = buildSettings(filterName, synonymSets);

			updateIndexSettingsIndexRequest.setSettings(settings);

			searchEngineAdapter.execute(updateIndexSettingsIndexRequest);
			_log.error(clazz.getName() + ": setings updated on" + indexName);
		}
		finally {
			openIndex(indexName);
			_log.error(clazz.getName() + ": index reopened > " + indexName);
		}
	}

	protected String buildSettings(String filterName, String[] synonymSets) {
		return JSONUtil.put(
			"analysis",
			JSONUtil.put(
				"filter",
				JSONUtil.put(
					filterName,
					JSONUtil.put(
						"lenient", true
					).put(
						"synonyms", jsonFactory.createJSONArray(synonymSets)
					).put(
						"type", "synonym_graph"
					)))
		).toString();
	}

	protected void closeIndex(String indexName) {
		CloseIndexRequest closeIndexRequest = new CloseIndexRequest(indexName);

		searchEngineAdapter.execute(closeIndexRequest);
	}

	protected void openIndex(String indexName) {
		OpenIndexRequest openIndexRequest = new OpenIndexRequest(indexName);

		searchEngineAdapter.execute(openIndexRequest);
	}

	@Reference
	protected IndexNameBuilder indexNameBuilder;

	@Reference
	protected JSONFactory jsonFactory;

	@Reference
	protected SearchEngineAdapter searchEngineAdapter;

	private static final Log _log = LogFactoryUtil.getLog(
		SynonymIndexerImpl.class);

}