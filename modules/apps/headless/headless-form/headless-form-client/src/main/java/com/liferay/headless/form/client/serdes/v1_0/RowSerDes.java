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

package com.liferay.headless.form.client.serdes.v1_0;

import com.liferay.headless.form.client.dto.v1_0.Row;
import com.liferay.headless.form.client.json.BaseJSONParser;

import java.util.Collection;
import java.util.Objects;

import javax.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class RowSerDes {

	public static Row toDTO(String json) {
		RowJSONParser rowJSONParser = new RowJSONParser();

		return rowJSONParser.parseToDTO(json);
	}

	public static Row[] toDTOs(String json) {
		RowJSONParser rowJSONParser = new RowJSONParser();

		return rowJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Row row) {
		if (row == null) {
			return "{}";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		sb.append("\"id\": ");

		if (row.getId() == null) {
			sb.append("null");
		}
		else {
			sb.append(row.getId());
		}

		sb.append(", ");

		sb.append("\"label\": ");

		if (row.getLabel() == null) {
			sb.append("null");
		}
		else {
			sb.append(row.getLabel());
		}

		sb.append(", ");

		sb.append("\"value\": ");

		if (row.getValue() == null) {
			sb.append("null");
		}
		else {
			sb.append(row.getValue());
		}

		sb.append("}");

		return sb.toString();
	}

	public static String toJSON(Collection<Row> rows) {
		if (rows == null) {
			return "[]";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("[");

		for (Row row : rows) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append(toJSON(row));
		}

		sb.append("]");

		return sb.toString();
	}

	private static class RowJSONParser extends BaseJSONParser<Row> {

		protected Row createDTO() {
			return new Row();
		}

		protected Row[] createDTOArray(int size) {
			return new Row[size];
		}

		protected void setField(
			Row row, String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					row.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				if (jsonParserFieldValue != null) {
					row.setLabel((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "value")) {
				if (jsonParserFieldValue != null) {
					row.setValue((String)jsonParserFieldValue);
				}
			}
			else {
				throw new IllegalArgumentException(
					"Unsupported field name " + jsonParserFieldName);
			}
		}

	}

}