package com.liferay.portal.search.web.internal.modified.facet.portlet.action;

import com.liferay.petra.string.StringBundler;
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
			getDateRangeJSON("past-hour", "[20190908 TO *]"));
	}

	@Test(expected = ParseException.class)
	public void testInvalidRangeAliases() throws Exception {
		_modifiedFacetPortletConfigurationAction.validateRange(
			getDateRangeJSON("past-hour", "[past-test TO *]"));
	}

	@Test(expected = ParseException.class)
	public void testInvalidRangeWithoutBrackets() throws Exception {
		_modifiedFacetPortletConfigurationAction.validateRange(
			getDateRangeJSON("past-hour", "past-hour TO *"));
	}

	@Test
	public void testValidRangeWithDates() throws Exception {
		_modifiedFacetPortletConfigurationAction.validateRange(
			getDateRangeJSON("past-hour", "[20190509000000 TO 20190509235999]"));
	}

	@Test
	public void testValidRangeWithAsterisk() throws Exception {
		_modifiedFacetPortletConfigurationAction.validateRange(
			getDateRangeJSON("past-hour", "[20190509000000 TO *]"));
	}
	
	@Test
	public void testValidRangeWithPastHour() throws Exception {
		_modifiedFacetPortletConfigurationAction.validateRange(
			getDateRangeJSON("past-hour", "[20190509000000 TO past-hour]"));
	}
	
	protected String getDateRangeJSON(String label, String range) {
		return StringBundler.concat(
			"[{\"label\":\"", label, "\",\"range\":\"", range, "\"}]");
	}

	private final ModifiedFacetPortletConfigurationAction
		_modifiedFacetPortletConfigurationAction =
			new ModifiedFacetPortletConfigurationAction();

}