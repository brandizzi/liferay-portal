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

package com.liferay.portal.search.tuning.synonyms.web.internal.messaging;

import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.search.background.task.ReindexBackgroundTaskConstants;
import com.liferay.portal.kernel.search.background.task.ReindexStatusMessageSender;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.SynonymSetFilterHelper;

import java.util.Objects;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adam Brandizzi
 */
@Component(
	immediate = true, service = UpdateSynonymSetFiltersMessageListener.class
)
public class UpdateSynonymSetFiltersMessageListener
	extends BaseMessageListener {

	@Activate
	protected void activate() {
		_messageBus.registerMessageListener(
			DestinationNames.BACKGROUND_TASK_STATUS, this);
	}

	@Deactivate
	protected void deactivate() {
		_messageBus.unregisterMessageListener(
			DestinationNames.BACKGROUND_TASK_STATUS, this);
	}

	@Override
	protected void doReceive(Message message) throws Exception {
		String source = GetterUtil.getString(message.get("source"));

		if (!Objects.equals(
				source, ReindexStatusMessageSender.class.getName())) {

			return;
		}

		String phase = GetterUtil.getString(
			message.get(ReindexBackgroundTaskConstants.PHASE));

		if (!Objects.equals(phase, ReindexBackgroundTaskConstants.PORTAL_END)) {
			return;
		}

		long[] companyIds = GetterUtil.getLongValues(
			message.get(ReindexBackgroundTaskConstants.COMPANY_IDS));

		for (long companyId : companyIds) {
			_synonymSetFilterHelper.updateFilters(companyId);
		}
	}

	@Reference
	private MessageBus _messageBus;

	@Reference
	private SynonymSetFilterHelper _synonymSetFilterHelper;

}