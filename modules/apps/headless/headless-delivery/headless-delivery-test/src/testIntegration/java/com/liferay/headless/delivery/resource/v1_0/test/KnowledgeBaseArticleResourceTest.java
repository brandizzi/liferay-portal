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
import com.liferay.headless.delivery.dto.v1_0.KnowledgeBaseArticle;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.model.KBFolder;
import com.liferay.knowledge.base.service.KBArticleLocalServiceUtil;
import com.liferay.knowledge.base.service.KBFolderLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;

/**
 * @author Javier Gamarra
 */
@RunWith(Arquillian.class)
public class KnowledgeBaseArticleResourceTest
	extends BaseKnowledgeBaseArticleResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setScopeGroupId(testGroup.getGroupId());

		_kbFolder = KBFolderLocalServiceUtil.addKBFolder(
			UserLocalServiceUtil.getDefaultUserId(testGroup.getCompanyId()),
			testGroup.getGroupId(),
			PortalUtil.getClassNameId(KBFolder.class.getName()), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			serviceContext);
	}

	@Override
	protected void assertValid(KnowledgeBaseArticle knowledgeBaseArticle) {
		boolean valid = false;

		if ((knowledgeBaseArticle.getDateCreated() != null) &&
			(knowledgeBaseArticle.getDateModified() != null) &&
			(knowledgeBaseArticle.getId() != null) &&
			(knowledgeBaseArticle.getTitle() != null)) {

			valid = true;
		}

		Assert.assertTrue(valid);
	}

	@Override
	protected boolean equals(
		KnowledgeBaseArticle knowledgeBaseArticle1,
		KnowledgeBaseArticle knowledgeBaseArticle2) {

		if (Objects.equals(
				knowledgeBaseArticle1.getSiteId(),
				knowledgeBaseArticle2.getSiteId()) &&
			Objects.equals(
				knowledgeBaseArticle1.getDescription(),
				knowledgeBaseArticle2.getDescription()) &&
			Objects.equals(
				knowledgeBaseArticle1.getTitle(),
				knowledgeBaseArticle2.getTitle())) {

			return true;
		}

		return false;
	}

	@Override
	protected KnowledgeBaseArticle randomIrrelevantKnowledgeBaseArticle() {
		KnowledgeBaseArticle knowledgeBaseArticle =
			super.randomIrrelevantKnowledgeBaseArticle();

		knowledgeBaseArticle.setSiteId(irrelevantGroup.getGroupId());

		return knowledgeBaseArticle;
	}

	@Override
	protected KnowledgeBaseArticle randomKnowledgeBaseArticle() {
		KnowledgeBaseArticle knowledgeBaseArticle =
			super.randomKnowledgeBaseArticle();

		knowledgeBaseArticle.setSiteId(testGroup.getGroupId());

		return knowledgeBaseArticle;
	}

	@Override
	protected KnowledgeBaseArticle
			testDeleteKnowledgeBaseArticle_addKnowledgeBaseArticle()
		throws Exception {

		return invokePostSiteKnowledgeBaseArticle(
			testGroup.getGroupId(), randomKnowledgeBaseArticle());
	}

	@Override
	protected KnowledgeBaseArticle
			testDeleteKnowledgeBaseArticleMyRating_addKnowledgeBaseArticle()
		throws Exception {

		return invokePostSiteKnowledgeBaseArticle(
			testGroup.getGroupId(), randomKnowledgeBaseArticle());
	}

	@Override
	protected KnowledgeBaseArticle
			testGetKnowledgeBaseArticle_addKnowledgeBaseArticle()
		throws Exception {

		return invokePostSiteKnowledgeBaseArticle(
			testGroup.getGroupId(), randomKnowledgeBaseArticle());
	}

	@Override
	protected KnowledgeBaseArticle
			testGetKnowledgeBaseArticleKnowledgeBaseArticlesPage_addKnowledgeBaseArticle(
				Long parentKnowledgeBaseArticleId,
				KnowledgeBaseArticle knowledgeBaseArticle)
		throws Exception {

		return invokePostKnowledgeBaseArticleKnowledgeBaseArticle(
			parentKnowledgeBaseArticleId, knowledgeBaseArticle);
	}

	@Override
	protected Long
			testGetKnowledgeBaseArticleKnowledgeBaseArticlesPage_getParentKnowledgeBaseArticleId()
		throws Exception {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setScopeGroupId(testGroup.getGroupId());

		KBArticle kbArticle = KBArticleLocalServiceUtil.addKBArticle(
			UserLocalServiceUtil.getDefaultUserId(testGroup.getCompanyId()),
			PortalUtil.getClassNameId(KBFolder.class.getName()), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			null, null, serviceContext);

		return kbArticle.getResourcePrimKey();
	}

	@Override
	protected KnowledgeBaseArticle
			testGetKnowledgeBaseFolderKnowledgeBaseArticlesPage_addKnowledgeBaseArticle(
				Long knowledgeBaseFolderId,
				KnowledgeBaseArticle knowledgeBaseArticle)
		throws Exception {

		return invokePostKnowledgeBaseFolderKnowledgeBaseArticle(
			knowledgeBaseFolderId, knowledgeBaseArticle);
	}

	@Override
	protected Long
			testGetKnowledgeBaseFolderKnowledgeBaseArticlesPage_getKnowledgeBaseFolderId()
		throws Exception {

		return _kbFolder.getKbFolderId();
	}

	@Override
	protected KnowledgeBaseArticle
			testGetSiteKnowledgeBaseArticlesPage_addKnowledgeBaseArticle(
				Long siteId, KnowledgeBaseArticle knowledgeBaseArticle)
		throws Exception {

		return invokePostSiteKnowledgeBaseArticle(siteId, knowledgeBaseArticle);
	}

	@Override
	protected KnowledgeBaseArticle
			testPatchKnowledgeBaseArticle_addKnowledgeBaseArticle()
		throws Exception {

		return invokePostSiteKnowledgeBaseArticle(
			testGroup.getGroupId(), randomKnowledgeBaseArticle());
	}

	@Override
	protected KnowledgeBaseArticle
			testPostKnowledgeBaseArticleKnowledgeBaseArticle_addKnowledgeBaseArticle(
				KnowledgeBaseArticle knowledgeBaseArticle)
		throws Exception {

		return invokePostKnowledgeBaseArticleKnowledgeBaseArticle(
			testGetKnowledgeBaseArticleKnowledgeBaseArticlesPage_getParentKnowledgeBaseArticleId(),
			knowledgeBaseArticle);
	}

	@Override
	protected KnowledgeBaseArticle
			testPostKnowledgeBaseFolderKnowledgeBaseArticle_addKnowledgeBaseArticle(
				KnowledgeBaseArticle knowledgeBaseArticle)
		throws Exception {

		return invokePostKnowledgeBaseFolderKnowledgeBaseArticle(
			_kbFolder.getKbFolderId(), knowledgeBaseArticle);
	}

	@Override
	protected KnowledgeBaseArticle
			testPostSiteKnowledgeBaseArticle_addKnowledgeBaseArticle(
				KnowledgeBaseArticle knowledgeBaseArticle)
		throws Exception {

		return invokePostSiteKnowledgeBaseArticle(
			testGroup.getGroupId(), knowledgeBaseArticle);
	}

	@Override
	protected KnowledgeBaseArticle
			testPutKnowledgeBaseArticle_addKnowledgeBaseArticle()
		throws Exception {

		return invokePostSiteKnowledgeBaseArticle(
			testGroup.getGroupId(), randomKnowledgeBaseArticle());
	}

	private KBFolder _kbFolder;

}