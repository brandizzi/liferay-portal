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

import com.liferay.digital.signature.model.DSParticipant;
import com.liferay.digital.signature.model.builder.DSParticipantBuilder;
import com.liferay.digital.signature.model.builder.ParticipantModifyingDSParticipantBuilder;

/**
 * @author Michael C. Han
 */
public abstract class BaseParticipantModifyingDSParticipantBuilderImpl
	<T extends DSParticipant>
		extends BaseDSParticipantBuilderImpl<T>
		implements ParticipantModifyingDSParticipantBuilder {

	public BaseParticipantModifyingDSParticipantBuilderImpl(
		String emailAddress, String name, int routingOrder) {

		super(emailAddress, name, routingOrder);
	}

	@Override
	public <S extends DSParticipantBuilder> S
		setCanEditParticipantEmailAddresses(
			Boolean canEditParticipantEmailAddresses) {

		_canEditParticipantEmailAddresses = canEditParticipantEmailAddresses;

		return (S)this;
	}

	@Override
	public <S extends DSParticipantBuilder> S setCanEditParticipantNames(
		Boolean canEditParticipantNames) {

		_canEditParticipantNames = canEditParticipantNames;

		return (S)this;
	}

	protected Boolean getCanEditParticipantEmailAddresses() {
		return _canEditParticipantEmailAddresses;
	}

	protected Boolean getCanEditParticipantNames() {
		return _canEditParticipantNames;
	}

	private Boolean _canEditParticipantEmailAddresses;
	private Boolean _canEditParticipantNames;

}