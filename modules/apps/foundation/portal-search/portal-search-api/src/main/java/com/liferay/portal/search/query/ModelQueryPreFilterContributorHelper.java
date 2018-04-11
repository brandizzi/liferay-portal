package com.liferay.portal.search.query;

import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.filter.BooleanFilter;

import aQute.bnd.annotation.ProviderType;

/**
 * @author Bryan Engler
 */
@ProviderType
public interface ModelQueryPreFilterContributorHelper {

	public void addPermissionFilter(
		BooleanFilter booleanFilter, String entryClassName,
		SearchContext searchContext);

	public void addSearchClassTypeIdsFilter(
		BooleanFilter fullQueryBooleanFilter, SearchContext searchContext);

	public void addStagingFilter(
		BooleanFilter booleanFilter, SearchContext searchContext);

	public void addStatusFilter(
		BooleanFilter booleanFilter, SearchContext searchContext);
}
