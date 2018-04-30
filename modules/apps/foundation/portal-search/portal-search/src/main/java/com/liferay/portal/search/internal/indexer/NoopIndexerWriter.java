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

package com.liferay.portal.search.internal.indexer;

import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.search.batch.BatchIndexingActionable;
import com.liferay.portal.search.indexer.IndexerWriter;

import java.util.Collection;

/**
 * @author Adam Brandizzi
 */
public class NoopIndexerWriter<T extends BaseModel<?>>
	implements IndexerWriter<T> {

	@Override
	public void delete(long companyId, String uid) {

		// TODO Auto-generated method stub

	}

	@Override
	public void delete(T baseModel) {

		// TODO Auto-generated method stub

	}

	@Override
	public BatchIndexingActionable getBatchIndexingActionable() {

		// TODO Auto-generated method stub

		return null;
	}

	@Override
	public boolean isEnabled() {

		// TODO Auto-generated method stub

		return false;
	}

	@Override
	public void reindex(Collection<T> baseModels) {

		// TODO Auto-generated method stub

	}

	@Override
	public void reindex(long classPK) {

		// TODO Auto-generated method stub

	}

	@Override
	public void reindex(String[] ids) {

		// TODO Auto-generated method stub

	}

	@Override
	public void reindex(T baseModel) {

		// TODO Auto-generated method stub

	}

	@Override
	public void setEnabled(boolean enabled) {

		// TODO Auto-generated method stub

	}

	@Override
	public void updatePermissionFields(T baseModel) {

		// TODO Auto-generated method stub

	}

}