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

package com.liferay.journal.search.test;

import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.model.JournalFolderConstants;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vagner B.C
 */
public class JournalFolderFixture {

	public JournalFolderFixture(Group group) {
		_group = group;
	}

	public JournalFolder createJournalFolder() throws Exception {
		JournalFolder journalFolder = JournalTestUtil.addFolder(
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), getServiceContext());

		_journalFolders.add(journalFolder);

		return journalFolder;
	}

	public List<JournalFolder> getJournalFolders() {
		return _journalFolders;
	}

	public ServiceContext getServiceContext() throws Exception {
		return ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), getUserId());
	}

	protected long getUserId() throws Exception {
		return TestPropsValues.getUserId();
	}

	private final Group _group;
	private final List<JournalFolder> _journalFolders = new ArrayList<>();

}