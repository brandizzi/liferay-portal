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

package com.liferay.digital.signature.internal.model.field.builder;

import com.liferay.digital.signature.model.field.builder.ApproveDSFieldBuilder;
import com.liferay.digital.signature.model.field.builder.CheckboxDSFieldBuilder;
import com.liferay.digital.signature.model.field.builder.CompanyNameDSFieldBuilder;
import com.liferay.digital.signature.model.field.builder.DSFieldBuildersFactory;
import com.liferay.digital.signature.model.field.builder.DateDSFieldBuilder;
import com.liferay.digital.signature.model.field.builder.DateSignedDSFieldBuilder;
import com.liferay.digital.signature.model.field.builder.DeclineDSFieldBuilder;
import com.liferay.digital.signature.model.field.builder.EmailAddressDSFieldBuilder;
import com.liferay.digital.signature.model.field.builder.FirstNameDSFieldBuilder;
import com.liferay.digital.signature.model.field.builder.FormulaDSFieldBuilder;
import com.liferay.digital.signature.model.field.builder.FullNameDSFieldBuilder;
import com.liferay.digital.signature.model.field.builder.InitialHereDSFieldBuilder;
import com.liferay.digital.signature.model.field.builder.LastNameDSFieldBuilder;
import com.liferay.digital.signature.model.field.builder.ListDSFieldBuilder;
import com.liferay.digital.signature.model.field.builder.NotarizeDSFieldBuilder;
import com.liferay.digital.signature.model.field.builder.NoteDSFieldBuilder;
import com.liferay.digital.signature.model.field.builder.NumberDSFieldBuilder;
import com.liferay.digital.signature.model.field.builder.ParticipantEmailAddressDSFieldBuilder;
import com.liferay.digital.signature.model.field.builder.PostalCodeDSFieldBuilder;
import com.liferay.digital.signature.model.field.builder.RadioDSFieldBuilder;
import com.liferay.digital.signature.model.field.builder.RadioGroupDSFieldBuilder;
import com.liferay.digital.signature.model.field.builder.SignHereDSFieldBuilder;
import com.liferay.digital.signature.model.field.builder.SignaturePackageKeyDSFieldBuilder;
import com.liferay.digital.signature.model.field.builder.SignerAttachmentDSFieldBuilder;
import com.liferay.digital.signature.model.field.builder.SocialSecurityNumberDSFieldBuilder;
import com.liferay.digital.signature.model.field.builder.TextDSFieldBuilder;
import com.liferay.digital.signature.model.field.builder.TitleDSFieldBuilder;
import com.liferay.digital.signature.model.field.builder.ViewDSFieldBuilder;

import org.osgi.service.component.annotations.Component;

/**
 * @author Michael C. Han
 */
@Component(service = DSFieldBuildersFactory.class)
public class DSFieldBuildersFactoryImpl implements DSFieldBuildersFactory {

	@Override
	public ApproveDSFieldBuilder createApproveDSFieldBuilder(
		String documentKey, String fieldKey, Integer pageNumber) {

		return new ApproveDSFieldBuilderImpl(documentKey, fieldKey, pageNumber);
	}

	@Override
	public CheckboxDSFieldBuilder createCheckboxDSFieldBuilder(
		String documentKey, String fieldKey, Integer pageNumber) {

		return new CheckboxDSFieldBuilderImpl(
			documentKey, fieldKey, pageNumber);
	}

	@Override
	public CompanyNameDSFieldBuilder createCompanyNameDSFieldBuilder(
		String documentKey, String fieldKey, Integer pageNumber) {

		return new CompanyNameDSFieldBuilderImpl(
			documentKey, fieldKey, pageNumber);
	}

	@Override
	public DateDSFieldBuilder createDateDSFieldBuilder(
		String documentKey, String fieldKey, Integer pageNumber) {

		return new DateDSFieldBuilderImpl(documentKey, fieldKey, pageNumber);
	}

	@Override
	public DateSignedDSFieldBuilder createDateSignedDSFieldBuilder(
		String documentKey, String fieldKey, Integer pageNumber) {

		return new DateSignedDSFieldBuilderImpl(
			documentKey, fieldKey, pageNumber);
	}

	@Override
	public DeclineDSFieldBuilder createDeclineDSFieldBuilder(
		String documentKey, String fieldKey, Integer pageNumber) {

		return new DeclineDSFieldBuilderImpl(documentKey, fieldKey, pageNumber);
	}

	@Override
	public EmailAddressDSFieldBuilder createEmailAddressDSFieldBuilder(
		String documentKey, String fieldKey, Integer pageNumber) {

		return new EmailAddressDSFieldBuilderImpl(
			documentKey, fieldKey, pageNumber);
	}

	@Override
	public FirstNameDSFieldBuilder createFirstNameDSFieldBuilder(
		String documentKey, String fieldKey, Integer pageNumber) {

		return new FirstNameDSFieldBuilderImpl(
			documentKey, fieldKey, pageNumber);
	}

