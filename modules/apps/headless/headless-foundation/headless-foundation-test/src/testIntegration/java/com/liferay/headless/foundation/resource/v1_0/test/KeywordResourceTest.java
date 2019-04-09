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

package com.liferay.headless.foundation.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.headless.foundation.dto.v1_0.Keyword;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Objects;

import org.junit.Assert;
import org.junit.runner.RunWith;

/**
 * @author Javier Gamarra
 */
@RunWith(Arquillian.class)
public class KeywordResourceTest extends BaseKeywordResourceTestCase {

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"name"};
	}

	@Override
	protected Keyword testDeleteKeyword_addKeyword() throws Exception {
		return invokePostSiteKeyword(testGroup.getGroupId(), randomKeyword());
	}

	@Override
	protected Keyword testGetKeyword_addKeyword() throws Exception {
		return invokePostSiteKeyword(testGroup.getGroupId(), randomKeyword());
	}

	@Override
	protected Keyword testGetSiteKeywordsPage_addKeyword(
			Long siteId, Keyword keyword)
		throws Exception {

		return invokePostSiteKeyword(siteId, keyword);
	}

	@Override
	protected Keyword testPostSiteKeyword_addKeyword(Keyword keyword)
		throws Exception {

		return invokePostSiteKeyword(testGroup.getGroupId(), keyword);
	}

	@Override
	protected Keyword testPutKeyword_addKeyword() throws Exception {
		return invokePostSiteKeyword(testGroup.getGroupId(), randomKeyword());
	}

}