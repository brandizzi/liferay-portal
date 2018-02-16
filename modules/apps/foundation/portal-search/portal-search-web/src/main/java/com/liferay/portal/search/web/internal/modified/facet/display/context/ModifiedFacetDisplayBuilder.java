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

package com.liferay.portal.search.web.internal.modified.facet.display.context;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.search.facet.collector.FacetCollector;
import com.liferay.portal.kernel.search.facet.collector.TermCollector;
import com.liferay.portal.kernel.search.facet.config.FacetConfiguration;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.web.internal.modified.facet.builder.DateRangeFactory;

import java.io.Serializable;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.stream.Stream;

/**
 * @author Lino Alves
 */
public class ModifiedFacetDisplayBuilder implements Serializable {

	public ModifiedFacetDisplayContext build() {
		ModifiedFacetDisplayContext modifiedFacetDisplayContext =
			new ModifiedFacetDisplayContext();

		modifiedFacetDisplayContext.setCalendarDisplayContext(
			buildCalendarDisplayContext());
		modifiedFacetDisplayContext.setCustomRangeTermDisplayContext(
			buildCustomRangeTermDisplayContext());
		modifiedFacetDisplayContext.setDefaultTermDisplayContext(
			buildDefaultTermDisplay());
		modifiedFacetDisplayContext.setRenderNothing(isRenderNothing());
		modifiedFacetDisplayContext.setNothingSelected(isNothingSelected());
		modifiedFacetDisplayContext.setParameterName(_parameterName);
		modifiedFacetDisplayContext.setTermDisplayContexts(
			buildTermDisplayContexts());

		return modifiedFacetDisplayContext;
	}

	public String getCustomRangeURL() {
		DateFormat format = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd");

		Calendar calendar = CalendarFactoryUtil.getCalendar(_timeZone);

		String to = format.format(calendar.getTime());

		calendar.add(Calendar.DATE, -1);

		String from = format.format(calendar.getTime());

		String rangeURL = HttpUtil.setParameter(
			_currentURL, "modifiedFrom", from);

		return HttpUtil.setParameter(rangeURL, "modifiedTo", to);
	}

	public void setCurrentURL(String currentURL) {
		_currentURL = currentURL;
	}

	public void setFacet(Facet facet) {
		_facet = facet;
	}

	public void setFromParameterValue(String from) {
		_from = from;
	}

	public void setLocale(Locale locale) {
		_locale = locale;
	}

	public void setParameterName(String paramName) {
		_parameterName = paramName;
	}

	public void setParameterValues(String... parameterValues) {
		_selectedRanges = Arrays.asList(
			Objects.requireNonNull(parameterValues));
	}

	public void setRangesJSONArray(JSONArray rangesJSONArray) {
		_rangesJSONArray = rangesJSONArray;
	}

	public void setTimeZone(TimeZone timeZone) {
		_timeZone = timeZone;
	}

	public void setToParameterValue(String to) {
		_to = to;
	}

	public void setTotalHits(int totalHits) {
		_totalHits = totalHits;
	}

	protected ModifiedFacetCalendarDisplayContext
		buildCalendarDisplayContext() {

		ModifiedFacetCalendarDisplayBuilder
			modifiedFacetCalendarDisplayBuilder =
				new ModifiedFacetCalendarDisplayBuilder();

		Stream<String> selectedRangesStream = _selectedRanges.stream();

		selectedRangesStream.filter(
			s -> s.startsWith(StringPool.OPEN_CURLY_BRACE)
		).findAny(
		).ifPresent(
			modifiedFacetCalendarDisplayBuilder::setRangeString
		);

		modifiedFacetCalendarDisplayBuilder.setFrom(_from);
		modifiedFacetCalendarDisplayBuilder.setLocale(_locale);
		modifiedFacetCalendarDisplayBuilder.setTimeZone(_timeZone);
		modifiedFacetCalendarDisplayBuilder.setTo(_to);

		return modifiedFacetCalendarDisplayBuilder.build();
	}

