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

package com.liferay.headless.delivery.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.delivery.dto.v1_0.StructuredContentFolder;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import org.junit.runner.RunWith;

/**
 * @author Javier Gamarra
 */
@RunWith(Arquillian.class)
public class StructuredContentFolderResourceTest
	extends BaseStructuredContentFolderResourceTestCase {

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"description", "name"};
	}

	@Override
	protected StructuredContentFolder
			testDeleteStructuredContentFolder_addStructuredContentFolder()
		throws Exception {

		return invokePostSiteStructuredContentFolder(
			testGroup.getGroupId(), randomStructuredContentFolder());
	}

	@Override
	protected StructuredContentFolder
			testGetSiteStructuredContentFoldersPage_addStructuredContentFolder(
				Long siteId, StructuredContentFolder structuredContentFolder)
		throws Exception {

		return invokePostSiteStructuredContentFolder(
			siteId, structuredContentFolder);
	}

	@Override
	protected StructuredContentFolder
			testGetStructuredContentFolder_addStructuredContentFolder()
		throws Exception {

		return invokePostSiteStructuredContentFolder(
			testGroup.getGroupId(), randomStructuredContentFolder());
	}

	@Override
	protected StructuredContentFolder
			testGetStructuredContentFolderStructuredContentFoldersPage_addStructuredContentFolder(
				Long structuredContentFolderId,
				StructuredContentFolder structuredContentFolder)
		throws Exception {

		return invokePostStructuredContentFolderStructuredContentFolder(
			structuredContentFolderId, structuredContentFolder);
	}

	@Override
	protected Long
			testGetStructuredContentFolderStructuredContentFoldersPage_getIrrelevantParentStructuredContentFolderId()
		throws Exception {

		JournalFolder journalFolder = JournalTestUtil.addFolder(
			irrelevantGroup.getGroupId(), RandomTestUtil.randomString());

		return journalFolder.getFolderId();
	}

	@Override
	protected Long
			testGetStructuredContentFolderStructuredContentFoldersPage_getParentStructuredContentFolderId()
		throws Exception {

		JournalFolder journalFolder = JournalTestUtil.addFolder(
			testGroup.getGroupId(), RandomTestUtil.randomString());

		return journalFolder.getFolderId();
	}

	@Override
	protected StructuredContentFolder
			testPatchStructuredContentFolder_addStructuredContentFolder()
		throws Exception {

		return invokePostSiteStructuredContentFolder(
			testGroup.getGroupId(), randomStructuredContentFolder());
	}

	@Override
	protected StructuredContentFolder
			testPostSiteStructuredContentFolder_addStructuredContentFolder(
				StructuredContentFolder structuredContentFolder)
		throws Exception {

		return invokePostSiteStructuredContentFolder(
			testGroup.getGroupId(), structuredContentFolder);
	}

	@Override
	protected StructuredContentFolder
			testPostStructuredContentFolderStructuredContentFolder_addStructuredContentFolder(
				StructuredContentFolder structuredContentFolder)
		throws Exception {

		return invokePostSiteStructuredContentFolder(
			testGroup.getGroupId(), structuredContentFolder);
	}

	@Override
	protected StructuredContentFolder
			testPutStructuredContentFolder_addStructuredContentFolder()
		throws Exception {

		return invokePostSiteStructuredContentFolder(
			testGroup.getGroupId(), randomStructuredContentFolder());
	}

}