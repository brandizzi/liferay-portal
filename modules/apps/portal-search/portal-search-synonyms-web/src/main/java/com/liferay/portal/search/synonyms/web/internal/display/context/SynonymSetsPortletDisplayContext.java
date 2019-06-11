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

import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.synonym.SynonymIndexer;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Filipe Oshiro
 */
public class SynonymSetsPortletDisplayContext {

	public SynonymSetsPortletDisplayContext(
			HttpServletRequest httpServletRequest, RenderRequest renderRequest,
			RenderResponse renderResponse, SynonymIndexer synonymIndexer)
		throws PortalException {

		_httpServletRequest = httpServletRequest;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;

		_themeDisplay = (ThemeDisplay)_httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		List<SynonymSetsEntryDisplayContext>
			synonymSetsEntryDisplayContextList = new ArrayList<>();

		for (String synonymSet :
				synonymIndexer.getSynonymSets(
					_themeDisplay.getCompanyId(),
					"liferay_filter_synonym_en")) {

			synonymSetsEntryDisplayContextList.add(
				new SynonymSetsEntryDisplayContext(synonymSet));
		}

		_searchContainer = getSearchContainer();

		_searchContainer.setSearch(true);
		_searchContainer.setTotal(synonymSetsEntryDisplayContextList.size());
		_searchContainer.setResults(synonymSetsEntryDisplayContextList);
	}

	public List<DropdownItem> getActionDropdownItems(
		SynonymSetsEntryDisplayContext synonymSetsEntryDisplayContext) {

		return new DropdownItemList() {
			{
				add(
					dropdownItem -> {
						dropdownItem.setHref(
							_renderResponse.createActionURL(),
							ActionRequest.ACTION_NAME,
							"updateSynonymsEntryAction", "synonymSetsInput", "",
							"originalSynonymSetsInput",
							synonymSetsEntryDisplayContext.getSynonyms());
						dropdownItem.setIcon("times");
						dropdownItem.setLabel(
							LanguageUtil.get(_httpServletRequest, "delete"));
						dropdownItem.setQuickAction(true);
					});

				add(
					dropdownItem -> {
						dropdownItem.setHref(
							_renderResponse.createRenderURL(),
							"mvcRenderCommandName", "updateSynonymsEntryRender",
							"redirect",
							PortalUtil.getCurrentURL(_httpServletRequest),
							"synonymSets",
							synonymSetsEntryDisplayContext.getSynonyms());

						dropdownItem.setLabel(
							LanguageUtil.get(_httpServletRequest, "edit"));
					});
			}
		};
	}

	public List<DropdownItem> getActionDropdownMultipleItems() {
		return new DropdownItemList() {
			{
				add(
					dropdownItem -> {
						dropdownItem.putData(
							"action", "deleteMultipleSynonyms");
						dropdownItem.setIcon("times-circle");
						dropdownItem.setLabel(
							LanguageUtil.get(_httpServletRequest, "delete"));
						dropdownItem.setQuickAction(true);
					});
			}
		};
	}

	public CreationMenu getCreationMenu() {
		return new CreationMenu() {
			{
				addPrimaryDropdownItem(
					dropdownItem -> {
						dropdownItem.setHref(
							_renderResponse.createRenderURL(),
							"mvcRenderCommandName", "updateSynonymsEntryRender",
							"redirect",
							PortalUtil.getCurrentURL(_httpServletRequest));
						dropdownItem.setLabel(
							LanguageUtil.get(
								_httpServletRequest, "new-synonym-set"));
					});
			}
		};
	}

	public SearchContainer<SynonymSetsEntryDisplayContext>
		getSearchContainer() {

		if (_searchContainer == null) {
			_searchContainer = new SearchContainer<>(
				_renderRequest, _getPortletURL(), null, "there-are-no-entries");

			_searchContainer.setId("synonymSetsEntries");
			_searchContainer.setRowChecker(
				new EmptyOnClickRowChecker(_renderResponse));
		}

		return _searchContainer;
	}

	public boolean isDisabledManagementBar() throws PortalException {
		if (_hasResults()) {
			return false;
		}

		return true;
	}

	private PortletURL _getPortletURL() {
		PortletURL portletURL = _renderResponse.createRenderURL();

		portletURL.setParameter("mvcPath", "/view.jsp");

		return portletURL;
	}

	private int _getTotalItems() {
		SearchContainer<?> searchContainer = getSearchContainer();

		return searchContainer.getTotal();
	}

	private boolean _hasResults() throws PortalException {
		if (_getTotalItems() > 0) {
			return true;
		}

		return false;
	}

	private final HttpServletRequest _httpServletRequest;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;
	private SearchContainer<SynonymSetsEntryDisplayContext> _searchContainer;
	private final ThemeDisplay _themeDisplay;

}