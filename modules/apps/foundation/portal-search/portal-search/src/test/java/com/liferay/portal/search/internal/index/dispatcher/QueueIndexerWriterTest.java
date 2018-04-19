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

package com.liferay.portal.search.internal.index.dispatcher;

import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.batch.BatchIndexingActionable;
import com.liferay.portal.search.indexer.BaseModelRetriever;
import com.liferay.portal.search.indexer.IndexerDocumentBuilder;
import com.liferay.portal.search.internal.indexer.QueueIndexerWriter;
import com.liferay.portal.search.internal.indexer.queue.IndexerQueue;
import com.liferay.portal.search.internal.indexer.token.IndexerToken;
import com.liferay.portal.search.internal.indexer.token.IndexerTokenFactory;
import com.liferay.portal.search.internal.indexer.token.IndexerTokenFactoryImpl;
import com.liferay.portal.search.spi.model.index.contributor.ModelIndexerWriterContributor;
import com.liferay.portal.search.spi.model.index.contributor.helper.IndexerWriterMode;
import com.liferay.portal.search.spi.model.registrar.ModelSearchSettings;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author Adam Brandizzi
 */
public class QueueIndexerWriterTest {

	@Before
	public void setUp() {
		_indexerTokenFactory = new IndexerTokenFactoryImpl();
		_indexerQueue = new IndexerQueue();

		MockitoAnnotations.initMocks(this);

		setUpBaseModelRetriever();
		setUpIndexerDocumentBuilder();
		setUpModelIndexerContributor();
		setUpModelSearchSettings();
	}

	@Test
	public void testDeleteByBaseModelDoesNotPutIndexerTokenInQueueIfNotEnabled()
		throws Exception {

		QueueIndexerWriter<Example> queueIndexerWriter =
			createQueueIndexerWriter();

		queueIndexerWriter.setEnabled(false);

		queueIndexerWriter.delete(
			createExampleBaseModel(RandomTestUtil.randomLong()));

		Assert.assertTrue(_indexerQueue.isEmpty());
	}

	@Test
	public void testDeleteByBaseModelPutIndexerTokenInQueue() throws Exception {
		QueueIndexerWriter<Example> queueIndexerWriter =
			createQueueIndexerWriter();

		long classPK = RandomTestUtil.randomLong();

		Example baseModel = createExampleBaseModel(classPK);

		queueIndexerWriter.delete(baseModel);

		assertIndexerQueueHasOneIndexerToken(
			createDeleteIndexerToken(baseModel), _indexerQueue);
	}

	@Test
	public void testDeleteByUidDoesNotPutIndexerTokenInQueueIfNotEnabled()
		throws Exception {

		QueueIndexerWriter<Example> queueIndexerWriter =
			createQueueIndexerWriter();

		queueIndexerWriter.setEnabled(false);

		queueIndexerWriter.delete(
			RandomTestUtil.randomLong(), RandomTestUtil.randomString());

		Assert.assertTrue(_indexerQueue.isEmpty());
	}

	@Test
	public void testDeleteByUidPutIndexerTokenInQueue() throws Exception {
		QueueIndexerWriter<Example> queueIndexerWriter =
			createQueueIndexerWriter();

		long companyId = RandomTestUtil.randomLong();
		String uid = RandomTestUtil.randomString();

		queueIndexerWriter.delete(companyId, uid);

		assertIndexerQueueHasOneIndexerToken(
			createDeleteIndexerToken(companyId, uid), _indexerQueue);
	}

	@Test
	public void testGetBatchIndexingActionable() {
		QueueIndexerWriter<Example> queueIndexerWriter =
			createQueueIndexerWriter();

		BatchIndexingActionable batchIndexingActionable =
			queueIndexerWriter.getBatchIndexingActionable();

		Assert.assertEquals(
			_modelIndexerWriterContributor.getBatchIndexingActionable(),
			batchIndexingActionable);
	}

	@Test
	public void testGetBatchIndexingActionableHasEngineIdSet() {
		QueueIndexerWriter<Example> queueIndexerWriter =
			createQueueIndexerWriter();

		queueIndexerWriter.getBatchIndexingActionable();

		Mockito.verify(
			_batchIndexingActionable
		).setSearchEngineId(
			_modelSearchSettings.getSearchEngineId()
		);
	}

	@Test
	public void testReindexCompanyIdsDoesNotPutIndexerTokenInQueueIfEmpty()
		throws Exception {

		QueueIndexerWriter<Example> queueIndexerWriter =
			createDisabledQueueIndexerWriter();

		queueIndexerWriter.reindex(new String[0]);

		Assert.assertTrue(_indexerQueue.isEmpty());
	}

