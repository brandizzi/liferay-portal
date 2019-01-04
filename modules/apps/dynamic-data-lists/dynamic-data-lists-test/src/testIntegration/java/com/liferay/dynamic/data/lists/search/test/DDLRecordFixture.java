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

package com.liferay.dynamic.data.lists.search.test;

import com.liferay.dynamic.data.lists.helper.DDLRecordSetTestHelper;
import com.liferay.dynamic.data.lists.helper.DDLRecordTestHelper;
import com.liferay.dynamic.data.lists.model.DDLRecord;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.lists.service.DDLRecordSetLocalService;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.StorageType;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestHelper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author Luan Maoski
 * @author Lucas Marques
 */
public class DDLRecordFixture {

	public DDLRecordFixture(
			DDLRecordSetLocalService ddlRecordSetLocalService, Group group,
			User user)
		throws Exception {

		_ddlRecordSetLocalService = ddlRecordSetLocalService;
		_group = group;
		_user = user;

		setUpDDMStructureTestHelper();

		setUpDDLRecordTestHelper();
	}

	public DDLRecord createDDLRecord(
			String name, String description, Locale locale)
		throws Exception {

		DDLRecord ddlRecord = _addDDLRecord(name, description, locale);

		_ddlRecords.add(ddlRecord);

		return ddlRecord;
	}

	public List<DDLRecord> getDDLRecords() {
		return _ddlRecords;
	}

	public void updateDisplaySettings(Locale locale) throws Exception {
		Group group = GroupTestUtil.updateDisplaySettings(
			_group.getGroupId(), null, locale);

		_group.setModelAttributes(group.getModelAttributes());
	}

	protected DDLRecordSet addDDLRecordSet() throws Exception {
		DDLRecordSetTestHelper ddlRecordSetTestHelper =
			new DDLRecordSetTestHelper(_group);

		DDMStructure ddmStructure = _ddmStructureTestHelper.addStructure(
			createDDMForm(LocaleUtil.US), StorageType.JSON.toString());

		return ddlRecordSetTestHelper.addRecordSet(ddmStructure);
	}

	protected DDMForm createDDMForm(Locale... locales) {
		DDMForm ddmForm = DDMFormTestUtil.createDDMForm(
			DDMFormTestUtil.createAvailableLocales(locales), locales[0]);

		DDMFormField nameDDMFormField = DDMFormTestUtil.createTextDDMFormField(
			"name", true, false, false);

		nameDDMFormField.setIndexType("keyword");

		ddmForm.addDDMFormField(nameDDMFormField);

		DDMFormField descriptionDDMFormField =
			DDMFormTestUtil.createTextDDMFormField(
				"description", true, false, false);

		descriptionDDMFormField.setIndexType("text");

		ddmForm.addDDMFormField(descriptionDDMFormField);

		return ddmForm;
	}

	protected DDMFormValues createDDMFormValues(Locale... locales)
		throws Exception {

		DDLRecordSet ddlRecordSet = _ddlRecordTestHelper.getRecordSet();

		DDMStructure ddmStructure = ddlRecordSet.getDDMStructure();

		return DDMFormValuesTestUtil.createDDMFormValues(
			ddmStructure.getDDMForm(),
			DDMFormValuesTestUtil.createAvailableLocales(locales), locales[0]);
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

	protected DDLRecordSet getDDLRecordSet(long recordSetId)
		throws PortalException {

		return _ddlRecordSetLocalService.getRecordSet(recordSetId);
	}

	protected void setUpDDLRecordTestHelper() throws Exception {
		DDLRecordSet ddlRecordSet = addDDLRecordSet();

		_ddlRecordTestHelper = new DDLRecordTestHelper(
			_group, _user, ddlRecordSet);
	}

	protected void setUpDDMStructureTestHelper() throws Exception {
		_ddmStructureTestHelper = new DDMStructureTestHelper(
			PortalUtil.getClassNameId(DDLRecordSet.class), _group);
	}

	private DDLRecord _addDDLRecord(
			String name, String description, Locale locale)
		throws Exception {

		Map<Locale, String> nameMap = new HashMap<>();

		nameMap.put(locale, name);

		Map<Locale, String> descriptionMap = new HashMap<>();

		descriptionMap.put(locale, description);

		Set<Locale> localesSet = nameMap.keySet();

		Locale[] locales = new Locale[nameMap.size()];

		localesSet.toArray(locales);

		DDMFormValues ddmFormValues = createDDMFormValues(locales);

		DDMFormFieldValue nameDDMFormFieldValue =
			createLocalizedDDMFormFieldValue("name", nameMap, locale);

		ddmFormValues.addDDMFormFieldValue(nameDDMFormFieldValue);

		DDMFormFieldValue descriptionDDMFormFieldValue =
			createLocalizedDDMFormFieldValue(
				"description", descriptionMap, locale);

		ddmFormValues.addDDMFormFieldValue(descriptionDDMFormFieldValue);

		return _ddlRecordTestHelper.addRecord(
			ddmFormValues, WorkflowConstants.ACTION_PUBLISH);
	}

	private final List<DDLRecord> _ddlRecords = new ArrayList<>();
	private final DDLRecordSetLocalService _ddlRecordSetLocalService;
	private DDLRecordTestHelper _ddlRecordTestHelper;
	private DDMStructureTestHelper _ddmStructureTestHelper;
	private final Group _group;
	private final User _user;

}