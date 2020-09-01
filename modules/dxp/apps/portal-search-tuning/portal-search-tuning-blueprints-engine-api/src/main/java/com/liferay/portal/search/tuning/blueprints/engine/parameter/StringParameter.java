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

package com.liferay.portal.search.tuning.blueprints.engine.parameter;

import com.liferay.portal.search.tuning.blueprints.constants.json.values.EvaluationType;
import com.liferay.portal.search.tuning.blueprints.engine.exception.ParameterEvaluationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Petteri Karttunen
 */
public class StringParameter implements Parameter {

	public StringParameter(
		String name, String configurationVariable, String value) {

		_name = name;
		_configurationVariable = configurationVariable;
		_value = value;
	}

	@Override
	public boolean accept(ConditionEvaluationVisitor visitor)
		throws ParameterEvaluationException {

		return visitor.visit(this);
	}

	@Override
	public String accept(ToStringVisitor visitor, Map<String, String> options) throws Exception {
		return visitor.visit(this, options);
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public List<EvaluationType> getSupportedEvaluationTypes() {
		List<EvaluationType> evaluationTypes = new ArrayList<>();

		evaluationTypes.add(EvaluationType.EQ);
		evaluationTypes.add(EvaluationType.EXISTS);
		evaluationTypes.add(EvaluationType.NE);
		evaluationTypes.add(EvaluationType.IN);
		evaluationTypes.add(EvaluationType.NOT_IN);
		evaluationTypes.add(EvaluationType.ANY_WORD_IN);
		evaluationTypes.add(EvaluationType.NO_WORD_IN);

		return evaluationTypes;
	}

	@Override
	public String getTemplateVariable() {
		return _configurationVariable;
	}

	@Override
	public String getValue() {
		return _value;
	}

	@Override
	public String toString() {
		return _name + "=" + _value;
	}

	private final String _configurationVariable;
	private final String _name;
	private final String _value;

}