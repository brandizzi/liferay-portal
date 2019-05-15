package com.liferay.portal.search.web.internal.modified.facet.portlet.action;

import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactoryUtil;

import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Filipe Oshiro
 */
public class ModifiedFacetPortletConfigurationActionTest {

	@Before
	public void setUp() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());
	}

	@Test(expected = ParseException.class)
	public void testDateFormatShouldBeyyyyMMddHHmmss() throws Exception {
		_modifiedFacetPortletConfigurationAction.validateRange(
			_INVALID_DATE_FORMAT);
	}

	@Test(expected = ParseException.class)
	public void testInvalidRangeAliases() throws Exception {
		_modifiedFacetPortletConfigurationAction.validateRange(
			_INVALID_RANGE_ALIASES);
	}

	@Test(expected = ParseException.class)
	public void testInvalidRangeWithoutBrackets() throws Exception {
		_modifiedFacetPortletConfigurationAction.validateRange(
			_WITHOUT_BRACKETS);
	}

	private static final String _INVALID_DATE_FORMAT =
		"[{\"label\":\"past-hour\",\"range\":\"20190908 TO *\"}]";

	private static final String _INVALID_RANGE_ALIASES =
		"[{\"label\":\"past-hour\",\"range\":\"[past-test TO *]\"}]";

	private static final String _WITHOUT_BRACKETS =
		"[{\"label\":\"past-hour\",\"range\":\"past-hour TO *\"}]";

	private final ModifiedFacetPortletConfigurationAction
		_modifiedFacetPortletConfigurationAction =
			new ModifiedFacetPortletConfigurationAction();

}