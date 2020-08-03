
package com.liferay.portal.search.tuning.gsearch.api.query.contributor;

import com.liferay.portal.search.query.Query;
import com.liferay.portal.search.tuning.gsearch.api.query.context.QueryContext;

/**
 * Adds clauses/subqueries/signals to the main query.
 *
 * This is meant to be an interface for add on modules. An example case could
 * be adding a subquery based audience targeting information to increase
 * relevance for content targeted to current user.
 *
 * @author Petteri Karttunen
 */
public interface QueryContributor {

	/**
	 * Builds query.
	 *
	 * @param queryContext
	 * @return Query
	 * @throws Exception
	 */
	public Query buildQuery(QueryContext queryContext) throws Exception;

	/**
	 * Get occur.
	 *
	 * @return
	 */
	public String getOccur();

	/**
	 * Is contributor enabled.
	 *
	 * @return
	 */
	public boolean isEnabled();

}