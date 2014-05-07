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

package com.liferay.portal.kernel.scheduler;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.util.BasePortalLifecycle;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Tina Tian
 */
public class SchedulerLifecycle extends BasePortalLifecycle {

	@Override
	protected void doPortalDestroy() throws Exception {
	}

	@Override
	protected void doPortalInit() throws Exception {

		String[] requiredDeploymentContexts = PropsUtil.getArray(
			PropsKeys.SCHEDULER_INITIALIZATION_REQUIRED_DEPLOYMENT_CONTEXTS);

		if (ServletContextPool.containsAll(requiredDeploymentContexts)) {
			SchedulerEngineHelperUtil.start();
		}
		else {
			TimerTask timerTask = new TimerTask() {

				@Override
				public void run() {
					try {
						SchedulerEngineHelperUtil.start();
					}
					catch (SchedulerException e) {
						_log.error(e.getMessage(), e);
					}
				}
			};

			Timer timer = new Timer();

			timer.schedule(
				timerTask,
				GetterUtil.getInteger(
					PropsUtil.get(PropsKeys.SCHEDULER_INITIALIZATION_DELAY)));
		}
	}

	private static Log _log = LogFactoryUtil.getLog(SchedulerLifecycle.class);

}