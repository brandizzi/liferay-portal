/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.portal.search.tuning.gsearch.impl.internal.request.parameter.contributor;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.search.tuning.gsearch.configuration.constants.ParameterConfigurationKeys;
import com.liferay.portal.search.tuning.gsearch.parameter.DoubleParameter;
import com.liferay.portal.search.tuning.gsearch.parameter.SearchParameterData;
import com.liferay.portal.search.tuning.gsearch.util.GSearchUtil;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

/**
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, property = "type=double",
	service = RequestParameterContributor.class
)
public class DoubleParameterContributor implements RequestParameterContributor {

	@Override
	public void contribute(
		HttpServletRequest httpServletRequest,
		SearchParameterData searchParameterData,
		JSONObject configurationJsonObject) {

		String parameterName = configurationJsonObject.getString(
			ParameterConfigurationKeys.PARAMETER_NAME);

		String valueString = ParamUtil.getString(
			httpServletRequest, parameterName);

		Optional<Double> valueOptional = GSearchUtil.stringToDoubleOptional(
			valueString);

		if (!valueOptional.isPresent()) {
			valueOptional = GSearchUtil.stringToDoubleOptional(
				configurationJsonObject.getString(
					ParameterConfigurationKeys.DEFAULT));
		}

		if (!valueOptional.isPresent()) {
			return;
		}

		double value = valueOptional.get();

		Optional<Double> minValue = GSearchUtil.stringToDoubleOptional(
			configurationJsonObject.getString(
				ParameterConfigurationKeys.MIN_VALUE));

		if (minValue.isPresent() &&
			(Double.compare(value, minValue.get()) < 0)) {

			if (_log.isWarnEnabled()) {
				_log.warn(minValue.get() + " is below the minimum.");
			}

			value = minValue.get();
		}

		Optional<Integer> maxValue = GSearchUtil.stringToIntegerOptional(
			configurationJsonObject.getString(
				ParameterConfigurationKeys.MAX_VALUE));

		if (maxValue.isPresent() &&
			(Double.compare(value, maxValue.get()) > 0)) {

			if (_log.isWarnEnabled()) {
				_log.warn(maxValue.get() + " is above the maximum.");
			}

			value = maxValue.get();
		}

		String parameterRole = configurationJsonObject.getString(
			ParameterConfigurationKeys.ROLE);

		searchParameterData.addParameter(
			new DoubleParameter(
				parameterName, parameterRole,
				"${request." + parameterName + "}", value));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DoubleParameterContributor.class);

}