	@Test
	public void testReindexCompanyIdsDoesNotPutIndexerTokenInQueueIfNotEnabled()
		throws Exception {

		QueueIndexerWriter<Example> queueIndexerWriter =
			createDisabledQueueIndexerWriter();

		long[] companyIds =
			{RandomTestUtil.randomLong(), RandomTestUtil.randomLong()};

		queueIndexerWriter.reindex(ArrayUtil.toStringArray(companyIds));

		Assert.assertTrue(_indexerQueue.isEmpty());
	}

	@Test
	public void testReindexCompanyIdsPutIndexerTokenInQueue() throws Exception {
		QueueIndexerWriter<Example> queueIndexerWriter =
			createQueueIndexerWriter();

		long[] companyIds =
			{RandomTestUtil.randomLong(), RandomTestUtil.randomLong()};

		queueIndexerWriter.reindex(ArrayUtil.toStringArray(companyIds));

		for (long companyId : companyIds) {
			assertIndexerQueueContainsIndexerToken(
				createReindexCompanyIndexerToken(companyId), _indexerQueue);
		}
	}

	@Test
	public void testReindexModelDoestPutIndexerTokenInQueueIfBaseModelIsNull()
		throws Exception {

		QueueIndexerWriter<Example> queueIndexerWriter =
			createQueueIndexerWriter();

		queueIndexerWriter.reindex((Example)null);

		Assert.assertTrue(_indexerQueue.isEmpty());
	}

	@Test
	public void testReindexModelDoestPutIndexerTokenInQueueIfModelIndexerWriterContributorAsksForSkipping()
		throws Exception {

		mockModelIndexerWriterContributorIndexerWriterModel(
			IndexerWriterMode.SKIP);

		QueueIndexerWriter<Example> queueIndexerWriter =
			createQueueIndexerWriter();

		queueIndexerWriter.reindex(
			createExampleBaseModel(RandomTestUtil.randomLong()));

		Assert.assertTrue(_indexerQueue.isEmpty());
	}

	@Test
	public void testReindexModelDoestPutIndexerTokenInQueueIfModelIsPending()
		throws Exception {

		QueueIndexerWriter<Example> queueIndexerWriter =
			createQueueIndexerWriter();

		queueIndexerWriter.reindex(
			createExampleBaseModel(
				RandomTestUtil.randomLong(), WorkflowConstants.STATUS_PENDING));

		Assert.assertTrue(_indexerQueue.isEmpty());
	}

	@Test
	public void testReindexModelDoestPutIndexerTokenInQueueIfNotEnabled()
		throws Exception {

		QueueIndexerWriter<Example> queueIndexerWriter =
			createDisabledQueueIndexerWriter();

		queueIndexerWriter.reindex(
			createExampleBaseModel(RandomTestUtil.randomLong()));

		Assert.assertTrue(_indexerQueue.isEmpty());
	}

	@Test
	public void testReindexModelPutIndexerTokenInQueue() throws Exception {
		QueueIndexerWriter<Example> queueIndexerWriter =
			createQueueIndexerWriter();

		long classPK = RandomTestUtil.randomLong();

		Example baseModel = createExampleBaseModel(classPK);

		queueIndexerWriter.reindex(baseModel);

		assertIndexerQueueHasOneIndexerToken(
			createReindexModelIndexerToken(baseModel), _indexerQueue);
	}

	@Test
	public void testReindexModelPutsDeleteIndexerTokenInQueueIfModelIndexerWriterContributorAsksForDeleting()
		throws Exception {

		mockModelIndexerWriterContributorIndexerWriterModel(
			IndexerWriterMode.DELETE);

		QueueIndexerWriter<Example> queueIndexerWriter =
			createQueueIndexerWriter();

		Example baseModel = createExampleBaseModel(RandomTestUtil.randomLong());

		queueIndexerWriter.reindex(baseModel);

		assertIndexerQueueHasOneIndexerToken(
			createDeleteIndexerToken(baseModel), _indexerQueue);
	}

	@Test
	public void testReindexModelPutsDeleteIndexerTokenInQueueIfModelIndexerWriterContributorAsksForPartialUpdate()
		throws Exception {

		mockModelIndexerWriterContributorIndexerWriterModel(
			IndexerWriterMode.PARTIAL_UPDATE);

		QueueIndexerWriter<Example> queueIndexerWriter =
			createQueueIndexerWriter();

		Example baseModel = createExampleBaseModel(RandomTestUtil.randomLong());

		queueIndexerWriter.reindex(baseModel);

		assertIndexerQueueHasOneIndexerToken(
			createReindexModelIndexerToken(baseModel), _indexerQueue);
	}

