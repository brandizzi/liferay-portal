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

package com.liferay.portal.search.web.internal.custom.filter.display.context;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Igor Fabiano Nazar
 * @author Luan Maoski
 */
public class CustomFilterDisplayBuilder {

	public CustomFilterDisplayContext build() {
		CustomFilterDisplayContext customFilterDisplayContext =
			new CustomFilterDisplayContext();

		customFilterDisplayContext.setDisplayCaption(getDisplayCaption());
		customFilterDisplayContext.setParameterName(_parameterName);
		customFilterDisplayContext.setParameterValue(getFirstParameterValue());
		customFilterDisplayContext.setParameterValues(_parameterValues);
		customFilterDisplayContext.setIsInvisible(_invisible);

		return customFilterDisplayContext;
	}

	public void setCustomDisplayCaption(String customDisplayCaption) {
		_customDisplayCaption = customDisplayCaption;
	}

	public void setIsInvisible(boolean invisible) {
		_invisible = invisible;
	}

	public void setParameterName(String parameterName) {
		_parameterName = parameterName;
	}

	public void setParameterValue(String parameterValue) {
		parameterValue = StringUtil.trim(
			Objects.requireNonNull(parameterValue));

		if (parameterValue.isEmpty()) {
			return;
		}

		_parameterValues = Collections.singletonList(parameterValue);
	}

	public void setParameterValues(List<String> parameterValues) {
		_parameterValues = parameterValues;
	}

	protected String getDisplayCaption() {
		return _customDisplayCaption;
	}

	protected String getFirstParameterValue() {
		if (_parameterValues.isEmpty()) {
			return StringPool.BLANK;
		}

		return _parameterValues.get(0);
	}

	private String _customDisplayCaption;
	private boolean _invisible;
	private String _parameterName;
	private List<String> _parameterValues = Collections.emptyList();

}