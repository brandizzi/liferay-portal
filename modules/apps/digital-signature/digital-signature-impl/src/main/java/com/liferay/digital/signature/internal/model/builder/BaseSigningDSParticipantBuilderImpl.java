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

import com.liferay.digital.signature.internal.model.DSSignatureInfoImpl;
import com.liferay.digital.signature.internal.model.SignerDSParticipantImpl;
import com.liferay.digital.signature.model.DSParticipant;
import com.liferay.digital.signature.model.DSSignatureInfo;
import com.liferay.digital.signature.model.builder.DSParticipantBuilder;
import com.liferay.digital.signature.model.builder.SigningDSParticipantBuilder;
import com.liferay.digital.signature.model.field.DSField;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Michael C. Han
 */
public abstract class BaseSigningDSParticipantBuilderImpl
	<T extends DSParticipant>
		extends BaseDSParticipantBuilderImpl<T>
		implements SigningDSParticipantBuilder {

	public BaseSigningDSParticipantBuilderImpl(
		String emailAddress, String name, int routingOrder) {

		super(emailAddress, name, routingOrder);
	}

	@Override
	public <S extends DSParticipantBuilder> S addDSField(DSField<?> dsField) {
		_dsFields.add(dsField);

		return (S)this;
	}

	@Override
	public <S extends DSParticipantBuilder> S addDSFields(
		Collection<DSField<?>> dsFields) {

		_dsFields.addAll(dsFields);

		return (S)this;
	}

	@Override
	public <S extends DSParticipantBuilder> S addDSFields(
		DSField<?>... dsFields) {

		Collections.addAll(_dsFields, dsFields);

		return (S)this;
	}

	@Override
	public <S extends DSParticipantBuilder> S setAutoNavigation(
		Boolean autoNavigation) {

		_autoNavigation = autoNavigation;

		return (S)this;
	}

	@Override
	public <S extends DSParticipantBuilder> S setDefaultParticipant(
		Boolean defaultParticipant) {

		_defaultParticipant = defaultParticipant;

		return (S)this;
	}

	@Override
	public <S extends DSParticipantBuilder> S setSignatureInfo(
		String signatureFont, String signatureInitials, String signatureName) {

		_dsSignatureInfo = new DSSignatureInfoImpl() {
			{
				setSignatureFont(signatureFont);
				setSignatureInitials(signatureInitials);
				setSignatureName(signatureName);
			}
		};

		return (S)this;
	}

	@Override
	public <S extends DSParticipantBuilder> S setSignInEachLocation(
		Boolean signInEachLocation) {

		_signInEachLocation = signInEachLocation;

		return (S)this;
	}

	@Override
	protected T createDSParticipant() {
		SignerDSParticipantImpl signerDSParticipantImpl =
			createSignerDSParticipantImpl();

		signerDSParticipantImpl.addDSFields(getDSFields());
		signerDSParticipantImpl.setAutoNavigation(getAutoNavigation());
		signerDSParticipantImpl.setDefaultParticipant(getDefaultParticipant());
		signerDSParticipantImpl.setDSSignatureInfo(getDSSignatureInfo());
		signerDSParticipantImpl.setSignInEachLocation(getSignInEachLocation());

		return (T)signerDSParticipantImpl;
	}

	protected abstract SignerDSParticipantImpl createSignerDSParticipantImpl();

	protected Boolean getAutoNavigation() {
		return _autoNavigation;
	}

	protected Boolean getDefaultParticipant() {
		return _defaultParticipant;
	}

	protected Set<DSField<?>> getDSFields() {
		return _dsFields;
	}

	protected DSSignatureInfo getDSSignatureInfo() {
		return _dsSignatureInfo;
	}

	protected Boolean getSignInEachLocation() {
		return _signInEachLocation;
	}

	private Boolean _autoNavigation;
	private Boolean _defaultParticipant;
	private Set<DSField<?>> _dsFields = new HashSet<>();
	private DSSignatureInfo _dsSignatureInfo;
	private Boolean _signInEachLocation;

}