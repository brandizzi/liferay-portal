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

package com.liferay.portal.search.page.search;

import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.DocumentImpl;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.generic.MatchAllQuery;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.search.engine.adapter.document.DocumentRequestExecutor;
import com.liferay.portal.search.engine.adapter.document.IndexDocumentRequest;
import com.liferay.portal.search.engine.adapter.search.SearchRequestExecutor;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Wade Cao
 */
@Component(service = PageSearchCommands.class)
public class PageSearchCommands {

	public void crawl()
		throws Exception {

		System.out.println("START CRAWLING");

		Runtime rt = Runtime.getRuntime();

		System.out.println("\n");

		Process pr = rt.exec(
			"apache-nutch-1.15/bin/nutch inject " +
			"apache-nutch-1.15/crawl/crawldb " +
			"apache-nutch-1.15/urls/seed.txt");

		BufferedReader stdInput = new BufferedReader(
			new InputStreamReader(pr.getInputStream()));

		String line = null;

		while ((line = stdInput.readLine()) != null) {
			System.out.println(line);
		}

		pr.waitFor();

		System.out.println("\n");

		pr = rt.exec(
			"apache-nutch-1.15/bin/nutch generate " +
			"apache-nutch-1.15/crawl/crawldb " +
			"apache-nutch-1.15/crawl/segments");

		stdInput = new BufferedReader(
			new InputStreamReader(pr.getInputStream()));

		line = null;
		String date = null;

		while ((line = stdInput.readLine()) != null) {
			System.out.println(line);

			if (line.startsWith("Generator: segment:")) {
				date = line.substring(
					line.lastIndexOf("/") + 1, line.length());
			}
		}

		pr.waitFor();

		System.out.println("\n");

		pr = rt.exec(
			"apache-nutch-1.15/bin/nutch fetch " +
			"apache-nutch-1.15/crawl/segments/" + date);

		stdInput = new BufferedReader(
			new InputStreamReader(pr.getInputStream()));

		line = null;

		while ((line = stdInput.readLine()) != null) {
			System.out.println(line);
		}

		pr.waitFor();

		System.out.println("\n");

		pr = rt.exec(
			"apache-nutch-1.15/bin/nutch parse " +
			"apache-nutch-1.15/crawl/segments/" + date);

		stdInput = new BufferedReader(
			new InputStreamReader(pr.getInputStream()));

		line = null;

		while ((line = stdInput.readLine()) != null) {
			System.out.println(line);
		}

		pr.waitFor();

		System.out.println("\n");

		pr = rt.exec(
			"apache-nutch-1.15/bin/nutch index " +
			"apache-nutch-1.15/crawl/crawldb " +
			"apache-nutch-1.15/crawl/segments/" + date + " -addBinaryContent");

		stdInput = new BufferedReader(
			new InputStreamReader(pr.getInputStream()));

		line = null;

		while ((line = stdInput.readLine()) != null) {
			System.out.println(line);
		}

		pr.waitFor();

		System.out.println("DONE CRAWLING");
	}

