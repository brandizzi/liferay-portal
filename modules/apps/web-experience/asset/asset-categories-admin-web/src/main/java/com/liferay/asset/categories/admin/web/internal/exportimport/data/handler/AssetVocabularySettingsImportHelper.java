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

package com.liferay.asset.categories.admin.web.internal.exportimport.data.handler;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.ClassType;
import com.liferay.asset.kernel.model.ClassTypeReader;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portlet.asset.util.AssetVocabularySettingsHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Rafael Praxedes
 */
public class AssetVocabularySettingsImportHelper
	extends AssetVocabularySettingsHelper {

	public AssetVocabularySettingsImportHelper(
		String settings, ClassNameLocalService classNameLocalService,
		long groupId, Locale locale, JSONObject settingsMetadataJSONObject) {

		super(settings);

		_classNameLocalService = classNameLocalService;
		_groupId = groupId;
		_locale = locale;
		_settingsMetadataJSONObject = settingsMetadataJSONObject;

		updateSettings();
	}

	public String getSettings() {
		return super.toString();
	}

	public void updateSettings() {
		List<Long> newClassNameIds = new ArrayList<>();
		List<Long> newClassTypePKs = new ArrayList<>();
		List<Boolean> newRequireds = new ArrayList<>();

		fillClassNameIdsAndClassTypePKs(
			getClassNameIdsAndClassTypePKs(), false, newClassNameIds,
			newClassTypePKs, newRequireds);

		fillClassNameIdsAndClassTypePKs(
			getRequiredClassNameIdsAndClassTypePKs(), true, newClassNameIds,
			newClassTypePKs, newRequireds);

		long[] newClassNameIdsArray = ArrayUtil.toArray(
			newClassNameIds.toArray(new Long[newClassNameIds.size()]));

		long[] newClassTypePKsArray = ArrayUtil.toArray(
			newClassTypePKs.toArray(new Long[newClassTypePKs.size()]));

		boolean[] newRequiredsArray = ArrayUtil.toArray(
			newRequireds.toArray(new Boolean[newRequireds.size()]));

		setClassNameIdsAndClassTypePKs(
			newClassNameIdsArray, newClassTypePKsArray, newRequiredsArray);
	}

	protected boolean existClassName(long classNameId) {
		if (classNameId == AssetCategoryConstants.ALL_CLASS_NAME_ID) {
			return false;
		}

		JSONObject metadataJSONObject = getMetadataJSONObject(classNameId);

		String className = metadataJSONObject.getString("className");

		if (_classNameLocalService.fetchClassName(className) != null) {
			return true;
		}
		else {
			return false;
		}
	}

	protected void fillClassNameIdsAndClassTypePKs(
		String[] classNameIdsAndClassTypePKs, boolean required,
		List<Long> newClassNameIds, List<Long> newClassTypePKs,
		List<Boolean> newRequireds) {

		for (String classNameIdAndClassTypePK : classNameIdsAndClassTypePKs) {
			long classNameId = getClassNameId(classNameIdAndClassTypePK);

			if (!existClassName(classNameId)) {
				continue;
			}

			long classTypePK = getClassTypePK(classNameIdAndClassTypePK);

			newClassNameIds.add(getNewClassNameId(classNameId));
			newClassTypePKs.add(getNewClassTypePK(classNameId, classTypePK));
			newRequireds.add(required);
		}
	}

	protected JSONObject getMetadataJSONObject(long classNameId) {
		return _settingsMetadataJSONObject.getJSONObject(
			String.valueOf(classNameId));
	}

	protected long getNewClassNameId(long oldClassNameId) {
		if (oldClassNameId == AssetCategoryConstants.ALL_CLASS_NAME_ID) {
			return AssetCategoryConstants.ALL_CLASS_NAME_ID;
		}

		String oldClassName = _settingsMetadataJSONObject.getString(
			String.valueOf(oldClassNameId));

		return _classNameLocalService.getClassNameId(oldClassName);
	}

	protected long getNewClassTypePK(long classNameId, long classTypePK) {
		if (classTypePK == AssetCategoryConstants.ALL_CLASS_TYPE_PK) {
			return AssetCategoryConstants.ALL_CLASS_TYPE_PK;
		}

		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.
				getAssetRendererFactoryByClassNameId(classNameId);

		ClassTypeReader classTypeReader =
			assetRendererFactory.getClassTypeReader();

		List<ClassType> availableClassTypes =
			classTypeReader.getAvailableClassTypes(
				new long[] {_groupId}, _locale);

		JSONObject metadataJSONObject = getMetadataJSONObject(classNameId);

		String classTypeName = metadataJSONObject.getString("classTypeName");

		for (ClassType classType : availableClassTypes) {
			if (classType.getName().equals(classTypeName)) {
				return classType.getClassTypeId();
			}
		}

		return AssetCategoryConstants.ALL_CLASS_TYPE_PK;
	}

	private final ClassNameLocalService _classNameLocalService;
	private final long _groupId;
	private final Locale _locale;
	private final JSONObject _settingsMetadataJSONObject;

}