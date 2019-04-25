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

package com.liferay.digital.signature.internal.model;

import com.liferay.digital.signature.model.DSEmailNotificationSettings;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Michael C. Han
 */
public class DSEmailNotificationSettingsImpl
	implements DSEmailNotificationSettings {

	public void addBccEmailAddresses(Collection<String> bccEmailAddresses) {
		_bccEmailAddresses.addAll(bccEmailAddresses);
	}

	@Override
	public Set<String> getBccEmailAddresses() {
		return _bccEmailAddresses;
	}

	@Override
	public String getOverrideReplyEmailAddress() {
		return _overrideReplyEmailAddress;
	}

	@Override
	public String getOverrideReplyName() {
		return _overrideReplyName;
	}

	public void setOverrideReplyEmailAddress(String overrideReplyEmailAddress) {
		_overrideReplyEmailAddress = overrideReplyEmailAddress;
	}

	public void setOverrideReplyName(String overrideReplyName) {
		_overrideReplyName = overrideReplyName;
	}

	private final Set<String> _bccEmailAddresses = new HashSet<>();
	private String _overrideReplyEmailAddress;
	private String _overrideReplyName;

}