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

import com.liferay.headless.form.client.dto.v1_0.Field;
import com.liferay.headless.form.client.dto.v1_0.FormPage;
import com.liferay.headless.form.client.json.BaseJSONParser;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

import javax.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class FormPageSerDes {

	public static FormPage toDTO(String json) {
		FormPageJSONParser formPageJSONParser = new FormPageJSONParser();

		return formPageJSONParser.parseToDTO(json);
	}

	public static FormPage[] toDTOs(String json) {
		FormPageJSONParser formPageJSONParser = new FormPageJSONParser();

		return formPageJSONParser.parseToDTOs(json);
	}

	public static String toJSON(FormPage formPage) {
		if (formPage == null) {
			return "{}";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		sb.append("\"fields\": ");

		if (formPage.getFields() == null) {
			sb.append("null");
		}
		else {
			sb.append("[");

			for (int i = 0; i < formPage.getFields().length; i++) {
				sb.append(formPage.getFields()[i]);

				if ((i + 1) < formPage.getFields().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append(", ");

		sb.append("\"headline\": ");

		if (formPage.getHeadline() == null) {
			sb.append("null");
		}
		else {
			sb.append(formPage.getHeadline());
		}

		sb.append(", ");

		sb.append("\"id\": ");

		if (formPage.getId() == null) {
			sb.append("null");
		}
		else {
			sb.append(formPage.getId());
		}

		sb.append(", ");

		sb.append("\"text\": ");

		if (formPage.getText() == null) {
			sb.append("null");
		}
		else {
			sb.append(formPage.getText());
		}

		sb.append("}");

		return sb.toString();
	}

	public static String toJSON(Collection<FormPage> formPages) {
		if (formPages == null) {
			return "[]";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("[");

		for (FormPage formPage : formPages) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append(toJSON(formPage));
		}

		sb.append("]");

		return sb.toString();
	}

	private static class FormPageJSONParser extends BaseJSONParser<FormPage> {

		protected FormPage createDTO() {
			return new FormPage();
		}

		protected FormPage[] createDTOArray(int size) {
			return new FormPage[size];
		}

		protected void setField(
			FormPage formPage, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "fields")) {
				if (jsonParserFieldValue != null) {
					formPage.setFields(
						Stream.of(
							toStrings((Object[])jsonParserFieldValue)
						).map(
							object -> FieldSerDes.toDTO((String)object)
						).toArray(
							size -> new Field[size]
						));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "headline")) {
				if (jsonParserFieldValue != null) {
					formPage.setHeadline((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					formPage.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "text")) {
				if (jsonParserFieldValue != null) {
					formPage.setText((String)jsonParserFieldValue);
				}
			}
			else {
				throw new IllegalArgumentException(
					"Unsupported field name " + jsonParserFieldName);
			}
		}

	}

}