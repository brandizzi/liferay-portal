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

package com.liferay.digital.signature.internal.model.builder;

import com.liferay.digital.signature.internal.model.BaseDSParticipantImpl;
import com.liferay.digital.signature.model.DSEmailNotification;
import com.liferay.digital.signature.model.DSParticipant;
import com.liferay.digital.signature.model.builder.DSParticipantBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * @author Michael C. Han
 */
public abstract class BaseDSParticipantBuilderImpl<T extends DSParticipant>
	implements DSParticipantBuilder {

	public BaseDSParticipantBuilderImpl(
		String emailAddress, String name, int routingOrder) {

		_emailAddress = emailAddress;
		_name = name;
		_routingOrder = routingOrder;
	}

	public String getAccessCode() {
		return _accessCode;
	}

	public String getClientUserKey() {
		return _clientUserKey;
	}

	public Collection<String> getCustomFieldKeys() {
		return _customFieldKeys;
	}

	public DSEmailNotification getDSEmailNotification() {
		return _dsEmailNotification;
	}

	@Override
	public T getDSParticipant() {
		BaseDSParticipantImpl baseDSParticipantImpl =
			(BaseDSParticipantImpl)createDSParticipant();

		baseDSParticipantImpl.setAccessCode(getAccessCode());
		baseDSParticipantImpl.setClientUserKey(getClientUserKey());
		baseDSParticipantImpl.setCustomFieldKeys(getCustomFieldKeys());
		baseDSParticipantImpl.setDSEmailNotification(getDSEmailNotification());
		baseDSParticipantImpl.setNote(getNote());
		baseDSParticipantImpl.setParticipantKey(getParticipantKey());
		baseDSParticipantImpl.setRoleName(getRoleName());

		return (T)baseDSParticipantImpl;
	}

	public String getEmailAddress() {
		return _emailAddress;
	}

	public String getName() {
		return _name;
	}

	public String getNote() {
		return _note;
	}

	public String getParticipantKey() {
		return _participantKey;
	}

	public String getRoleName() {
		return _roleName;
	}

	public int getRoutingOrder() {
		return _routingOrder;
	}

	@Override
	public DSParticipantBuilder setAccessCode(String accessCode) {
		_accessCode = accessCode;

		return this;
	}

	@Override
	public DSParticipantBuilder setClientUserKey(String clientUserKey) {
		_clientUserKey = clientUserKey;

		return this;
	}

	@Override
	public DSParticipantBuilder setCustomFieldKeys(String... customFieldKeys) {
		Collections.addAll(_customFieldKeys, customFieldKeys);

		return this;
	}

	@Override
	public DSParticipantBuilder setDSEmailNotification(
		DSEmailNotification dsEmailNotification) {

		_dsEmailNotification = dsEmailNotification;

		return this;
	}

	@Override
	public DSParticipantBuilder setNote(String note) {
		_note = note;

		return this;
	}

	@Override
	public DSParticipantBuilder setParticipantKey(String participantKey) {
		_participantKey = participantKey;

		return this;
	}

	@Override
	public DSParticipantBuilder setRoleName(String roleName) {
		_roleName = roleName;

		return this;
	}

	protected abstract T createDSParticipant();

	private String _accessCode;
	private String _clientUserKey;
	private Collection<String> _customFieldKeys = new HashSet<>();
	private DSEmailNotification _dsEmailNotification;
	private final String _emailAddress;
	private final String _name;
	private String _note;
	private String _participantKey;
	private String _roleName;
	private final int _routingOrder;

}