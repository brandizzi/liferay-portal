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

package com.liferay.portal.workflow.web.internal.portlet;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.workflow.WorkflowException;
import com.liferay.portal.workflow.web.internal.constants.WorkflowWebKeys;
import com.liferay.portal.workflow.web.internal.portlet.tab.WorkflowPortletTab;

import java.io.IOException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Adam Brandizzi
 */
public abstract class BaseWorkflowPortlet extends MVCPortlet {

	public String getDefaultPortletTabName() {
		List<String> tabNames = getPortletTabNames();

		return tabNames.get(0);
	}

	public abstract List<String> getPortletTabNames();

	public List<WorkflowPortletTab> getPortletTabs() {
		List<String> portletTabNames = getPortletTabNames();

		Stream<String> stream = portletTabNames.stream();

		return stream.map(
			name -> _portletTabMap.get(name)
		).collect(
			Collectors.toList()
		);
	}

	@Override
	public void processAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws IOException, PortletException {

		actionRequest.setAttribute(
			WorkflowWebKeys.WORKFLOW_DEFAULT_TAB, getDefaultTab());

		for (String tabName : getWorkflowTabNames()) {
			WorkflowPortletTab dynamicInclude = _dynamicIncludes.get(tabName);

			dynamicInclude.prepareProcessAction(actionRequest, actionResponse);
		}

		super.processAction(actionRequest, actionResponse);
	}

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		addRenderRequestAttributes(renderRequest);

		for (String tabName : getWorkflowTabNames()) {
			WorkflowPortletTab dynamicInclude = _dynamicIncludes.get(tabName);

			dynamicInclude.prepareRender(renderRequest, renderResponse);
		}

		super.render(renderRequest, renderResponse);
	}

	protected void addRenderRequestAttributes(RenderRequest renderRequest) {
		renderRequest.setAttribute(
			WorkflowWebKeys.WORKFLOW_DEFAULT_TAB, getDefaultTab());
		renderRequest.setAttribute(
			WorkflowWebKeys.WORKFLOW_TABS, _dynamicIncludes);
		renderRequest.setAttribute(
			WorkflowWebKeys.WORKFLOW_TAB_NAMES, getWorkflowTabNames());
	}

	@Override
	protected void doDispatch(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		if (SessionErrors.contains(
				renderRequest, WorkflowException.class.getName())) {

			include("/instance/error.jsp", renderRequest, renderResponse);
		}
		else {
			for (String tabName : getWorkflowTabNames()) {
				WorkflowPortletTab dynamicInclude = _dynamicIncludes.get(
					tabName);

				dynamicInclude.prepareDispatch(renderRequest, renderResponse);
			}

			super.doDispatch(renderRequest, renderResponse);
		}
	}

	protected WorkflowPortletTab getPortletTab(String name) {
		return _portletTabMap.get(name);
	}

	protected WorkflowPortletTab getSelectedPortletTab(
		RenderRequest renderRequest) {

		String tabName = ParamUtil.get(renderRequest, "tab", getDefaultTab());
		List<WorkflowPortletTab> portletTabs = getPortletTabs();

		Stream<WorkflowPortletTab> stream = portletTabs.stream();

		return stream.filter(
			portletTab -> portletTab.getName().equals(tabName)
		).findFirst(
		).orElse(
			portletTabs.get(0)
		);
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	protected void setPortletTab(
		WorkflowPortletTab portletTab, Map<String, Object> properties) {

		_portletTabMap.put(portletTab.getName(), portletTab);
	}

	protected void unsetPortletTab(
		WorkflowPortletTab portletTab, Map<String, Object> properties) {

		_portletTabMap.remove(portletTab.getName());
	}

	private final Map<String, WorkflowPortletTab> _portletTabMap =
		new ConcurrentHashMap<>();

}