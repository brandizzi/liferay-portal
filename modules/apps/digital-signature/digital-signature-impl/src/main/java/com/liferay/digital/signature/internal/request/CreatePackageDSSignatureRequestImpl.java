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

package com.liferay.digital.signature.internal.request;

import com.liferay.digital.signature.common.DSSignaturePackageStatus;
import com.liferay.digital.signature.model.DSSessionKey;
import com.liferay.digital.signature.model.DSSignaturePackage;
import com.liferay.digital.signature.request.CreatePackageDSSignatureRequest;

/**
 * @author Michael C. Han
 */
public class CreatePackageDSSignatureRequestImpl
	implements CreatePackageDSSignatureRequest {

	public CreatePackageDSSignatureRequestImpl(
		DSSessionKey dsSessionKey, DSSignaturePackage dsSignaturePackage,
		DSSignaturePackageStatus dsSignaturePackageStatus) {

		_dsSessionKey = dsSessionKey;
		_dsSignaturePackage = dsSignaturePackage;
		_dsSignaturePackageStatus = dsSignaturePackageStatus;
	}

	@Override
	public DSSessionKey getDSSessionKey() {
		return _dsSessionKey;
	}

	@Override
	public DSSignaturePackage getDSSignaturePackage() {
		return _dsSignaturePackage;
	}

	@Override
	public DSSignaturePackageStatus getDSSignaturePackageStatus() {
		return _dsSignaturePackageStatus;
	}

	private final DSSessionKey _dsSessionKey;
	private final DSSignaturePackage _dsSignaturePackage;
	private final DSSignaturePackageStatus _dsSignaturePackageStatus;

}