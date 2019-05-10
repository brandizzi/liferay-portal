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

package com.liferay.portal.search.ranking.web.internal.index;

import com.liferay.portal.kernel.util.ListUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author Bryan Engler
 */
public class Ranking {

	public static <T, V extends T> List<T> toList(List<V> list) {
		if (list != null) {
			return new ArrayList<>(list);
		}

		return new ArrayList<>();
	}

	public List<String> getAliases() {
		return Collections.unmodifiableList(_aliases);
	}

	public Date getDisplayDate() {
		return _displayDate;
	}

	public List<String> getHiddenIds() {
		return Collections.unmodifiableList(_hiddenIds);
	}

	public String getIndex() {
		return _index;
	}

	public Date getModifiedDate() {
		return _modifiedDate;
	}

	public List<Pin> getPins() {
		return Collections.unmodifiableList(_pins);
	}

	public String getQueryString() {
		return _queryString;
	}

	public int getStatus() {
		return _status;
	}

	public String getUid() {
		return _uid;
	}

	public void setAliases(String... aliases) {
		_aliases = ListUtil.toList(aliases);
	}

	public void setDisplayDate(Date displayDate) {
		_displayDate = displayDate;
	}

	public void setHiddenIds(List<String> hiddenDocuments) {
		_hiddenIds = toList(hiddenDocuments);
	}

	public void setIndex(String index) {
		_index = index;
	}

	public void setModifiedDate(Date modifiedDate) {
		_modifiedDate = modifiedDate;
	}

	public void setPins(List<Pin> pins) {
		_pins = toList(pins);
	}

	public void setQueryString(String queryString) {
		_queryString = queryString;
	}

	public void setStatus(int status) {
		_status = status;
	}

	public void setUid(String uid) {
		_uid = uid;
	}

	public static class Pin {

		public Pin(int position, String id) {
			_position = position;
			_id = id;
		}

		public String getId() {
			return _id;
		}

		public int getPosition() {
			return _position;
		}

		private final String _id;
		private final int _position;

	}

	private List<String> _aliases = new ArrayList<>();
	private Date _displayDate;
	private List<String> _hiddenIds = new ArrayList<>();
	private String _index;
	private Date _modifiedDate;
	private List<Pin> _pins = new ArrayList<>();
	private String _queryString;
	private int _status;
	private String _uid;

}