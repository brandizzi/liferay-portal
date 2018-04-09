package com.liferay.portal.search.query;

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.filter.BooleanFilter;

/**
 * @author Bryan Engler
 */
@ProviderType
public interface ModelQueryPreFilterContributorHelper {

	public void addClassTypeIdsFilter(
		BooleanFilter fullQueryBooleanFilter, SearchContext searchContext);

	public void addPermissionFilter(
		BooleanFilter booleanFilter, String entryClassName,
		SearchContext searchContext);

	public void addStagingFilter(
		BooleanFilter booleanFilter, SearchContext searchContext);

	public void addWorkflowStatusesFilter(
		BooleanFilter booleanFilter, SearchContext searchContext);

}