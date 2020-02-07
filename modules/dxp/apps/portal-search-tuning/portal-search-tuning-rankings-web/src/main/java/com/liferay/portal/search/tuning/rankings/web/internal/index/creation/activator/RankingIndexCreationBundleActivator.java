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

package com.liferay.portal.search.tuning.rankings.web.internal.index.creation.activator;

import com.liferay.portal.kernel.backgroundtask.BackgroundTaskManager;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.search.tuning.rankings.web.internal.background.task.RankingIndexRenameBackgroundTaskExecutor;
import com.liferay.portal.search.tuning.rankings.web.internal.index.RankingIndexDefinition;
import com.liferay.portal.search.tuning.rankings.web.internal.index.RankingIndexUtil;

import java.io.Serializable;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adam Brandizzi
 */
@Component(
	immediate = true, service = RankingIndexCreationBundleActivator.class
)
public class RankingIndexCreationBundleActivator {

	@Activate
	protected void activate() {
		_addBackgroundTask();
	}

	private void _addBackgroundTask() {
		Map<String, Serializable> taskContextMap = new HashMap<>();

		taskContextMap.put("indexName", RankingIndexDefinition.INDEX_NAME);

		String jobName = "RankingIndexRename";

		try {
			_backgroundTaskManager.addBackgroundTask(
				UserConstants.USER_ID_DEFAULT, CompanyConstants.SYSTEM, jobName,
				RankingIndexRenameBackgroundTaskExecutor.class.getName(),
				taskContextMap, new ServiceContext());
		}
		catch (PortalException pe) {
			_log.error("Unable to schedule the job for " + jobName, pe);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RankingIndexCreationBundleActivator.class);

	@Reference
	private BackgroundTaskManager _backgroundTaskManager;

	@Reference
	private RankingIndexRenameBackgroundTaskExecutor
		_rankingIndexRenameBackgroundTaskExecutor;

	@Reference
	private RankingIndexUtil _rankingIndexUtil;

}