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

import com.liferay.digital.signature.model.DSEmailNotification;
import com.liferay.digital.signature.model.DSParticipant;
import com.liferay.digital.signature.model.DSParticipantRole;

import java.util.Collection;

/**
 * @author Michael C. Han
 */
public abstract class BaseDSParticipantImpl implements DSParticipant {

	public BaseDSParticipantImpl(
		String emailAddress, String name, int routingOrder) {

		_emailAddress = emailAddress;
		_name = name;
		_routingOrder = routingOrder;
	}

	@Override
	public String getAccessCode() {
		return _accessCode;
	}

	@Override
	public String getClientUserKey() {
		return _clientUserKey;
	}

	@Override
	public Collection<String> getCustomFieldKeys() {
		return _customFieldKeys;
	}

	@Override
	public DSEmailNotification getDSEmailNotification() {
		return _dsEmailNotification;
	}

	@Override
	public DSParticipantRole getDSParticipantRole() {
		return _dsParticipantRole;
	}

	@Override
	public String getEmailAddress() {
		return _emailAddress;
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public String getNote() {
		return _note;
	}

	@Override
	public String getParticipantKey() {
		return _participantKey;
	}

	@Override
	public String getRoleName() {
		return _roleName;
	}

	@Override
	public int getRoutingOrder() {
		return _routingOrder;
	}

	public void setAccessCode(String accessCode) {
		_accessCode = accessCode;
	}

	public void setClientUserKey(String clientUserKey) {
		_clientUserKey = clientUserKey;
	}

	public void setCustomFieldKeys(Collection<String> customFieldKeys) {
		_customFieldKeys = customFieldKeys;
	}

	public void setDSEmailNotification(
		DSEmailNotification dsEmailNotification) {

		_dsEmailNotification = dsEmailNotification;
	}

	public void setNote(String note) {
		_note = note;
	}

	public void setParticipantKey(String participantKey) {
		_participantKey = participantKey;
	}

	public void setRoleName(String roleName) {
		_roleName = roleName;
	}

	protected void setDSParticipantRole(DSParticipantRole dsParticipantRole) {
		_dsParticipantRole = dsParticipantRole;
	}

	private String _accessCode;
	private String _clientUserKey;
	private Collection<String> _customFieldKeys;
	private DSEmailNotification _dsEmailNotification;
	private DSParticipantRole _dsParticipantRole;
	private final String _emailAddress;
	private final String _name;
	private String _note;
	private String _participantKey;
	private String _roleName;
	private final int _routingOrder;

}