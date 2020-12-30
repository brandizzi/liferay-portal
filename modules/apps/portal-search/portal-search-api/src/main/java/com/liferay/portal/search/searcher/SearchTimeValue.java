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

package com.liferay.portal.search.searcher;

import java.util.concurrent.TimeUnit;

/**
 * @author Wade Cao
 */
public final class SearchTimeValue {

	public long getDays() {
		return _days;
	}

	public double getDaysFrac() {
		return _daysFrac;
	}

	public long getDuration() {
		return _duration;
	}

	public long getHours() {
		return _hours;
	}

	public double getHoursFrac() {
		return _hoursFrac;
	}

	public long getMicros() {
		return _micros;
	}

	public double getMicrosFrac() {
		return _microsFrac;
	}

	public long getMillis() {
		return _millis;
	}

	public double getMillisFrac() {
		return _millisFrac;
	}

	public long getMinutes() {
		return _minutes;
	}

	public double getMinutesFrac() {
		return _minutesFrac;
	}

	public long getNanos() {
		return _nanos;
	}

	public long getSeconds() {
		return _seconds;
	}

	public double getSecondsFrac() {
		return _secondsFrac;
	}

	public String getStringRep() {
		return _stringRep;
	}

	public TimeUnit getTimeUnit() {
		return _timeUnit;
	}

	public static class Builder {

		public static Builder newBuilder() {
			return new Builder();
		}

		public SearchTimeValue build() {
			return _searchTimeValue;
		}

		public Builder days(long days) {
			_searchTimeValue._days = days;

			return this;
		}

		public Builder daysFrac(double daysFrac) {
			_searchTimeValue._daysFrac = daysFrac;

			return this;
		}

		public Builder duration(long duration) {
			_searchTimeValue._duration = duration;

			return this;
		}

		public Builder hours(long hours) {
			_searchTimeValue._hours = hours;

			return this;
		}

		public Builder hoursFrac(double hoursFrac) {
			_searchTimeValue._hoursFrac = hoursFrac;

			return this;
		}

		public Builder micros(long micros) {
			_searchTimeValue._micros = micros;

			return this;
		}

		public Builder microsFrac(double microsFrac) {
			_searchTimeValue._microsFrac = microsFrac;

			return this;
		}

		public Builder millis(long millis) {
			_searchTimeValue._millis = millis;

			return this;
		}

		public Builder millisFrac(double millisFrac) {
			_searchTimeValue._millisFrac = millisFrac;

			return this;
		}

		public Builder minutes(long minutes) {
			_searchTimeValue._minutes = minutes;

			return this;
		}

		public Builder minutesFrac(double minutesFrac) {
			_searchTimeValue._minutesFrac = minutesFrac;

			return this;
		}

		public Builder nanos(long nanos) {
			_searchTimeValue._nanos = nanos;

			return this;
		}

		public Builder seconds(long seconds) {
			_searchTimeValue._seconds = seconds;

			return this;
		}

		public Builder secondsFrac(double secondsFrac) {
			_searchTimeValue._secondsFrac = secondsFrac;

			return this;
		}

		public Builder stringRep(String stringRep) {
			_searchTimeValue._stringRep = stringRep;

			return this;
		}

		public Builder timeUnit(TimeUnit timeUnit) {
			_searchTimeValue._timeUnit = timeUnit;

			return this;
		}

		private Builder() {
		}

		private final SearchTimeValue _searchTimeValue = new SearchTimeValue();

	}

	private SearchTimeValue() {
	}

	private long _days;
	private double _daysFrac;
	private long _duration;
	private long _hours;
	private double _hoursFrac;
	private long _micros;
	private double _microsFrac;
	private long _millis;
	private double _millisFrac;
	private long _minutes;
	private double _minutesFrac;
	private long _nanos;
	private long _seconds;
	private double _secondsFrac;
	private String _stringRep;
	private TimeUnit _timeUnit;

}