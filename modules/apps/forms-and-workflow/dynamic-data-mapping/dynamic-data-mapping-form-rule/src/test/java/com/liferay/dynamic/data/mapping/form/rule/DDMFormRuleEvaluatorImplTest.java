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

package com.liferay.dynamic.data.mapping.form.rule;

import com.liferay.dynamic.data.mapping.expression.internal.DDMExpressionFactoryImpl;
import com.liferay.dynamic.data.mapping.form.rule.internal.DDMFormRuleEvaluatorImpl;
import com.liferay.dynamic.data.mapping.form.rule.internal.functions.CallFunction;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldRule;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldRuleType;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.LocaleUtil;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Leonardo Barros
 */
@RunWith(PowerMockRunner.class)
public class DDMFormRuleEvaluatorImplTest extends PowerMockito {

	@Before
	public void setUp() throws Exception {
		setUpLanguageUtil();
	}

	@Test
	public void testEvaluateBetween() throws Exception {
		DDMForm ddmForm = new DDMForm();

		DDMFormField fieldDDMFormField0 = new DDMFormField("field0", "text");

		ddmForm.addDDMFormField(fieldDDMFormField0);

		DDMFormField fieldDDMFormField1 = new DDMFormField("field1", "text");

		DDMFormFieldRule ddmFormFieldRule1 = new DDMFormFieldRule(
			"between(field1,10,field0)", DDMFormFieldRuleType.READ_ONLY);

		fieldDDMFormField1.addDDMFormFieldRule(ddmFormFieldRule1);

		ddmForm.addDDMFormField(fieldDDMFormField1);

		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		DDMFormFieldValue fieldDDMFormFieldValue0 =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_instanceId", "field0", new UnlocalizedValue("30"));

		List<DDMFormFieldValue> ddmFormFieldValues = new ArrayList<>();

		ddmFormFieldValues.add(fieldDDMFormFieldValue0);

		DDMFormFieldValue fieldDDMFormFieldValue1 =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field1_instanceId", "field1", new UnlocalizedValue("15"));

		ddmFormFieldValues.add(fieldDDMFormFieldValue1);

		ddmFormValues.setDDMFormFieldValues(ddmFormFieldValues);

		DDMFormRuleEvaluator ddmFormRuleEvaluator =
			createDDMFormRuleEvaluator();

		List<DDMFormFieldRuleEvaluationResult>
			ddmFormFieldRuleEvaluationResults = ddmFormRuleEvaluator.evaluate(
				ddmForm, ddmFormValues, LocaleUtil.US);

		Assert.assertEquals(2, ddmFormFieldRuleEvaluationResults.size());

		for (DDMFormFieldRuleEvaluationResult ddmFormFieldRuleEvaluationResult :
				ddmFormFieldRuleEvaluationResults) {

			if (ddmFormFieldRuleEvaluationResult.getName().equals("field0")) {
				Assert.assertEquals(
					"30", ddmFormFieldRuleEvaluationResult.getValue());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isVisible());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isValid());
				Assert.assertFalse(
					ddmFormFieldRuleEvaluationResult.isReadOnly());
			}
			else if(
				ddmFormFieldRuleEvaluationResult.getName().equals("field1")) {

				Assert.assertEquals(
					"15", ddmFormFieldRuleEvaluationResult.getValue());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isVisible());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isValid());
				Assert.assertTrue(
					ddmFormFieldRuleEvaluationResult.isReadOnly());
			}
		}
	}

	@Test
	public void testEvaluateCalculatedValue() throws Exception {
		DDMForm ddmForm = new DDMForm();

		DDMFormField fieldDDMFormField0 = new DDMFormField("field0", "text");

		ddmForm.addDDMFormField(fieldDDMFormField0);

		DDMFormFieldRule ddmFormFieldRule1 = new DDMFormFieldRule(
			"field1 * field2", DDMFormFieldRuleType.VALUE);

		fieldDDMFormField0.addDDMFormFieldRule(ddmFormFieldRule1);

		DDMFormField fieldDDMFormField1 = new DDMFormField("field1", "text");

		ddmForm.addDDMFormField(fieldDDMFormField1);

		DDMFormField fieldDDMFormField2 = new DDMFormField("field2", "text");

		ddmForm.addDDMFormField(fieldDDMFormField2);

		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		DDMFormFieldValue fieldDDMFormFieldValue0 =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_instanceId", "field0", new UnlocalizedValue("test"));

		List<DDMFormFieldValue> ddmFormFieldValues = new ArrayList<>();

		ddmFormFieldValues.add(fieldDDMFormFieldValue0);

		DDMFormFieldValue fieldDDMFormFieldValue1 =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field1_instanceId", "field1", new UnlocalizedValue("10"));

		ddmFormFieldValues.add(fieldDDMFormFieldValue1);

		DDMFormFieldValue fieldDDMFormFieldValue2 =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field2_instanceId", "field2", new UnlocalizedValue("5"));

		ddmFormFieldValues.add(fieldDDMFormFieldValue2);

		ddmFormValues.setDDMFormFieldValues(ddmFormFieldValues);

		DDMFormRuleEvaluator ddmFormRuleEvaluator =
			createDDMFormRuleEvaluator();

		List<DDMFormFieldRuleEvaluationResult>
			ddmFormFieldRuleEvaluationResults = ddmFormRuleEvaluator.evaluate(
				ddmForm, ddmFormValues, LocaleUtil.US);

		Assert.assertEquals(3, ddmFormFieldRuleEvaluationResults.size());

		for (DDMFormFieldRuleEvaluationResult ddmFormFieldRuleEvaluationResult :
				ddmFormFieldRuleEvaluationResults) {

			if (ddmFormFieldRuleEvaluationResult.getName().equals("field0")) {
				Assert.assertEquals(
					"50.0", ddmFormFieldRuleEvaluationResult.getValue());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isVisible());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isValid());
				Assert.assertFalse(
					ddmFormFieldRuleEvaluationResult.isReadOnly());
			}
			else if(
				ddmFormFieldRuleEvaluationResult.getName().equals("field1")) {

				Assert.assertEquals(
					"10", ddmFormFieldRuleEvaluationResult.getValue());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isVisible());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isValid());
				Assert.assertFalse(
					ddmFormFieldRuleEvaluationResult.isReadOnly());
			}
			else if(
				ddmFormFieldRuleEvaluationResult.getName().equals("field2")) {

				Assert.assertEquals(
					"5", ddmFormFieldRuleEvaluationResult.getValue());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isVisible());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isValid());
				Assert.assertFalse(
					ddmFormFieldRuleEvaluationResult.isReadOnly());
			}
		}
	}

	@Test
	public void testEvaluateCalculatedValueWithNoInput() throws Exception {
		DDMForm ddmForm = new DDMForm();

		DDMFormField fieldDDMFormField0 = new DDMFormField("field0", "text");

		ddmForm.addDDMFormField(fieldDDMFormField0);

		DDMFormFieldRule ddmFormFieldRule1 = new DDMFormFieldRule(
			"field1 * field2", DDMFormFieldRuleType.VALUE);

		fieldDDMFormField0.addDDMFormFieldRule(ddmFormFieldRule1);

		DDMFormField fieldDDMFormField1 = new DDMFormField("field1", "text");

		ddmForm.addDDMFormField(fieldDDMFormField1);

		DDMFormField fieldDDMFormField2 = new DDMFormField("field2", "text");

		ddmForm.addDDMFormField(fieldDDMFormField2);

		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		DDMFormFieldValue fieldDDMFormFieldValue0 =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_instanceId", "field0", new UnlocalizedValue("test"));

		List<DDMFormFieldValue> ddmFormFieldValues = new ArrayList<>();

		ddmFormFieldValues.add(fieldDDMFormFieldValue0);

		DDMFormFieldValue fieldDDMFormFieldValue1 =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field1_instanceId", "field1", new UnlocalizedValue("10"));

		ddmFormFieldValues.add(fieldDDMFormFieldValue1);

		DDMFormFieldValue fieldDDMFormFieldValue2 =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field2_instanceId", "field2", new UnlocalizedValue(""));

		ddmFormFieldValues.add(fieldDDMFormFieldValue2);

		ddmFormValues.setDDMFormFieldValues(ddmFormFieldValues);

		DDMFormRuleEvaluator ddmFormRuleEvaluator =
			createDDMFormRuleEvaluator();

		List<DDMFormFieldRuleEvaluationResult>
			ddmFormFieldRuleEvaluationResults = ddmFormRuleEvaluator.evaluate(
				ddmForm, ddmFormValues, LocaleUtil.US);

		Assert.assertEquals(3, ddmFormFieldRuleEvaluationResults.size());

		for (DDMFormFieldRuleEvaluationResult ddmFormFieldRuleEvaluationResult :
				ddmFormFieldRuleEvaluationResults) {

			if (ddmFormFieldRuleEvaluationResult.getName().equals("field0")) {
				Assert.assertEquals(
					LanguageUtil.format(
						LocaleUtil.US, "the-value-of-field-was-not-entered-x",
						"field2", false),
					ddmFormFieldRuleEvaluationResult.getErrorMessage());
				Assert.assertEquals(
					"", ddmFormFieldRuleEvaluationResult.getValue());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isVisible());
				Assert.assertFalse(ddmFormFieldRuleEvaluationResult.isValid());
				Assert.assertFalse(
					ddmFormFieldRuleEvaluationResult.isReadOnly());
			}
			else if(
				ddmFormFieldRuleEvaluationResult.getName().equals("field1")) {

				Assert.assertEquals(
					"10", ddmFormFieldRuleEvaluationResult.getValue());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isVisible());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isValid());
				Assert.assertFalse(
					ddmFormFieldRuleEvaluationResult.isReadOnly());
			}
			else if(
				ddmFormFieldRuleEvaluationResult.getName().equals("field2")) {

				Assert.assertEquals(
					"", ddmFormFieldRuleEvaluationResult.getValue());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isVisible());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isValid());
				Assert.assertFalse(
					ddmFormFieldRuleEvaluationResult.isReadOnly());
			}
		}
	}

	@Test
	public void testEvaluateContains() throws Exception {
		DDMForm ddmForm = new DDMForm();

		DDMFormField fieldDDMFormField0 = new DDMFormField("field0", "text");

		ddmForm.addDDMFormField(fieldDDMFormField0);

		DDMFormFieldRule ddmFormFieldRule0 = new DDMFormFieldRule(
			"contains(field1,\"val\")", DDMFormFieldRuleType.READ_ONLY);

		fieldDDMFormField0.addDDMFormFieldRule(ddmFormFieldRule0);

		DDMFormField fieldDDMFormField1 = new DDMFormField("field1", "text");

		ddmForm.addDDMFormField(fieldDDMFormField1);

		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		DDMFormFieldValue fieldDDMFormFieldValue0 =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_instanceId", "field0", new UnlocalizedValue("field0"));

		List<DDMFormFieldValue> ddmFormFieldValues = new ArrayList<>();

		ddmFormFieldValues.add(fieldDDMFormFieldValue0);

		DDMFormFieldValue fieldDDMFormFieldValue1 =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field1_instanceId", "field1", new UnlocalizedValue("value1"));

		ddmFormFieldValues.add(fieldDDMFormFieldValue1);

		ddmFormValues.setDDMFormFieldValues(ddmFormFieldValues);

		DDMFormRuleEvaluator ddmFormRuleEvaluator =
			createDDMFormRuleEvaluator();

		List<DDMFormFieldRuleEvaluationResult>
			ddmFormFieldRuleEvaluationResults = ddmFormRuleEvaluator.evaluate(
				ddmForm, ddmFormValues, LocaleUtil.US);

		Assert.assertEquals(2, ddmFormFieldRuleEvaluationResults.size());

		for (DDMFormFieldRuleEvaluationResult ddmFormFieldRuleEvaluationResult :
				ddmFormFieldRuleEvaluationResults) {

			if (ddmFormFieldRuleEvaluationResult.getName().equals("field0")) {
				Assert.assertEquals(
					"field0", ddmFormFieldRuleEvaluationResult.getValue());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isVisible());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isValid());
				Assert.assertTrue(
					ddmFormFieldRuleEvaluationResult.isReadOnly());
			}
			else if(
				ddmFormFieldRuleEvaluationResult.getName().equals("field1")) {

				Assert.assertEquals(
					"value1", ddmFormFieldRuleEvaluationResult.getValue());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isVisible());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isValid());
				Assert.assertFalse(
					ddmFormFieldRuleEvaluationResult.isReadOnly());
			}
		}
	}

	@Test
	public void testEvaluateEquals() throws Exception {
		DDMForm ddmForm = new DDMForm();

		DDMFormField fieldDDMFormField0 = new DDMFormField("field0", "text");

		ddmForm.addDDMFormField(fieldDDMFormField0);

		DDMFormFieldRule ddmFormFieldRule0 = new DDMFormFieldRule(
			"equals(field1,\"value1\")", DDMFormFieldRuleType.READ_ONLY);

		fieldDDMFormField0.addDDMFormFieldRule(ddmFormFieldRule0);

		DDMFormField fieldDDMFormField1 = new DDMFormField("field1", "text");

		ddmForm.addDDMFormField(fieldDDMFormField1);

		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		DDMFormFieldValue fieldDDMFormFieldValue0 =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_instanceId", "field0", new UnlocalizedValue("field0"));

		List<DDMFormFieldValue> ddmFormFieldValues = new ArrayList<>();

		ddmFormFieldValues.add(fieldDDMFormFieldValue0);

		DDMFormFieldValue fieldDDMFormFieldValue1 =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field1_instanceId", "field1", new UnlocalizedValue("value1"));

		ddmFormFieldValues.add(fieldDDMFormFieldValue1);

		ddmFormValues.setDDMFormFieldValues(ddmFormFieldValues);

		DDMFormRuleEvaluator ddmFormRuleEvaluator =
			createDDMFormRuleEvaluator();

		List<DDMFormFieldRuleEvaluationResult>
			ddmFormFieldRuleEvaluationResults = ddmFormRuleEvaluator.evaluate(
				ddmForm, ddmFormValues, LocaleUtil.US);

		Assert.assertEquals(2, ddmFormFieldRuleEvaluationResults.size());

		for (DDMFormFieldRuleEvaluationResult ddmFormFieldRuleEvaluationResult :
				ddmFormFieldRuleEvaluationResults) {

			if (ddmFormFieldRuleEvaluationResult.getName().equals("field0")) {
				Assert.assertEquals(
					"field0", ddmFormFieldRuleEvaluationResult.getValue());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isVisible());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isValid());
				Assert.assertTrue(
					ddmFormFieldRuleEvaluationResult.isReadOnly());
			}
			else if(
				ddmFormFieldRuleEvaluationResult.getName().equals("field1")) {

				Assert.assertEquals(
					"value1", ddmFormFieldRuleEvaluationResult.getValue());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isVisible());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isValid());
				Assert.assertFalse(
					ddmFormFieldRuleEvaluationResult.isReadOnly());
			}
		}
	}

	@Test
	public void testEvaluateNotBetween() throws Exception {
		DDMForm ddmForm = new DDMForm();

		DDMFormField fieldDDMFormField0 = new DDMFormField("field0", "text");

		ddmForm.addDDMFormField(fieldDDMFormField0);

		DDMFormField fieldDDMFormField1 = new DDMFormField("field1", "text");

		DDMFormFieldRule ddmFormFieldRule1 = new DDMFormFieldRule(
			"between(field1,25,field0)", DDMFormFieldRuleType.VISIBILITY);

		fieldDDMFormField1.addDDMFormFieldRule(ddmFormFieldRule1);

		ddmForm.addDDMFormField(fieldDDMFormField1);

		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		DDMFormFieldValue fieldDDMFormFieldValue0 =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_instanceId", "field0", new UnlocalizedValue("30"));

		List<DDMFormFieldValue> ddmFormFieldValues = new ArrayList<>();

		ddmFormFieldValues.add(fieldDDMFormFieldValue0);

		DDMFormFieldValue fieldDDMFormFieldValue1 =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field1_instanceId", "field1", new UnlocalizedValue("15"));

		ddmFormFieldValues.add(fieldDDMFormFieldValue1);

		ddmFormValues.setDDMFormFieldValues(ddmFormFieldValues);

		DDMFormRuleEvaluator ddmFormRuleEvaluator =
			createDDMFormRuleEvaluator();

		List<DDMFormFieldRuleEvaluationResult>
			ddmFormFieldRuleEvaluationResults = ddmFormRuleEvaluator.evaluate(
				ddmForm, ddmFormValues, LocaleUtil.US);

		Assert.assertEquals(2, ddmFormFieldRuleEvaluationResults.size());

		for (DDMFormFieldRuleEvaluationResult ddmFormFieldRuleEvaluationResult :
				ddmFormFieldRuleEvaluationResults) {

			if (ddmFormFieldRuleEvaluationResult.getName().equals("field0")) {
				Assert.assertEquals(
					"30", ddmFormFieldRuleEvaluationResult.getValue());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isVisible());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isValid());
				Assert.assertFalse(
					ddmFormFieldRuleEvaluationResult.isReadOnly());
			}
			else if(
				ddmFormFieldRuleEvaluationResult.getName().equals("field1")) {

				Assert.assertEquals(
					"15", ddmFormFieldRuleEvaluationResult.getValue());
				Assert.assertFalse(
					ddmFormFieldRuleEvaluationResult.isVisible());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isValid());
				Assert.assertFalse(
					ddmFormFieldRuleEvaluationResult.isReadOnly());
			}
		}
	}

	@Test
	public void testEvaluateNotContains() throws Exception {
		DDMForm ddmForm = new DDMForm();

		DDMFormField fieldDDMFormField0 = new DDMFormField("field0", "text");

		ddmForm.addDDMFormField(fieldDDMFormField0);

		DDMFormFieldRule ddmFormFieldRule0 = new DDMFormFieldRule(
			"contains(field1,\"hello\")", DDMFormFieldRuleType.VISIBILITY);

		fieldDDMFormField0.addDDMFormFieldRule(ddmFormFieldRule0);

		DDMFormField fieldDDMFormField1 = new DDMFormField("field1", "text");

		ddmForm.addDDMFormField(fieldDDMFormField1);

		DDMFormFieldRule ddmFormFieldRule1 = new DDMFormFieldRule(
			"contains(field0,\"world\")", DDMFormFieldRuleType.VISIBILITY);

		fieldDDMFormField1.addDDMFormFieldRule(ddmFormFieldRule1);

		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		DDMFormFieldValue fieldDDMFormFieldValue0 =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_instanceId", "field0", new UnlocalizedValue("value0"));

		List<DDMFormFieldValue> ddmFormFieldValues = new ArrayList<>();

		ddmFormFieldValues.add(fieldDDMFormFieldValue0);

		DDMFormFieldValue fieldDDMFormFieldValue1 =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field1_instanceId", "field1", new UnlocalizedValue("value1"));

		ddmFormFieldValues.add(fieldDDMFormFieldValue1);

		ddmFormValues.setDDMFormFieldValues(ddmFormFieldValues);

		DDMFormRuleEvaluator ddmFormRuleEvaluator =
			createDDMFormRuleEvaluator();

		List<DDMFormFieldRuleEvaluationResult>
			ddmFormFieldRuleEvaluationResults = ddmFormRuleEvaluator.evaluate(
				ddmForm, ddmFormValues, LocaleUtil.US);

		Assert.assertEquals(2, ddmFormFieldRuleEvaluationResults.size());

		for (DDMFormFieldRuleEvaluationResult ddmFormFieldRuleEvaluationResult :
				ddmFormFieldRuleEvaluationResults) {

			if (ddmFormFieldRuleEvaluationResult.getName().equals("field0")) {
				Assert.assertEquals(
					"value0", ddmFormFieldRuleEvaluationResult.getValue());
				Assert.assertFalse(
					ddmFormFieldRuleEvaluationResult.isVisible());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isValid());
				Assert.assertFalse(
					ddmFormFieldRuleEvaluationResult.isReadOnly());
			}
			else if(
				ddmFormFieldRuleEvaluationResult.getName().equals("field1")) {

				Assert.assertEquals(
					"value1", ddmFormFieldRuleEvaluationResult.getValue());
				Assert.assertFalse(
					ddmFormFieldRuleEvaluationResult.isVisible());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isValid());
				Assert.assertFalse(
					ddmFormFieldRuleEvaluationResult.isReadOnly());
			}
		}
	}

	@Test
	public void testEvaluateNotEquals() throws Exception {
		DDMForm ddmForm = new DDMForm();

		DDMFormField fieldDDMFormField0 = new DDMFormField("field0", "text");

		ddmForm.addDDMFormField(fieldDDMFormField0);

		DDMFormFieldRule ddmFormFieldRule0 = new DDMFormFieldRule(
			"equals(field1,\"value2\")", DDMFormFieldRuleType.READ_ONLY);

		fieldDDMFormField0.addDDMFormFieldRule(ddmFormFieldRule0);

		DDMFormField fieldDDMFormField1 = new DDMFormField("field1", "text");

		ddmForm.addDDMFormField(fieldDDMFormField1);

		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		DDMFormFieldValue fieldDDMFormFieldValue0 =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_instanceId", "field0", new UnlocalizedValue("field0"));

		List<DDMFormFieldValue> ddmFormFieldValues = new ArrayList<>();

		ddmFormFieldValues.add(fieldDDMFormFieldValue0);

		DDMFormFieldValue fieldDDMFormFieldValue1 =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field1_instanceId", "field1", new UnlocalizedValue("value1"));

		ddmFormFieldValues.add(fieldDDMFormFieldValue1);

		ddmFormValues.setDDMFormFieldValues(ddmFormFieldValues);

		DDMFormRuleEvaluator ddmFormRuleEvaluator =
			createDDMFormRuleEvaluator();

		List<DDMFormFieldRuleEvaluationResult>
			ddmFormFieldRuleEvaluationResults = ddmFormRuleEvaluator.evaluate(
				ddmForm, ddmFormValues, LocaleUtil.US);

		Assert.assertEquals(2, ddmFormFieldRuleEvaluationResults.size());

		for (DDMFormFieldRuleEvaluationResult ddmFormFieldRuleEvaluationResult :
				ddmFormFieldRuleEvaluationResults) {

			if (ddmFormFieldRuleEvaluationResult.getName().equals("field0")) {
				Assert.assertEquals(
					"field0", ddmFormFieldRuleEvaluationResult.getValue());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isVisible());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isValid());
				Assert.assertFalse(
					ddmFormFieldRuleEvaluationResult.isReadOnly());
			}
			else if(
				ddmFormFieldRuleEvaluationResult.getName().equals("field1")) {

				Assert.assertEquals(
					"value1", ddmFormFieldRuleEvaluationResult.getValue());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isVisible());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isValid());
				Assert.assertFalse(
					ddmFormFieldRuleEvaluationResult.isReadOnly());
			}
		}
	}

	@Test
	public void testEvaluateNotReadOnly() throws Exception {
		DDMForm ddmForm = new DDMForm();

		DDMFormField fieldDDMFormField0 = new DDMFormField("field0", "text");

		DDMFormFieldRule ddmFormFieldRule0 = new DDMFormFieldRule(
			"equals(field0,\"read-only\")", DDMFormFieldRuleType.READ_ONLY);

		fieldDDMFormField0.addDDMFormFieldRule(ddmFormFieldRule0);

		ddmForm.addDDMFormField(fieldDDMFormField0);

		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		DDMFormFieldValue fieldDDMFormFieldValue0 =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_instanceId", "field0", new UnlocalizedValue("value0"));

		List<DDMFormFieldValue> ddmFormFieldValues = new ArrayList<>();

		ddmFormFieldValues.add(fieldDDMFormFieldValue0);

		ddmFormValues.setDDMFormFieldValues(ddmFormFieldValues);

		DDMFormRuleEvaluator ddmFormRuleEvaluator =
			createDDMFormRuleEvaluator();

		List<DDMFormFieldRuleEvaluationResult>
			ddmFormFieldRuleEvaluationResults = ddmFormRuleEvaluator.evaluate(
				ddmForm, ddmFormValues, LocaleUtil.US);

		Assert.assertEquals(1, ddmFormFieldRuleEvaluationResults.size());

		for (DDMFormFieldRuleEvaluationResult ddmFormFieldRuleEvaluationResult :
				ddmFormFieldRuleEvaluationResults) {

			if (ddmFormFieldRuleEvaluationResult.getName().equals("field0")) {
				Assert.assertEquals(
					"value0", ddmFormFieldRuleEvaluationResult.getValue());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isVisible());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isValid());
				Assert.assertFalse(
					ddmFormFieldRuleEvaluationResult.isReadOnly());
			}
		}
	}

	@Test
	public void testEvaluateNotReadOnly2() throws Exception {
		DDMForm ddmForm = new DDMForm();

		DDMFormField fieldDDMFormField0 = new DDMFormField("field0", "text");

		DDMFormFieldRule ddmFormFieldRule0 = new DDMFormFieldRule(
			"isReadOnly(field1)", DDMFormFieldRuleType.READ_ONLY);

		fieldDDMFormField0.addDDMFormFieldRule(ddmFormFieldRule0);

		ddmForm.addDDMFormField(fieldDDMFormField0);

		DDMFormField fieldDDMFormField1 = new DDMFormField("field1", "text");

		DDMFormFieldRule ddmFormFieldRule1 = new DDMFormFieldRule(
			"isReadOnly(field3)", DDMFormFieldRuleType.READ_ONLY);

		fieldDDMFormField1.addDDMFormFieldRule(ddmFormFieldRule1);

		ddmForm.addDDMFormField(fieldDDMFormField1);

		DDMFormField fieldDDMFormField2 = new DDMFormField("field2", "text");

		DDMFormFieldRule ddmFormFieldRule2 = new DDMFormFieldRule(
			"equals(field0,\"value0\") && contains(field3,\"world\")",
			DDMFormFieldRuleType.VISIBILITY);

		fieldDDMFormField2.addDDMFormFieldRule(ddmFormFieldRule2);

		ddmForm.addDDMFormField(fieldDDMFormField2);

		DDMFormField fieldDDMFormField3 = new DDMFormField("field3", "text");

		DDMFormFieldRule ddmFormFieldRule3 = new DDMFormFieldRule(
			"equals(field1,field2)", DDMFormFieldRuleType.READ_ONLY);

		fieldDDMFormField3.addDDMFormFieldRule(ddmFormFieldRule3);

		ddmForm.addDDMFormField(fieldDDMFormField3);

		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		DDMFormFieldValue fieldDDMFormFieldValue0 =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_instanceId", "field0", new UnlocalizedValue("value0"));

		DDMFormFieldValue fieldDDMFormFieldValue1 =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field1_instanceId", "field1", new UnlocalizedValue("test"));

		DDMFormFieldValue fieldDDMFormFieldValue2 =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field2_instanceId", "field2",
				new UnlocalizedValue("dif_test"));

		DDMFormFieldValue fieldDDMFormFieldValue3 =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field3_instanceId", "field3",
				new UnlocalizedValue("hello world"));

		List<DDMFormFieldValue> ddmFormFieldValues = new ArrayList<>();

		ddmFormFieldValues.add(fieldDDMFormFieldValue0);
		ddmFormFieldValues.add(fieldDDMFormFieldValue1);
		ddmFormFieldValues.add(fieldDDMFormFieldValue2);
		ddmFormFieldValues.add(fieldDDMFormFieldValue3);

		ddmFormValues.setDDMFormFieldValues(ddmFormFieldValues);

		DDMFormRuleEvaluator ddmFormRuleEvaluator =
			createDDMFormRuleEvaluator();

		List<DDMFormFieldRuleEvaluationResult>
			ddmFormFieldRuleEvaluationResults = ddmFormRuleEvaluator.evaluate(
				ddmForm, ddmFormValues, LocaleUtil.US);

		Assert.assertEquals(4, ddmFormFieldRuleEvaluationResults.size());

		for (DDMFormFieldRuleEvaluationResult ddmFormFieldRuleEvaluationResult :
				ddmFormFieldRuleEvaluationResults) {

			if (ddmFormFieldRuleEvaluationResult.getName().equals("field0")) {
				Assert.assertEquals(
					"value0", ddmFormFieldRuleEvaluationResult.getValue());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isVisible());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isValid());
				Assert.assertFalse(
					ddmFormFieldRuleEvaluationResult.isReadOnly());
			}
			else if (ddmFormFieldRuleEvaluationResult.getName().equals(
						"field1")) {

				Assert.assertEquals(
					"test", ddmFormFieldRuleEvaluationResult.getValue());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isVisible());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isValid());
				Assert.assertFalse(
					ddmFormFieldRuleEvaluationResult.isReadOnly());
			}
			else if (ddmFormFieldRuleEvaluationResult.getName().equals(
						"field2")) {

				Assert.assertEquals(
					"dif_test", ddmFormFieldRuleEvaluationResult.getValue());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isVisible());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isValid());
				Assert.assertFalse(
					ddmFormFieldRuleEvaluationResult.isReadOnly());
			}
			else if (ddmFormFieldRuleEvaluationResult.getName().equals(
						"field3")) {

				Assert.assertEquals(
					"hello world", ddmFormFieldRuleEvaluationResult.getValue());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isVisible());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isValid());
				Assert.assertFalse(
					ddmFormFieldRuleEvaluationResult.isReadOnly());
			}
		}
	}

	@Test
	public void testEvaluateNotVisible() throws Exception {
		DDMForm ddmForm = new DDMForm();

		DDMFormField fieldDDMFormField0 = new DDMFormField("field0", "text");

		ddmForm.addDDMFormField(fieldDDMFormField0);

		DDMFormField fieldDDMFormField1 = new DDMFormField("field1", "text");

		DDMFormFieldRule ddmFormFieldRule1 = new DDMFormFieldRule(
			"equals(field0,\"nothing\")", DDMFormFieldRuleType.VISIBILITY);

		fieldDDMFormField1.addDDMFormFieldRule(ddmFormFieldRule1);

		ddmForm.addDDMFormField(fieldDDMFormField1);

		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		DDMFormFieldValue fieldDDMFormFieldValue0 =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_instanceId", "field0", new UnlocalizedValue("value0"));

		List<DDMFormFieldValue> ddmFormFieldValues = new ArrayList<>();

		ddmFormFieldValues.add(fieldDDMFormFieldValue0);

		DDMFormFieldValue fieldDDMFormFieldValue1 =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field1_instanceId", "field1", new UnlocalizedValue("value1"));

		ddmFormFieldValues.add(fieldDDMFormFieldValue1);

		ddmFormValues.setDDMFormFieldValues(ddmFormFieldValues);

		DDMFormRuleEvaluator ddmFormRuleEvaluator =
			createDDMFormRuleEvaluator();

		List<DDMFormFieldRuleEvaluationResult>
			ddmFormFieldRuleEvaluationResults = ddmFormRuleEvaluator.evaluate(
				ddmForm, ddmFormValues, LocaleUtil.US);

		Assert.assertEquals(2, ddmFormFieldRuleEvaluationResults.size());

		for (DDMFormFieldRuleEvaluationResult ddmFormFieldRuleEvaluationResult :
				ddmFormFieldRuleEvaluationResults) {

			if (ddmFormFieldRuleEvaluationResult.getName().equals("field0")) {
				Assert.assertEquals(
					"value0", ddmFormFieldRuleEvaluationResult.getValue());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isVisible());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isValid());
				Assert.assertFalse(
					ddmFormFieldRuleEvaluationResult.isReadOnly());
			}
			else if(
				ddmFormFieldRuleEvaluationResult.getName().equals("field1")) {

				Assert.assertEquals(
					"value1", ddmFormFieldRuleEvaluationResult.getValue());
				Assert.assertFalse(
					ddmFormFieldRuleEvaluationResult.isVisible());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isValid());
				Assert.assertFalse(
					ddmFormFieldRuleEvaluationResult.isReadOnly());
			}
		}
	}

	@Test
	public void testEvaluateReadOnly() throws Exception {
		DDMForm ddmForm = new DDMForm();

		DDMFormField fieldDDMFormField0 = new DDMFormField("field0", "text");

		DDMFormFieldRule ddmFormFieldRule0 = new DDMFormFieldRule(
			"equals(field0,\"read-only\")", DDMFormFieldRuleType.READ_ONLY);

		fieldDDMFormField0.addDDMFormFieldRule(ddmFormFieldRule0);

		ddmForm.addDDMFormField(fieldDDMFormField0);

		DDMFormField fieldDDMFormField1 = new DDMFormField("field1", "text");

		DDMFormFieldRule ddmFormFieldRule1 = new DDMFormFieldRule(
			"contains(field1,\"nothing\")", DDMFormFieldRuleType.VISIBILITY);

		fieldDDMFormField1.addDDMFormFieldRule(ddmFormFieldRule1);

		DDMFormFieldRule ddmFormFieldRule2 = new DDMFormFieldRule(
			"isReadOnly(field0)", DDMFormFieldRuleType.READ_ONLY);

		fieldDDMFormField1.addDDMFormFieldRule(ddmFormFieldRule2);

		ddmForm.addDDMFormField(fieldDDMFormField1);

		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		DDMFormFieldValue fieldDDMFormFieldValue0 =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_instanceId", "field0",
				new UnlocalizedValue("read-only"));

		List<DDMFormFieldValue> ddmFormFieldValues = new ArrayList<>();

		ddmFormFieldValues.add(fieldDDMFormFieldValue0);

		DDMFormFieldValue fieldDDMFormFieldValue1 =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field1_instanceId", "field1", new UnlocalizedValue("value1"));

		ddmFormFieldValues.add(fieldDDMFormFieldValue1);

		ddmFormValues.setDDMFormFieldValues(ddmFormFieldValues);

		DDMFormRuleEvaluator ddmFormRuleEvaluator =
			createDDMFormRuleEvaluator();

		List<DDMFormFieldRuleEvaluationResult>
			ddmFormFieldRuleEvaluationResults = ddmFormRuleEvaluator.evaluate(
				ddmForm, ddmFormValues, LocaleUtil.US);

		Assert.assertEquals(2, ddmFormFieldRuleEvaluationResults.size());

		for (DDMFormFieldRuleEvaluationResult ddmFormFieldRuleEvaluationResult :
				ddmFormFieldRuleEvaluationResults) {

			if (ddmFormFieldRuleEvaluationResult.getName().equals("field0")) {
				Assert.assertEquals(
					"read-only", ddmFormFieldRuleEvaluationResult.getValue());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isVisible());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isValid());
				Assert.assertTrue(
					ddmFormFieldRuleEvaluationResult.isReadOnly());
			}
			else if(
				ddmFormFieldRuleEvaluationResult.getName().equals("field1")) {

				Assert.assertEquals(
					"value1", ddmFormFieldRuleEvaluationResult.getValue());
				Assert.assertFalse(
					ddmFormFieldRuleEvaluationResult.isVisible());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isValid());
				Assert.assertTrue(
					ddmFormFieldRuleEvaluationResult.isReadOnly());
			}
		}
	}

	@Test
	public void testEvaluateReadOnly2() throws Exception {
		DDMForm ddmForm = new DDMForm();

		DDMFormField fieldDDMFormField0 = new DDMFormField("field0", "text");

		DDMFormFieldRule ddmFormFieldRule0 = new DDMFormFieldRule(
			"contains(field0,\"hello\")", DDMFormFieldRuleType.READ_ONLY);

		fieldDDMFormField0.addDDMFormFieldRule(ddmFormFieldRule0);

		ddmForm.addDDMFormField(fieldDDMFormField0);

		DDMFormField fieldDDMFormField1 = new DDMFormField("field1", "text");

		DDMFormFieldRule ddmFormFieldRule1 = new DDMFormFieldRule(
			"isVisible(field2)", DDMFormFieldRuleType.VISIBILITY);

		fieldDDMFormField1.addDDMFormFieldRule(ddmFormFieldRule1);

		ddmForm.addDDMFormField(fieldDDMFormField1);

		DDMFormField fieldDDMFormField2 = new DDMFormField("field2", "text");

		DDMFormFieldRule ddmFormFieldRule2 = new DDMFormFieldRule(
			"isReadOnly(field0)", DDMFormFieldRuleType.VISIBILITY);

		fieldDDMFormField2.addDDMFormFieldRule(ddmFormFieldRule2);

		ddmForm.addDDMFormField(fieldDDMFormField2);

		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		DDMFormFieldValue fieldDDMFormFieldValue0 =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_instanceId", "field0",
				new UnlocalizedValue("hello world"));

		List<DDMFormFieldValue> ddmFormFieldValues = new ArrayList<>();

		ddmFormFieldValues.add(fieldDDMFormFieldValue0);

		DDMFormFieldValue fieldDDMFormFieldValue1 =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field1_instanceId", "field1", new UnlocalizedValue("value1"));

		ddmFormFieldValues.add(fieldDDMFormFieldValue1);

		DDMFormFieldValue fieldDDMFormFieldValue2 =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field2_instanceId", "field2", new UnlocalizedValue("value2"));

		ddmFormFieldValues.add(fieldDDMFormFieldValue2);

		ddmFormValues.setDDMFormFieldValues(ddmFormFieldValues);

		DDMFormRuleEvaluator ddmFormRuleEvaluator =
			createDDMFormRuleEvaluator();

		List<DDMFormFieldRuleEvaluationResult>
			ddmFormFieldRuleEvaluationResults = ddmFormRuleEvaluator.evaluate(
				ddmForm, ddmFormValues, LocaleUtil.US);

		Assert.assertEquals(3, ddmFormFieldRuleEvaluationResults.size());

		for (DDMFormFieldRuleEvaluationResult ddmFormFieldRuleEvaluationResult :
				ddmFormFieldRuleEvaluationResults) {

			if (ddmFormFieldRuleEvaluationResult.getName().equals("field0")) {
				Assert.assertEquals(
					"hello world", ddmFormFieldRuleEvaluationResult.getValue());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isVisible());
				Assert.assertTrue(
					ddmFormFieldRuleEvaluationResult.isReadOnly());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isValid());
			}
			else if(
				ddmFormFieldRuleEvaluationResult.getName().equals("field1")) {

				Assert.assertEquals(
					"value1", ddmFormFieldRuleEvaluationResult.getValue());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isVisible());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isValid());
				Assert.assertFalse(
					ddmFormFieldRuleEvaluationResult.isReadOnly());
			}
			else if(
				ddmFormFieldRuleEvaluationResult.getName().equals("field2")) {

				Assert.assertEquals(
					"value2", ddmFormFieldRuleEvaluationResult.getValue());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isVisible());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isValid());
				Assert.assertFalse(
					ddmFormFieldRuleEvaluationResult.isReadOnly());
			}
		}
	}

	@Test
	public void testEvaluateVisible() throws Exception {
		DDMForm ddmForm = new DDMForm();

		DDMFormField fieldDDMFormField0 = new DDMFormField("field0", "text");

		ddmForm.addDDMFormField(fieldDDMFormField0);

		DDMFormField fieldDDMFormField1 = new DDMFormField("field1", "text");

		DDMFormFieldRule ddmFormFieldRule1 = new DDMFormFieldRule(
			"equals(field0,\"test\")", DDMFormFieldRuleType.VISIBILITY);

		fieldDDMFormField1.addDDMFormFieldRule(ddmFormFieldRule1);

		ddmForm.addDDMFormField(fieldDDMFormField1);

		DDMFormField fieldDDMFormField2 = new DDMFormField("field2", "text");

		DDMFormFieldRule ddmFormFieldRule2 = new DDMFormFieldRule(
			"isVisible(field1)", DDMFormFieldRuleType.VISIBILITY);

		fieldDDMFormField2.addDDMFormFieldRule(ddmFormFieldRule2);

		ddmForm.addDDMFormField(fieldDDMFormField2);

		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		DDMFormFieldValue fieldDDMFormFieldValue0 =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_instanceId", "field0", new UnlocalizedValue("test"));

		List<DDMFormFieldValue> ddmFormFieldValues = new ArrayList<>();

		ddmFormFieldValues.add(fieldDDMFormFieldValue0);

		DDMFormFieldValue fieldDDMFormFieldValue1 =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field1_instanceId", "field1", new UnlocalizedValue("value1"));

		ddmFormFieldValues.add(fieldDDMFormFieldValue1);

		DDMFormFieldValue fieldDDMFormFieldValue2 =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field2_instanceId", "field2", new UnlocalizedValue("value2"));

		ddmFormFieldValues.add(fieldDDMFormFieldValue2);

		ddmFormValues.setDDMFormFieldValues(ddmFormFieldValues);

		DDMFormRuleEvaluator ddmFormRuleEvaluator =
			createDDMFormRuleEvaluator();

		List<DDMFormFieldRuleEvaluationResult>
			ddmFormFieldRuleEvaluationResults = ddmFormRuleEvaluator.evaluate(
				ddmForm, ddmFormValues, LocaleUtil.US);

		Assert.assertEquals(3, ddmFormFieldRuleEvaluationResults.size());

		for (DDMFormFieldRuleEvaluationResult ddmFormFieldRuleEvaluationResult :
				ddmFormFieldRuleEvaluationResults) {

			if (ddmFormFieldRuleEvaluationResult.getName().equals("field0")) {
				Assert.assertEquals(
					"test", ddmFormFieldRuleEvaluationResult.getValue());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isVisible());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isValid());
				Assert.assertFalse(
					ddmFormFieldRuleEvaluationResult.isReadOnly());
			}
			else if(
				ddmFormFieldRuleEvaluationResult.getName().equals("field1")) {

				Assert.assertEquals(
					"value1", ddmFormFieldRuleEvaluationResult.getValue());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isVisible());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isValid());
				Assert.assertFalse(
					ddmFormFieldRuleEvaluationResult.isReadOnly());
			}
			else if(
				ddmFormFieldRuleEvaluationResult.getName().equals("field2")) {

				Assert.assertEquals(
					"value2", ddmFormFieldRuleEvaluationResult.getValue());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isVisible());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isValid());
				Assert.assertFalse(
					ddmFormFieldRuleEvaluationResult.isReadOnly());
			}
		}
	}

	@Test
	public void testEvaluateVisible2() throws Exception {
		DDMForm ddmForm = new DDMForm();

		DDMFormField fieldDDMFormField2 = new DDMFormField("field2", "text");

		DDMFormFieldRule ddmFormFieldRule2 = new DDMFormFieldRule(
			"isVisible(field1)", DDMFormFieldRuleType.VISIBILITY);

		fieldDDMFormField2.addDDMFormFieldRule(ddmFormFieldRule2);

		ddmForm.addDDMFormField(fieldDDMFormField2);

		DDMFormField fieldDDMFormField1 = new DDMFormField("field1", "text");

		DDMFormFieldRule ddmFormFieldRule1 = new DDMFormFieldRule(
			"equals(field0,\"test\")", DDMFormFieldRuleType.VISIBILITY);

		fieldDDMFormField1.addDDMFormFieldRule(ddmFormFieldRule1);

		ddmForm.addDDMFormField(fieldDDMFormField1);

		DDMFormField fieldDDMFormField0 = new DDMFormField("field0", "text");

		ddmForm.addDDMFormField(fieldDDMFormField0);

		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		DDMFormFieldValue fieldDDMFormFieldValue0 =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_instanceId", "field0", new UnlocalizedValue("test"));

		List<DDMFormFieldValue> ddmFormFieldValues = new ArrayList<>();

		ddmFormFieldValues.add(fieldDDMFormFieldValue0);

		DDMFormFieldValue fieldDDMFormFieldValue1 =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field1_instanceId", "field1", new UnlocalizedValue("value1"));

		ddmFormFieldValues.add(fieldDDMFormFieldValue1);

		DDMFormFieldValue fieldDDMFormFieldValue2 =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field2_instanceId", "field2", new UnlocalizedValue("value2"));

		ddmFormFieldValues.add(fieldDDMFormFieldValue2);

		ddmFormValues.setDDMFormFieldValues(ddmFormFieldValues);

		DDMFormRuleEvaluator ddmFormRuleEvaluator =
			createDDMFormRuleEvaluator();

		List<DDMFormFieldRuleEvaluationResult>
			ddmFormFieldRuleEvaluationResults = ddmFormRuleEvaluator.evaluate(
				ddmForm, ddmFormValues, LocaleUtil.US);

		Assert.assertEquals(3, ddmFormFieldRuleEvaluationResults.size());

		for (DDMFormFieldRuleEvaluationResult ddmFormFieldRuleEvaluationResult :
				ddmFormFieldRuleEvaluationResults) {

			if (ddmFormFieldRuleEvaluationResult.getName().equals("field0")) {
				Assert.assertEquals(
					"test", ddmFormFieldRuleEvaluationResult.getValue());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isVisible());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isValid());
				Assert.assertFalse(
					ddmFormFieldRuleEvaluationResult.isReadOnly());
			}
			else if(
				ddmFormFieldRuleEvaluationResult.getName().equals("field1")) {

				Assert.assertEquals(
					"value1", ddmFormFieldRuleEvaluationResult.getValue());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isVisible());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isValid());
				Assert.assertFalse(
					ddmFormFieldRuleEvaluationResult.isReadOnly());
			}
			else if(
				ddmFormFieldRuleEvaluationResult.getName().equals("field2")) {

				Assert.assertEquals(
					"value2", ddmFormFieldRuleEvaluationResult.getValue());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isVisible());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isValid());
				Assert.assertFalse(
					ddmFormFieldRuleEvaluationResult.isReadOnly());
			}
		}
	}

	@Test
	public void testEvaluateWithDifferentFunctions() throws Exception {
		DDMForm ddmForm = new DDMForm();

		DDMFormField fieldDDMFormField0 = new DDMFormField("field0", "text");

		ddmForm.addDDMFormField(fieldDDMFormField0);

		DDMFormFieldRule ddmFormFieldRule1 = new DDMFormFieldRule(
			"equals(field1,\"test\") && not(contains(field2,\"hello\"))",
			DDMFormFieldRuleType.VISIBILITY);

		fieldDDMFormField0.addDDMFormFieldRule(ddmFormFieldRule1);

		DDMFormField fieldDDMFormField1 = new DDMFormField("field1", "text");

		ddmForm.addDDMFormField(fieldDDMFormField1);

		DDMFormField fieldDDMFormField2 = new DDMFormField("field2", "text");

		ddmForm.addDDMFormField(fieldDDMFormField2);

		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		DDMFormFieldValue fieldDDMFormFieldValue0 =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_instanceId", "field0", new UnlocalizedValue(""));

		List<DDMFormFieldValue> ddmFormFieldValues = new ArrayList<>();

		ddmFormFieldValues.add(fieldDDMFormFieldValue0);

		DDMFormFieldValue fieldDDMFormFieldValue1 =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field1_instanceId", "field1", new UnlocalizedValue("test"));

		ddmFormFieldValues.add(fieldDDMFormFieldValue1);

		DDMFormFieldValue fieldDDMFormFieldValue2 =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field2_instanceId", "field2",
				new UnlocalizedValue("hello world"));

		ddmFormFieldValues.add(fieldDDMFormFieldValue2);

		ddmFormValues.setDDMFormFieldValues(ddmFormFieldValues);

		DDMFormRuleEvaluator ddmFormRuleEvaluator =
			createDDMFormRuleEvaluator();

		List<DDMFormFieldRuleEvaluationResult>
			ddmFormFieldRuleEvaluationResults = ddmFormRuleEvaluator.evaluate(
				ddmForm, ddmFormValues, LocaleUtil.US);

		Assert.assertEquals(3, ddmFormFieldRuleEvaluationResults.size());

		for (DDMFormFieldRuleEvaluationResult ddmFormFieldRuleEvaluationResult :
				ddmFormFieldRuleEvaluationResults) {

			if (ddmFormFieldRuleEvaluationResult.getName().equals("field0")) {
				Assert.assertEquals(
					"", ddmFormFieldRuleEvaluationResult.getValue());
				Assert.assertFalse(
					ddmFormFieldRuleEvaluationResult.isVisible());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isValid());
				Assert.assertFalse(
					ddmFormFieldRuleEvaluationResult.isReadOnly());
			}
			else if(
				ddmFormFieldRuleEvaluationResult.getName().equals("field1")) {

				Assert.assertEquals(
					"test", ddmFormFieldRuleEvaluationResult.getValue());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isVisible());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isValid());
				Assert.assertFalse(
					ddmFormFieldRuleEvaluationResult.isReadOnly());
			}
			else if(
				ddmFormFieldRuleEvaluationResult.getName().equals("field2")) {

				Assert.assertEquals(
					"hello world", ddmFormFieldRuleEvaluationResult.getValue());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isVisible());
				Assert.assertTrue(ddmFormFieldRuleEvaluationResult.isValid());
				Assert.assertFalse(
					ddmFormFieldRuleEvaluationResult.isReadOnly());
			}
		}
	}

	@Test(expected = DDMFormRuleEvaluationException.class)
	public void testIndirectLoopCondition() throws Exception {
		DDMForm ddmForm = new DDMForm();

		DDMFormField fieldDDMFormField0 = new DDMFormField("field0", "text");

		DDMFormFieldRule ddmFormFieldRule0 = new DDMFormFieldRule(
			"isVisible(field1)", DDMFormFieldRuleType.READ_ONLY);

		fieldDDMFormField0.addDDMFormFieldRule(ddmFormFieldRule0);

		ddmForm.addDDMFormField(fieldDDMFormField0);

		DDMFormField fieldDDMFormField1 = new DDMFormField("field1", "text");

		DDMFormFieldRule ddmFormFieldRule1 = new DDMFormFieldRule(
			"isReadOnly(field2)", DDMFormFieldRuleType.VISIBILITY);

		fieldDDMFormField1.addDDMFormFieldRule(ddmFormFieldRule1);

		ddmForm.addDDMFormField(fieldDDMFormField1);

		DDMFormField fieldDDMFormField2 = new DDMFormField("field2", "text");

		DDMFormFieldRule ddmFormFieldRule2 = new DDMFormFieldRule(
			"isReadOnly(field0)", DDMFormFieldRuleType.READ_ONLY);

		fieldDDMFormField2.addDDMFormFieldRule(ddmFormFieldRule2);

		ddmForm.addDDMFormField(fieldDDMFormField2);

		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		DDMFormFieldValue fieldDDMFormFieldValue0 =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_instanceId", "field0", new UnlocalizedValue("value0"));

		List<DDMFormFieldValue> ddmFormFieldValues = new ArrayList<>();

		ddmFormFieldValues.add(fieldDDMFormFieldValue0);

		DDMFormFieldValue fieldDDMFormFieldValue1 =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field1_instanceId", "field1", new UnlocalizedValue("value1"));

		ddmFormFieldValues.add(fieldDDMFormFieldValue1);

		DDMFormFieldValue fieldDDMFormFieldValue2 =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field2_instanceId", "field2", new UnlocalizedValue("value2"));

		ddmFormFieldValues.add(fieldDDMFormFieldValue2);

		ddmFormValues.setDDMFormFieldValues(ddmFormFieldValues);

		DDMFormRuleEvaluator ddmFormRuleEvaluator =
			createDDMFormRuleEvaluator();

		ddmFormRuleEvaluator.evaluate(ddmForm, ddmFormValues, LocaleUtil.US);
	}

	@Test(expected = DDMFormRuleEvaluationException.class)
	public void testLoopCondition() throws Exception {
		DDMForm ddmForm = new DDMForm();

		DDMFormField fieldDDMFormField0 = new DDMFormField("field0", "text");

		DDMFormFieldRule ddmFormFieldRule0 = new DDMFormFieldRule(
			"isReadOnly(field1)", DDMFormFieldRuleType.READ_ONLY);

		fieldDDMFormField0.addDDMFormFieldRule(ddmFormFieldRule0);

		ddmForm.addDDMFormField(fieldDDMFormField0);

		DDMFormField fieldDDMFormField1 = new DDMFormField("field1", "text");

		DDMFormFieldRule ddmFormFieldRule1 = new DDMFormFieldRule(
			"isReadOnly(field0)", DDMFormFieldRuleType.READ_ONLY);

		fieldDDMFormField1.addDDMFormFieldRule(ddmFormFieldRule1);

		ddmForm.addDDMFormField(fieldDDMFormField1);

		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		DDMFormFieldValue fieldDDMFormFieldValue0 =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field0_instanceId", "field0", new UnlocalizedValue("value0"));

		List<DDMFormFieldValue> ddmFormFieldValues = new ArrayList<>();

		ddmFormFieldValues.add(fieldDDMFormFieldValue0);

		DDMFormFieldValue fieldDDMFormFieldValue1 =
			DDMFormValuesTestUtil.createDDMFormFieldValue(
				"field1_instanceId", "field1", new UnlocalizedValue("value1"));

		ddmFormFieldValues.add(fieldDDMFormFieldValue1);

		ddmFormValues.setDDMFormFieldValues(ddmFormFieldValues);

		DDMFormRuleEvaluator ddmFormRuleEvaluator =
			createDDMFormRuleEvaluator();

		ddmFormRuleEvaluator.evaluate(ddmForm, ddmFormValues, LocaleUtil.US);
	}

	protected DDMFormFieldValue createDDMFormFieldValue(
		String instanceId, String name, String value) {

		return DDMFormValuesTestUtil.createDDMFormFieldValue(
			instanceId, name, new UnlocalizedValue(value));
	}

	protected DDMFormRuleEvaluator createDDMFormRuleEvaluator()
		throws Exception {

		DDMFormRuleEvaluator ddmFormRuleEvaluator =
			new DDMFormRuleEvaluatorImpl();
		field(DDMFormRuleEvaluatorImpl.class, "_ddmExpressionFactory").set(
			ddmFormRuleEvaluator, new DDMExpressionFactoryImpl());

		return ddmFormRuleEvaluator;
	}

	protected void setUpLanguageUtil() {
		LanguageUtil languageUtil = new LanguageUtil();

		languageUtil.setLanguage(_language);
	}

	@Mock
	private CallFunction _ddmFormRuleCallFunction;

	@Mock
	private Language _language;

}