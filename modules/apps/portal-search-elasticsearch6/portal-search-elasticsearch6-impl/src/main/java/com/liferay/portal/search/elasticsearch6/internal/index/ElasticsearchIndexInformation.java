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

import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.search.elasticsearch6.internal.connection.ElasticsearchConnection;
import com.liferay.portal.search.index.IndexInformation;

import java.util.Map;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.get.GetFieldMappingsRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetFieldMappingsResponse;
import org.elasticsearch.action.admin.indices.mapping.get.GetFieldMappingsResponse.FieldMappingMetaData;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.common.Strings;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adam Brandizzi
 */
@Component(immediate = true, service = IndexInformation.class)
public class ElasticsearchIndexInformation implements IndexInformation {

	@Override
	public String getFieldMappings(String indexName) {
		GetFieldMappingsResponse getFieldMappingsResponse =
			getGetFieldMappingsResponse(indexName);

		return Strings.toString(getFieldMappingsResponse);
	}

	@Override
	public String[] getIndexNames() {
		GetIndexResponse getIndexResponse = getGetIndexResponse();

		return getIndexResponse.getIndices();
	}

	protected JSONObject getFieldMappingMetaDataJSONObject(
		FieldMappingMetaData fieldMappingMetaData) {

		JSONObject fieldMappingMetaDataJSONObject =
			jsonFactory.createJSONObject();

		Map<String, Object> source = fieldMappingMetaData.sourceAsMap();

		for (Map.Entry<String, Object> metaData : source.entrySet()) {
			fieldMappingMetaDataJSONObject.put(
				metaData.getKey(), metaData.getValue());
		}

		return fieldMappingMetaDataJSONObject;
	}

	protected JSONObject getFieldsJSONObject(
		Map<String, FieldMappingMetaData> fields) {

		JSONObject fieldsJSONObject = jsonFactory.createJSONObject();

		for (Map.Entry<String, FieldMappingMetaData> fieldEntry :
				fields.entrySet()) {

			JSONObject fieldMappingMetaDataJSONObject =
				getFieldMappingMetaDataJSONObject(fieldEntry.getValue());

			fieldsJSONObject.put(
				fieldEntry.getKey(), fieldMappingMetaDataJSONObject);
		}

		return fieldsJSONObject;
	}

	protected GetFieldMappingsResponse getGetFieldMappingsResponse(
		String index) {

		IndicesAdminClient indices = getIndicesAdminClient();

		GetFieldMappingsRequest request = new GetFieldMappingsRequest().indices(
			index);

		ActionFuture<GetFieldMappingsResponse> getIndexResponseFuture =
			indices.getFieldMappings(request);

		return getIndexResponseFuture.actionGet();
	}

	protected GetIndexResponse getGetIndexResponse() {
		IndicesAdminClient indices = getIndicesAdminClient();

		ActionFuture<GetIndexResponse> getIndexResponseFuture =
			indices.getIndex(new GetIndexRequest());

		return getIndexResponseFuture.actionGet();
	}

	protected IndicesAdminClient getIndicesAdminClient() {
		Client client = elasticsearchConnection.getClient();

		AdminClient adminClient = client.admin();

		IndicesAdminClient indices = adminClient.indices();

		return indices;
	}

	@Reference
	protected ElasticsearchConnection elasticsearchConnection;

	@Reference
	protected JSONFactory jsonFactory;

}