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

import com.liferay.portal.kernel.util.HashUtil;
import com.liferay.portal.search.spi.model.index.contributor.helper.IndexerWriterMode;
import com.liferay.portal.search.spi.model.index.queue.IndexerToken;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Adam Brandizzi
 */
public class IndexerTokenImpl implements IndexerToken {

	public IndexerTokenImpl(IndexerWriterMode indexerWriterMode, String uid) {
		_indexerWriterMode = indexerWriterMode;
		_className = null;
		_primaryKeyObj = null;
		_uid = uid;
	}

	public IndexerTokenImpl(
		IndexerWriterMode indexerWriterMode, String className,
		Serializable primaryKeyObj) {

		_indexerWriterMode = indexerWriterMode;
		_className = className;
		_primaryKeyObj = primaryKeyObj;
		_uid = null;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (!(obj instanceof IndexerTokenImpl)) {
			return false;
		}

		IndexerTokenImpl indexerTokenImpl = (IndexerTokenImpl)obj;

		if (!_indexerWriterMode.equals(indexerTokenImpl._indexerWriterMode)) {
			return false;
		}

		if (!Objects.equals(_className, indexerTokenImpl._className)) {
			return false;
		}

		if (!Objects.equals(_primaryKeyObj, indexerTokenImpl._primaryKeyObj)) {
			return false;
		}

		if (!Objects.equals(_uid, indexerTokenImpl._uid)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int hash = HashUtil.hash(0, _indexerWriterMode);

		hash = HashUtil.hash(hash, _className);

		return HashUtil.hash(hash, _primaryKeyObj);
	}

	private final String _className;
	private final IndexerWriterMode _indexerWriterMode;
	private final Serializable _primaryKeyObj;
	private final String _uid;

}