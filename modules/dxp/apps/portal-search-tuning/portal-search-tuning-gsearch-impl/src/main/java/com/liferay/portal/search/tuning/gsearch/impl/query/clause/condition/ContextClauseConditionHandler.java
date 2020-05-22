package com.liferay.portal.search.tuning.gsearch.impl.query.clause.condition;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.Validator;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.search.tuning.gsearch.api.constants.ClauseConfigurationKeys;
import com.liferay.portal.search.tuning.gsearch.api.constants.ClauseConfigurationValues;
import com.liferay.portal.search.tuning.gsearch.api.constants.ContextClauseConditionProperties;
import com.liferay.portal.search.tuning.gsearch.api.constants.ParameterNames;
import com.liferay.portal.search.tuning.gsearch.api.query.clause.ClauseConditionHandler;
import com.liferay.portal.search.tuning.gsearch.api.query.context.QueryContext;

/**
 * Processes context clause condition.
 *
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = ClauseConditionHandler.class
)
public class ContextClauseConditionHandler implements ClauseConditionHandler {

	@Override
	public boolean canProcess(String handlerName) {
		if (handlerName.equals(_HANDLER_NAME)) {
			return true;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isTrue(QueryContext queryContext, JSONObject configuration)
		throws Exception {
		
		String matchProperty = configuration.getString(
				ClauseConfigurationKeys.MATCH_PROPERTY);
			
		String matchType = configuration.getString(
				ClauseConfigurationKeys.MATCH_TYPE);
		
		if (Validator.isBlank(matchProperty) || Validator.isBlank(matchType)) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Cannot process the clause condition. Match property or type empty.");
			}
		}
		
		matchProperty = matchProperty.toLowerCase();
			
		switch(matchProperty) {

			case ContextClauseConditionProperties.COMPANY_ID: 
				return _checkCompanyId(queryContext, configuration);
			case ContextClauseConditionProperties.KEYWORDS: 
				return _checkKeywords(queryContext, configuration);
			case ContextClauseConditionProperties.LANGUAGE: 
				return _checkLanguage(queryContext, configuration);
			case ContextClauseConditionProperties.SCOPE_GROUP_ID: 
				return _checkScopeGroupId(queryContext, configuration);
			case ContextClauseConditionProperties.TIME_OF_THE_DAY: 
				return _checkTimeOfTheDay(queryContext, configuration);
			default:
				return false;
		}
	}

	/**
	 * Checks company id condition.
	 * 
	 * @param queryContext
	 * @param configuration
	 * @return
	 */
	private boolean _checkCompanyId(QueryContext queryContext, JSONObject configuration) {
		
		long companyId = (long)queryContext.getParameter(ParameterNames.COMPANY_ID);
		
		String matchType = configuration.getString(
				ClauseConfigurationKeys.MATCH_TYPE);

		JSONArray matchValues = configuration.getJSONArray(
				ClauseConfigurationKeys.MATCH_VALUES);

		if (matchValues == null || matchValues.length() == 0) {
			if (_log.isWarnEnabled()) {
				_log.warn("Cannot process the property condition. Check configuration");
				_log.warn("Matchvalues: " + (matchValues != null ? matchValues.length() : 0));
				_log.warn("Matchtype: " + matchType);
				_log.warn("Company ID: " + companyId);

			}
			return false;
		}		

		int matchCount = 0;
		
		for (int i = 0; i < matchValues.length(); i++) {
			
			long matchId = matchValues.getLong(i);
		
			if (matchId == companyId) {
				matchCount++;
			}
		}
		
		if (matchCount > 0) {
			if (matchType.equalsIgnoreCase(ClauseConfigurationValues.MATCH_ANY)) {
				return true;
			} else if (matchType.equalsIgnoreCase(ClauseConfigurationValues.MATCH_NOT)) {
				return false;
			}
		}
		else if (matchValues.length() > 0 
				&& matchType.equalsIgnoreCase(ClauseConfigurationValues.MATCH_NOT)) {
			return true;
		}
		
		return false;	
	}

	/**
	 * Checks keywords condition.
	 * 
	 * @param queryContext
	 * @param configuration
	 * @return
	 */
	private boolean _checkKeywords(QueryContext queryContext, JSONObject configuration) {

		String keywords = queryContext.getKeywords();

		String matchType = configuration.getString(
				ClauseConfigurationKeys.MATCH_TYPE);

		String keywordsMatchType = configuration.getString("keywords_match_type", "exact");

		String keywordsSplitter = configuration.getString("keywords_splitter_regexp", " ");
		
		JSONArray matchValues = configuration.getJSONArray(
				ClauseConfigurationKeys.MATCH_VALUES);

		if (matchValues == null || matchValues.length() == 0) {
			if (_log.isWarnEnabled()) {
				_log.warn("Cannot process the property condition. Check configuration");
				_log.warn("Matchvalues: " + (matchValues != null ? matchValues.length() : 0));
				_log.warn("Matchtype: " + matchType);
				_log.warn("Keywords: " + keywords);
			}
			return false;
		}

		int matchCount = 0;

		if (keywordsMatchType.equals("exact")) {

			for (int i = 0; i < matchValues.length(); i++) {
				
				if (keywords.equals(matchValues.getString(i))) {
					matchCount++;
				}
			}
		} else if (keywordsMatchType.equals("substring")) {

				String keywords_lowercase = keywords.toLowerCase();
			
				for (int i = 0; i < matchValues.length(); i++) {
					
					if (keywords_lowercase.contains(matchValues.getString(i).toLowerCase())) {
						matchCount++;
					}
				}
		} else {
			
			String[] keywordArray = keywords.split(keywordsSplitter);

			for (int i = 0; i < matchValues.length(); i++) {

				for (String keyword : keywordArray) {
				
					if (matchValues.getString(i).equalsIgnoreCase(keyword)) {
						matchCount++;
						break;
					}
				}
			}
		}
			
		if (matchCount > 0) {
			if (matchType.equalsIgnoreCase(ClauseConfigurationValues.MATCH_ANY)) {
				return true;
			} else if (matchType.equalsIgnoreCase(ClauseConfigurationValues.MATCH_NOT)) {
				return false;
			} else if (matchType.equalsIgnoreCase(ClauseConfigurationValues.MATCH_ALL)) {
				if (matchCount == matchValues.length()) {
					return true;
				}
			}
		}
		else if (matchType.equalsIgnoreCase(ClauseConfigurationValues.MATCH_NOT)) {
			return true;
		}
		
		return false;	
	}

	/**
	 * Checks language condition.
	 * 
	 * @param queryContext
	 * @param configuration
	 * @return
	 */
	private boolean _checkLanguage(QueryContext queryContext, JSONObject configuration) {
		
		Locale locale = (Locale)queryContext.getParameter(ParameterNames.LOCALE);

		String language = locale.getLanguage();

		String matchType = configuration.getString(
				ClauseConfigurationKeys.MATCH_TYPE);

		String matchValue = configuration.getString(
				ClauseConfigurationKeys.MATCH_VALUE);
		
		if (Validator.isBlank(language) || Validator.isBlank(matchValue)) {
			if (_log.isWarnEnabled()) {
				_log.warn("Cannot process the property condition. Check configuration");
				_log.warn("Matchvalue: " + matchValue);
				_log.warn("Matchtype: " + matchType);
				_log.warn("Language: " + language);
			}
			return false;
		}
		
		switch(matchType) {

			case ClauseConfigurationValues.MATCH_EQ: 
				return language.equalsIgnoreCase(matchValue);
			case ClauseConfigurationValues.MATCH_NOT: 
				return !language.equalsIgnoreCase(matchValue);
			default:
				return false;
		}
	}

	/**
	 * Checks scope group id condition.
	 * 
	 * @param queryContext
	 * @param configuration
	 * @return
	 */
	private boolean _checkScopeGroupId(QueryContext queryContext, JSONObject configuration) {
		
		long scopeGroupId = (long)queryContext.getParameter(ParameterNames.SCOPE_GROUP_ID);
		
		String matchType = configuration.getString(
				ClauseConfigurationKeys.MATCH_TYPE);

		JSONArray matchValues = configuration.getJSONArray(
				ClauseConfigurationKeys.MATCH_VALUES);

		if (matchValues == null || matchValues.length() == 0) {
			if (_log.isWarnEnabled()) {
				_log.warn("Cannot process the property condition. Check configuration");
				_log.warn("Matchvalues: " + (matchValues != null ? matchValues.length() : 0));
				_log.warn("Matchtype: " + matchType);
				_log.warn("Scope group ID: " + scopeGroupId);

			}
			return false;
		}		
		
		int matchCount = 0;
		
		for (int i = 0; i < matchValues.length(); i++) {
			
			long matchId = matchValues.getLong(i);
		
			if (matchId == scopeGroupId) {
				matchCount++;
			}
		}
		
		if (matchCount > 0) {
			if (matchType.equalsIgnoreCase(ClauseConfigurationValues.MATCH_ANY)) {
				return true;
			} else if (matchType.equalsIgnoreCase(ClauseConfigurationValues.MATCH_NOT)) {
				return false;
			}
		}
		else if (matchValues.length() > 0 
				&& matchType.equalsIgnoreCase(ClauseConfigurationValues.MATCH_NOT)) {
			return true;
		}
		
		return false;		
	}

	/**
	 * Checks time of the day condition.
	 * 
	 * @param queryContext
	 * @param configuration
	 * @return
	 */
	private boolean _checkTimeOfTheDay(QueryContext queryContext, JSONObject configuration) {

		String timeOfTheDay = (String)queryContext.getParameter(ParameterNames.TIME_OF_THE_DAY);

		String matchType = configuration.getString(
				ClauseConfigurationKeys.MATCH_TYPE);

		String matchValue = configuration.getString(
				ClauseConfigurationKeys.MATCH_VALUE);
		
		if (Validator.isBlank(timeOfTheDay) || Validator.isBlank(matchValue)) {
			if (_log.isWarnEnabled()) {
				_log.warn("Cannot process the property condition. Check configuration");
				_log.warn("Matchvalue: " + matchValue);
				_log.warn("Matchtype: " + matchType);
				_log.warn("Time of the day: " + timeOfTheDay);
			}
			return false;
		}
		
		switch(matchType) {

			case ClauseConfigurationValues.MATCH_EQ: 
				return timeOfTheDay.equalsIgnoreCase(matchValue);
			case ClauseConfigurationValues.MATCH_NOT: 
				return !timeOfTheDay.equalsIgnoreCase(matchValue);
			default:
				return false;
		}		
	}

	private static final String _HANDLER_NAME = "context";

	private static final Logger _log = LoggerFactory.getLogger(
		ContextClauseConditionHandler.class);

}