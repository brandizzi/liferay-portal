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
import com.liferay.journal.test.util.FieldValuesAssert;
import com.liferay.journal.test.util.JournalArticleBuilder;
import com.liferay.journal.test.util.search.JournalArticleContent;
import com.liferay.journal.test.util.search.JournalArticleTitle;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.QueryConfig;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchException;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.SearchContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.service.test.ServiceTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adam Brandizzi
 */
@RunWith(Arquillian.class)
public class JournalArticleIndexerMultiLanguageSearchGroupIdsTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		ServiceTestUtil.setUser(TestPropsValues.getUser());

		CompanyThreadLocal.setCompanyId(TestPropsValues.getCompanyId());

		Group japanGroup = addGroup(LocaleUtil.JAPAN);
		Group usGroup = addGroup(LocaleUtil.US);

		_japanGroup = japanGroup;
		_japanJournalArticleBuilder = createJournalArticleBuilder(japanGroup);
		_indexer = _indexerRegistry.getIndexer(JournalArticle.class);
		_usGroup = usGroup;
		_usJournalArticleBuilder = createJournalArticleBuilder(usGroup);
	}

	@Test
	public void testSearchWithJapanAndUSGroupIds() throws Exception {
		String japanContent = RandomTestUtil.randomString();
		String japanTitle = "新規作成";

		addJapanArticle(japanTitle, japanContent);

		String usContent = RandomTestUtil.randomString();
		String usTitle = "entity title";

		addUSArticle(usTitle, usContent);

		Map<String, String> japanContentMap = getContentMap(
			japanContent, LocaleUtil.JAPAN);
		Map<String, String> japanTitleMap = getTitleMap(
			japanTitle, LocaleUtil.JAPAN);
		Map<String, String> usContentMap = getContentMap(
			usContent, LocaleUtil.US);
		Map<String, String> usTitleMap = getTitleMap(usTitle, LocaleUtil.US);

		String searchTerm = "entity 作成";

		List<Document> documents = _searchDocuments(
			searchTerm, _usGroup, _japanGroup);

		Assert.assertEquals(documents.toString(), 2, documents.size());

		assertContentAndTitleValues(
			LocaleUtil.JAPAN, documents, japanContentMap, japanTitleMap,
			searchTerm);

		assertContentAndTitleValues(
			LocaleUtil.US, documents, usContentMap, usTitleMap, searchTerm);
	}

	@Test
	public void testSearchWithJapanGroupId() throws Exception {
		String japanContent = RandomTestUtil.randomString();
		String japanTitle = "新規作成";

		addJapanArticle(japanTitle, japanContent);

		String usContent = RandomTestUtil.randomString();
		String usTitle = "entity title";

		addUSArticle(usTitle, usContent);

		Map<String, String> contentMap = getContentMap(
			japanContent, LocaleUtil.JAPAN);
		Map<String, String> titleMap = getTitleMap(
			japanTitle, LocaleUtil.JAPAN);

		String searchTerm = "entity 作成";

		Document document = _searchDocument(searchTerm, _japanGroup);

		FieldValuesAssert.assertFieldValues(
			contentMap, "content", document, searchTerm);

		FieldValuesAssert.assertFieldValues(
			titleMap, "title", document, searchTerm);
	}

	@Test
	public void testSearchWithNoGroupIds() throws Exception {
		String japanContent = RandomTestUtil.randomString();
		String japanTitle = "新規作成";

		addJapanArticle(japanTitle, japanContent);

		String usContent = RandomTestUtil.randomString();
		String usTitle = "entity title";

		addUSArticle(usTitle, usContent);

		Map<String, String> japanContentMap = getContentMap(
			japanContent, LocaleUtil.JAPAN);
		Map<String, String> japanTitleMap = getTitleMap(
			japanTitle, LocaleUtil.JAPAN);
		Map<String, String> usContentMap = getContentMap(
			usContent, LocaleUtil.US);

		Map<String, String> usTitleMap = getTitleMap(usTitle, LocaleUtil.US);

		String searchTerm = "entity 作成";

		List<Document> documents = _searchDocuments(searchTerm);

		Assert.assertEquals(documents.toString(), 2, documents.size());

		assertContentAndTitleValues(
			LocaleUtil.JAPAN, documents, japanContentMap, japanTitleMap,
			searchTerm);

		assertContentAndTitleValues(
			LocaleUtil.US, documents, usContentMap, usTitleMap, searchTerm);
	}

	@Test
	public void testSearchWithUSGroupId() throws Exception {
		String japanContent = RandomTestUtil.randomString();
		String japanTitle = "新規作成";

		addJapanArticle(japanTitle, japanContent);

		String usContent = RandomTestUtil.randomString();
		String usTitle = "entity title";

		addUSArticle(usTitle, usContent);

		Map<String, String> contentMap = getContentMap(
			usContent, LocaleUtil.US);
		Map<String, String> titleMap = getTitleMap(usTitle, LocaleUtil.US);

		String searchTerm = "entity 作成";

		Document document = _searchDocument(searchTerm, _usGroup);

		FieldValuesAssert.assertFieldValues(
			contentMap, "content", document, searchTerm);

		FieldValuesAssert.assertFieldValues(
			titleMap, "title", document, searchTerm);
	}

	protected void addArticle(
			JournalArticleBuilder articleBuilder, Locale locale, String title,
			String content)
		throws Exception {

		articleBuilder.setContent(
			new JournalArticleContent() {
				{
					defaultLocale = locale;
					name = "content";

					put(locale, content);
				}
			});

		articleBuilder.setTitle(
			new JournalArticleTitle() {
				{
					put(locale, title);
				}
			});

		articleBuilder.addArticle();
	}

	protected Group addGroup(Locale locale) throws Exception, PortalException {
		Group group = GroupTestUtil.addGroup();

		String typeSettings = "languageId=" + locale + "\nlocales=" + locale;

		return _groupService.updateGroup(group.getGroupId(), typeSettings);
	}

	protected void addJapanArticle(String title, String content)
		throws Exception {

		addArticle(
			_japanJournalArticleBuilder, LocaleUtil.JAPAN, title, content);
	}

	protected void addUSArticle(String title, String content) throws Exception {
		addArticle(_usJournalArticleBuilder, LocaleUtil.US, title, content);
	}

	protected void assertContentAndTitleValues(
		Locale locale, List<Document> documents, Map<String, String> contentMap,
		Map<String, String> titleMap, String searchTerm) {

		Optional<Document> optional = getDocumentOptional(documents, locale);

		optional.ifPresent(
			document -> FieldValuesAssert.assertFieldValues(
				contentMap, "content", document, searchTerm));

		optional.ifPresent(
			document -> FieldValuesAssert.assertFieldValues(
				titleMap, "title", document, searchTerm));
	}

	protected JournalArticleBuilder createJournalArticleBuilder(Group group) {
		JournalArticleBuilder journalArticleBuilder =
			new JournalArticleBuilder();

		journalArticleBuilder.setGroupId(group.getGroupId());

		return journalArticleBuilder;
	}

	protected HashMap<String, String> getContentMap(
		String content, Locale locale) {

		return new HashMap<String, String>() {
			{
				put("content_" + locale, content);
			}
		};
	}

	protected Optional<Document> getDocumentOptional(
		List<Document> documents, Locale locale) {

		Stream<Document> stream = documents.stream();

		return stream.filter(
			document -> document.getField("title_" + locale) != null
		).findFirst();
	}

	protected HashMap<String, String> getTitleMap(String title, Locale locale) {
		return new HashMap<String, String>() {
			{
				put("title_" + locale, title);
			}
		};
	}

	private long[] _getGroupIds(Group[] groups) {
		if (ArrayUtil.isNotEmpty(groups)) {
			Long[] groupIds = ArrayUtil.toArray(
				groups, Group.GROUP_ID_ACCESSOR);

			return ArrayUtil.toArray(groupIds);
		}

		return null;
	}

	private Hits _getHits(String searchTerm, Group... groups)
		throws Exception, SearchException {

		SearchContext searchContext = _getSearchContext(
			searchTerm, _getGroupIds(groups));

		return _indexer.search(searchContext);
	}

	private SearchContext _getSearchContext(String searchTerm, long... groupIds)
		throws Exception {

		SearchContext searchContext = SearchContextTestUtil.getSearchContext(
			TestPropsValues.getGroupId());

		searchContext.setGroupIds(groupIds);

		searchContext.setKeywords(searchTerm);

		QueryConfig queryConfig = searchContext.getQueryConfig();

		queryConfig.setSelectedFieldNames(StringPool.STAR);

		return searchContext;
	}

	private Document _getSingleDocument(String searchTerm, Hits hits) {
		List<Document> documents = hits.toList();

		if (documents.size() == 1) {
			return documents.get(0);
		}

		throw new AssertionError(searchTerm + "->" + documents);
	}

	private Document _searchDocument(String searchTerm, Group... groups)
		throws Exception {

		Hits hits = _getHits(searchTerm, groups);

		return _getSingleDocument(searchTerm, hits);
	}

	private List<Document> _searchDocuments(String searchTerm, Group... groups)
		throws Exception {

		Hits hits = _getHits(searchTerm, groups);

		return hits.toList();
	}

	@Inject
	private static GroupService _groupService;

	@Inject
	private static IndexerRegistry _indexerRegistry;

	private Indexer<JournalArticle> _indexer;

	@DeleteAfterTestRun
	private Group _japanGroup;

	private JournalArticleBuilder _japanJournalArticleBuilder;

	@DeleteAfterTestRun
	private Group _usGroup;

	private JournalArticleBuilder _usJournalArticleBuilder;

}