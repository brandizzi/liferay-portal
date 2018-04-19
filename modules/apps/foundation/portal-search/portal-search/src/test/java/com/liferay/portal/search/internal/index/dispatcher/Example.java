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

package com.liferay.portal.search.internal.index.dispatcher;

import com.liferay.expando.kernel.model.ExpandoBridge;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.model.TrashedModel;
import com.liferay.portal.kernel.model.WorkflowedModel;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.trash.kernel.model.TrashEntry;

import java.io.Serializable;

import java.util.Date;
import java.util.Map;

/**
 * @author Adam Brandizzi
 */
public class Example
	implements BaseModel<Example>, TrashedModel, WorkflowedModel {

	public Example(Class<?> baseModelClass, long classPK) {
		_baseModelClass = baseModelClass;
		_classPK = classPK;
	}

	@Override
	public Object clone() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int compareTo(Example o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ExpandoBridge getExpandoBridge() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Class<?> getModelClass() {
		return _baseModelClass;
	}

	@Override
	public String getModelClassName() {
		return _baseModelClass.getName();
	}

	@Override
	public Serializable getPrimaryKeyObj() {
		return _classPK;
	}

	@Override
	public int getStatus() {
		throw new UnsupportedOperationException();
	}

	@Override
	public long getStatusByUserId() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getStatusByUserName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getStatusByUserUuid() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Date getStatusDate() {
		throw new UnsupportedOperationException();
	}

	@Override
	public TrashEntry getTrashEntry() throws PortalException {
		throw new UnsupportedOperationException();
	}

	@Override
	public long getTrashEntryClassPK() {
		throw new UnsupportedOperationException();
	}

	@Override
	public TrashHandler getTrashHandler() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isApproved() {
		if (_status == WorkflowConstants.STATUS_APPROVED) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isCachedModel() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isDenied() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isDraft() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isEntityCacheEnabled() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isEscapedModel() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isExpired() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isFinderCacheEnabled() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isInactive() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isIncomplete() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isInTrash() {
		if (_status == WorkflowConstants.STATUS_IN_TRASH) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isInTrashContainer() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isInTrashExplicitly() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isInTrashImplicitly() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isNew() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isPending() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isScheduled() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void resetOriginalValues() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setCachedModel(boolean cachedModel) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setExpandoBridgeAttributes(BaseModel<?> baseModel) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setExpandoBridgeAttributes(ExpandoBridge expandoBridge) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setExpandoBridgeAttributes(ServiceContext serviceContext) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setNew(boolean n) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setPrimaryKeyObj(Serializable primaryKeyObj) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setStatus(int status) {
		_status = status;
	}

	@Override
	public void setStatusByUserId(long statusByUserId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setStatusByUserName(String statusByUserName) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setStatusByUserUuid(String statusByUserUuid) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setStatusDate(Date statusDate) {
		throw new UnsupportedOperationException();
	}

	@Override
	public CacheModel<Example> toCacheModel() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Example toEscapedModel() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Example toUnescapedModel() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toXmlString() {
		throw new UnsupportedOperationException();
	}

	private final Class<?> _baseModelClass;
	private final long _classPK;
	private int _status;

}