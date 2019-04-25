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

import com.liferay.digital.signature.internal.model.DSEmailNotificationImpl;
import com.liferay.digital.signature.internal.model.DSEmailNotificationSettingsImpl;
import com.liferay.digital.signature.model.DSEmailNotification;
import com.liferay.digital.signature.model.builder.DSEmailNotificationBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Michael C. Han
 */
public class DSEmailNotificationBuilderImpl
	implements DSEmailNotificationBuilder {

	public DSEmailNotificationBuilderImpl(String message, String subject) {
		_message = message;
		_subject = subject;
	}

	@Override
	public DSEmailNotificationBuilderImpl addBccEmailAddresses(
		Collection<String> bccEmailAddresses) {

		_bccEmailAddresses.addAll(bccEmailAddresses);

		return this;
	}

	@Override
	public DSEmailNotificationBuilder addBccEmailAddresses(
		String... bccEmailAddresses) {

		Collections.addAll(_bccEmailAddresses, bccEmailAddresses);

		return this;
	}

	@Override
	public DSEmailNotification getDSEmailNotification() {
		return new DSEmailNotificationImpl(_message, _subject) {
			{
				setDSEmailNotificationSettings(
					new DSEmailNotificationSettingsImpl() {
						{
							addBccEmailAddresses(_bccEmailAddresses);
							setOverrideReplyEmailAddress(
								_overrideReplyEmailAddress);
							setOverrideReplyName(_overrideReplyName);
							setSupportedLanguage(_supportedLanguage);
						}
					});
			}
		};
	}

	@Override
	public DSEmailNotificationBuilder setOverrideReplyEmailAddress(
		String overrideReplyEmailAddress) {

		_overrideReplyEmailAddress = overrideReplyEmailAddress;

		return this;
	}

	@Override
	public DSEmailNotificationBuilder setOverrideReplyName(
		String overrideReplyName) {

		_overrideReplyName = overrideReplyName;

		return this;
	}

	@Override
	public DSEmailNotificationBuilder setSupportedLanguage(
		String supportedLanguage) {

		_supportedLanguage = supportedLanguage;

		return this;
	}

	private final Set<String> _bccEmailAddresses = new HashSet<>();
	private final String _message;
	private String _overrideReplyEmailAddress;
	private String _overrideReplyName;
	private final String _subject;
	private String _supportedLanguage;

}