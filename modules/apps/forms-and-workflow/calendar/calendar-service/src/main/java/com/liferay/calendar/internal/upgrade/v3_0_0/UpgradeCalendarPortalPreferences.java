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

package com.liferay.calendar.internal.upgrade.v3_0_0;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.PortalPreferences;
import com.liferay.portal.kernel.service.PortalPreferencesLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.List;

/**
 * @author Lino Alves
 */
public class UpgradeCalendarPortalPreferences extends UpgradeProcess {

	public UpgradeCalendarPortalPreferences(
		PortalPreferencesLocalService portalPreferencesLocalService) {

		_portalPreferencesLocalService = portalPreferencesLocalService;
	}

	@Override
	public void doUpgrade() throws Exception {
		updateOtherCalendarsKey();
	}

	public void updateOtherCalendarsKey() {
		List<PortalPreferences> portalPreferencesList =
			_portalPreferencesLocalService.getPortalPreferenceses(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		portalPreferencesList.forEach(
			portalPreferences -> {
				String preferences = portalPreferences.getPreferences();

				String[] oldKeys = {
					"calendar-portlet-other-calendars",
					"com.liferay.portal.util.SessionClicks"
				};

				String[] newKeys = {
					"com.liferay.calendar.web_otherCalendars",
					"com.liferay.portal.kernel.util.SessionClicks"
				};

				preferences = StringUtil.replace(preferences, oldKeys, newKeys);

				portalPreferences.setPreferences(preferences);

				_portalPreferencesLocalService.updatePortalPreferences(
					portalPreferences);
			});
	}

	private final PortalPreferencesLocalService _portalPreferencesLocalService;

}