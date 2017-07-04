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

package com.liferay.calendar.internal.upgrade.v1_0_6;

import com.liferay.calendar.model.CalendarResource;
import com.liferay.calendar.service.CalendarResourceLocalService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourceBlockConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourceBlockLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeException;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.security.permission.ResourceActionsImpl;
import com.liferay.portal.util.PropsValues;

import java.util.List;

/**
 * @author José María Muñoz
 * @author Alberto Chaparro
 */
public class UpgradeResourcePermission extends UpgradeProcess {

	public UpgradeResourcePermission(
		CalendarResourceLocalService calendarResourceLocalService,
		ResourceActionLocalService resourceActionLocalService,
		ResourceBlockLocalService resourceBlockLocalService,
		RoleLocalService roleLocalService) {

		_calendarResourceLocalService = calendarResourceLocalService;
		_resourceActionLocalService = resourceActionLocalService;
		_resourceBlockLocalService = resourceBlockLocalService;
		_roleLocalService = roleLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		upgradeGuestResourceBlockPermissions();
	}

	protected long getCalendarResourceUnsupportedActionsBitwiseValue()
		throws PortalException {

		int unsupportedBitwiseValue = 0;

		List<String> guestUnsupportedActions =
			getModelResourceGuestUnsupportedActions();

		for (String resourceActionId : _NEW_UNSUPPORTED_ACTION_IDS) {
			if (guestUnsupportedActions.contains(resourceActionId)) {
				ResourceAction resourceAction =
					_resourceActionLocalService.getResourceAction(
						_CALENDAR_RESOURCE_NAME, resourceActionId);

				unsupportedBitwiseValue |= resourceAction.getBitwiseValue();
			}
		}

		return unsupportedBitwiseValue;
	}

	protected List<String> getModelResourceGuestUnsupportedActions()
		throws UpgradeException {

		try {
			ResourceActionsImpl resourceActionsImpl = new ResourceActionsImpl();

			Class<?> clazz = getClass();

			ClassLoader classLoader = clazz.getClassLoader();

			for (String config : PropsValues.RESOURCE_ACTIONS_CONFIGS) {
				resourceActionsImpl.read(null, classLoader, config);
			}

			return resourceActionsImpl.getModelResourceGuestUnsupportedActions(
				_CALENDAR_RESOURCE_NAME);
		}
		catch (Exception e) {
			throw new UpgradeException(e);
		}
	}

	protected void upgradeGuestResourceBlockPermissions() throws Exception {
		long unsupportedBitwiseValue =
			getCalendarResourceUnsupportedActionsBitwiseValue();

		if (unsupportedBitwiseValue == 0) {
			return;
		}

		List<CalendarResource> calendarResources =
			_calendarResourceLocalService.getCalendarResources(
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (CalendarResource calendarResource : calendarResources) {
			Role guestRole = _roleLocalService.getRole(
				calendarResource.getCompanyId(), RoleConstants.GUEST);

			_resourceBlockLocalService.updateIndividualScopePermissions(
				calendarResource.getCompanyId(), calendarResource.getGroupId(),
				_CALENDAR_RESOURCE_NAME, calendarResource,
				guestRole.getRoleId(), unsupportedBitwiseValue,
				ResourceBlockConstants.OPERATOR_REMOVE);
		}
	}

	private static final String _CALENDAR_RESOURCE_NAME =
		"com.liferay.calendar.model.CalendarResource";

	private static final String[] _NEW_UNSUPPORTED_ACTION_IDS =
		{ActionKeys.PERMISSIONS, ActionKeys.VIEW};

	private final CalendarResourceLocalService _calendarResourceLocalService;
	private final ResourceActionLocalService _resourceActionLocalService;
	private final ResourceBlockLocalService _resourceBlockLocalService;
	private final RoleLocalService _roleLocalService;

}