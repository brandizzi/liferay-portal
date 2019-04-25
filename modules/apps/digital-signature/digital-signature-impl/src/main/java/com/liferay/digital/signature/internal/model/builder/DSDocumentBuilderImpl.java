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

import com.liferay.digital.signature.internal.model.DSDocumentImpl;
import com.liferay.digital.signature.internal.model.DSSupplementalDocumentInfoImpl;
import com.liferay.digital.signature.model.DSDocument;
import com.liferay.digital.signature.model.DSDocumentDisplay;
import com.liferay.digital.signature.model.DSSignerAcknowledgement;
import com.liferay.digital.signature.model.DSSupplementalDocumentInfo;
import com.liferay.digital.signature.model.builder.DSDocumentBuilder;

/**
 * @author Michael C. Han
 */
public class DSDocumentBuilderImpl implements DSDocumentBuilder {

	public DSDocumentBuilderImpl(String documentKey, String name) {
		_documentKey = documentKey;
		_name = name;
	}

	@Override
	public DSDocument getDSDocument() {
		return new DSDocumentImpl(_documentKey, _name) {
			{
				setAuthoritative(_authoritative);
				setBase64String(_base64String);
				setDescription(_description);
				setDocumentURL(_documentURL);
				setDSSupplementalDocumentInfo(_dsSupplementalDocumentInfo);
				setExtension(_extension);
			}
		};
	}

	@Override
	public DSDocumentBuilder setAuthoritative(Boolean authoritative) {
		_authoritative = authoritative;

		return this;
	}

	@Override
	public DSDocumentBuilder setBase64String(String base64String) {
		_base64String = base64String;

		return this;
	}

	@Override
	public DSDocumentBuilder setDescription(String description) {
		_description = description;

		return this;
	}

	@Override
	public DSDocumentBuilder setDocumentURL(String documentURL) {
		_documentURL = documentURL;

		return this;
	}

	@Override
	public DSDocumentBuilder setExtension(String extension) {
		_extension = extension;

		return this;
	}

	@Override
	public DSDocumentBuilder setSupplementalDocumentInfo(
		DSDocumentDisplay dsDocumentDisplay,
		DSSignerAcknowledgement dsSignerAcknowledgement,
		Boolean includeInDownload) {

		_dsSupplementalDocumentInfo = new DSSupplementalDocumentInfoImpl() {
			{
				setDSDocumentDisplay(dsDocumentDisplay);
				setDSSignerAcknowledgement(dsSignerAcknowledgement);
				setIncludeInDownload(includeInDownload);
			}
		};

		return this;
	}

	private Boolean _authoritative;
	private String _base64String;
	private String _description;
	private final String _documentKey;
	private String _documentURL;
	private DSSupplementalDocumentInfo _dsSupplementalDocumentInfo;
	private String _extension;
	private final String _name;

}