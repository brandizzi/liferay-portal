package com.liferay.portal.search.ranking.web.internal.display.context;

public class SynonymSetsEntryDisplayContext {

	public SynonymSetsEntryDisplayContext(String synonmys) {
		this.synonyms = synonmys;
	}

	public String getSynonyms() {
		return this.synonyms;
	}

	private final String synonyms;

}