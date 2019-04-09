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

package com.liferay.headless.foundation.client.serdes.v1_0;

import com.liferay.headless.foundation.client.dto.v1_0.AssetType;
import com.liferay.headless.foundation.client.dto.v1_0.TaxonomyVocabulary;
import com.liferay.headless.foundation.client.json.BaseJSONParser;

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
public class TaxonomyVocabularySerDes {

	public static TaxonomyVocabulary toDTO(String json) {
		TaxonomyVocabularyJSONParser taxonomyVocabularyJSONParser =
			new TaxonomyVocabularyJSONParser();

		return taxonomyVocabularyJSONParser.parseToDTO(json);
	}

	public static TaxonomyVocabulary[] toDTOs(String json) {
		TaxonomyVocabularyJSONParser taxonomyVocabularyJSONParser =
			new TaxonomyVocabularyJSONParser();

		return taxonomyVocabularyJSONParser.parseToDTOs(json);
	}

	public static String toJSON(TaxonomyVocabulary taxonomyVocabulary) {
		if (taxonomyVocabulary == null) {
			return "{}";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		sb.append("\"assetTypes\": ");

		if (taxonomyVocabulary.getAssetTypes() == null) {
			sb.append("null");
		}
		else {
			sb.append("[");

			for (int i = 0; i < taxonomyVocabulary.getAssetTypes().length;
				 i++) {

				sb.append(taxonomyVocabulary.getAssetTypes()[i]);

				if ((i + 1) < taxonomyVocabulary.getAssetTypes().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append(", ");

		sb.append("\"availableLanguages\": ");

		if (taxonomyVocabulary.getAvailableLanguages() == null) {
			sb.append("null");
		}
		else {
			sb.append("[");

			for (int i = 0;
				 i < taxonomyVocabulary.getAvailableLanguages().length; i++) {

				sb.append("\"");
				sb.append(taxonomyVocabulary.getAvailableLanguages()[i]);
				sb.append("\"");

				if ((i + 1) <
						taxonomyVocabulary.getAvailableLanguages().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append(", ");

		sb.append("\"creator\": ");

		sb.append(taxonomyVocabulary.getCreator());
		sb.append(", ");

		sb.append("\"dateCreated\": ");

		sb.append("\"");
		sb.append(taxonomyVocabulary.getDateCreated());
		sb.append("\"");
		sb.append(", ");

		sb.append("\"dateModified\": ");

		sb.append("\"");
		sb.append(taxonomyVocabulary.getDateModified());
		sb.append("\"");
		sb.append(", ");

		sb.append("\"description\": ");

		sb.append("\"");
		sb.append(taxonomyVocabulary.getDescription());
		sb.append("\"");
		sb.append(", ");

		sb.append("\"id\": ");

		sb.append(taxonomyVocabulary.getId());
		sb.append(", ");

		sb.append("\"name\": ");

		sb.append("\"");
		sb.append(taxonomyVocabulary.getName());
		sb.append("\"");
		sb.append(", ");

		sb.append("\"numberOfTaxonomyCategories\": ");

		sb.append(taxonomyVocabulary.getNumberOfTaxonomyCategories());
		sb.append(", ");

		sb.append("\"siteId\": ");

		sb.append(taxonomyVocabulary.getSiteId());
		sb.append(", ");

		sb.append("\"viewableBy\": ");

		sb.append("\"");
		sb.append(taxonomyVocabulary.getViewableBy());
		sb.append("\"");

		sb.append("}");

		return sb.toString();
	}

	public static String toJSON(
		Collection<TaxonomyVocabulary> taxonomyVocabularies) {

		if (taxonomyVocabularies == null) {
			return "[]";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("[");

		for (TaxonomyVocabulary taxonomyVocabulary : taxonomyVocabularies) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append(toJSON(taxonomyVocabulary));
		}

		sb.append("]");

		return sb.toString();
	}

	private static class TaxonomyVocabularyJSONParser
		extends BaseJSONParser<TaxonomyVocabulary> {

		protected TaxonomyVocabulary createDTO() {
			return new TaxonomyVocabulary();
		}

		protected TaxonomyVocabulary[] createDTOArray(int size) {
			return new TaxonomyVocabulary[size];
		}

		protected void setField(
			TaxonomyVocabulary taxonomyVocabulary, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "assetTypes")) {
				if (jsonParserFieldValue != null) {
					taxonomyVocabulary.setAssetTypes(
						Stream.of(
							toStrings((Object[])jsonParserFieldValue)
						).map(
							object -> AssetTypeSerDes.toDTO((String)object)
						).toArray(
							size -> new AssetType[size]
						));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "availableLanguages")) {

				if (jsonParserFieldValue != null) {
					taxonomyVocabulary.setAvailableLanguages(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					taxonomyVocabulary.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					taxonomyVocabulary.setDateCreated(
						(Date)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					taxonomyVocabulary.setDateModified(
						(Date)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					taxonomyVocabulary.setDescription(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					taxonomyVocabulary.setId((Long)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					taxonomyVocabulary.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "numberOfTaxonomyCategories")) {

				if (jsonParserFieldValue != null) {
					taxonomyVocabulary.setNumberOfTaxonomyCategories(
						(Number)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				if (jsonParserFieldValue != null) {
					taxonomyVocabulary.setSiteId((Long)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "viewableBy")) {
				if (jsonParserFieldValue != null) {
					taxonomyVocabulary.setViewableBy(
						TaxonomyVocabulary.ViewableBy.create(
							(String)jsonParserFieldValue));
				}
			}
			else {
				throw new IllegalArgumentException(
					"Unsupported field name " + jsonParserFieldName);
			}
		}

	}

}