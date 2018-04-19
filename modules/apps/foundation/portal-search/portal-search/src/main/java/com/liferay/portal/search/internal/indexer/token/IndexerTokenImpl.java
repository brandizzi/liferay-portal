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

package com.liferay.portal.search.internal.indexer.token;

import com.liferay.portal.kernel.util.HashUtil;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Adam Brandizzi
 */
public class IndexerTokenImpl implements IndexerToken {

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (!(obj instanceof IndexerTokenImpl)) {
			return false;
		}

		IndexerTokenImpl indexerTokenImpl = (IndexerTokenImpl)obj;

		if (!Objects.equals(_className, indexerTokenImpl._className)) {
			return false;
		}

		if (_commitImmediately != indexerTokenImpl._commitImmediately) {
			return false;
		}

		if (_companyId != indexerTokenImpl._companyId) {
			return false;
		}

		if (_indexerOperation != indexerTokenImpl._indexerOperation) {
			return false;
		}

		if (!Objects.equals(_primaryKeyObj, indexerTokenImpl._primaryKeyObj)) {
			return false;
		}

		if (!Objects.equals(
				_searchEngineId, indexerTokenImpl._searchEngineId)) {

			return false;
		}

		if (!Objects.equals(_uid, indexerTokenImpl._uid)) {
			return false;
		}

		return true;
	}

	@Override
	public String getClassName() {
		return _className;
	}

	@Override
	public long getCompanyId() {
		return _companyId;
	}

	@Override
	public IndexerOperation getIndexerOperation() {
		return _indexerOperation;
	}

	@Override
	public Serializable getPrimaryKeyObj() {
		return _primaryKeyObj;
	}

	@Override
	public String getSearchEngineId() {
		return _searchEngineId;
	}

	@Override
	public String getUid() {
		return _uid;
	}

	@Override
	public int hashCode() {
		int hash = HashUtil.hash(0, _className);

		hash = HashUtil.hash(hash, _commitImmediately);
		hash = HashUtil.hash(hash, _companyId);
		hash = HashUtil.hash(hash, _indexerOperation);
		hash = HashUtil.hash(hash, _primaryKeyObj);
		hash = HashUtil.hash(hash, _searchEngineId);
		hash = HashUtil.hash(hash, _uid);

		return HashUtil.hash(hash, _companyId);
	}

	@Override
	public boolean isCommitImmediately() {
		return _commitImmediately;
	}

	public void setClassName(String className) {
		_className = className;
	}

	public void setCommitImmediately(boolean commitImmediately) {
		_commitImmediately = commitImmediately;
	}

	public void setCompanyId(long companyId) {
		_companyId = companyId;
	}

	public void setIndexerOperation(IndexerOperation indexerOperation) {
		_indexerOperation = indexerOperation;
	}

	public void setPrimaryKeyObj(Serializable primaryKeyObj) {
		_primaryKeyObj = primaryKeyObj;
	}

	public void setSearchEngineId(String searchEngineId) {
		_searchEngineId = searchEngineId;
	}

	public void setUid(String uid) {
		_uid = uid;
	}

	@Override
	public String toString() {
		String params = String.join(
			", ", _className, String.valueOf(_commitImmediately),
			String.valueOf(_companyId), String.valueOf(_indexerOperation),
			String.valueOf(_primaryKeyObj), _searchEngineId,
			String.valueOf(_uid));

		return "IndexerTokenImpl(" + params + ")";
	}

	private String _className;
	private boolean _commitImmediately;
	private long _companyId;
	private IndexerOperation _indexerOperation;
	private Serializable _primaryKeyObj;
	private String _searchEngineId;
	private String _uid;

}