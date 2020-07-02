
package com.liferay.portal.search.tuning.gsearch.impl.params;

import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.tuning.gsearch.impl.util.GSearchUtil;

import com.liferay.portal.search.tuning.gsearch.api.constants.ParameterNames;
import com.liferay.portal.search.tuning.gsearch.api.exception.ParameterValidationException;
import com.liferay.portal.search.tuning.gsearch.api.params.FilterParameter;
import com.liferay.portal.search.tuning.gsearch.api.params.ParameterBuilder;
import com.liferay.portal.search.tuning.gsearch.api.query.context.QueryContext;

import java.util.Map;

import javax.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;

/**
 * Scope / group parameter builder.
 *
 * @author Petteri Karttunen
 */
@Component(
	immediate = true, 
	service = ParameterBuilder.class
)
public class ScopeParameterBuilder implements ParameterBuilder {

	@Override
	public void addParameter(QueryContext queryContext) throws Exception {
		PortletRequest portletRequest =
			GSearchUtil.getPortletRequestFromContext(queryContext);

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String scopeFilter = ParamUtil.getString(
			portletRequest, ParameterNames.SCOPE);

		if ("this-site".equals(scopeFilter)) {
			FilterParameter filter = new FilterParameter(ParameterNames.GROUP_ID);

			filter.setAttribute(
				"values", new long[] {themeDisplay.getScopeGroupId()});

			queryContext.addFilterParameter(ParameterNames.GROUP_ID, filter);
		}
	}

	@Override
	public void addParameterHeadless(
			QueryContext queryContext, Map<String, Object> parameters)
		throws Exception {

		long groupId = GetterUtil.getLong(
			parameters.get(ParameterNames.GROUP_ID), -1);

		if (groupId > 0) {
			FilterParameter filter = new FilterParameter(ParameterNames.GROUP_ID);

			filter.setAttribute("values", new long[] {groupId});

			queryContext.addFilterParameter(ParameterNames.GROUP_ID, filter);
		}
	}

	@Override
	public boolean validate(QueryContext queryContext)
		throws ParameterValidationException {

		return true;
	}

	@Override
	public boolean validateHeadless(
			QueryContext queryContext, Map<String, Object> parameters)
		throws ParameterValidationException {

		return true;
	}

}