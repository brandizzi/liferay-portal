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

package com.liferay.dynamic.data.mapping.search.test;

import com.liferay.dynamic.data.mapping.helper.DDMFormInstanceRecordTestHelper;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Luan Maoski
 * @author Lucas Marques
 */
public class DDMFormInstanceRecordFixture {

	public DDMFormInstanceRecordFixture(Group group, User user) {
		_group = group;
		_user = user;

		_setUpDDMFormInstanceFixture();
	}

	public DDMFormInstanceRecord createDDMFormInstanceRecord()
		throws Exception {

		return createDDMFormInstanceRecord(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			LocaleUtil.US);
	}

	public DDMFormInstanceRecord createDDMFormInstanceRecord(
			String name, String description, Locale locale)
		throws Exception {

		DDMFormInstanceRecord ddmFormInstanceRecord = _addDDMFormInstanceRecord(
			name, description, locale);

		_ddmFormInstanceRecords.add(ddmFormInstanceRecord);

		return ddmFormInstanceRecord;
	}

	public List<DDMFormInstanceRecord> getDdmFormInstanceRecords() {
		return _ddmFormInstanceRecords;
	}

	public ServiceContext getServiceContext() throws Exception {
		return ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), _user.getUserId());
	}

	protected DDMFormValues createDDMFormValues(
		DDMForm ddmForm, Locale locale) {

		return DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm, DDMFormValuesTestUtil.createAvailableLocales(locale),
			locale);
	}

	protected DDMFormFieldValue createLocalizedDDMFormFieldValue(
		String name, Map<Locale, String> values, Locale locale) {

		Value localizedValue = new LocalizedValue(locale);

		for (Map.Entry<Locale, String> value : values.entrySet()) {
			localizedValue.addString(value.getKey(), value.getValue());
		}

		return DDMFormValuesTestUtil.createDDMFormFieldValue(
			name, localizedValue);
	}

	private DDMFormInstanceRecord _addDDMFormInstanceRecord(
			String name, String description, Locale locale)
		throws Exception {

		DDMFormInstanceRecordTestHelper ddmFormInstanceRecordTestHelper =
			_getDDMFormInstanceRecordTestHelper(locale);

		Map<Locale, String> nameMap = new HashMap<>();

		nameMap.put(locale, name);

		Map<Locale, String> descriptionMap = new HashMap<>();

		descriptionMap.put(locale, description);

		DDMFormInstance ddmFormInstance =
			ddmFormInstanceRecordTestHelper.getDDMFormInstance();

		DDMStructure ddmStructure = ddmFormInstance.getStructure();

		DDMFormValues ddmFormValues = createDDMFormValues(
			ddmStructure.getDDMForm(), locale);

		DDMFormFieldValue nameDDMFormFieldValue =
			createLocalizedDDMFormFieldValue("name", nameMap, locale);

		ddmFormValues.addDDMFormFieldValue(nameDDMFormFieldValue);

		DDMFormFieldValue descriptionDDMFormFieldValue =
			createLocalizedDDMFormFieldValue(
				"description", descriptionMap, locale);

		ddmFormValues.addDDMFormFieldValue(descriptionDDMFormFieldValue);

		return ddmFormInstanceRecordTestHelper.addDDMFormInstanceRecord(
			ddmFormValues);
	}

	private DDMFormInstanceRecordTestHelper _getDDMFormInstanceRecordTestHelper(
			Locale locale)
		throws Exception {

		return new DDMFormInstanceRecordTestHelper(
			_group, _user, _ddmFormInstanceFixture.addDDMFormInstance(locale));
	}

	private void _setUpDDMFormInstanceFixture() {
		_ddmFormInstanceFixture = new DDMFormInstanceFixture(_group, _user);
	}

	private DDMFormInstanceFixture _ddmFormInstanceFixture;
	private final List<DDMFormInstanceRecord> _ddmFormInstanceRecords =
		new ArrayList<>();
	private final Group _group;
	private final User _user;

}