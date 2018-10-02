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

package com.liferay.asset.categories.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalArticleBlueprint;
import com.liferay.journal.test.util.JournalArticleContent;
import com.liferay.journal.test.util.JournalArticleSearchFixture;
import com.liferay.journal.test.util.JournalArticleTitle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.faceted.searcher.FacetedSearcher;
import com.liferay.portal.kernel.search.facet.faceted.searcher.FacetedSearcherManager;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.workflow.WorkflowThreadLocal;
import com.liferay.portal.search.facet.category.CategoryFacetFactory;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.users.admin.test.util.search.UserSearchFixture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.After;
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
public class CategoryMultiLanguageSearchTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		WorkflowThreadLocal.setEnabled(false);

		setUpJournalArticleSearchFixture();
		setUpUserSearchFixture();

		_assetCategoryList = new ArrayList<>();
		_assetVocabularyList = new ArrayList<>();
		_updateJournalArticleList = new ArrayList<>();

		Group group = userSearchFixture.addGroup();

		user = userSearchFixture.addUser(RandomTestUtil.randomString(), group);
	}

	@After
	public void tearDown() throws Exception {
		journalArticleSearchFixture.tearDown();
		userSearchFixture.tearDown();
	}

	@Test
	public void testChineseCategories() throws Exception {
		String categoryTitle = "你好";
		String webContentTitle = "whatever";

		Group group = addGroup(LocaleUtil.CHINA);

		AssetVocabulary assetVocabulary = addVocabulary(group);

		AssetCategory category = addCategory(
			group, assetVocabulary, categoryTitle, LocaleUtil.CHINA);

		addJournalArticle(
			group, webContentTitle, webContentTitle, category,
			LocaleUtil.CHINA);

		assertCategoryInSearchResults(
			group, categoryTitle, category, LocaleUtil.CHINA);
	}

	@Test
	public void testEnglishCategories() throws Exception {
		String categoryTitle = "testEngine";
		String webContentTitle = "testContent";
		Group group = addGroup(LocaleUtil.US);

		AssetVocabulary assetVocabulary = addVocabulary(group);

		AssetCategory category = addCategory(
			group, assetVocabulary, categoryTitle, LocaleUtil.US);

		addJournalArticle(
			group, webContentTitle, webContentTitle, category, LocaleUtil.US);

		assertCategoryInSearchResults(
			group, categoryTitle, category, LocaleUtil.US);
	}

	@Test
	public void testJapaneseCategories() throws Exception {
		String vocabularyTitle = "ボキャブラリ";
		String categoryTitle1 = "東京";
		String categoryTitle2 = "京都";
		String webContentTitle1 = "豊島区";
		String webContentTitle2 = "伏見区";
		Group group = addGroup(LocaleUtil.JAPAN);

		AssetVocabulary assetVocabulary = addVocabulary(group, vocabularyTitle);

		AssetCategory category1 = addCategory(
			group, assetVocabulary, categoryTitle1, LocaleUtil.JAPAN);

		AssetCategory category2 = addCategory(
			group, assetVocabulary, categoryTitle2, LocaleUtil.JAPAN);

		addJournalArticle(
			group, webContentTitle1, webContentTitle1, category1,
			LocaleUtil.JAPAN);
		addJournalArticle(
			group, webContentTitle2, webContentTitle2, category2,
			LocaleUtil.JAPAN);

		assertCategoryInSearchResults(
			group, categoryTitle1, category1, LocaleUtil.JAPAN);
		assertCategoryInSearchResults(
			group, categoryTitle2, category2, LocaleUtil.JAPAN);
	}

	protected AssetCategory addCategory(
			Group group, AssetVocabulary assetVocabulary, String title,
			Locale locale)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), user.getUserId());

		Map<Locale, String> titleMap = new HashMap<Locale, String>() {
			{
				put(locale, title);
			}
		};

		Locale previousLocale = LocaleThreadLocal.getSiteDefaultLocale();

		LocaleThreadLocal.setSiteDefaultLocale(locale);

		AssetCategory assetCategory = assetCategoryLocalService.addCategory(
			user.getUserId(), group.getGroupId(),
			AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID, titleMap,
			new HashMap<>(), assetVocabulary.getVocabularyId(), new String[0],
			serviceContext);

		_assetCategoryList.add(assetCategory);

		LocaleThreadLocal.setSiteDefaultLocale(previousLocale);

		return assetCategory;
	}

	protected Group addGroup(Locale locale) throws Exception {
		Group group = userSearchFixture.addGroup();

		StringBundler sb = new StringBundler(6);

		sb.append("locales=");
		sb.append(locale.toString());
		sb.append("\n");
		sb.append("languageId=");
		sb.append(locale.toString());
		sb.append("\n");

		group.setTypeSettings(sb.toString());

		group.persist();

		return group;
	}

	protected JournalArticle addJournalArticle(
			Group group, String title, String content, AssetCategory category,
			Locale locale)
		throws Exception {

		JournalArticle journalArticle = addJournalArticle(
			group, title, content, locale, locale);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				group.getGroupId(), user.getUserId());

		serviceContext.setAssetCategoryIds(
			new long[] {category.getCategoryId()});

		journalArticle = updateJournalArticle(journalArticle, serviceContext);

		_updateJournalArticleList.add(journalArticle);

		return journalArticle;
	}

	protected JournalArticle addJournalArticle(
			Group group, String title, String content, Locale defLocale,
			Locale targetLocale)
		throws Exception {

		return journalArticleSearchFixture.addArticle(
			new JournalArticleBlueprint() {
				{
					groupId = group.getGroupId();
					journalArticleContent = new JournalArticleContent() {
						{
							defaultLocale = defLocale;
							name = "content";
							put(targetLocale, content);
						}
					};
					journalArticleTitle = new JournalArticleTitle() {
						{
							put(targetLocale, title);
						}
					};
				}
			});
	}

	protected AssetVocabulary addVocabulary(Group group) throws Exception {
		return addVocabulary(group, RandomTestUtil.randomString());
	}

	protected AssetVocabulary addVocabulary(Group group, String vocabularyTitle)
		throws Exception {

		AssetVocabulary bassetVocabulary =
			assetVocabularyLocalService.addDefaultVocabulary(
				group.getGroupId());

		bassetVocabulary.setTitle(vocabularyTitle);

		assetVocabularyLocalService.updateAssetVocabulary(bassetVocabulary);

		_assetVocabularyList.add(bassetVocabulary);

		return bassetVocabulary;
	}

	protected void assertCategoryInSearchResults(
			Group group, String keywords, AssetCategory category, Locale locale)
		throws Exception {

		SearchContext searchContext = getSearchContext(group, keywords, locale);

		Hits hits = search(searchContext);

		DocumentsAssert.assertValuesIgnoreRelevance(
			searchContext.getKeywords(), hits.getDocs(),
			Field.ASSET_CATEGORY_IDS, getCategoryIds(category));
	}

	protected List<String> getCategoryIds(AssetCategory... categories) {
		Stream<AssetCategory> stream = Arrays.asList(categories).stream();

		return stream.map(
			category -> String.valueOf(category.getCategoryId())
		).collect(
			Collectors.toList()
		);
	}

	protected SearchContext getSearchContext(
			Group group, String keywords, Locale locale)
		throws Exception {

		SearchContext searchContext = getSearchContext(keywords);

		searchContext.setGroupIds(new long[] {group.getGroupId()});
		searchContext.setLocale(locale);

		return searchContext;
	}

	protected SearchContext getSearchContext(String keywords) throws Exception {
		return userSearchFixture.getSearchContext(keywords);
	}

	protected Hits search(SearchContext searchContext) throws Exception {
		FacetedSearcher facetedSearcher =
			facetedSearcherManager.createFacetedSearcher();

		return facetedSearcher.search(searchContext);
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

	protected JournalArticle updateJournalArticle(
			JournalArticle journalArticle, ServiceContext serviceContext)
		throws Exception {

		return JournalTestUtil.updateArticle(
			journalArticle, journalArticle.getTitleMap(),
			journalArticle.getContent(), true, true, serviceContext);
	}

	@Inject
	protected static FacetedSearcherManager facetedSearcherManager;

	@Inject
	protected AssetCategoryLocalService assetCategoryLocalService;

	@Inject
	protected AssetVocabularyLocalService assetVocabularyLocalService;

	@Inject
	protected CategoryFacetFactory categoryFacetFactory;

	protected final JournalArticleSearchFixture journalArticleSearchFixture =
		new JournalArticleSearchFixture();
	protected User user;
	protected final UserSearchFixture userSearchFixture =
		new UserSearchFixture();

	@DeleteAfterTestRun
	private List<AssetCategory> _assetCategoryList;

	@DeleteAfterTestRun
	private List<AssetVocabulary> _assetVocabularyList;

	@DeleteAfterTestRun
	private List<Group> _groups;

	@DeleteAfterTestRun
	private List<JournalArticle> _journalArticles;

	@DeleteAfterTestRun
	private List<JournalArticle> _updateJournalArticleList;

	@DeleteAfterTestRun
	private List<User> _users;

}