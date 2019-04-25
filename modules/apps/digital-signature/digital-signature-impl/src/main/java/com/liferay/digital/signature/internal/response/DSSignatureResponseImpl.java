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

package com.liferay.digital.signature.internal.response;

import com.liferay.digital.signature.common.DSSignaturePackageStatus;
import com.liferay.digital.signature.response.DSSignatureResponse;

import java.time.ZonedDateTime;

/**
 * @author Michael C. Han
 */
public class DSSignatureResponseImpl implements DSSignatureResponse {

	public DSSignatureResponseImpl(
		String dsSignatureRequestKey, String externalReferenceKey) {

		_dsSignatureRequestKey = dsSignatureRequestKey;
		_externalReferenceKey = externalReferenceKey;
	}

	@Override
	public String getDSSignatureRequestKey() {
		return _dsSignatureRequestKey;
	}

	@Override
	public DSSignaturePackageStatus getDSSignatureRequestStatus() {
		return _dsSignaturePackageStatus;
	}

	@Override
	public String getErrorCode() {
		return _errorCode;
	}

	@Override
	public String getErrorDebugMessage() {
		return _errorDebugMessage;
	}

	@Override
	public String getErrorMessage() {
		return _errorMessage;
	}

	@Override
	public String getExternalReferenceKey() {
		return _externalReferenceKey;
	}

	@Override
	public String getExternalReferenceURI() {
		return _externalReferenceURI;
	}

	@Override
	public ZonedDateTime getTimestampZonedDateTime() {
		return _timestampZonedDateTime;
	}

	public void setDSSignatureRequestStatus(
		DSSignaturePackageStatus dsSignaturePackageStatus) {

		_dsSignaturePackageStatus = dsSignaturePackageStatus;
	}

	public void setErrorCode(String errorCode) {
		_errorCode = errorCode;
	}

	public void setErrorDebugMessage(String errorDebugMessage) {
		_errorDebugMessage = errorDebugMessage;
	}

	public void setErrorMessage(String errorMessage) {
		_errorMessage = errorMessage;
	}

	public void setExternalReferenceURI(String externalReferenceURI) {
		_externalReferenceURI = externalReferenceURI;
	}

	public void setTimestampZonedDateTime(
		ZonedDateTime timestampZonedDateTime) {

		_timestampZonedDateTime = timestampZonedDateTime;
	}

	private DSSignaturePackageStatus _dsSignaturePackageStatus;
	private final String _dsSignatureRequestKey;
	private String _errorCode;
	private String _errorDebugMessage;
	private String _errorMessage;
	private final String _externalReferenceKey;
	private String _externalReferenceURI;
	private ZonedDateTime _timestampZonedDateTime;

}