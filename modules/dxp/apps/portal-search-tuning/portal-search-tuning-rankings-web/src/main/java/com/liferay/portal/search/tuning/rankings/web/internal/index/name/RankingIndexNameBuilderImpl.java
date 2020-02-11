/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.portal.search.tuning.rankings.web.internal.index.name;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.search.tuning.rankings.web.internal.index.RankingIndexDefinition;

import org.osgi.service.component.annotations.Component;

/**
 * @author Wade Cao
 * @author Adam Brandizzi
 */
@Component(service = RankingIndexNameBuilder.class)
public class RankingIndexNameBuilderImpl implements RankingIndexNameBuilder {

	@Override
	public String getRankingIndexName(String companyIndexName) {
		return RankingIndexDefinition.INDEX_NAME + StringPool.MINUS +
			companyIndexName;
	}

}