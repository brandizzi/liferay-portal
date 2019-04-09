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
import com.liferay.headless.delivery.dto.v1_0.KnowledgeBaseFolder;
import com.liferay.knowledge.base.model.KBFolder;
import com.liferay.knowledge.base.service.KBFolderLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import java.util.Objects;

import org.junit.Assert;
import org.junit.runner.RunWith;

/**
 * @author Javier Gamarra
 */
@RunWith(Arquillian.class)
public class KnowledgeBaseFolderResourceTest
	extends BaseKnowledgeBaseFolderResourceTestCase {

	@Override
	protected void assertValid(KnowledgeBaseFolder knowledgeBaseFolder) {
		boolean valid = false;

		if ((knowledgeBaseFolder.getDateCreated() != null) &&
			(knowledgeBaseFolder.getDateModified() != null) &&
			(knowledgeBaseFolder.getId() != null) &&
			(knowledgeBaseFolder.getName() != null)) {

			valid = true;
		}

		Assert.assertTrue(valid);
	}

	@Override
	protected boolean equals(
		KnowledgeBaseFolder knowledgeBaseFolder1,
		KnowledgeBaseFolder knowledgeBaseFolder2) {

		if (Objects.equals(
				knowledgeBaseFolder1.getSiteId(),
				knowledgeBaseFolder2.getSiteId()) &&
			Objects.equals(
				knowledgeBaseFolder1.getDescription(),
				knowledgeBaseFolder2.getDescription()) &&
			Objects.equals(
				knowledgeBaseFolder1.getName(),
				knowledgeBaseFolder2.getName())) {

			return true;
		}

		return false;
	}

	@Override
	protected KnowledgeBaseFolder randomIrrelevantKnowledgeBaseFolder() {
		KnowledgeBaseFolder knowledgeBaseFolder =
			super.randomIrrelevantKnowledgeBaseFolder();

		knowledgeBaseFolder.setSiteId(irrelevantGroup.getGroupId());

		return knowledgeBaseFolder;
	}

	@Override
	protected KnowledgeBaseFolder randomKnowledgeBaseFolder() {
		KnowledgeBaseFolder knowledgeBaseFolder =
			super.randomKnowledgeBaseFolder();

		knowledgeBaseFolder.setSiteId(testGroup.getGroupId());

		return knowledgeBaseFolder;
	}

	@Override
	protected KnowledgeBaseFolder
			testDeleteKnowledgeBaseFolder_addKnowledgeBaseFolder()
		throws Exception {

		return invokePostSiteKnowledgeBaseFolder(
			testGroup.getGroupId(), randomKnowledgeBaseFolder());
	}

	@Override
	protected KnowledgeBaseFolder
			testGetKnowledgeBaseFolder_addKnowledgeBaseFolder()
		throws Exception {

		return invokePostSiteKnowledgeBaseFolder(
			testGroup.getGroupId(), randomKnowledgeBaseFolder());
	}

	@Override
	protected KnowledgeBaseFolder
			testGetKnowledgeBaseFolderKnowledgeBaseFoldersPage_addKnowledgeBaseFolder(
				Long parentKnowledgeBaseFolderId,
				KnowledgeBaseFolder knowledgeBaseFolder)
		throws Exception {

		return invokePostKnowledgeBaseFolderKnowledgeBaseFolder(
			parentKnowledgeBaseFolderId, knowledgeBaseFolder);
	}

	@Override
	protected Long
			testGetKnowledgeBaseFolderKnowledgeBaseFoldersPage_getParentKnowledgeBaseFolderId()
		throws Exception {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setScopeGroupId(testGroup.getGroupId());

		KBFolder kbFolder = KBFolderLocalServiceUtil.addKBFolder(
			UserLocalServiceUtil.getDefaultUserId(testGroup.getCompanyId()),
			testGroup.getGroupId(),
			PortalUtil.getClassNameId(KBFolder.class.getName()), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			serviceContext);

		return kbFolder.getKbFolderId();
	}

	@Override
	protected KnowledgeBaseFolder
			testGetSiteKnowledgeBaseFoldersPage_addKnowledgeBaseFolder(
				Long siteId, KnowledgeBaseFolder knowledgeBaseFolder)
		throws Exception {

		return invokePostSiteKnowledgeBaseFolder(siteId, knowledgeBaseFolder);
	}

	@Override
	protected KnowledgeBaseFolder
			testPatchKnowledgeBaseFolder_addKnowledgeBaseFolder()
		throws Exception {

		return invokePostSiteKnowledgeBaseFolder(
			testGroup.getGroupId(), randomKnowledgeBaseFolder());
	}

	@Override
	protected KnowledgeBaseFolder
			testPostKnowledgeBaseFolderKnowledgeBaseFolder_addKnowledgeBaseFolder(
				KnowledgeBaseFolder knowledgeBaseFolder)
		throws Exception {

		return invokePostKnowledgeBaseFolderKnowledgeBaseFolder(
			testGetKnowledgeBaseFolderKnowledgeBaseFoldersPage_getParentKnowledgeBaseFolderId(),
			knowledgeBaseFolder);
	}

	@Override
	protected KnowledgeBaseFolder
			testPostSiteKnowledgeBaseFolder_addKnowledgeBaseFolder(
				KnowledgeBaseFolder knowledgeBaseFolder)
		throws Exception {

		return invokePostSiteKnowledgeBaseFolder(
			testGroup.getGroupId(), knowledgeBaseFolder);
	}

	@Override
	protected KnowledgeBaseFolder
			testPutKnowledgeBaseFolder_addKnowledgeBaseFolder()
		throws Exception {

		return invokePostSiteKnowledgeBaseFolder(
			testGroup.getGroupId(), randomKnowledgeBaseFolder());
	}

}