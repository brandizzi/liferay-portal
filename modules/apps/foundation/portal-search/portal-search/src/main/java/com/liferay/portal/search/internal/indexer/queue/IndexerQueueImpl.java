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

package com.liferay.portal.search.internal.indexer.queue;

import com.liferay.portal.search.spi.model.index.queue.IndexerQueue;
import com.liferay.portal.search.spi.model.index.queue.IndexerRequest;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Adam Brandizzi
 */
@Component(service = IndexerQueue.class)
public class IndexerQueueImpl implements IndexerQueue {

	@Override
	public void put(IndexerRequest indexerRequest) {
		_indexerTokens.add(indexerRequest);
	}

	@Override
	public List<IndexerRequest> take(int count) {
		if (_indexerTokens.size() <= count) {
			return _indexerTokens;
		}

		return _indexerTokens.subList(0, count);
	}

	private final List<IndexerRequest> _indexerTokens = new ArrayList<>();

}