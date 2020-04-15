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

package com.liferay.portal.search.indexer;

import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerPostProcessor;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.security.permission.PermissionChecker;

import java.util.Collection;
import java.util.Objects;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

/**
 * @author Michael C. Han
 */
public class IndexerWrapper<T> implements Indexer<T> {

	public IndexerWrapper(Indexer<T> indexer) {
		this.indexer = indexer;
	}

	@Override
	public void delete(long companyId, String uid) throws SearchException {
		indexer.delete(companyId, uid);
	}

	@Override
	public void delete(T object) throws SearchException {
		indexer.delete(object);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		Class<? extends Object> clazz = object.getClass();

		if (!clazz.equals(getClass())) {
			return false;
		}

		IndexerWrapper<?> indexerWrapper = (IndexerWrapper<?>)object;

		return Objects.equals(indexer, indexerWrapper.indexer);
	}

	@Override
	public String getClassName() {
		return indexer.getClassName();
	}

	@Override
	public Document getDocument(T object) throws SearchException {
		return indexer.getDocument(object);
	}

	@Override
	public BooleanFilter getFacetBooleanFilter(
			String className, SearchContext searchContext)
		throws Exception {

		return indexer.getFacetBooleanFilter(className, searchContext);
	}

	@Override
	public BooleanQuery getFullQuery(SearchContext searchContext)
		throws SearchException {

		return indexer.getFullQuery(searchContext);
	}

	@Override
	public IndexerPostProcessor[] getIndexerPostProcessors() {
		return indexer.getIndexerPostProcessors();
	}

	@Override
	public String[] getSearchClassNames() {
		return indexer.getSearchClassNames();
	}

	@Override
	public String getSearchEngineId() {
		return indexer.getSearchEngineId();
	}

	/**
	 * @deprecated As of Judson (7.1.x), replaced by {@link
	 *             com.liferay.portal.sort.SortFieldBuilder}
	 */
	@Deprecated
	@Override
	public String getSortField(String orderByCol) {
		return indexer.getSortField(orderByCol);
	}

	@Override
	public Summary getSummary(
			Document document, String snippet, PortletRequest portletRequest,
			PortletResponse portletResponse)
		throws SearchException {

		return indexer.getSummary(
			document, snippet, portletRequest, portletResponse);
	}

	@Override
	public int hashCode() {
		return indexer.hashCode();
	}

	@Override
	public boolean hasPermission(
			PermissionChecker permissionChecker, String entryClassName,
			long entryClassPK, String actionId)
		throws Exception {

		return indexer.hasPermission(
			permissionChecker, entryClassName, entryClassPK, actionId);
	}

	@Override
	public boolean isCommitImmediately() {
		return indexer.isCommitImmediately();
	}

	@Override
	public boolean isFilterSearch() {
		return indexer.isFilterSearch();
	}

	@Override
	public boolean isIndexerEnabled() {
		return indexer.isIndexerEnabled();
	}

	@Override
	public boolean isPermissionAware() {
		return indexer.isPermissionAware();
	}

	@Override
	public boolean isStagingAware() {
		return indexer.isStagingAware();
	}

	@Override
	public boolean isVisible(long classPK, int status) throws Exception {
		return indexer.isVisible(classPK, status);
	}

	@Override
	public void postProcessContextBooleanFilter(
			BooleanFilter contextBooleanFilter, SearchContext searchContext)
		throws Exception {

		indexer.postProcessContextBooleanFilter(
			contextBooleanFilter, searchContext);
	}

	/**
	 * @deprecated As of Wilberforce (7.0.x), replaced by {@link
	 *             #postProcessContextBooleanFilter(BooleanFilter,
	 *             SearchContext)}
	 */
	@Deprecated
	@Override
	public void postProcessContextQuery(
			BooleanQuery contextQuery, SearchContext searchContext)
		throws Exception {

		indexer.postProcessContextQuery(contextQuery, searchContext);
	}

	@Override
	public void postProcessSearchQuery(
			BooleanQuery searchQuery, BooleanFilter fullQueryBooleanFilter,
			SearchContext searchContext)
		throws Exception {

		indexer.postProcessSearchQuery(
			searchQuery, fullQueryBooleanFilter, searchContext);
	}

	/**
	 * @deprecated As of Wilberforce (7.0.x), replaced by {@link
	 *             #postProcessSearchQuery(BooleanQuery, BooleanFilter,
	 *             SearchContext)}
	 */
	@Deprecated
	@Override
	public void postProcessSearchQuery(
			BooleanQuery searchQuery, SearchContext searchContext)
		throws Exception {

		indexer.postProcessSearchQuery(searchQuery, searchContext);
	}

	@Override
	public void registerIndexerPostProcessor(
		IndexerPostProcessor indexerPostProcessor) {

		indexer.registerIndexerPostProcessor(indexerPostProcessor);
	}

	@Override
	public void reindex(Collection<T> objects) throws SearchException {
		indexer.reindex(objects);
	}

	@Override
	public void reindex(String className, long classPK) throws SearchException {
		indexer.reindex(className, classPK);
	}

	@Override
	public void reindex(String[] ids) throws SearchException {
		indexer.reindex(ids);
	}

	@Override
	public void reindex(T object) throws SearchException {
		indexer.reindex(object);
	}

	@Override
	public Hits search(SearchContext searchContext) throws SearchException {
		return indexer.search(searchContext);
	}

	@Override
	public Hits search(
			SearchContext searchContext, String... selectedFieldNames)
		throws SearchException {

		return indexer.search(searchContext, selectedFieldNames);
	}

	@Override
	public long searchCount(SearchContext searchContext)
		throws SearchException {

		return indexer.searchCount(searchContext);
	}

	@Override
	public void setIndexerEnabled(boolean indexerEnabled) {
		indexer.setIndexerEnabled(indexerEnabled);
	}

	@Override
	public void unregisterIndexerPostProcessor(
		IndexerPostProcessor indexerPostProcessor) {

		indexer.unregisterIndexerPostProcessor(indexerPostProcessor);
	}

	protected final Indexer<T> indexer;

}