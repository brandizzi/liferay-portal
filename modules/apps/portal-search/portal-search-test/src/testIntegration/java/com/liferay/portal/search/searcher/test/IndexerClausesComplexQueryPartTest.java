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
import com.liferay.journal.test.util.search.JournalArticleBlueprintBuilder;
import com.liferay.journal.test.util.search.JournalArticleContent;
import com.liferay.journal.test.util.search.JournalArticleSearchFixture;
import com.liferay.journal.test.util.search.JournalArticleTitle;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.facet.faceted.searcher.FacetedSearcherManager;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.search.context.SearchContextFactory;
import com.liferay.portal.search.filter.ComplexQueryPartBuilderFactory;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.query.Query;
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
import java.util.List;
import java.util.stream.Stream;

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
public class IndexerClausesComplexQueryPartTest {

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
	public void testComplexQueryPartNotSuppressedWithBaseIndexer()
		throws Exception {

		addJournalArticle("alpha", "omega");

		assertComplexQueryPartNotSupressed(
			"omega", _queries.match(_TITLE_EN_US, "omega"),
			Arrays.asList(JournalArticle.class), _TITLE_EN_US,
			Arrays.asList("alpha"), Arrays.asList("alpha"));
	}

	@Test
	public void testComplexQueryPartNotSuppressedWithDefaultIndexer()
		throws Exception {

		addUser("alpha", "gama", "omega");

		assertComplexQueryPartNotSupressed(
			"omega", _queries.match(Field.USER_NAME, "omega"),
			Arrays.asList(User.class), Field.USER_NAME,
			Arrays.asList("gama omega"), Arrays.asList("gama omega"));
	}

	@Test
	public void testComplexQueryPartNotSuppressedWithFacetedSearcher()
		throws Exception {

		User user = addUser("alpha", "gama", "omega");

		addJournalArticle(user.getUserId(), "alpha", "omega");

		assertComplexQueryPartNotSupressed(
			"omega",
			_queries.multiMatch("omega", _TITLE_EN_US, Field.USER_NAME),
			Arrays.asList(User.class, JournalArticle.class), Field.USER_NAME,
			Arrays.asList("gama omega", "gama omega"),
			Arrays.asList("gama omega", "gama omega"));
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	@Rule
	public TestName testName = new TestName();

	protected JournalArticle addJournalArticle(
		long userId, String title, String content) {

		return _journalArticleSearchFixture.addArticle(
			new JournalArticleBlueprintBuilder().groupId(
				_group.getGroupId()
			).journalArticleContent(
				new JournalArticleContent() {
					{
						put(LocaleUtil.US, content);

						setDefaultLocale(LocaleUtil.US);
						setName("content");
					}
				}
			).journalArticleTitle(
				new JournalArticleTitle() {
					{
						put(LocaleUtil.US, title);
					}
				}
			).userId(
				userId
			).build());
	}

	protected JournalArticle addJournalArticle(String title, String content) {
		return addJournalArticle(_user.getUserId(), title, content);
	}

	protected User addUser(String userName, String firstName, String lastName)
		throws Exception {

		return _userSearchFixture.addUser(
			userName, firstName, lastName, LocaleUtil.US, _group,
			new String[0]);
	}

	protected void assertClausesSuppression(
		String queryString, List<Class<?>> modelIndexerClasses,
		String fieldName, List<String> expectedResults) {

		SearchRequestBuilder searchRequestBuilder1 = createSearchRequestBuilder(
			queryString, modelIndexerClasses);

		assertSearch(searchRequestBuilder1, fieldName, expectedResults);

		/*
		SearchRequestBuilder searchRequestBuilder2 = createSearchRequestBuilder(
			queryString, modelIndexerClasses
		).withSearchContext(
			searchContext -> searchContext.setAttribute(
				"search.full.query.suppress.indexer.provided.clauses", true)
		);

		assertSearch(
			searchRequestBuilder2, fieldName, Collections.emptyList());
		 */
	}

	protected void assertComplexQueryPartNotSupressed(
		String queryString, Query complexQueryPartQuery,
		List<Class<?>> modelIndexerClasses, String fieldName,
		List<String> expectedResults,
		List<String> expectedComplexQueryPartResults) {

		assertClausesSuppression(
			queryString, modelIndexerClasses, fieldName, expectedResults);

		SearchRequestBuilder searchRequestBuilder = createSearchRequestBuilder(
			queryString, modelIndexerClasses
		).addComplexQueryPart(
			_complexQueryPartBuilderFactory.builder(
			).query(
				complexQueryPartQuery
			).occur(
				"should"
			).build()
		).withSearchContext(
			searchContext -> searchContext.setAttribute(
				"search.full.query.suppress.indexer.provided.clauses", true)
		);

		assertSearch(
			searchRequestBuilder, fieldName, expectedComplexQueryPartResults);
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

	protected SearchRequestBuilder createSearchRequestBuilder(
		String queryString, List<Class<?>> modelIndexerClasses) {

		Stream<Class<?>> stream = modelIndexerClasses.stream();

		return _searchRequestBuilderFactory.builder(
		).companyId(
			_group.getCompanyId()
		).groupIds(
			_group.getGroupId()
		).modelIndexerClassNames(
			stream.map(
				Class::getCanonicalName
			).toArray(
				String[]::new
			)
		).queryString(
			queryString
		).addSelectedFieldNames(
			StringPool.STAR
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

	@Inject
	private FacetedSearcherManager _facetedSearcherManager;

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
	private SearchContextFactory _searchContextFactory;

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