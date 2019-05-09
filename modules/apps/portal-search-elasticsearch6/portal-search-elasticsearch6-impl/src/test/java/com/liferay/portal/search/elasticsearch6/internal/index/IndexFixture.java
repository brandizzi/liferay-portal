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

package com.liferay.portal.search.elasticsearch6.internal.index;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.search.elasticsearch6.internal.connection.ElasticsearchClientResolver;
import com.liferay.portal.search.elasticsearch6.internal.util.LogUtil;

import java.util.Iterator;

import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequestBuilder;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.common.xcontent.XContentType;

/**
 * @author Adam Brandizzi
 */
public class IndexFixture {

	public IndexFixture(
		ElasticsearchClientResolver elasticsearchClientResolver) {

		Client client = elasticsearchClientResolver.getClient();

		AdminClient adminClient = client.admin();

		_indicesAdminClient = adminClient.indices();
	}

	public void addTypeMappings(
		String indexName, JSONObject typeMappingsJSONObject) {

		Iterator<String> typeNames = typeMappingsJSONObject.keys();

		while (typeNames.hasNext()) {
			addTypeMappings(
				indexName, typeMappingsJSONObject.toJSONString(),
				typeNames.next());
		}
	}

	public void addTypeMappings(
		String indexName, String typeSource, String typeName) {

		PutMappingRequestBuilder putMappingRequestBuilder =
			_indicesAdminClient.preparePutMapping(indexName);

		putMappingRequestBuilder.setSource(typeSource, XContentType.JSON);
		putMappingRequestBuilder.setType(typeName);

		ActionResponse actionResponse = putMappingRequestBuilder.get();

		LogUtil.logActionResponse(_log, actionResponse);
	}

	public void createIndex(String indexName) {
		if (hasIndex(indexName)) {
			return;
		}

		CreateIndexRequestBuilder createIndexRequestBuilder =
			_indicesAdminClient.prepareCreate(indexName);

		CreateIndexResponse createIndexResponse =
			createIndexRequestBuilder.get();

		LogUtil.logActionResponse(_log, createIndexResponse);
	}

	public void deleteIndices(String indexName) {
		if (!hasIndex(indexName)) {
			return;
		}

		DeleteIndexRequestBuilder deleteIndexRequestBuilder =
			_indicesAdminClient.prepareDelete(indexName);

		ActionResponse actionResponse = deleteIndexRequestBuilder.get();

		LogUtil.logActionResponse(_log, actionResponse);
	}

	protected boolean hasIndex(String indexName) {
		IndicesExistsRequestBuilder indicesExistsRequestBuilder =
			_indicesAdminClient.prepareExists(indexName);

		IndicesExistsResponse indicesExistsResponse =
			indicesExistsRequestBuilder.get();

		return indicesExistsResponse.isExists();
	}

	private static final Log _log = LogFactoryUtil.getLog(IndexFixture.class);

	private final IndicesAdminClient _indicesAdminClient;

}