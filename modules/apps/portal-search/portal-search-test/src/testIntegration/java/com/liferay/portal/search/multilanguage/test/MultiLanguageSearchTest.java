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
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
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
		assertSearch("content", "content_en_US", documents, searchTerm);
		assertSearch("content", "content_nl_NL", documents, searchTerm);

		documents = _search(searchTerm, LocaleUtil.NETHERLANDS);

		Assert.assertEquals(documents.toString(), 3, documents.size());
		assertSearch("content", "content_en_US", documents, searchTerm);
		assertSearch("content", "content_nl_NL", documents, searchTerm);
	}

	@Test
	public void testMultiLanguageArticleDescription() throws Exception {
		String searchTerm = "description";

		List<Document> documents = _search(searchTerm, LocaleUtil.US);

		Assert.assertEquals(documents.toString(), 3, documents.size());
		assertSearch("description", "description_en_US", documents, searchTerm);
		assertSearch("description", "description_nl_NL", documents, searchTerm);

		documents = _search(searchTerm, LocaleUtil.NETHERLANDS);

		Assert.assertEquals(documents.toString(), 3, documents.size());
		assertSearch("description", "description_en_US", documents, searchTerm);
		assertSearch("description", "description_nl_NL", documents, searchTerm);
	}

	@Test
	public void testMultiLanguageArticleTitle() throws Exception {
		String searchTerm = "english";

		List<Document> documents = _search(searchTerm, LocaleUtil.US);

		Assert.assertEquals(documents.toString(), 3, documents.size());
		assertSearch("title", "title", documents, searchTerm);
		assertSearch("title", "localized_title_nl_NL", documents, searchTerm);

		documents = _search(searchTerm, LocaleUtil.NETHERLANDS);

		Assert.assertEquals(documents.toString(), 3, documents.size());
		assertSearch("title", "title", documents, searchTerm);
		assertSearch("title", "localized_title_nl_NL", documents, searchTerm);
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

	protected Group addGroup() throws Exception {
		Group group = GroupTestUtil.addGroup();

		_groups.add(group);

		return group;
	}

	protected JournalArticle addJournalArticle(
			Group group, User user,
			JournalArticleContent journalArticleContentParam,
			JournalArticleDescription journalArticleDescriptionParam,
			JournalArticleTitle journalArticleTitleParam)
		throws Exception {

		return journalArticleSearchFixture.addArticle(
			new JournalArticleBlueprint() {
				{
					groupId = group.getGroupId();
					journalArticleContent = journalArticleContentParam;
					journalArticleDescription = journalArticleDescriptionParam;
					journalArticleTitle = journalArticleTitleParam;
					userId = user.getUserId();
				}
			});
	}

	protected void addJournalArticlesExpectedResults() throws Exception {
		Map<String, Map<String, Map<String, String>>>
			articleIdTitleExpectedMap = new HashMap<>();
		Map<String, Map<String, Map<String, String>>>
			articleIdDescriptionExpectedMap = new HashMap<>();
		Map<String, Map<String, Map<String, String>>>
			articleIdContentExpectedMap = new HashMap<>();

		_indexTypeExpectedMap.put("content", articleIdContentExpectedMap);

		_indexTypeExpectedMap.put(
			"description", articleIdDescriptionExpectedMap);
		_indexTypeExpectedMap.put("title", articleIdTitleExpectedMap);

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

		_addExpectedValueMap_1(
			articleIdTitleExpectedMap, articleIdDescriptionExpectedMap,
			articleIdContentExpectedMap, usJournalArticle);

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

		_addExpectedValueMap_2(
			articleIdTitleExpectedMap, articleIdDescriptionExpectedMap,
			articleIdContentExpectedMap, netherlandsUSJournalArticle);

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

		_addExpectedValueMap_3(
			articleIdTitleExpectedMap, articleIdDescriptionExpectedMap,
			articleIdContentExpectedMap, netherlandsJournalArticle);
	}

	protected User addUser() throws Exception {
		User user = UserTestUtil.addUser();

		_users.add(user);

		return user;
	}

	protected void assertSearch(
		String indexType, String prefix, List<Document> documents,
		String searchTerm) {

		Map<String, Map<String, Map<String, String>>> articleIdExpectedMap =
			_indexTypeExpectedMap.get(indexType);

		documents.forEach(
			document -> {
				Map<String, Map<String, String>> expected =
					articleIdExpectedMap.get(document.get(Field.ARTICLE_ID));

				FieldValuesAssert.assertFieldValues(
					expected.get(prefix), prefix, document, searchTerm);
			});
	}

	protected SearchContext getSearchContext(String keywords) throws Exception {
		return userSearchFixture.getSearchContext(keywords);
	}

	protected void init() throws Exception {
		_usTitle = "english";
		_netherlandsTitle = "engels";
		_usDescription = "description";
		_netherlandsDescription = "beschrijving";
		_usContent = "content";
		_netherandsContent = "inhoud";

		_group = addGroup();
		_user = addUser();
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

	protected final JournalArticleSearchFixture journalArticleSearchFixture =
		new JournalArticleSearchFixture();
	protected final UserSearchFixture userSearchFixture =
		new UserSearchFixture();

	@SuppressWarnings("serial")
	private void _addExpectedValueMap_1(
		Map<String, Map<String, Map<String, String>>> articleIdTitleExpectedMap,
		Map<String, Map<String, Map<String, String>>>
			articleIdDescriptionExpectedMap,
		Map<String, Map<String, Map<String, String>>>
			articleIdContentExpectedMap,
		JournalArticle journalArticle) {

		Map<String, String> titleStrings = new HashMap<String, String>() {
			{
				put("title_en_US", _usTitle);
			}
		};

		Map<String, String> localizedTitleStrings =
			new HashMap<String, String>() {
				{
					put("localized_title_nl_NL", _usTitle);
					put("localized_title_nl_NL_sortable", _usTitle);
				}
			};

		Map<String, Map<String, String>> titlesMap =
			new HashMap<String, Map<String, String>>() {
				{
					put("title", titleStrings);
					put("localized_title_nl_NL", localizedTitleStrings);
				}
			};

		articleIdTitleExpectedMap.put(journalArticle.getArticleId(), titlesMap);

		Map<String, String> descStrings_US = new HashMap<String, String>() {
			{
				put("description_en_US", _usDescription);
			}
		};

		Map<String, String> descStrings_NL = new HashMap<>();

		Map<String, Map<String, String>> descMap =
			new HashMap<String, Map<String, String>>() {
				{
					put("description_en_US", descStrings_US);
					put("description_nl_NL", descStrings_NL);
				}
			};

		articleIdDescriptionExpectedMap.put(
			journalArticle.getArticleId(), descMap);

		Map<String, String> contentStrings_US = new HashMap<String, String>() {
			{
				put("content_en_US", _usContent);
			}
		};

		Map<String, String> contentStrings_NL = new HashMap<>();

		Map<String, Map<String, String>> contentsMap =
			new HashMap<String, Map<String, String>>() {
				{
					put("content_en_US", contentStrings_US);
					put("content_nl_NL", contentStrings_NL);
				}
			};

		articleIdContentExpectedMap.put(
			journalArticle.getArticleId(), contentsMap);
	}

	@SuppressWarnings("serial")
	private void _addExpectedValueMap_2(
		Map<String, Map<String, Map<String, String>>> articleIdTitleExpectedMap,
		Map<String, Map<String, Map<String, String>>>
			articleIdDescriptionExpectedMap,
		Map<String, Map<String, Map<String, String>>>
			articleIdContentExpectedMap,
		JournalArticle journalArticle) {

		HashMap<String, String> titleStrings = new HashMap<String, String>() {
			{
				put("title_en_US", _usTitle);
				put("title_nl_NL", _netherlandsTitle);
			}
		};

		HashMap<String, String> localizedTitleStrings =
			new HashMap<String, String>() {
				{
					put("localized_title_nl_NL", _netherlandsTitle);
					put("localized_title_nl_NL_sortable", _netherlandsTitle);
				}
			};

		Map<String, Map<String, String>> titlesMap =
			new HashMap<String, Map<String, String>>() {
				{
					put("title", titleStrings);
					put("localized_title_nl_NL", localizedTitleStrings);
				}
			};

		articleIdTitleExpectedMap.put(journalArticle.getArticleId(), titlesMap);

		Map<String, String> descStrings_US = new HashMap<String, String>() {
			{
				put("description_en_US", _usDescription);
			}
		};

		Map<String, String> descStrings_NL = new HashMap<String, String>() {
			{
				put("description_nl_NL", _netherlandsDescription);
			}
		};

		Map<String, Map<String, String>> descMap =
			new HashMap<String, Map<String, String>>() {
				{
					put("description_en_US", descStrings_US);
					put("description_nl_NL", descStrings_NL);
				}
			};

		articleIdDescriptionExpectedMap.put(
			journalArticle.getArticleId(), descMap);

		Map<String, String> contentStrings_US = new HashMap<String, String>() {
			{
				put("content_en_US", _usContent);
			}
		};

		Map<String, String> contentStrings_NL = new HashMap<String, String>() {
			{
				put("content_nl_NL", _netherandsContent);
			}
		};

		Map<String, Map<String, String>> contentsMap =
			new HashMap<String, Map<String, String>>() {
				{
					put("content_en_US", contentStrings_US);
					put("content_nl_NL", contentStrings_NL);
				}
			};

		articleIdContentExpectedMap.put(
			journalArticle.getArticleId(), contentsMap);
	}

	@SuppressWarnings("serial")
	private void _addExpectedValueMap_3(
		Map<String, Map<String, Map<String, String>>> articleIdTitleExpectedMap,
		Map<String, Map<String, Map<String, String>>>
			articleIdDescriptionExpectedMap,
		Map<String, Map<String, Map<String, String>>>
			articleIdContentExpectedMap,
		JournalArticle journalArticle) {

		HashMap<String, String> titleStrings = new HashMap<String, String>() {
			{
				put("title_nl_NL", _usTitle);
			}
		};

		HashMap<String, String> localizedTitleStrings =
			new HashMap<String, String>() {
				{
					put("localized_title_nl_NL", _usTitle);
					put("localized_title_nl_NL_sortable", _usTitle);
				}

			};

		Map<String, Map<String, String>> titlesMap =
			new HashMap<String, Map<String, String>>() {
				{
					put("title", titleStrings);
					put("localized_title_nl_NL", localizedTitleStrings);
				}
			};

		articleIdTitleExpectedMap.put(journalArticle.getArticleId(), titlesMap);

		Map<String, String> descStrings_NL = new HashMap<String, String>() {
			{
				put("description_nl_NL", _usDescription);
			}
		};

		Map<String, String> descStrings_US = new HashMap<>();

		Map<String, Map<String, String>> descMap =
			new HashMap<String, Map<String, String>>() {
				{
					put("description_en_US", descStrings_US);
					put("description_nl_NL", descStrings_NL);
				}
			};

		articleIdDescriptionExpectedMap.put(
			journalArticle.getArticleId(), descMap);

		HashMap<String, String> contentStrings_NL =
			new HashMap<String, String>() {

				{
					put("content_nl_NL", _usContent);
				}

			};

		HashMap<String, String> contentStrings_US = new HashMap<>();

		Map<String, Map<String, String>> contentsMap =
			new HashMap<String, Map<String, String>>() {
				{
					put("content_en_US", contentStrings_US);
					put("content_nl_NL", contentStrings_NL);
				}
			};

		articleIdContentExpectedMap.put(
			journalArticle.getArticleId(), contentsMap);
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

	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups;

	private Indexer<JournalArticle> _indexer;
	private final Map<String, Map<String, Map<String, Map<String, String>>>>
		_indexTypeExpectedMap = new HashMap<>(3);

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