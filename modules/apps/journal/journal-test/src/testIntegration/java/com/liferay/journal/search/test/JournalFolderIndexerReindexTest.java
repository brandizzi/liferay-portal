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

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.journal.model.JournalFolder;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.search.test.util.IndexerFixture;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerTestRule;
import com.liferay.users.admin.test.util.search.UserSearchFixture;

import java.util.List;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Vagner B.C
 */
@RunWith(Arquillian.class)
public class JournalFolderIndexerReindexTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		setUpUserSearchFixture();
		setUpJournalFolderIndexerFixture();
		setUpJournalFolderFixture();
	}

	@Test
	public void testReindexFolders() throws Exception {
		JournalFolder journalFolder =
			journalFolderFixture.createJournalFolder();

		String searchTerm = journalFolder.getName();

		journalFolderIndexerFixture.searchOnlyOne(searchTerm);

		Document document = journalFolderIndexerFixture.searchOnlyOne(
			searchTerm);

		journalFolderIndexerFixture.deleteDocument(document);

		journalFolderIndexerFixture.searchNoOne(searchTerm);

		journalFolderIndexerFixture.reindex(journalFolder.getCompanyId());

		journalFolderIndexerFixture.searchOnlyOne(searchTerm);
	}

	protected void setUpJournalFolderFixture() {
		journalFolderFixture = new JournalFolderFixture(group);

		_journalFolders = journalFolderFixture.getJournalFolders();
	}

	protected void setUpJournalFolderIndexerFixture() {
		journalFolderIndexerFixture = new IndexerFixture<>(JournalFolder.class);
	}

	protected void setUpUserSearchFixture() throws Exception {
		userSearchFixture = new UserSearchFixture();

		userSearchFixture.setUp();

		_groups = userSearchFixture.getGroups();
		_users = userSearchFixture.getUsers();

		group = userSearchFixture.addGroup();
	}

	protected Group group;
	protected JournalFolderFixture journalFolderFixture;
	protected IndexerFixture<JournalFolder> journalFolderIndexerFixture;
	protected UserSearchFixture userSearchFixture;

	@DeleteAfterTestRun
	private List<Group> _groups;

	@DeleteAfterTestRun
	private List<JournalFolder> _journalFolders;

	@DeleteAfterTestRun
	private List<User> _users;

}