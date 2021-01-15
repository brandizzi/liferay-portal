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

package com.liferay.portal.search.searcher.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.search.JournalArticleBlueprint;
import com.liferay.journal.test.util.search.JournalArticleContent;
import com.liferay.journal.test.util.search.JournalArticleSearchFixture;
import com.liferay.journal.test.util.search.JournalArticleTitle;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.search.filter.ComplexQueryPart;
import com.liferay.portal.search.filter.ComplexQueryPartBuilderFactory;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.rescore.RescoreBuilderFactory;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.sort.Sorts;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.portal.search.test.util.SearchTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.users.admin.test.util.search.GroupBlueprint;
import com.liferay.users.admin.test.util.search.GroupSearchFixture;
import com.liferay.users.admin.test.util.search.UserSearchFixture;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

/**
 * @author Adam Brandizzi
 */
@RunWith(Arquillian.class)
public class IndexerClausesSuppressionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_journalArticleSearchFixture = new JournalArticleSearchFixture(
			_journalArticleLocalService);

		_journalArticleSearchFixture.setUp();

		_journalArticles = _journalArticleSearchFixture.getJournalArticles();

		_userSearchFixture = new UserSearchFixture();

		_userSearchFixture.setUp();

		_users = _userSearchFixture.getUsers();

		_addGroupAndUser();
	}

	@After
	public void tearDown() throws Exception {
		_journalArticleSearchFixture.tearDown();

		_userSearchFixture.tearDown();
	}

	@Test
	public void testSuppressClauseWithBaseIndexer() throws Exception {
		addJournalArticle("alpha", "gamma");

		String queryString = "gamma";

		SearchRequestBuilder searchRequestBuilder1 = createSearchRequestBuilder(
			queryString);

		assertSearch(
			searchRequestBuilder1, _TITLE_EN_US, Arrays.asList("gamma"));

		SearchRequestBuilder searchRequestBuilder2 = createSearchRequestBuilder(
			queryString
		).withSearchContext(
			searchContext -> searchContext.setAttribute(
				"search.full.query.suppress.indexer.provided.clauses", true)
		);

		assertSearch(
			searchRequestBuilder2, _TITLE_EN_US, Collections.emptyList());

		SearchRequestBuilder searchRequestBuilder3 = createSearchRequestBuilder(
			queryString
		).withSearchContext(
			searchContext -> searchContext.setAttribute(
				"search.full.query.suppress.indexer.provided.clauses", true)
		).addComplexQueryPart(
			createComplexQueryPart("title_en_US", "gamma")
		);

		assertSearch(
			searchRequestBuilder3, _TITLE_EN_US, Arrays.asList("gamma"));
	}

	@Test
	public void testSuppressClauseWithDefaultIndexer() throws Exception {
		addUser("alpha", "gama", "omega");

		String queryString = "omega";

		SearchRequestBuilder searchRequestBuilder1 = createSearchRequestBuilder(
			queryString);

		assertSearch(
			searchRequestBuilder1, Field.USER_NAME,
			Arrays.asList("gama omega"));

		SearchRequestBuilder searchRequestBuilder2 = createSearchRequestBuilder(
			queryString
		).withSearchContext(
			searchContext -> searchContext.setAttribute(
				"search.full.query.suppress.indexer.provided.clauses", true)
		);

		assertSearch(
			searchRequestBuilder2, Field.USER_NAME, Collections.emptyList());

		SearchRequestBuilder searchRequestBuilder3 = createSearchRequestBuilder(
			queryString
		).withSearchContext(
			searchContext -> searchContext.setAttribute(
				"search.full.query.suppress.indexer.provided.clauses", true)
		).addComplexQueryPart(
			createComplexQueryPart("userName", "alpha")
		);

		assertSearch(
			searchRequestBuilder3, Field.USER_NAME,
			Arrays.asList("gama omega"));
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	@Rule
	public TestName testName = new TestName();

	protected JournalArticle addJournalArticle(String content, String title) {
		return _journalArticleSearchFixture.addArticle(
			new JournalArticleBlueprint() {
				{
					setGroupId(_group.getGroupId());
					setJournalArticleContent(
						new JournalArticleContent() {
							{
								put(LocaleUtil.US, content);

								setDefaultLocale(LocaleUtil.US);
								setName("content");
							}
						});
					setJournalArticleTitle(
						new JournalArticleTitle() {
							{
								put(LocaleUtil.US, title);
							}
						});
				}
			});
	}

	protected void addUser(String userName, String firstName, String lastName)
		throws Exception {

		String[] assetTagNames = {};

		_userSearchFixture.addUser(
			userName, firstName, lastName, LocaleUtil.US, _group,
			assetTagNames);
	}

	protected void assertSearch(
		SearchRequestBuilder searchRequestBuilder, String fieldName,
		Collection<String> expectedValues) {

		SearchResponse searchResponse = _searcher.search(
			searchRequestBuilder.build());

		DocumentsAssert.assertValuesIgnoreRelevance(
			searchResponse.getRequestString(),
			searchResponse.getDocumentsStream(), fieldName,
			expectedValues.stream());
	}

	protected ComplexQueryPart createComplexQueryPart(
		String field, String value) {

		return _complexQueryPartBuilderFactory.builder(
		).query(
			_queries.match(field, value)
		).build();
	}

	protected SearchRequestBuilder createSearchRequestBuilder(
		String queryString) {

		return _searchRequestBuilderFactory.builder(
		).companyId(
			_group.getCompanyId()
		).groupIds(
			_group.getGroupId()
		).queryString(
			queryString
		);
	}

	@Inject
	protected Searcher searcher;

	@Inject
	protected SearchRequestBuilderFactory searchRequestBuilderFactory;

	private void _addGroupAndUser() throws Exception {
		GroupSearchFixture groupSearchFixture = new GroupSearchFixture();

		_group = groupSearchFixture.addGroup(new GroupBlueprint());

		_groups = groupSearchFixture.getGroups();

		_user = TestPropsValues.getUser();

		PermissionThreadLocal.setPermissionChecker(
			_permissionCheckerFactory.create(_user));
	}

	private static final String _TITLE_EN_US = StringBundler.concat(
		Field.TITLE, StringPool.UNDERLINE, LocaleUtil.US);

	@Inject
	private ComplexQueryPartBuilderFactory _complexQueryPartBuilderFactory;

	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@DeleteAfterTestRun
	private List<JournalArticle> _journalArticles;

	private JournalArticleSearchFixture _journalArticleSearchFixture;

	@Inject
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Inject
	private Queries _queries;

	@Inject
	private RescoreBuilderFactory _rescoreBuilderFactory;

	@Inject
	private Searcher _searcher;

	@Inject
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Inject
	private Sorts _sorts;

	private User _user;

	@DeleteAfterTestRun
	private List<User> _users;

	private UserSearchFixture _userSearchFixture;

}