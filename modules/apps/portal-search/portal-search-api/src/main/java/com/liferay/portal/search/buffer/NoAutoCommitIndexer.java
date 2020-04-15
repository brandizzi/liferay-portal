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

package com.liferay.portal.search.buffer;

import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.search.indexer.IndexerWrapper;

/**
 * @author Michael C. Han
 */
public class NoAutoCommitIndexer<T> extends IndexerWrapper<T> {

	public NoAutoCommitIndexer(Indexer<T> indexer) {
		super(indexer);
	}

	@Override
	public boolean isCommitImmediately() {
		return false;
	}

}