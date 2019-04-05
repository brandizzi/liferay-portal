package com.liferay.portal.search.ranking.web.internal.display.context;

/* FIXME: REMOVE MOCK */
public class SynonymSetsMock {

	public SynonymSetsMock(int id, String synonmys, String status) {
		this.id = id;
		this.synonyms = synonmys;
		this.status = status;
	}

	public int getId() {
		return this.id;
	}

	public String getStatus() {
		return this.status;
	}

	public String getSynonyms() {
		return this.synonyms;
	}

	public boolean isApproved() {
		return APPROVED.equals(this.status);
	}

	private static String APPROVED = "APPROVED";

	private final int id;
	private final String status;
	private final String synonyms;

}