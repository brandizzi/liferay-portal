
package com.liferay.portal.search.tuning.gsearch.impl.results.item;

import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleService;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.tuning.gsearch.impl.util.GSearchUtil;
import com.liferay.wiki.model.WikiPage;

import java.util.Locale;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.WindowState;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.portal.search.tuning.gsearch.api.constants.ParameterNames;
import com.liferay.portal.search.tuning.gsearch.api.query.context.QueryContext;
import com.liferay.portal.search.tuning.gsearch.api.results.item.ResultItemBuilder;

/**
 * MB message result item builder.
 *
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = ResultItemBuilder.class
)
public class MBMessageItemBuilder
	extends BaseResultItemBuilder implements ResultItemBuilder {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canBuild(Document document) {
		return _NAME.equals(document.getString(Field.ENTRY_CLASS_NAME));
	}

	/**
	 * This currently handles links for following MBMessage types (by message
	 * classNameId field) WikiPage
	 * JournalArticle
	 */
	@Override
	public String getLink(QueryContext queryContext, Document document)
		throws Exception {

		PortletRequest portletRequest =
			GSearchUtil.getPortletRequestFromContext(queryContext);

		if (portletRequest == null) {
			return null;
		}

		PortletResponse portletResponse =
			GSearchUtil.getPortletResponseFromContext(queryContext);

		long classNameId = 
			document.getLong(Field.CLASS_NAME_ID);

		long classPK = document.getLong(Field.CLASS_PK);

		if (classNameId > 0) {
			boolean viewResultsInContext = isViewInContext(queryContext);

			String assetPublisherPageURL = getAssetPublisherPageURL(
				queryContext);

			String className = _portal.getClassName(classNameId);

			if (JournalArticle.class.getName().equals(className)) {
				return getJournalArticleCommentLink(
					portletRequest, portletResponse, queryContext,
					assetPublisherPageURL, classPK, viewResultsInContext);
			}
			else if (WikiPage.class.getName().equals(className)) {
				return getWikiPageCommentLink(
					portletRequest, portletResponse, queryContext, classPK,
					viewResultsInContext);
			}
		}

		return super.getLink(queryContext, document);
	}

	/**
	 * This is overridden as LR 7.1 SP 10 doesn't index the title correctly.
	 * 
	 * - creates the title from non HTML content field, wrapping it in HTML
	 * - creates a substring from HTML string, ignoring the HTML tags
	 * 		= occasionally broken HTML
	 */
	@Override
	public String getTitle(
			QueryContext queryContext, Document document)
		throws Exception {

		Locale locale = (Locale)queryContext.getParameter(
			ParameterNames.LOCALE);

		String title = getStringFieldContent(
				document, Field.CONTENT, locale);		
		
		return GSearchUtil.stripHTML(title, _TITLE_MAX_LENGTH);
	}

	protected String getDLFileEntryCommentLink() {
		return null;
	}

	/**
	 * Gets journal article comment link.
	 *
	 * @param classPK
	 * @return
	 * @throws Exception
	 */
	protected String getJournalArticleCommentLink(
			PortletRequest portletRequest, PortletResponse portletResponse,
			QueryContext queryContext, String assetPublisherPageFriendlyURL,
			long classPK, boolean viewResultsInContext)
		throws Exception {

		AssetRenderer<?> assetRenderer = getAssetRenderer(
			JournalArticle.class.getName(), classPK);

		String link = null;

		if (viewResultsInContext || (assetPublisherPageFriendlyURL == null)) {
			link = assetRenderer.getURLViewInContext(
				(LiferayPortletRequest)portletRequest,
				(LiferayPortletResponse)portletResponse, null);
		}

		if (Validator.isNull(link)) {
			JournalArticle journalArticle =
				_journalArticleService.getLatestArticle(classPK);

			link = getNotLayoutBoundJournalArticleUrl(
				portletRequest, journalArticle, assetPublisherPageFriendlyURL);
		}

		return link;
	}

	/**
	 * Gets wiki comment link.
	 *
	 * @return
	 * @throws Exception
	 */
	protected String getWikiPageCommentLink(
			PortletRequest portletRequest, PortletResponse portletResponse,
			QueryContext queryContext, long classPK,
			boolean viewResultsInContext)
		throws Exception {

		AssetRenderer<?> assetRenderer = getAssetRenderer(
			WikiPage.class.getName(), classPK);

		if (viewResultsInContext) {
			return assetRenderer.getURLViewInContext(
				(LiferayPortletRequest)portletRequest,
				(LiferayPortletResponse)portletResponse, "");
		}

		return assetRenderer.getURLView(
			(LiferayPortletResponse)portletResponse, WindowState.MAXIMIZED);
	}

	private static final String _NAME = MBMessage.class.getName();

	// This is also the platform default MBMessage title length.

	private static final int _TITLE_MAX_LENGTH = 150;

	@Reference
	private JournalArticleService _journalArticleService;

	@Reference
	private Portal _portal;

}