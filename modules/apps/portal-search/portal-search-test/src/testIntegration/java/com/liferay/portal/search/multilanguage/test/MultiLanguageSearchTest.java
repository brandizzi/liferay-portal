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

package com.liferay.portal.search.multilanguage.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.settings.LocalizedValuesMap;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.SearchContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.workflow.WorkflowThreadLocal;
import com.liferay.portal.search.test.internal.util.UserSearchFixture;
import com.liferay.portal.search.test.journal.util.JournalArticleBlueprint;
import com.liferay.portal.search.test.journal.util.JournalArticleContent;
import com.liferay.portal.search.test.journal.util.JournalArticleDescription;
import com.liferay.portal.search.test.journal.util.JournalArticleSearchFixture;
import com.liferay.portal.search.test.journal.util.JournalArticleTitle;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Wade Cao
 */
@RunWith(Arquillian.class)
@Sync
public class MultiLanguageSearchTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		WorkflowThreadLocal.setEnabled(false);

		setUpJournalArticleSearchFixture();
		setUpUserSearchFixture();

		init();
		addJournalArticlesExpectedResults();
	}

	@After
	public void tearDown() throws Exception {
		journalArticleSearchFixture.tearDown();
		userSearchFixture.tearDown();
	}

	@Test
	public void testMultiLanguageArticleContent() throws Exception {
		String searchTerm = "content";

		List<Document> documents = _search(searchTerm, LocaleUtil.US);

		Assert.assertEquals(documents.toString(), 3, documents.size());

		assertSearchContent(documents, searchTerm);

		documents = _search(searchTerm, LocaleUtil.NETHERLANDS);

		Assert.assertEquals(documents.toString(), 3, documents.size());

		assertSearchContent(documents, searchTerm);
	}

	@Test
	public void testMultiLanguageArticleDescription() throws Exception {
		String searchTerm = "description";

		List<Document> documents = _search(searchTerm, LocaleUtil.US);

		Assert.assertEquals(documents.toString(), 3, documents.size());

		assertSearchDescription(documents, searchTerm);

		documents = _search(searchTerm, LocaleUtil.NETHERLANDS);

		Assert.assertEquals(documents.toString(), 3, documents.size());

		assertSearchDescription(documents, searchTerm);
	}

	@Test
	public void testMultiLanguageArticleTitle() throws Exception {
		String searchTerm = "english";

		List<Document> documents = _search(searchTerm, LocaleUtil.US);

		Assert.assertEquals(documents.toString(), 3, documents.size());

		assertSearchTitle(documents, searchTerm);

		documents = _search(searchTerm, LocaleUtil.NETHERLANDS);

		Assert.assertEquals(documents.toString(), 3, documents.size());

		assertSearchTitle(documents, searchTerm);
	}

	@Test
	public void testMultiLanguageEmtpySearch() throws Exception {
		String searchTerm = null;

		List<Document> documents = _search(searchTerm, LocaleUtil.US);

		Assert.assertEquals(documents.toString(), 3, documents.size());

		documents = _search(searchTerm, LocaleUtil.NETHERLANDS);

		Assert.assertEquals(documents.toString(), 3, documents.size());

		searchTerm = "no field value";

		documents = _search(searchTerm, LocaleUtil.US);

		Assert.assertEquals(documents.toString(), 0, documents.size());
	}

	protected JournalArticle addJournalArticle(
			Group group, User user,
			JournalArticleContent journalArticleContentParam,
			JournalArticleDescription journalArticleDescriptionParam,
			JournalArticleTitle journalArticleTitleParam)
		throws Exception {

		JournalArticle journalArticle = journalArticleSearchFixture.addArticle(
			new JournalArticleBlueprint() {
				{
					groupId = group.getGroupId();
					journalArticleContent = journalArticleContentParam;
					journalArticleDescription = journalArticleDescriptionParam;
					journalArticleTitle = journalArticleTitleParam;
					userId = user.getUserId();
				}
			});

		return journalArticle;
	}

	protected void addJournalArticlesExpectedResults() throws Exception {
		JournalArticleContent usJournalArticleContent =
			new JournalArticleContent() {
				{
					defaultLocale = LocaleUtil.US;
					name = "content";
					put(LocaleUtil.US, _usContent);
				}
			};
		JournalArticleDescription usJournalArticleDescription =
			new JournalArticleDescription() {
				{
					put(LocaleUtil.US, _usDescription);
				}
			};
		JournalArticleTitle usJournalArticleTitle = new JournalArticleTitle() {
			{
				put(LocaleUtil.US, _usTitle);
			}
		};

		JournalArticle usJournalArticle = addJournalArticle(
			_group, _user, usJournalArticleContent, usJournalArticleDescription,
			usJournalArticleTitle);

		_addExpectedValuesMaps(
			usJournalArticle, usJournalArticleContent,
			usJournalArticleDescription, usJournalArticleTitle);

		JournalArticleContent netherlandsUSJournalArticleContent =
			new JournalArticleContent() {
				{
					defaultLocale = LocaleUtil.US;
					name = "content";
					put(LocaleUtil.NETHERLANDS, _netherandsContent);
					put(LocaleUtil.US, _usContent);
				}
			};
		JournalArticleDescription netherlandsUSJournalArticleDescription =
			new JournalArticleDescription() {
				{
					put(LocaleUtil.NETHERLANDS, _netherlandsDescription);
					put(LocaleUtil.US, _usDescription);
				}
			};
		JournalArticleTitle netherlandsUSJournalArticleTitle =
			new JournalArticleTitle() {
				{
					put(LocaleUtil.NETHERLANDS, _netherlandsTitle);
					put(LocaleUtil.US, _usTitle);
				}
			};

		JournalArticle netherlandsUSJournalArticle = addJournalArticle(
			_group, _user, netherlandsUSJournalArticleContent,
			netherlandsUSJournalArticleDescription,
			netherlandsUSJournalArticleTitle);

		_addExpectedValuesMaps(
			netherlandsUSJournalArticle, netherlandsUSJournalArticleContent,
			netherlandsUSJournalArticleDescription,
			netherlandsUSJournalArticleTitle);

		JournalArticleContent netherlandsJournalArticleContent =
			new JournalArticleContent() {
				{
					defaultLocale = LocaleUtil.NETHERLANDS;
					name = "content";
					put(LocaleUtil.NETHERLANDS, _usContent);
				}
			};
		JournalArticleDescription netherlandsJournalArticleDescription =
			new JournalArticleDescription() {
				{
					put(LocaleUtil.NETHERLANDS, _usDescription);
				}
			};
		JournalArticleTitle netherlandsJournalArticleTitle =
			new JournalArticleTitle() {
				{
					put(LocaleUtil.NETHERLANDS, _usTitle);
				}
			};

		JournalArticle netherlandsJournalArticle = addJournalArticle(
			_group, _user, netherlandsJournalArticleContent,
			netherlandsJournalArticleDescription,
			netherlandsJournalArticleTitle);

		_addExpectedValuesMaps(
			netherlandsJournalArticle, netherlandsJournalArticleContent,
			netherlandsJournalArticleDescription,
			netherlandsJournalArticleTitle);
	}

	protected void assertSearch(
		List<Document> documents,
		Map<String, ? extends LocalizedValuesMap> localizedValuesMaps,
		String prefix, String message) {

		documents.forEach(
			document -> {
				String articleId = document.get(Field.ARTICLE_ID);

				LocalizedValuesMap localizedValuesMap = localizedValuesMaps.get(
					articleId);

				FieldValuesAssert.assertFieldValues(
					_getStringKeyMap(localizedValuesMap, prefix), prefix,
					document, message);
			});
	}

	protected void assertSearchContent(
		List<Document> documents, String message) {

		assertSearch(documents, journalArticleContentsMap, "content", message);
	}

	protected void assertSearchDescription(
		List<Document> documents, String message) {

		assertSearch(
			documents, journalArticleDescriptionsMap, "description", message);
	}

	protected void assertSearchTitle(List<Document> documents, String message) {
		assertSearch(documents, journalArticleTitlesMap, "title", message);
	}

	protected SearchContext getSearchContext(String keywords) throws Exception {
		return userSearchFixture.getSearchContext(keywords);
	}

	protected void init() throws Exception {
		Group group = userSearchFixture.addGroup();

		User user = userSearchFixture.addUser(
			RandomTestUtil.randomString(), group);

		_usTitle = "english";
		_netherlandsTitle = "engels";
		_usDescription = "description";
		_netherlandsDescription = "beschrijving";
		_usContent = "content";
		_netherandsContent = "inhoud";

		_group = group;
		_user = user;
		_indexer = _indexerRegistry.getIndexer(JournalArticle.class);
	}

	protected void setUpJournalArticleSearchFixture() throws Exception {
		journalArticleSearchFixture.setUp();

		_journalArticles = journalArticleSearchFixture.getJournalArticles();
	}

	protected void setUpUserSearchFixture() throws Exception {
		userSearchFixture.setUp();

		_groups = userSearchFixture.getGroups();
		_users = userSearchFixture.getUsers();
	}

	protected Map<String, JournalArticleContent> journalArticleContentsMap =
		new HashMap<>();
	protected Map<String, JournalArticleDescription>
		journalArticleDescriptionsMap = new HashMap<>();
	protected final JournalArticleSearchFixture journalArticleSearchFixture =
		new JournalArticleSearchFixture();
	protected Map<String, JournalArticleTitle> journalArticleTitlesMap =
		new HashMap<>();
	protected final UserSearchFixture userSearchFixture =
		new UserSearchFixture();

	private void _addExpectedValuesMaps(
		JournalArticle journalArticle,
		JournalArticleContent journalArticleContent,
		JournalArticleDescription journalArticleDescription,
		JournalArticleTitle journalArticleTitle) {

		String articleId = journalArticle.getArticleId();

		journalArticleContentsMap.put(articleId, journalArticleContent);
		journalArticleDescriptionsMap.put(articleId, journalArticleDescription);
		journalArticleTitlesMap.put(articleId, journalArticleTitle);
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

	private String _getStringKey(String prefix, Locale locale) {
		return prefix + StringPool.UNDERLINE + LocaleUtil.toLanguageId(locale);
	}

	private Map<String, String> _getStringKeyMap(
		LocalizedValuesMap localizedValuesMap, String prefix) {

		Map<Locale, String> localesMap = localizedValuesMap.getValues();
		Map<String, String> stringsMap = new HashMap<>();

		localesMap.forEach(
			(locale, value) -> stringsMap.put(
				_getStringKey(prefix, locale), value));

		return stringsMap;
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

	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups;

	private Indexer<JournalArticle> _indexer;

	@DeleteAfterTestRun
	private List<JournalArticle> _journalArticles;

	private String _netherandsContent;
	private String _netherlandsDescription;
	private String _netherlandsTitle;
	private String _usContent;
	private String _usDescription;
	private User _user;

	@DeleteAfterTestRun
	private List<User> _users;

	private String _usTitle;

}