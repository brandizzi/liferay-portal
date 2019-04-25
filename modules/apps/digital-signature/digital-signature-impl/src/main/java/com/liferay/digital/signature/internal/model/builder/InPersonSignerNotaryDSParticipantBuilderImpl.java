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

import com.liferay.digital.signature.internal.model.DSNotaryInfoImpl;
import com.liferay.digital.signature.internal.model.InPersonSignerNotaryDSParticipantImpl;
import com.liferay.digital.signature.internal.model.SignerDSParticipantImpl;
import com.liferay.digital.signature.model.InPersonSignerNotaryDSParticipant;
import com.liferay.digital.signature.model.builder.InPersonSignerNotaryDSParticipantBuilder;

/**
 * @author Michael C. Han
 */
public class InPersonSignerNotaryDSParticipantBuilderImpl
	extends BaseSigningDSParticipantBuilderImpl
		<InPersonSignerNotaryDSParticipant>
	implements InPersonSignerNotaryDSParticipantBuilder {

	public InPersonSignerNotaryDSParticipantBuilderImpl(
		String emailAddress, String name, String notaryEmailAddress,
		String notaryName, String notaryParticipantKey, int routingOrder) {

		super(emailAddress, name, routingOrder);

		_notaryEmailAddress = notaryEmailAddress;
		_notaryName = notaryName;
		_notaryParticipantKey = notaryParticipantKey;
	}

	@Override
	protected SignerDSParticipantImpl createSignerDSParticipantImpl() {
		return new InPersonSignerNotaryDSParticipantImpl(
			new DSNotaryInfoImpl(
				_notaryEmailAddress, _notaryName, _notaryParticipantKey),
			getEmailAddress(), getName(), getRoutingOrder());
	}

	private final String _notaryEmailAddress;
	private final String _notaryName;
	private final String _notaryParticipantKey;

}