	@Test
	public void testReindexModelPutsDeleteIndexerTokenInQueueIfModelIndexerWriterContributorAsksForUpdate()
		throws Exception {

		mockModelIndexerWriterContributorIndexerWriterModel(
			IndexerWriterMode.UPDATE);

		QueueIndexerWriter<Example> queueIndexerWriter =
			createQueueIndexerWriter();

		Example baseModel = createExampleBaseModel(RandomTestUtil.randomLong());

		queueIndexerWriter.reindex(baseModel);

		assertIndexerQueueHasOneIndexerToken(
			createReindexModelIndexerToken(baseModel), _indexerQueue);
	}

	@Test
	public void testReindexModelPutsIndexerTokenInQueueIfModelIsApproved()
		throws Exception {

		QueueIndexerWriter<Example> queueIndexerWriter =
			createQueueIndexerWriter();

		Example baseModel = createExampleBaseModel(RandomTestUtil.randomLong());

		queueIndexerWriter.reindex(baseModel);

		assertIndexerQueueHasOneIndexerToken(
			createReindexModelIndexerToken(baseModel), _indexerQueue);
	}

	@Test
	public void testReindexModelPutsIndexerTokenInQueueIfModelIsInTrash()
		throws Exception {

		QueueIndexerWriter<Example> queueIndexerWriter =
			createQueueIndexerWriter();

		Example baseModel = createExampleBaseModel(
			RandomTestUtil.randomLong(), WorkflowConstants.STATUS_IN_TRASH);

		queueIndexerWriter.reindex(baseModel);

		assertIndexerQueueHasOneIndexerToken(
			createReindexModelIndexerToken(baseModel), _indexerQueue);
	}

	@Test
	public void testReindexModelsDoesNotPutIndexerTokenInQueueIfNotEnabled()
		throws Exception {

		QueueIndexerWriter<Example> queueIndexerWriter =
			createDisabledQueueIndexerWriter();

		long classPK1 = RandomTestUtil.randomLong();
		long classPK2 = RandomTestUtil.randomLong();

		List<Example> baseModels = Arrays.asList(
			createExampleBaseModel(classPK1), createExampleBaseModel(classPK2));

		queueIndexerWriter.reindex(baseModels);

		Assert.assertTrue(_indexerQueue.isEmpty());
	}

	@Test
	public void testReindexModelsPutIndexerTokenInQueue() throws Exception {
		QueueIndexerWriter<Example> queueIndexerWriter =
			createQueueIndexerWriter();

		long classPK1 = RandomTestUtil.randomLong();
		long classPK2 = RandomTestUtil.randomLong();

		Example baseModel1 = createExampleBaseModel(classPK1);
		Example baseModel2 = createExampleBaseModel(classPK2);

		queueIndexerWriter.reindex(Arrays.asList(baseModel1, baseModel2));

		assertIndexerQueueContainsIndexerToken(
			createReindexModelIndexerToken(baseModel1), _indexerQueue);
		assertIndexerQueueContainsIndexerToken(
			createReindexModelIndexerToken(baseModel2), _indexerQueue);
	}

	@Test
	public void testUpdatePermissionFieldssPutIndexerTokenInQueue()
		throws Exception {

		QueueIndexerWriter<Example> queueIndexerWriter =
			createQueueIndexerWriter();

		Example baseModel = createExampleBaseModel(RandomTestUtil.randomLong());

		queueIndexerWriter.updatePermissionFields(baseModel);

		assertIndexerQueueHasOneIndexerToken(
			createUpdatePermissionFieldsIndexerToken(baseModel), _indexerQueue);
	}

	protected void assertIndexerQueueContainsIndexerToken(
		IndexerToken indexerToken, IndexerQueue indexerQueue) {

		List<IndexerToken> indexerTokens = indexerQueue.take(Integer.MAX_VALUE);

		Assert.assertTrue(indexerTokens.contains(indexerToken));
	}

	protected void assertIndexerQueueHasOneIndexerToken(
		IndexerToken expected, IndexerQueue indexerQueue) {

		List<IndexerToken> indexerTokens = indexerQueue.take(1);

		Assert.assertEquals(indexerTokens.toString(), 1, indexerTokens.size());

		Assert.assertEquals(expected, indexerTokens.get(0));
	}

	protected IndexerToken createDeleteIndexerToken(Example baseModel) {
		return _indexerTokenFactory.createDeleteIndexerToken(
			_modelSearchSettings.getSearchEngineId(),
			_modelIndexerWriterContributor.getCompanyId(baseModel),
			_indexerDocumentBuilder.getDocumentUID(baseModel),
			_modelSearchSettings.isCommitImmediately());
	}