	protected ModifiedFacetTermDisplayContext
		buildCustomRangeTermDisplayContext() {

		boolean selected = false;

		if (Validator.isNotNull(_from) || Validator.isNotNull(_to)) {
			selected = true;
		}

		Map<String, Object> data = new HashMap<>();

		FacetCollector facetCollector = _facet.getFacetCollector();

		TermCollector termCollector = null;

		if (selected) {
			String term = _dateRangeFactory.getRangeString(_from, _to);

			termCollector = facetCollector.getTermCollector(term);
		}

		ModifiedFacetTermDisplayContext customRangeTermDisplayContext =
			buildTermDisplay(
				"custom-range", "custom-range", selected, data,
				getFrequency(termCollector), getCustomRangeURL());

		return customRangeTermDisplayContext;
	}

	protected ModifiedFacetTermDisplayContext buildDefaultTermDisplay() {
		Map<String, Object> data = new HashMap<>();

		data.put("selection", 0);
		data.put("value", StringPool.BLANK);

		boolean selected = true;
		int frequency = 0;

		FacetConfiguration facetConfiguration = _facet.getFacetConfiguration();

		String label = facetConfiguration.getLabel();

		ModifiedFacetTermDisplayContext defaultTermDisplayContext =
			buildTermDisplay(label, label, selected, data, frequency, null);

		return defaultTermDisplayContext;
	}

	protected ModifiedFacetTermDisplayContext buildTermDisplay(
		String dataTermId, String label, boolean selected,
		Map<String, Object> data, int frequency, String rangeURL) {

		ModifiedFacetTermDisplayContext modifiedSearchFacetTermDisplayContext =
			new ModifiedFacetTermDisplayContext();

		modifiedSearchFacetTermDisplayContext.setFrequency(frequency);
		modifiedSearchFacetTermDisplayContext.setLabel(label);
		modifiedSearchFacetTermDisplayContext.setRange(dataTermId);
		modifiedSearchFacetTermDisplayContext.setRangeURL(rangeURL);
		modifiedSearchFacetTermDisplayContext.setSelected(selected);

		return modifiedSearchFacetTermDisplayContext;
	}

	protected List<ModifiedFacetTermDisplayContext> buildTermDisplayContexts() {
		List<ModifiedFacetTermDisplayContext>
			modifiedSearchFacetTermDisplayContexts = new ArrayList<>();

		FacetCollector facetCollector = _facet.getFacetCollector();

		int index = 0;

		for (int i = 0; i < _rangesJSONArray.length(); i++) {
			JSONObject rangeJSONObject = _rangesJSONArray.getJSONObject(i);

			String label = rangeJSONObject.getString("label");
			String range = rangeJSONObject.getString("range");

			index = i + 1;

			boolean selected = _selectedRanges.contains(label);

			Map<String, Object> data = new HashMap<>();

			data.put("selection", index);
			data.put("value", HtmlUtil.escape(range));

			TermCollector termCollector = null;

			if (facetCollector != null) {
				termCollector = facetCollector.getTermCollector(range);
			}

			String rangeURL = getLabeledRangeURL(label);

			ModifiedFacetTermDisplayContext
				modifiedSearchFacetTermDisplayContext = buildTermDisplay(
					label, label, selected, data, getFrequency(termCollector),
					rangeURL);

			modifiedSearchFacetTermDisplayContexts.add(
				modifiedSearchFacetTermDisplayContext);
		}

		return modifiedSearchFacetTermDisplayContexts;
	}

	protected int getFrequency(TermCollector termCollector) {
		if (termCollector != null) {
			return termCollector.getFrequency();
		}

		return 0;
	}

	protected String getLabeledRangeURL(String label) {
		String rangeURL = HttpUtil.removeParameter(_currentURL, "modifiedFrom");

		rangeURL = HttpUtil.removeParameter(rangeURL, "modifiedTo");

		return HttpUtil.setParameter(rangeURL, "modified", label);
	}

	protected boolean isNothingSelected() {
		if (!_selectedRanges.isEmpty()) {
			return false;
		}

		if (Validator.isNotNull(_from) && Validator.isNotNull(_to)) {
			return false;
		}

		return true;
	}

	protected boolean isRenderNothing() {
		if (_totalHits > 0) {
			return false;
		}

		return isNothingSelected();
	}

	private String _currentURL;
	private final DateRangeFactory _dateRangeFactory = new DateRangeFactory();
	private Facet _facet;
	private String _from;
	private Locale _locale;
	private String _parameterName;
	private JSONArray _rangesJSONArray;
	private List<String> _selectedRanges = Collections.emptyList();
	private TimeZone _timeZone;
	private String _to;
	private int _totalHits;

}