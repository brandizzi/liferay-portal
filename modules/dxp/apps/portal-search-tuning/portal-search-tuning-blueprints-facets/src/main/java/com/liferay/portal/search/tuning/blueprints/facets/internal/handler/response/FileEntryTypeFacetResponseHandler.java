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

package com.liferay.portal.search.tuning.blueprints.facets.internal.handler.response;

import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.service.DLFileEntryTypeService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.search.aggregation.AggregationResult;
import com.liferay.portal.search.aggregation.bucket.Bucket;
import com.liferay.portal.search.aggregation.bucket.TermsAggregationResult;
import com.liferay.portal.search.tuning.blueprints.engine.context.SearchRequestContext;
import com.liferay.portal.search.tuning.blueprints.engine.message.Message;
import com.liferay.portal.search.tuning.blueprints.engine.message.Severity;
import com.liferay.portal.search.tuning.blueprints.engine.spi.facet.FacetResponseHandler;
import com.liferay.portal.search.tuning.blueprints.facets.constants.FacetJSONResponseKeys;

import java.util.Locale;
import java.util.Optional;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, property = "name=file_entry_type",
	service = FacetResponseHandler.class
)
public class FileEntryTypeFacetResponseHandler
	extends BaseFacetResponseHandler implements FacetResponseHandler {

	@Override
	public Optional<JSONObject> getResultObject(
		SearchRequestContext searchRequestContext,
		AggregationResult aggregationResult,
		JSONObject configurationJsonObject) {

		TermsAggregationResult termsAggregationResult =
			(TermsAggregationResult)aggregationResult;

		Locale locale = searchRequestContext.getLocale();

		JSONArray termsArray = JSONFactoryUtil.createJSONArray();

		for (Bucket bucket : termsAggregationResult.getBuckets()) {
			try {
				JSONObject jsonObject = _getDLFileEntryTypeObject(
					bucket, locale);

				if (jsonObject != null) {
					termsArray.put(jsonObject);
				}
			}
			catch (PortalException portalException) {
				searchRequestContext.addMessage(
					new Message(
						Severity.ERROR, "core",
						"core.error.file-entry-type-not-found",
						portalException.getMessage(), portalException,
						configurationJsonObject, null, null));

				if (_log.isWarnEnabled()) {
					_log.warn(portalException.getMessage(), portalException);
				}
			}
		}

		return createResultObject(termsArray, configurationJsonObject);
	}

	protected JSONObject _getDLFileEntryTypeObject(Bucket bucket, Locale locale)
		throws PortalException {

		long fileEntryTypeId = Long.valueOf(bucket.getKey());

		if (fileEntryTypeId == 0) {
			return null;
		}

		DLFileEntryType type = _dLFileEntryTypeService.getFileEntryType(
			fileEntryTypeId);

		JSONObject resultJsonObject = JSONFactoryUtil.createJSONObject();

		resultJsonObject.put(FacetJSONResponseKeys.FREQUENCY, bucket.getDocCount());
		resultJsonObject.put(
			FacetJSONResponseKeys.GROUP_NAME,
			_groupLocalService.getGroup(
				type.getGroupId()
			).getName(
				locale, true
			));
		resultJsonObject.put(FacetJSONResponseKeys.NAME, type.getName(locale, true));
		resultJsonObject.put(FacetJSONResponseKeys.VALUE, fileEntryTypeId);

		return resultJsonObject;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FileEntryTypeFacetResponseHandler.class);

	@Reference
	private DLFileEntryTypeService _dLFileEntryTypeService;

	@Reference
	private GroupLocalService _groupLocalService;

}