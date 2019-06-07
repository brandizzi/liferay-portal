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

package com.liferay.portal.search.synonyms.web.internal.display.context;

/**
 * @author Filipe Oshiro
 */
public class SynonymSetsEntryDisplayContext {

	public SynonymSetsEntryDisplayContext(String synonmys) {
		_synonyms = synonmys;
	}

	public String getSynonyms() {
		return _synonyms;
	}

	public String getSynonymsFormatted() {
		return _synonyms.replace(",", ", ");
	}

	private final String _synonyms;

}