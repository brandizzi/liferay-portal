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
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.facet.faceted.searcher.FacetedSearcherManager;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.ModelPermissions;
import com.liferay.portal.kernel.service.permission.ModelPermissionsFactory;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
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
import com.liferay.users.admin.test.util.search.UserBlueprint;
import com.liferay.users.admin.test.util.search.UserBlueprintImpl.UserBlueprintBuilderImpl;
import com.liferay.users.admin.test.util.search.UserSearchFixture;

import java.time.Month;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
public class IndexerClausesPermissionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		_journalArticleSearchFixture = new JournalArticleSearchFixture(
			_journalArticleLocalService);

		_journalArticleSearchFixture.setUp();

		_journalArticles = _journalArticleSearchFixture.getJournalArticles();

		_userSearchFixture = new UserSearchFixture();

		_userSearchFixture.setUp();

		_users = _userSearchFixture.getUsers();

		_addGroupAndUsers();
	}

	@After
	public void tearDown() throws Exception {
		_journalArticleSearchFixture.tearDown();

		_userSearchFixture.tearDown();

		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);
	}

	@Test
	public void testBaseIndexer() throws Exception {
		addJournalArticle(_user1, "alpha", "omega");

		SearchRequestBuilder searchRequestBuilder1 = createSearchRequestBuilder(
			"omega", Arrays.asList(JournalArticle.class));

		assertSearch(
			searchRequestBuilder1, _TITLE_EN_US, Arrays.asList("alpha"));

		SearchRequestBuilder searchRequestBuilder2 = createSearchRequestBuilder(
			"omega", Arrays.asList(JournalArticle.class)
		).withSearchContext(
			searchContext -> searchContext.setUserId(_user2.getUserId())
		);

		assertSearch(
			searchRequestBuilder2, _TITLE_EN_US, Collections.emptyList());
	}

	@Test
	public void testDefaultIndexer() throws Exception {
		addUser("alpha", "gama", "omega");

		SearchRequestBuilder searchRequestBuilder1 = createSearchRequestBuilder(
			"omega", Arrays.asList(User.class));

		assertSearch(
			searchRequestBuilder1, Field.USER_NAME,
			Arrays.asList("gama omega"));

		SearchRequestBuilder searchRequestBuilder2 = createSearchRequestBuilder(
			"omega", Arrays.asList(JournalArticle.class)
		).withSearchContext(
			searchContext -> searchContext.setUserId(_user2.getUserId())
		);

		assertSearch(
			searchRequestBuilder2, Field.USER_NAME, Collections.emptyList());
	}

	@Test
	public void testFacetedSearcher() throws Exception {
		addJournalArticle(_user1, "alpha", "omega");

		SearchRequestBuilder searchRequestBuilder1 = createSearchRequestBuilder(
			"omega", Arrays.asList(JournalArticle.class, User.class));

		assertSearch(
			searchRequestBuilder1, _TITLE_EN_US, Arrays.asList("alpha"));

		SearchRequestBuilder searchRequestBuilder2 = createSearchRequestBuilder(
			"omega", Arrays.asList(JournalArticle.class, User.class)
		).withSearchContext(
			searchContext -> searchContext.setUserId(_user2.getUserId())
		);

		assertSearch(
			searchRequestBuilder2, _TITLE_EN_US, Collections.emptyList());
	}

	@Test
	public void testPermissionsNotSuppressedWithBaseIndexer() throws Exception {
		addJournalArticle(_user1, "alpha", "omega");

		assertPermissionsNotSupressed(
			"omega", _user1, _queries.match(_TITLE_EN_US, "alpha"),
			Arrays.asList(JournalArticle.class), _TITLE_EN_US,
			Arrays.asList("alpha"));

		assertPermissionsNotSupressed(
			"omega", _user2, _queries.match(_TITLE_EN_US, "alpha"),
			Arrays.asList(JournalArticle.class), _TITLE_EN_US,
			Collections.emptyList());
	}

	@Test
	public void testPermissionsNotSuppressedWithDefaultIndexer()
		throws Exception {

		addUser("alpha", "gama", "omega");

		assertPermissionsNotSupressed(
			"omega", _user1, _queries.match(Field.USER_NAME, "omega"),
			Arrays.asList(User.class), Field.USER_NAME,
			Arrays.asList("gama omega"));

		assertPermissionsNotSupressed(
			"omega", _user2, _queries.match(Field.USER_NAME, "omega"),
			Arrays.asList(User.class), Field.USER_NAME,
			Collections.emptyList());
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	@Rule
	public TestName testName = new TestName();

	protected JournalArticle addJournalArticle(
		User user, String title, String content) {

		ServiceContext serviceContext = createServiceContext(_group, user);

		JournalArticleBlueprintBuilder journalArticleBlueprintBuilder =
			new JournalArticleBlueprintBuilder();

		return _journalArticleSearchFixture.addArticle(
			journalArticleBlueprintBuilder.groupId(
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
			).serviceContext(
				serviceContext
			).userId(
				user.getUserId()
			).build());
	}

	protected User addUser(String screenName, String firstName, String lastName)
		throws Exception {

		UserBlueprint.UserBlueprintBuilder userBlueprintBuilder =
			createUserBlueprintBuilder(
				screenName
			).firstName(
				firstName
			).lastName(
				lastName
			).serviceContext(
				createServiceContext(_group, _user1)
			);

		return _userSearchFixture.addUser(userBlueprintBuilder);
	}

	protected void assertPermissionsNotSupressed(
		String queryString, User user, Query complexQueryPartQuery,
		List<Class<?>> modelIndexerClasses, String fieldName,
		List<String> expectedResults) {

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
			searchContext -> {
				searchContext.setAttribute(
					"search.full.query.suppress.indexer.provided.clauses",
					Boolean.TRUE);
				searchContext.setUserId(user.getUserId());
			}
		);

		assertSearch(searchRequestBuilder, fieldName, expectedResults);
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

	protected ModelPermissions createModelPermissions() {
		ModelPermissions modelPermissions =
			ModelPermissionsFactory.createForAllResources();

		modelPermissions.addRolePermissions(
			RoleConstants.OWNER, ActionKeys.VIEW);

		return modelPermissions;
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

	protected ServiceContext createServiceContext(Group group, User user) {
		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(false);
		serviceContext.setAddGuestPermissions(false);
		serviceContext.setCompanyId(group.getCompanyId());
		serviceContext.setModelPermissions(createModelPermissions());
		serviceContext.setScopeGroupId(group.getGroupId());
		serviceContext.setUserId(user.getUserId());

		return serviceContext;
	}

	protected UserBlueprint.UserBlueprintBuilder createUserBlueprintBuilder(
		String screenName) {

		UserBlueprint.UserBlueprintBuilder userBlueprintBuilder =
			new UserBlueprintBuilderImpl();

		String password = RandomTestUtil.randomString();

		return userBlueprintBuilder.birthdayDay(
			1
		).birthdayMonth(
			Month.JANUARY.getValue()
		).birthdayYear(
			1970
		).companyId(
			_group.getCompanyId()
		).emailAddress(
			screenName + "@example.com"
		).firstName(
			RandomTestUtil.randomString()
		).groupIds(
			_group.getGroupId()
		).lastName(
			RandomTestUtil.randomString()
		).locale(
			LocaleUtil.US
		).password1(
			password
		).password2(
			password
		).screenName(
			screenName
		);
	}

	@Inject
	protected Searcher searcher;

	@Inject
	protected SearchRequestBuilderFactory searchRequestBuilderFactory;

	private void _addGroupAndUsers() throws Exception {
		GroupSearchFixture groupSearchFixture = new GroupSearchFixture();

		_group = groupSearchFixture.addGroup(new GroupBlueprint());

		_groups = groupSearchFixture.getGroups();

		_user1 = _userSearchFixture.addUser(
			createUserBlueprintBuilder("user1"));

		PermissionThreadLocal.setPermissionChecker(
			_permissionCheckerFactory.create(_user1));

		_user2 = _userSearchFixture.addUser(
			createUserBlueprintBuilder("user2"));
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
	private PermissionChecker _originalPermissionChecker;

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

	private User _user1;
	private User _user2;

	@DeleteAfterTestRun
	private List<User> _users;

	private UserSearchFixture _userSearchFixture;

}