	public void ingest()
		throws Exception {

		System.out.println("START INGESTING");

		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		searchSearchRequest.setIndexNames(new String[]{"nutch"});

		searchSearchRequest.setQuery(new MatchAllQuery());
		searchSearchRequest.setSize(10);

		//only needed if nutch index is not in same cluster as liferay index
		searchSearchRequest.setConnectionId("federated");

		SearchSearchResponse searchSearchResponse =
			searchSearchRequest.accept(searchRequestExecutor);

		Hits hits = searchSearchResponse.getHits();

		for (Document document : hits.getDocs()) {
			Document liferayDoc = new DocumentImpl();

			liferayDoc.addText(Field.UID, document.get("url"));
			liferayDoc.addText(Field.TYPE, "doc");
			liferayDoc.addText("page_title", document.get("title"));
			liferayDoc.addText("url", document.get("url"));
			//liferayDoc.addText("groupId", );
			//liferayDoc.addText("roleId", );
			//liferayDoc.addText("plid", );

			String binaryContent = document.get("binaryContent");

			org.jsoup.nodes.Document doc =
				Jsoup.parseBodyFragment(binaryContent);

			//add META elements

			Elements metaElements = doc.getElementsByTag("meta");

			addMetaData(liferayDoc, metaElements, "keywords", "meta_keywords");

			addMetaData(
				liferayDoc, metaElements, "description", "meta_description");

			//add TARGET elements

			CrawlerTarget wcContentCrawlerTarget = new CrawlerTarget(
				"com_liferay_journal_content_web_portlet_JournalContentPortlet",
				"content",
				".journal-content-article");

			CrawlerTarget wcTitleCrawlerTarget = new CrawlerTarget(
				"com_liferay_journal_content_web_portlet_JournalContentPortlet",
				"title",
				".portlet-title-text");

			CrawlerTarget blogContentCrawlerTarget = new CrawlerTarget(
				"com_liferay_blogs_web_portlet_BlogsPortlet",
				"content",
				".widget-content > p");

			CrawlerTarget blogTitleCrawlerTarget = new CrawlerTarget(
				"com_liferay_blogs_web_portlet_BlogsPortlet",
				"title",
				".title");

			CrawlerTarget blogUserNameCrawlerTarget = new CrawlerTarget(
				"com_liferay_blogs_web_portlet_BlogsPortlet",
				"userName",
				".username");

			CrawlerTarget blogAggContentCrawlerTarget = new CrawlerTarget(
				"com_liferay_blogs_web_portlet_BlogsAgreggatorPortlet",
				"content",
				".entry-body > p");

			CrawlerTarget blogAggTitleCrawlerTarget = new CrawlerTarget(
				"com_liferay_blogs_web_portlet_BlogsAgreggatorPortlet",
				"title",
				".entry-title");

			CrawlerTarget wikiTitleCrawlerTarget = new CrawlerTarget(
				"com_liferay_wiki_web_portlet_WikiDisplayPortlet",
				"title",
				".header-title");

			CrawlerTarget wikiContentCrawlerTarget = new CrawlerTarget(
				"com_liferay_wiki_web_portlet_WikiDisplayPortlet",
				"content",
				".wiki-body > p");

			CrawlerTarget mbTitleCrawlerTarget = new CrawlerTarget(
				"com_liferay_message_boards_web_portlet_MBPortlet",
				"title",
				".main-content-body h4");

			CrawlerTarget mbContentCrawlerTarget = new CrawlerTarget(
				"com_liferay_message_boards_web_portlet_MBPortlet",
				"content",
				".message-content");

			CrawlerTarget fragmentCrawlerTarget = new CrawlerTarget(
				"fragment",
				"content",
				null);

			List<CrawlerTarget> crawlerTargets = new ArrayList<>();

			crawlerTargets.add(wcContentCrawlerTarget);
			crawlerTargets.add(wcTitleCrawlerTarget);
			crawlerTargets.add(blogContentCrawlerTarget);
			crawlerTargets.add(blogTitleCrawlerTarget);
			crawlerTargets.add(blogUserNameCrawlerTarget);
			crawlerTargets.add(blogAggContentCrawlerTarget);
			crawlerTargets.add(blogAggTitleCrawlerTarget);
			crawlerTargets.add(wikiTitleCrawlerTarget);
			crawlerTargets.add(wikiContentCrawlerTarget);
			crawlerTargets.add(fragmentCrawlerTarget);
			crawlerTargets.add(mbTitleCrawlerTarget);
			crawlerTargets.add(mbContentCrawlerTarget);

			addTargetFields(liferayDoc, doc, crawlerTargets);

			//INDEX

			index(liferayDoc);
		}

		System.out.println("DONE INGESTING");
	}


	protected class CrawlerTarget {

		public CrawlerTarget(
			String portletId,
			String fieldPrefix,
			String contentSelectorQuery) {

			_contentSelectorQuery = contentSelectorQuery;
			_portletId = portletId;
			_fieldPrefix = fieldPrefix;
		}

		public String getPortletId() {
			return _portletId;
		}

		public String getContentSelectorQuery(String id) {
			if (id.startsWith("fragment")) {
				return "#" + id;
			}

			return _contentSelectorQuery;
		}

		public String getFieldPrefix() {
			return _fieldPrefix;
		}

		private String _contentSelectorQuery;
		private String _portletId;
		private String _fieldPrefix;
	}

	protected void addMetaData(
		Document document, Elements metaElements, String name, String field) {

		for (Element metaElement : metaElements) {
			String elementName = metaElement.attr("name");

			if (elementName.equals(name)) {
				document.addText(field, metaElement.attr("content"));

				return;
			}
		}
	}

	protected void addTargetFields(
		Document liferayDocument, org.jsoup.nodes.Document doc,
		List<CrawlerTarget> crawlerTargets) {

		for (CrawlerTarget crawlerTarget : crawlerTargets) {
			Elements portletElements =
				doc.select(buildPortletSelectorQuery(crawlerTarget));

			for (Element portletElement : portletElements) {
				String id = portletElement.attr("id");

				Elements portletContentElements =
					portletElement.select(
						crawlerTarget.getContentSelectorQuery(id));

				int count = 0;

				for (Element portletContentElement : portletContentElements) {
					String content = portletContentElement.text();

					liferayDocument.addText(
						buildFieldName(crawlerTarget, id, count), content);

					count++;
				}
			}
		}
	}

	protected String buildFieldName(
		CrawlerTarget crawlerTarget, String id, int count) {

		StringBundler sb = new StringBundler(5);

		sb.append(crawlerTarget.getFieldPrefix());
		sb.append("_");
		sb.append(id);

		if (!id.contains("INSTANCE")) {
			sb.append("_");
			sb.append(count);
		}

		return sb.toString();
	}

	protected String buildPortletSelectorQuery(CrawlerTarget crawlerTarget) {
		StringBundler sb = new StringBundler(3);

		String portletId = crawlerTarget.getPortletId();

		if (portletId.equals("fragment")) {
			sb.append("div[id~=fragment");
		}
		else {
			sb.append("section[id~=portlet_");
			sb.append(portletId);
		}

		sb.append("*]");

		return sb.toString();
	}

	protected void index(Document document) {
		IndexDocumentRequest indexDocumentRequest = new IndexDocumentRequest(
			"liferay_page_index", document);

		documentRequestExecutor.executeDocumentRequest(indexDocumentRequest);
	}

	@Reference
	protected DocumentRequestExecutor documentRequestExecutor;

	@Reference
	protected SearchRequestExecutor searchRequestExecutor;

}