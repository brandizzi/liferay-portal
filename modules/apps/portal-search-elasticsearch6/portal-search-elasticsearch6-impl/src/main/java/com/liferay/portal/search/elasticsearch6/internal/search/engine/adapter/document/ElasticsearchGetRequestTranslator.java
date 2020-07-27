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

package com.liferay.portal.search.elasticsearch6.internal.search.engine.adapter.document;

import com.liferay.portal.search.elasticsearch6.internal.connection.ElasticsearchConnectionManager;
import com.liferay.portal.search.engine.adapter.document.GetDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.GetRequestTranslator;

import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.client.Client;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adam Brandizzi
 */
@Component(
	property = "search.engine.impl=Elasticsearch",
	service = GetRequestTranslator.class
)
public class ElasticsearchGetRequestTranslator implements GetRequestTranslator {

	public GetRequestBuilder translate(GetDocumentRequest getDocumentRequest) {
		Client client = elasticsearchConnectionManager.getClient();

		GetRequestBuilder getRequestBuilder = client.prepareGet();

		return getRequestBuilder.setId(
			getDocumentRequest.getId()
		).setIndex(
			getDocumentRequest.getIndexName()
		).setRefresh(
			getDocumentRequest.isRefresh()
		).setFetchSource(
			getDocumentRequest.getFetchSourceIncludes(),
			getDocumentRequest.getFetchSourceExcludes()
		).setStoredFields(
			getDocumentRequest.getStoredFields()
		).setType(
			_getType(getDocumentRequest.getType())
		);
	}

	@Reference
	protected ElasticsearchConnectionManager elasticsearchConnectionManager;

	private String _getType(String type) {
		if (type != null) {
			return type;
		}

		return "_doc";
	}

}