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

package com.liferay.knowledge.base.search.test;

import com.liferay.knowledge.base.constants.KBFolderConstants;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.service.KBArticleLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.service.test.ServiceTestUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Luan Maoski
 * @author Lucas Marques
 */
public class KBArticleFixture {

	public KBArticleFixture(Group group, User user) {
		_group = group;
		_user = user;
	}

	public KBArticle createKBArticle(String url) throws Exception {
		try {
			long kbFolderClassNameId = ClassNameLocalServiceUtil.getClassNameId(
				KBFolderConstants.getClassName());

			KBArticle kbArticle = KBArticleLocalServiceUtil.addKBArticle(
				getUserId(), kbFolderClassNameId,
				KBFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				StringUtil.randomString(), StringUtil.randomString(),
				StringUtil.randomString(), StringUtil.randomString(), url, null,
				null, getServiceContext());

			_kbArticles.add(kbArticle);

			return kbArticle;
		}
		catch (PortalException pe) {
			throw new RuntimeException(pe);
		}
	}

	public List<KBArticle> getKbArticles() {
		return _kbArticles;
	}

	public ServiceContext getServiceContext() throws Exception {
		return ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), getUserId());
	}

	public void setUp() throws Exception {
		ServiceTestUtil.setUser(TestPropsValues.getUser());

		CompanyThreadLocal.setCompanyId(TestPropsValues.getCompanyId());
	}

	public void updateDisplaySettings(Locale locale) throws Exception {
		Group group = GroupTestUtil.updateDisplaySettings(
			_group.getGroupId(), null, locale);

		_group.setModelAttributes(group.getModelAttributes());
	}

	protected long getUserId() throws Exception {
		return _user.getUserId();
	}

	private final Group _group;
	private final List<KBArticle> _kbArticles = new ArrayList<>();
	private final User _user;

}