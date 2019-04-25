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

import com.liferay.digital.signature.internal.model.CarbonCopyDSParticipantImpl;
import com.liferay.digital.signature.model.CarbonCopyDSParticipant;
import com.liferay.digital.signature.model.builder.CarbonCopyDSParticipantBuilder;

/**
 * @author Michael C. Han
 */
public class CarbonCopyDSParticipantBuilderImpl
	extends BaseDSParticipantBuilderImpl<CarbonCopyDSParticipant>
	implements CarbonCopyDSParticipantBuilder {

	public CarbonCopyDSParticipantBuilderImpl(
		String emailAddress, String name, int routingOrder) {

		super(emailAddress, name, routingOrder);
	}

	@Override
	protected CarbonCopyDSParticipant createDSParticipant() {
		return new CarbonCopyDSParticipantImpl(
			getEmailAddress(), getName(), getRoutingOrder());
	}

}