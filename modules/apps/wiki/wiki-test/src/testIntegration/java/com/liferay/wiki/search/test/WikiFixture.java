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

package com.liferay.wiki.search.test;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiNodeLocalServiceUtil;
import com.liferay.wiki.util.test.WikiTestUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Luan Maoski
 */
public class WikiFixture {

	public WikiFixture(Group group) {
		_group = group;
	}

	public WikiNode createWikiNode() throws Exception {
		WikiNode wikiNode = WikiNodeLocalServiceUtil.addNode(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), getServiceContext());

		_wikiNodes.add(wikiNode);

		return wikiNode;
	}

	public WikiPage createWikiPage() throws Exception, PortalException {
		try {
			WikiNode wikiNode = createWikiNode();

			WikiPage wikiPage = WikiTestUtil.addPage(
				TestPropsValues.getUserId(), wikiNode.getNodeId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				true, getServiceContext());

			_wikiPages.add(wikiPage);

			return wikiPage;
		}
		catch (PortalException pe) {
			throw new RuntimeException(pe);
		}
	}

	public List<WikiNode> getWikiNodes() {
		return _wikiNodes;
	}

	public List<WikiPage> getWikiPages() {
		return _wikiPages;
	}

	protected ServiceContext getServiceContext() throws Exception {
		return ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), getUserId());
	}

	protected long getUserId() throws Exception {
		return TestPropsValues.getUserId();
	}

	private final Group _group;
	private final List<WikiNode> _wikiNodes = new ArrayList<>();
	private final List<WikiPage> _wikiPages = new ArrayList<>();

}