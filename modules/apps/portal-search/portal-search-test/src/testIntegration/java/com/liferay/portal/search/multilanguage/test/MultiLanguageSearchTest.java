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
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFolderLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.search.JournalArticleBlueprint;
import com.liferay.journal.test.util.search.JournalArticleContent;
import com.liferay.journal.test.util.search.JournalArticleDescription;
import com.liferay.journal.test.util.search.JournalArticleSearchFixture;
import com.liferay.journal.test.util.search.JournalArticleTitle;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.faceted.searcher.FacetedSearcher;
import com.liferay.portal.kernel.search.facet.faceted.searcher.FacetedSearcherManager;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.settings.LocalizedValuesMap;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.SearchContextTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.workflow.WorkflowThreadLocal;
import com.liferay.portal.search.constants.SearchContextAttributes;
import com.liferay.portal.search.test.documentlibrary.util.DLFolderSearchFixture;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.users.admin.test.util.search.UserSearchFixture;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Wade Cao
 * @author Adam Brandizzi
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

		setUpUserAndGroup();

		setUpDLFolders();

		setUpJournalArticles();
	}

	@After
	public void tearDown() throws Exception {
		journalArticleSearchFixture.tearDown();

		userSearchFixture.tearDown();
	}

	@Test
	public void testMultiLanguageArticleContent() throws Exception {
		String searchTerm = "content";

		List<Document> documents = search(searchTerm, LocaleUtil.US);

		Assert.assertEquals(documents.toString(), 3, documents.size());

		assertSearchContent(documents, searchTerm);

		documents = search(searchTerm, LocaleUtil.NETHERLANDS);

		Assert.assertEquals(documents.toString(), 3, documents.size());

		assertSearchContent(documents, searchTerm);
	}

	@Test
	public void testMultiLanguageArticleDescription() throws Exception {
		String searchTerm = "description";

		List<Document> documents = search(searchTerm, LocaleUtil.US);

		Assert.assertEquals(documents.toString(), 3, documents.size());

		assertSearchDescription(documents, searchTerm);

		documents = search(searchTerm, LocaleUtil.NETHERLANDS);

		Assert.assertEquals(documents.toString(), 3, documents.size());

		assertSearchDescription(documents, searchTerm);
	}

	@Test
	public void testMultiLanguageArticleTitle() throws Exception {
		String searchTerm = "english";

		List<Document> documents = search(searchTerm, LocaleUtil.US);

		Assert.assertEquals(documents.toString(), 3, documents.size());

		assertSearchTitle(documents, searchTerm);

		documents = search(searchTerm, LocaleUtil.NETHERLANDS);

		Assert.assertEquals(documents.toString(), 3, documents.size());

		assertSearchTitle(documents, searchTerm);
	}

	@Test
	public void testMultiLanguageEmptySearch() throws Exception {
		String searchTerm = null;

		List<Document> documents = search(searchTerm, LocaleUtil.US, true);

		Assert.assertEquals(documents.toString(), 3, documents.size());

		documents = search(searchTerm, LocaleUtil.NETHERLANDS, true);

		Assert.assertEquals(documents.toString(), 3, documents.size());

		searchTerm = "no field value";

		documents = search(searchTerm, LocaleUtil.US, true);

		Assert.assertEquals(documents.toString(), 0, documents.size());
	}

	@Test
	public void testMultiLanguageJournalArticleDLFile() throws Exception {
		addDLFolder("english title", "english content");

		String searchTerm = "english";

		List<Document> documents = search(searchTerm, LocaleUtil.US);

		Assert.assertEquals(documents.toString(), 4, documents.size());
		assertJournalArticleCount(documents, 3);
		assertDLFolderCount(documents, 1);

		documents = search(searchTerm, LocaleUtil.NETHERLANDS);

		Assert.assertEquals(documents.toString(), 4, documents.size());
		assertJournalArticleCount(documents, 3);
		assertDLFolderCount(documents, 1);
	}

	protected DLFolder addDLFolder(String keywords, String content)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), _user.getUserId());

		DLFolder folder = folderSearchFixture.addDLFolderAndDLFileEntry(
			_group, _user, keywords, content, serviceContext);

		return folder;
	}

	protected void addExpectedValuesMaps(
		JournalArticle journalArticle,
		JournalArticleContent journalArticleContent,
		JournalArticleDescription journalArticleDescription,
		JournalArticleTitle journalArticleTitle) {

		String articleId = journalArticle.getArticleId();

		journalArticleContentsMap.put(articleId, journalArticleContent);
		journalArticleDescriptionsMap.put(articleId, journalArticleDescription);
		journalArticleTitlesMap.put(articleId, journalArticleTitle);
	}

	protected JournalArticle addJournalArticle(
			JournalArticleContent journalArticleContentParam,
			JournalArticleDescription journalArticleDescriptionParam,
			JournalArticleTitle journalArticleTitleParam)
		throws Exception {

		JournalArticle journalArticle = journalArticleSearchFixture.addArticle(
			new JournalArticleBlueprint() {
				{
					groupId = _group.getGroupId();
					journalArticleContent = journalArticleContentParam;
					journalArticleDescription = journalArticleDescriptionParam;
					journalArticleTitle = journalArticleTitleParam;
					userId = _user.getUserId();
				}
			});

		addExpectedValuesMaps(
			journalArticle, journalArticleContentParam,
			journalArticleDescriptionParam, journalArticleTitleParam);

		return journalArticle;
	}

	protected JournalArticle addNetherlandsJournalArticle(
			String content, String description, String title)
		throws Exception {

		JournalArticleContent journalArticleContent =
			new JournalArticleContent() {
				{
					defaultLocale = LocaleUtil.NETHERLANDS;
					name = "content";
					put(LocaleUtil.NETHERLANDS, content);
				}
			};
		JournalArticleDescription journalArticleDescription =
			new JournalArticleDescription() {
				{
					put(LocaleUtil.NETHERLANDS, description);
				}
			};
		JournalArticleTitle journalArticleTitle = new JournalArticleTitle() {
			{
				put(LocaleUtil.NETHERLANDS, title);
			}
		};

		return addJournalArticle(
			journalArticleContent, journalArticleDescription,
			journalArticleTitle);
	}

	protected JournalArticle addNetherlandsUSJournalArticle(
			String netherandsContent, String usContent,
			String netherlandsDescription, String usDescription,
			String netherlandsTitle, String usTitle)
		throws Exception {

		JournalArticleContent journalArticleContent =
			new JournalArticleContent() {
				{
					defaultLocale = LocaleUtil.US;
					name = "content";
					put(LocaleUtil.NETHERLANDS, netherandsContent);
					put(LocaleUtil.US, usContent);
				}
			};
		JournalArticleDescription journalArticleDescription =
			new JournalArticleDescription() {
				{
					put(LocaleUtil.NETHERLANDS, netherlandsDescription);
					put(LocaleUtil.US, usDescription);
				}
			};
		JournalArticleTitle journalArticleTitle = new JournalArticleTitle() {
			{
				put(LocaleUtil.NETHERLANDS, netherlandsTitle);
				put(LocaleUtil.US, usTitle);
			}
		};

		return addJournalArticle(
			journalArticleContent, journalArticleDescription,
			journalArticleTitle);
	}

	protected JournalArticle addUSJournalArticle(
			String content, String description, String title)
		throws Exception {

		JournalArticleContent journalArticleContent =
			new JournalArticleContent() {
				{
					defaultLocale = LocaleUtil.US;
					name = "content";
					put(LocaleUtil.US, content);
				}
			};
		JournalArticleDescription journalArticleDescription =
			new JournalArticleDescription() {
				{
					put(LocaleUtil.US, description);
				}
			};
		JournalArticleTitle journalArticleTitle = new JournalArticleTitle() {
			{
				put(LocaleUtil.US, title);
			}
		};

		return addJournalArticle(
			journalArticleContent, journalArticleDescription,
			journalArticleTitle);
	}

	protected void assertDLFolderCount(List<Document> documents, int count) {
		Assert.assertEquals(
			count, countByEntryClassName(documents, DLFolder.class.getName()));
	}

	protected void assertJournalArticleCount(
		List<Document> documents, int count) {

		Assert.assertEquals(
			count,
			countByEntryClassName(documents, JournalArticle.class.getName()));
	}

	protected void assertSearch(
		List<Document> documents,
		Map<String, ? extends LocalizedValuesMap> localizedValuesMaps,
		String prefix, String message) {

		documents.forEach(
			document -> {
				if (!Objects.equals(
						document.get(Field.ENTRY_CLASS_NAME),
						JournalArticle.class.getName())) {

					return;
				}

				String articleId = document.get(Field.ARTICLE_ID);

				LocalizedValuesMap localizedValuesMap = localizedValuesMaps.get(
					articleId);

				FieldValuesAssert.assertFieldValues(
					getStringKeyMap(localizedValuesMap, prefix), prefix,
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

	protected long countByEntryClassName(
		List<Document> documents, String entryClassName) {

		Stream<Document> stream = documents.stream();

		return stream.filter(
			document -> Objects.equals(
				document.get(Field.ENTRY_CLASS_NAME), entryClassName)
		).count();
	}

	protected SearchContext getSearchContext(String keywords) throws Exception {
		return userSearchFixture.getSearchContext(keywords);
	}

	protected SearchContext getSearchContext(
			String searchTerm, Locale locale, boolean enableEmptySearch)
		throws Exception {

		SearchContext searchContext = SearchContextTestUtil.getSearchContext(
			_group.getGroupId());

		searchContext.setAttribute(
			SearchContextAttributes.ATTRIBUTE_KEY_EMPTY_SEARCH,
			enableEmptySearch);
		searchContext.setEntryClassNames(
			new String[] {
				JournalArticle.class.getName(), DLFolder.class.getName()
			});
		searchContext.setKeywords(searchTerm);
		searchContext.setLocale(locale);

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setSelectedFieldNames(StringPool.STAR);

		return searchContext;
	}

	protected String getStringKey(String prefix, Locale locale) {
		return prefix + StringPool.UNDERLINE + LocaleUtil.toLanguageId(locale);
	}

	protected Map<String, String> getStringKeyMap(
		LocalizedValuesMap localizedValuesMap, String prefix) {

		Map<Locale, String> localesMap = localizedValuesMap.getValues();
		Map<String, String> stringsMap = new HashMap<>();

		localesMap.forEach(
			(locale, value) -> stringsMap.put(
				getStringKey(prefix, locale), value));

		return stringsMap;
	}

	protected List<Document> search(String searchTerm, Locale locale) {
		return search(searchTerm, locale, false);
	}

	protected List<Document> search(
		String searchTerm, Locale locale, boolean enableEmptySearch) {

		try {
			SearchContext searchContext = getSearchContext(
				searchTerm, locale, enableEmptySearch);

			FacetedSearcher facetedSearcher =
				facetedSearcherManager.createFacetedSearcher();

			Hits hits = facetedSearcher.search(searchContext);

			return hits.toList();
		}
		catch (RuntimeException re) {
			throw re;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected void setUpDLFolders() throws Exception {
		folderSearchFixture = new DLFolderSearchFixture(
			folderLocalService, fileEntryLocalService);

		folderSearchFixture.setUp();

		_folders = folderSearchFixture.getDLFolders();
	}

	protected void setUpJournalArticles() throws Exception {
		journalArticleSearchFixture = new JournalArticleSearchFixture(
			journalArticleLocalService);
		journalArticleSearchFixture.setUp();

		String netherlandsDescription = "beschrijving";
		String netherandsContent = "inhoud";
		String netherlandsTitle = "engels";
		String usDescription = "description";
		String usContent = "content";
		String usTitle = "english";

		addUSJournalArticle(usContent, usDescription, usTitle);
		addNetherlandsJournalArticle(usContent, usDescription, usTitle);
		addNetherlandsUSJournalArticle(
			netherandsContent, usContent, netherlandsDescription, usDescription,
			netherlandsTitle, usTitle);

		_journalArticles = journalArticleSearchFixture.getJournalArticles();
	}

	protected void setUpUserAndGroup() throws Exception {
		userSearchFixture.setUp();

		Group group = userSearchFixture.addGroup();

		User user = userSearchFixture.addUser(
			RandomTestUtil.randomString(), group);

		_groups = userSearchFixture.getGroups();
		_users = userSearchFixture.getUsers();

		_group = group;
		_user = user;
	}

	@Inject
	protected static FacetedSearcherManager facetedSearcherManager;

	@Inject
	protected DLFileEntryLocalService fileEntryLocalService;

	@Inject
	protected DLFolderLocalService folderLocalService;

	protected DLFolderSearchFixture folderSearchFixture;
	protected final Map<String, JournalArticleContent>
		journalArticleContentsMap = new HashMap<>();
	protected final Map<String, JournalArticleDescription>
		journalArticleDescriptionsMap = new HashMap<>();

	@Inject
	protected JournalArticleLocalService journalArticleLocalService;

	protected JournalArticleSearchFixture journalArticleSearchFixture;
	protected final Map<String, JournalArticleTitle> journalArticleTitlesMap =
		new HashMap<>();
	protected final UserSearchFixture userSearchFixture =
		new UserSearchFixture();

	@DeleteAfterTestRun
	private List<DLFolder> _folders;

	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups;

	@DeleteAfterTestRun
	private List<JournalArticle> _journalArticles;

	private User _user;

	@DeleteAfterTestRun
	private List<User> _users;

}