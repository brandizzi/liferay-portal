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

package com.liferay.journal.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalArticleBuilder;
import com.liferay.journal.test.util.JournalArticleContent;
import com.liferay.journal.test.util.JournalArticleTitle;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.ThemeLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.SearchContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.TimeZoneUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.service.test.ServiceTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.test.LayoutTestUtil;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.portlet.MockPortletResponse;
import org.springframework.mock.web.portlet.MockRenderRequest;

/**
 * @author Adam Brandizzi
 */
@RunWith(Arquillian.class)
public class JournalArticleIndexerLocalizedContentSummaryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_indexer = _indexerRegistry.getIndexer(JournalArticle.class);

		_journalArticleBuilder = new JournalArticleBuilder();

		_journalArticleBuilder.setGroupId(_group.getGroupId());

		_user = UserTestUtil.addUser();

		ServiceTestUtil.setUser(TestPropsValues.getUser());

		CompanyThreadLocal.setCompanyId(TestPropsValues.getCompanyId());
	}

	@Test
	public void testMultiLanguageSummaryWithBrazilLocale() throws Exception {
		String content = "Clocks are great for telling time";

		setContent(
			new JournalArticleContent() {
				{
					defaultLocale = LocaleUtil.US;
					name = "content";

					put(LocaleUtil.US, content);
				}
			});

		String title = "Clocks";

		setTitle(
			new JournalArticleTitle() {
				{
					put(LocaleUtil.US, title);
				}
			});

		addArticle();

		String originalContent = "Soccer teams are fun to watch";
		String translatedContent =
			"Times de futebol são divertidos de assistir";

		setContent(
			new JournalArticleContent() {
				{
					defaultLocale = LocaleUtil.US;
					name = "content";

					put(LocaleUtil.US, originalContent);
					put(LocaleUtil.BRAZIL, translatedContent);
				}
			});

		String originalTitle = "Soccer";
		String translatedTitle = "Futebol";

		setTitle(
			new JournalArticleTitle() {
				{
					put(LocaleUtil.US, originalTitle);
					put(LocaleUtil.BRAZIL, translatedTitle);
				}
			});

		addArticle();

		Locale locale = LocaleUtil.BRAZIL;
		String searchTerm = "time";

		List<Document> documents = _search(searchTerm, locale);

		Assert.assertEquals(documents.toString(), 2, documents.size());

		Document document1 = getDocumentByUSTitle(documents, title);

		assertSummary(title, content, document1, locale);

		Document document2 = getDocumentByUSTitle(documents, originalTitle);

		assertSummary(translatedTitle, translatedContent, document2, locale);
	}

	@Test
	public void testMultiLanguageSummaryWithUSLocale() throws Exception {
		String content = "Clocks are great for telling time";

		setContent(
			new JournalArticleContent() {
				{
					defaultLocale = LocaleUtil.US;
					name = "content";

					put(LocaleUtil.US, content);
				}
			});

		String title = "Clocks";

		setTitle(
			new JournalArticleTitle() {
				{
					put(LocaleUtil.US, title);
				}
			});

		addArticle();

		String originalContent = "Soccer teams are fun to watch";
		String translatedContent =
			"Times de futebol são divertidos de assistir";

		setContent(
			new JournalArticleContent() {
				{
					defaultLocale = LocaleUtil.US;
					name = "content";

					put(LocaleUtil.US, originalContent);
					put(LocaleUtil.BRAZIL, translatedContent);
				}
			});

		String originalTitle = "Soccer";
		String translatedTitle = "Futebol";

		setTitle(
			new JournalArticleTitle() {
				{
					put(LocaleUtil.US, originalTitle);
					put(LocaleUtil.BRAZIL, translatedTitle);
				}
			});

		addArticle();

		Locale displayLocale = LocaleUtil.US;
		String searchTerm = "time";

		List<Document> documents = _search(searchTerm, displayLocale);

		Assert.assertEquals(documents.toString(), 2, documents.size());

		Document document1 = getDocumentByUSTitle(documents, title);

		assertSummary(title, content, document1, displayLocale);

		Document document2 = getDocumentByUSTitle(documents, originalTitle);

		assertSummary(originalTitle, originalContent, document2, displayLocale);
	}

	protected JournalArticle addArticle() {
		try {
			return _journalArticleBuilder.addArticle();
		}
		catch (RuntimeException re) {
			throw re;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected void assertSummary(
			String title, String content, Document document, Locale locale)
		throws Exception {

		PortletRequest portletRequest = createPortletRequest(locale);

		Summary summary = getSummary(document, portletRequest);

		Assert.assertEquals(content, summary.getContent());
		Assert.assertEquals(title, summary.getTitle());
	}

	protected HttpServletRequest createHttpServletRequest(
		PortletRequest portletRequest) {

		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute(
			JavaConstants.JAVAX_PORTLET_REQUEST, portletRequest);

		return httpServletRequest;
	}

	protected HttpServletResponse createHttpServletResponse() {
		return new MockHttpServletResponse();
	}

	protected PortletRequest createPortletRequest(Locale locale)
		throws Exception {

		MockRenderRequest portletRequest = new MockRenderRequest();

		HttpServletRequest request = createHttpServletRequest(portletRequest);

		HttpServletResponse response = createHttpServletResponse();

		ThemeDisplay themeDisplay = createThemeDisplay(request, response);

		portletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		request.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		portletRequest.addPreferredLocale(locale);

		return portletRequest;
	}

	protected PortletResponse createPortletResponse() {
		return new MockPortletResponse();
	}

	protected ThemeDisplay createThemeDisplay(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(
			CompanyLocalServiceUtil.getCompany(_group.getCompanyId()));
		themeDisplay.setLayout(LayoutTestUtil.addLayout(_group));

		LayoutSet layoutSet = _group.getPublicLayoutSet();

		themeDisplay.setLayoutSet(layoutSet);

		Theme theme = ThemeLocalServiceUtil.getTheme(
			_group.getCompanyId(), layoutSet.getThemeId());

		themeDisplay.setLookAndFeel(theme, null);

		themeDisplay.setRealUser(_user);
		themeDisplay.setRequest(httpServletRequest);
		themeDisplay.setResponse(httpServletResponse);
		themeDisplay.setTimeZone(TimeZoneUtil.getDefault());
		themeDisplay.setUser(_user);

		return themeDisplay;
	}

	protected Document getDocumentByUSTitle(
		List<Document> documents, String title) {

		Stream<Document> stream = documents.stream();

		Optional<Document> documentOptional = stream.filter(
			document -> document.get(LocaleUtil.US, "title").equals(title)
		).findAny();

		Assert.assertTrue(documentOptional.isPresent());

		return documentOptional.get();
	}

	protected Summary getSummary(
			Document document, PortletRequest portletRequest)
		throws Exception {

		return _indexer.getSummary(
			document, document.get(Field.SNIPPET), portletRequest,
			createPortletResponse());
	}

	protected void setContent(JournalArticleContent journalArticleContent) {
		_journalArticleBuilder.setContent(journalArticleContent);
	}

	protected void setTitle(JournalArticleTitle journalArticleTitle) {
		_journalArticleBuilder.setTitle(journalArticleTitle);
	}

	private SearchContext _getSearchContext(String searchTerm, Locale locale)
		throws Exception {

		SearchContext searchContext = SearchContextTestUtil.getSearchContext(
			_group.getGroupId());

		searchContext.setKeywords(searchTerm);

		searchContext.setLocale(locale);

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setSelectedFieldNames(StringPool.STAR);

		return searchContext;
	}

	private List<Document> _search(String searchTerm, Locale locale) {
		try {
			SearchContext searchContext = _getSearchContext(searchTerm, locale);

			Hits hits = _indexer.search(searchContext);

			return hits.toList();
		}
		catch (RuntimeException re) {
			throw re;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Inject
	private static IndexerRegistry _indexerRegistry;

	@DeleteAfterTestRun
	private Group _group;

	private Indexer<JournalArticle> _indexer;
	private JournalArticleBuilder _journalArticleBuilder;

	@DeleteAfterTestRun
	private User _user;

}