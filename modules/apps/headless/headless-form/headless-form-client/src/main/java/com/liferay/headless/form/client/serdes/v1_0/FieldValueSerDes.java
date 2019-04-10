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

import com.liferay.headless.form.client.dto.v1_0.FieldValue;
import com.liferay.headless.form.client.json.BaseJSONParser;

import java.util.Collection;
import java.util.Objects;

import javax.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class FieldValueSerDes {

	public static FieldValue toDTO(String json) {
		FieldValueJSONParser fieldValueJSONParser = new FieldValueJSONParser();

		return fieldValueJSONParser.parseToDTO(json);
	}

	public static FieldValue[] toDTOs(String json) {
		FieldValueJSONParser fieldValueJSONParser = new FieldValueJSONParser();

		return fieldValueJSONParser.parseToDTOs(json);
	}

	public static String toJSON(FieldValue fieldValue) {
		if (fieldValue == null) {
			return "{}";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		sb.append("\"document\": ");

		if (fieldValue.getDocument() == null) {
			sb.append("null");
		}
		else {
			sb.append(fieldValue.getDocument());
		}

		sb.append(", ");

		sb.append("\"documentId\": ");

		if (fieldValue.getDocumentId() == null) {
			sb.append("null");
		}
		else {
			sb.append(fieldValue.getDocumentId());
		}

		sb.append(", ");

		sb.append("\"id\": ");

		if (fieldValue.getId() == null) {
			sb.append("null");
		}
		else {
			sb.append(fieldValue.getId());
		}

		sb.append(", ");

		sb.append("\"name\": ");

		if (fieldValue.getName() == null) {
			sb.append("null");
		}
		else {
			sb.append(fieldValue.getName());
		}

		sb.append(", ");

		sb.append("\"value\": ");

		if (fieldValue.getValue() == null) {
			sb.append("null");
		}
		else {
			sb.append(fieldValue.getValue());
		}

		sb.append("}");

		return sb.toString();
	}

	public static String toJSON(Collection<FieldValue> fieldValues) {
		if (fieldValues == null) {
			return "[]";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("[");

		for (FieldValue fieldValue : fieldValues) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append(toJSON(fieldValue));
		}

		sb.append("]");

		return sb.toString();
	}

	private static class FieldValueJSONParser
		extends BaseJSONParser<FieldValue> {

		protected FieldValue createDTO() {
			return new FieldValue();
		}

		protected FieldValue[] createDTOArray(int size) {
			return new FieldValue[size];
		}

		protected void setField(
			FieldValue fieldValue, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "document")) {
				if (jsonParserFieldValue != null) {
					fieldValue.setDocument(
						FormDocumentSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "documentId")) {
				if (jsonParserFieldValue != null) {
					fieldValue.setDocumentId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					fieldValue.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					fieldValue.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "value")) {
				if (jsonParserFieldValue != null) {
					fieldValue.setValue((String)jsonParserFieldValue);
				}
			}
			else {
				throw new IllegalArgumentException(
					"Unsupported field name " + jsonParserFieldName);
			}
		}

	}

}