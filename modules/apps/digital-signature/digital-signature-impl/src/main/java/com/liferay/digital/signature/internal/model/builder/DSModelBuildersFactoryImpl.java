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

import com.liferay.digital.signature.model.builder.AgentDSParticipantBuilder;
import com.liferay.digital.signature.model.builder.CarbonCopyDSParticipantBuilder;
import com.liferay.digital.signature.model.builder.CertifiedDeliveryDSParticipantBuilder;
import com.liferay.digital.signature.model.builder.DSDocumentBuilder;
import com.liferay.digital.signature.model.builder.DSEmailNotificationBuilder;
import com.liferay.digital.signature.model.builder.DSModelBuildersFactory;
import com.liferay.digital.signature.model.builder.DSSignaturePackageBuilder;
import com.liferay.digital.signature.model.builder.EditorDSParticipantBuilder;
import com.liferay.digital.signature.model.builder.InPersonSignerDSParticipantBuilder;
import com.liferay.digital.signature.model.builder.InPersonSignerNotaryDSParticipantBuilder;
import com.liferay.digital.signature.model.builder.IntermediaryDSParticipantBuilder;
import com.liferay.digital.signature.model.builder.SealDSParticipantBuilder;
import com.liferay.digital.signature.model.builder.SignerDSParticipantBuilder;
import com.liferay.portal.kernel.uuid.PortalUUID;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(service = DSModelBuildersFactory.class)
public class DSModelBuildersFactoryImpl implements DSModelBuildersFactory {

	@Override
	public AgentDSParticipantBuilder createAgentDSParticipantBuilder(
		String emailAddress, String name, int routingOrder) {

		return new AgentDSParticipantBuilderImpl(
			emailAddress, name, routingOrder);
	}

	@Override
	public CarbonCopyDSParticipantBuilder createCarbonCopyDSParticipantBuilder(
		String emailAddress, String name, int routingOrder) {

		return new CarbonCopyDSParticipantBuilderImpl(
			emailAddress, name, routingOrder);
	}

	@Override
	public CertifiedDeliveryDSParticipantBuilder
		createCertifiedDeliveryDSParticipantBuilder(
			String emailAddress, String name, int routingOrder) {

		return new CertifiedDeliveryDSParticipantBuilderImpl(
			emailAddress, name, routingOrder);
	}

	@Override
	public DSDocumentBuilder createDSDocumentBuilder(
		String documentKey, String name) {

		return new DSDocumentBuilderImpl(documentKey, name);
	}

	@Override
	public DSEmailNotificationBuilder createDSEmailNotificationBuilder(
		String message, String subject) {

		return new DSEmailNotificationBuilderImpl(message, subject);
	}

	@Override
	public DSSignaturePackageBuilder createDSSignatureRequestBuilder() {
		return new DSSignaturePackageBuilderImpl(_portalUUID);
	}

	@Override
	public EditorDSParticipantBuilder createEditorDSParticipantBuilder(
		String emailAddress, String name, int routingOrder) {

		return new EditorDSParticipantBuilderImpl(
			emailAddress, name, routingOrder);
	}

	@Override
	public InPersonSignerDSParticipantBuilder
		createInPersonSignerDSParticipantBuilder(
			String hostEmailAddress, String hostName, int routingOrder,
			String signerEmailAddress, String signerName) {

		return new InPersonSignerDSParticipantBuilderImpl(
			hostEmailAddress, hostName, routingOrder, signerEmailAddress,
			signerName);
	}

	@Override
	public InPersonSignerNotaryDSParticipantBuilder
		createInPersonSignerNotaryDSParticipantBuilder(
			String emailAddress, String name, String notaryEmailAddress,
			String notaryName, String notaryParticipantKey, int routingOrder) {

		return new InPersonSignerNotaryDSParticipantBuilderImpl(
			emailAddress, name, notaryEmailAddress, notaryName,
			notaryParticipantKey, routingOrder);
	}

	@Override
	public IntermediaryDSParticipantBuilder
		createIntermediaryDSParticipantBuilder(
			String emailAddress, String name, int routingOrder) {

		return new IntermediaryDSParticipantBuilderImpl(
			emailAddress, name, routingOrder);
	}

	@Override
	public SealDSParticipantBuilder createSealDSParticipantBuilder(
		String emailAddress, String name, String participantKey,
		int routingOrder) {

		return new SealDSParticipantBuilderImpl(
			emailAddress, name, participantKey, routingOrder);
	}

	@Override
	public SignerDSParticipantBuilder createSignerDSParticipantBuilder(
		String emailAddress, int routingOrder, String name) {

		return new SignerDSParticipantBuilderImpl(
			emailAddress, name, routingOrder);
	}

	protected void setPortalUUID(PortalUUID portalUUID) {
		_portalUUID = portalUUID;
	}

	@Reference
	private PortalUUID _portalUUID;

}