	protected IndexerToken createDeleteIndexerToken(
		long companyId, String uid) {

		return _indexerTokenFactory.createDeleteIndexerToken(
			_modelSearchSettings.getSearchEngineId(), companyId, uid,
			_modelSearchSettings.isCommitImmediately());
	}

	protected QueueIndexerWriter<Example> createDisabledQueueIndexerWriter() {
		QueueIndexerWriter<Example> queueIndexerWriter =
			new QueueIndexerWriter<>(
				_baseModelRetriever, _indexerTokenFactory, _indexerQueue,
				_modelSearchSettings, _modelIndexerWriterContributor,
				_indexerDocumentBuilder, false);

		return queueIndexerWriter;
	}

	protected Example createExampleBaseModel(long classPK) {
		return new Example(Example.class, classPK);
	}

	protected Example createExampleBaseModel(long classPK, int status) {
		Example example = new Example(Example.class, classPK);

		example.setStatus(status);

		return example;
	}

	protected QueueIndexerWriter<Example> createQueueIndexerWriter() {
		QueueIndexerWriter<Example> queueIndexerWriter =
			new QueueIndexerWriter<>(
				_baseModelRetriever, _indexerTokenFactory, _indexerQueue,
				_modelSearchSettings, _modelIndexerWriterContributor,
				_indexerDocumentBuilder, true);

		return queueIndexerWriter;
	}

	protected IndexerToken createReindexCompanyIndexerToken(long companyId) {
		return _indexerTokenFactory.createReindexCompanyIndexerToken(
			_modelSearchSettings.getSearchEngineId(), companyId,
			_modelSearchSettings.getClassName(),
			_modelSearchSettings.isCommitImmediately());
	}

	protected IndexerToken createReindexModelIndexerToken(Example baseModel) {
		return _indexerTokenFactory.createReindexModelIndexerToken(
			_modelSearchSettings.getSearchEngineId(),
			_modelIndexerWriterContributor.getCompanyId(baseModel),
			baseModel.getModelClassName(), baseModel.getPrimaryKeyObj(),
			_modelSearchSettings.isCommitImmediately());
	}

	protected IndexerToken createUpdatePermissionFieldsIndexerToken(
		Example baseModel) {

		return _indexerTokenFactory.createUpdatePermissionFieldsIndexerToken(
			_modelSearchSettings.getSearchEngineId(),
			_modelIndexerWriterContributor.getCompanyId(baseModel),
			baseModel.getModelClassName(), baseModel.getPrimaryKeyObj(),
			_modelSearchSettings.isCommitImmediately());
	}

	protected void mockModelIndexerWriterContributorIndexerWriterModel(
		IndexerWriterMode indexerWriterMode) {

		Mockito.when(
			_modelIndexerWriterContributor.getIndexerWriterMode(Matchers.any())
		).thenReturn(
			indexerWriterMode
		);
	}

	protected void setUpBaseModelRetriever() {
		Mockito.when(
			_baseModelRetriever.fetchBaseModel(
				Mockito.eq(Example.class.getName()), Matchers.anyLong())
		).thenAnswer(
			invocation -> createExampleBaseModel(
				invocation.getArgumentAt(1, Long.class))
		);
	}

	protected void setUpIndexerDocumentBuilder() {
		Mockito.when(
			_document.getUID()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_indexerDocumentBuilder.getDocument(Matchers.any())
		).thenReturn(
			_document
		);
	}

	protected void setUpModelIndexerContributor() {
		Mockito.when(
			_modelIndexerWriterContributor.getCompanyId(Matchers.any())
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			_modelIndexerWriterContributor.getBatchIndexingActionable()
		).thenReturn(
			_batchIndexingActionable
		);
	}

	protected void setUpModelSearchSettings() {
		Mockito.when(
			_modelSearchSettings.getClassName()
		).thenReturn(
			Example.class.getName()
		);

		Mockito.when(
			_modelSearchSettings.isCommitImmediately()
		).thenReturn(
			RandomTestUtil.randomBoolean()
		);

		Mockito.when(
			_modelSearchSettings.getSearchEngineId()
		).thenReturn(
			RandomTestUtil.randomString()
		);
	}

	@Mock
	private BaseModelRetriever _baseModelRetriever;

	@Mock
	private BatchIndexingActionable _batchIndexingActionable;

	@Mock
	private Document _document;

	@Mock
	private IndexerDocumentBuilder _indexerDocumentBuilder;

	private IndexerQueue _indexerQueue;
	private IndexerTokenFactory _indexerTokenFactory;

	@Mock
	private ModelIndexerWriterContributor<Example>
		_modelIndexerWriterContributor;

	@Mock
	private ModelSearchSettings _modelSearchSettings;

}