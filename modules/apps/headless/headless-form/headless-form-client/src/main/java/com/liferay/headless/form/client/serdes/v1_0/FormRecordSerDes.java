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
import com.liferay.headless.form.client.dto.v1_0.FormRecord;
import com.liferay.headless.form.client.json.BaseJSONParser;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Stream;

import javax.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class FormRecordSerDes {

	public static FormRecord toDTO(String json) {
		FormRecordJSONParser formRecordJSONParser = new FormRecordJSONParser();

		return formRecordJSONParser.parseToDTO(json);
	}

	public static FormRecord[] toDTOs(String json) {
		FormRecordJSONParser formRecordJSONParser = new FormRecordJSONParser();

		return formRecordJSONParser.parseToDTOs(json);
	}

	public static String toJSON(FormRecord formRecord) {
		if (formRecord == null) {
			return "{}";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		sb.append("\"creator\": ");

		if (formRecord.getCreator() == null) {
			sb.append("null");
		}
		else {
			sb.append(formRecord.getCreator());
		}

		sb.append(", ");

		sb.append("\"dateCreated\": ");

		if (formRecord.getDateCreated() == null) {
			sb.append("null");
		}
		else {
			sb.append(formRecord.getDateCreated());
		}

		sb.append(", ");

		sb.append("\"dateModified\": ");

		if (formRecord.getDateModified() == null) {
			sb.append("null");
		}
		else {
			sb.append(formRecord.getDateModified());
		}

		sb.append(", ");

		sb.append("\"datePublished\": ");

		if (formRecord.getDatePublished() == null) {
			sb.append("null");
		}
		else {
			sb.append(formRecord.getDatePublished());
		}

		sb.append(", ");

		sb.append("\"draft\": ");

		if (formRecord.getDraft() == null) {
			sb.append("null");
		}
		else {
			sb.append(formRecord.getDraft());
		}

		sb.append(", ");

		sb.append("\"fieldValues\": ");

		if (formRecord.getFieldValues() == null) {
			sb.append("null");
		}
		else {
			sb.append("[");

			for (int i = 0; i < formRecord.getFieldValues().length; i++) {
				sb.append(formRecord.getFieldValues()[i]);

				if ((i + 1) < formRecord.getFieldValues().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append(", ");

		sb.append("\"form\": ");

		if (formRecord.getForm() == null) {
			sb.append("null");
		}
		else {
			sb.append(formRecord.getForm());
		}

		sb.append(", ");

		sb.append("\"formId\": ");

		if (formRecord.getFormId() == null) {
			sb.append("null");
		}
		else {
			sb.append(formRecord.getFormId());
		}

		sb.append(", ");

		sb.append("\"id\": ");

		if (formRecord.getId() == null) {
			sb.append("null");
		}
		else {
			sb.append(formRecord.getId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static String toJSON(Collection<FormRecord> formRecords) {
		if (formRecords == null) {
			return "[]";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("[");

		for (FormRecord formRecord : formRecords) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append(toJSON(formRecord));
		}

		sb.append("]");

		return sb.toString();
	}

	private static class FormRecordJSONParser
		extends BaseJSONParser<FormRecord> {

		protected FormRecord createDTO() {
			return new FormRecord();
		}

		protected FormRecord[] createDTOArray(int size) {
			return new FormRecord[size];
		}

		protected void setField(
			FormRecord formRecord, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					formRecord.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					formRecord.setDateCreated(
						_toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					formRecord.setDateModified(
						_toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "datePublished")) {
				if (jsonParserFieldValue != null) {
					formRecord.setDatePublished(
						_toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "draft")) {
				if (jsonParserFieldValue != null) {
					formRecord.setDraft((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fieldValues")) {
				if (jsonParserFieldValue != null) {
					formRecord.setFieldValues(
						Stream.of(
							toStrings((Object[])jsonParserFieldValue)
						).map(
							object -> FieldValueSerDes.toDTO((String)object)
						).toArray(
							size -> new FieldValue[size]
						));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "form")) {
				if (jsonParserFieldValue != null) {
					formRecord.setForm(
						FormSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "formId")) {
				if (jsonParserFieldValue != null) {
					formRecord.setFormId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					formRecord.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else {
				throw new IllegalArgumentException(
					"Unsupported field name " + jsonParserFieldName);
			}
		}

		private Date _toDate(String string) {
			try {
				DateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ss'Z'");

				return dateFormat.parse(string);
			}
			catch (ParseException pe) {
				throw new IllegalArgumentException("Unable to parse " + string);
			}
		}

	}

}