	@Override
	public FormulaDSFieldBuilder createFormulaDSFieldBuilder(
		String documentKey, String fieldKey, Integer pageNumber) {

		return new FormulaDSFieldBuilderImpl(documentKey, fieldKey, pageNumber);
	}

	@Override
	public FullNameDSFieldBuilder createFullNameDSFieldBuilder(
		String documentKey, String fieldKey, Integer pageNumber) {

		return new FullNameDSFieldBuilderImpl(
			documentKey, fieldKey, pageNumber);
	}

	@Override
	public InitialHereDSFieldBuilder createInitialHereDSFieldBuilder(
		String documentKey, String fieldKey, Integer pageNumber) {

		return new InitialHereDSFieldBuilderImpl(
			documentKey, fieldKey, pageNumber);
	}

	@Override
	public LastNameDSFieldBuilder createLastNameDSFieldBuilder(
		String documentKey, String fieldKey, Integer pageNumber) {

		return new LastNameDSFieldBuilderImpl(
			documentKey, fieldKey, pageNumber);
	}

	@Override
	public ListDSFieldBuilder createListDSFieldBuilder(
		String documentKey, String fieldKey, Integer pageNumber) {

		return new ListDSFieldBuilderImpl(documentKey, fieldKey, pageNumber);
	}

	@Override
	public NotarizeDSFieldBuilder createNotarizeDSFieldBuilder(
		String documentKey, String fieldKey, Integer pageNumber) {

		return new NotarizeDSFieldBuilderImpl(
			documentKey, fieldKey, pageNumber);
	}

	@Override
	public NoteDSFieldBuilder createNoteDSFieldBuilder(
		String documentKey, String fieldKey, Integer pageNumber) {

		return new NoteDSFieldBuilderImpl(documentKey, fieldKey, pageNumber);
	}

	@Override
	public NumberDSFieldBuilder createNumberDSFieldBuilder(
		String documentKey, String fieldKey, Integer pageNumber) {

		return new NumberDSFieldBuilderImpl(documentKey, fieldKey, pageNumber);
	}

	@Override
	public ParticipantEmailAddressDSFieldBuilder
		createParticipantEmailAddressDSFieldBuilder(
			String documentKey, String fieldKey, Integer pageNumber) {

		return new ParticipantEmailAddressDSFieldBuilderImpl(
			documentKey, fieldKey, pageNumber);
	}

	@Override
	public PostalCodeDSFieldBuilder createPostalCodeDSFieldBuilder(
		String documentKey, String fieldKey, Integer pageNumber) {

		return new PostalCodeDSFieldBuilderImpl(
			documentKey, fieldKey, pageNumber);
	}

	@Override
	public RadioDSFieldBuilder createRadioDSFieldBuilder(
		String documentKey, String fieldKey, Integer pageNumber) {

		return new RadioDSFieldBuilderImpl(documentKey, fieldKey, pageNumber);
	}

	@Override
	public RadioGroupDSFieldBuilder createRadioGroupDSFieldBuilder(
		String documentKey, String fieldKey, Integer pageNumber) {

		return new RadioGroupDSFieldBuilderImpl(
			documentKey, fieldKey, pageNumber);
	}

	@Override
	public SignaturePackageKeyDSFieldBuilder
		createSignaturePackageKeyDSFieldBuilder(
			String documentKey, String fieldKey, Integer pageNumber) {

		return new SignaturePackageKeyDSFieldBuilderImpl(
			documentKey, fieldKey, pageNumber);
	}

	@Override
	public SignerAttachmentDSFieldBuilder createSignerAttachmentDSFieldBuilder(
		String documentKey, String fieldKey, Integer pageNumber) {

		return new SignerAttachmentDSFieldBuilderImpl(
			documentKey, fieldKey, pageNumber);
	}

	@Override
	public SignHereDSFieldBuilder createSignHereDSFieldBuilder(
		String documentKey, String fieldKey, Integer pageNumber) {

		return new SignHereDSFieldBuilderImpl(
			documentKey, fieldKey, pageNumber);
	}

	@Override
	public SocialSecurityNumberDSFieldBuilder
		createSocialSecurityNumberDSFieldBuilder(
			String documentKey, String fieldKey, Integer pageNumber) {

		return new SocialSecurityNumberDSFieldBuilderImpl(
			documentKey, fieldKey, pageNumber);
	}

	@Override
	public TextDSFieldBuilder createTextDSFieldBuilder(
		String documentKey, String fieldKey, Integer pageNumber) {

		return new TextDSFieldBuilderImpl(documentKey, fieldKey, pageNumber);
	}

	@Override
	public TitleDSFieldBuilder createTitleDSFieldBuilder(
		String documentKey, String fieldKey, Integer pageNumber) {

		return new TitleDSFieldBuilderImpl(documentKey, fieldKey, pageNumber);
	}

	@Override
	public ViewDSFieldBuilder createViewDSFieldBuilder(
		String documentKey, String fieldKey, Integer pageNumber) {

		return new ViewDSFieldBuilderImpl(documentKey, fieldKey, pageNumber);
	}

}