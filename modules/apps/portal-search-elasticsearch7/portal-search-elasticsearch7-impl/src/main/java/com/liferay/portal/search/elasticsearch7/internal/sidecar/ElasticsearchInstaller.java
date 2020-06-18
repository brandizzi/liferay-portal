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

package com.liferay.portal.search.elasticsearch7.internal.sidecar;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.SystemProperties;

import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.stream.Stream;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Wade Cao
 */
public class ElasticsearchInstaller {

	public ElasticsearchInstaller(
		Path destinationDirectoryPath, Distribution distribution) {

		_destinationDirectoryPath = destinationDirectoryPath;
		_distribution = distribution;
	}

	public void install() {
		if (isAlreadyInstalled()) {
			return;
		}

		try {
			createTemporaryDownloadDirectory();

			createDestinationDirectory();

			downloadAndInstallElasticsearch();

			downloadAndInstallPlugins();
		}
		catch (Exception exception) {
			if (_logger.isWarnEnabled()) {
				_logger.warn(
					StringBundler.concat(
						"Elasticsearch sidecar could not be automatically ",
						"installed. Search will be unavailable. Manual ",
						"installation of Elasticsearch and activation of ",
						"remote mode is recommended."),
					exception);
			}
		}
		finally {
			deleteTemporaryDownloadDirectory();
		}
	}

	protected static String getChecksum(Path path) throws IOException {
		try (InputStream inputStream = Files.newInputStream(path)) {
			return DigestUtils.sha512Hex(inputStream);
		}
	}

	protected static Path getTemporaryDownloadDirectoryPath() {
		Path path = Paths.get(SystemProperties.get(SystemProperties.TMP_DIR));

		return path.resolve(ElasticsearchInstaller.class.getSimpleName());
	}

	protected static void guardChecksum(Path filePath, String checksum)
		throws IOException {

		if (!checksum.equals(getChecksum(filePath))) {
			throw new RuntimeException("Checksum mismatch");
		}
	}

	protected void createDestinationDirectory() throws IOException {
		Files.createDirectories(_destinationDirectoryPath);
	}

	protected void createTemporaryDownloadDirectory() throws IOException {
		Files.createDirectories(_temporaryDownloadDirectoryPath);
	}

	protected void deleteTemporaryDownloadDirectory() {
		PathUtil.deleteDir(_temporaryDownloadDirectoryPath);
	}

	protected void downloadAndInstallElasticsearch() throws IOException {
		Distributable distributable =
			_distribution.getElasticsearchDistributable();

		String downloadURLString = distributable.getDownloadURLString();

		String fileName = StringUtils.substringAfterLast(
			downloadURLString, StringPool.FORWARD_SLASH);

		Path downloadedFilePath = _temporaryDownloadDirectoryPath.resolve(
			fileName);

		PathUtil.download(new URL(downloadURLString), downloadedFilePath);

		guardChecksum(downloadedFilePath, distributable.getChecksum());

		UncompressUtil.unarchive(
			downloadedFilePath, _temporaryDownloadDirectoryPath);

		Path extractedDirectoryPath = getExtractedElasticsearchDirectoryPath();

		PathUtil.copyDirectory(
			extractedDirectoryPath.resolve("lib"),
			_destinationDirectoryPath.resolve("lib"));

		Path extractedModulesDirectoryPath = extractedDirectoryPath.resolve(
			"modules");

		PathUtil.copyDirectory(
			extractedModulesDirectoryPath,
			_destinationDirectoryPath.resolve("modules"),
			extractedModulesDirectoryPath.resolve("ingest-geoip"));
	}

	protected void downloadAndInstallPlugin(Distributable distributable)
		throws IOException {

		String downloadURLString = distributable.getDownloadURLString();

		String fileName = StringUtils.substringAfterLast(
			downloadURLString, StringPool.FORWARD_SLASH);

		Path downloadedFilePath = _temporaryDownloadDirectoryPath.resolve(
			fileName);

		PathUtil.download(new URL(downloadURLString), downloadedFilePath);

		guardChecksum(downloadedFilePath, distributable.getChecksum());

		String pluginName = StringUtils.substringBeforeLast(
			fileName, StringPool.DASH);

		Path extractedDirectoryPath = _temporaryDownloadDirectoryPath.resolve(
			pluginName);

		UncompressUtil.unzip(downloadedFilePath, extractedDirectoryPath);

		Path pluginsDirectoryPath = _destinationDirectoryPath.resolve(
			"plugins");

		Files.createDirectories(pluginsDirectoryPath);

		Path pluginDestinationDirectoryPath = pluginsDirectoryPath.resolve(
			pluginName);

		PathUtil.copyDirectory(
			extractedDirectoryPath, pluginDestinationDirectoryPath);
	}

	protected void downloadAndInstallPlugins() throws IOException {
		for (Distributable distributable :
				_distribution.getPluginDistributables()) {

			downloadAndInstallPlugin(distributable);
		}
	}

	protected Path getExtractedElasticsearchDirectoryPath() throws IOException {
		try (Stream<Path> stream = Files.list(
				_temporaryDownloadDirectoryPath)) {

			return stream.filter(
				Files::isDirectory
			).findAny(
			).get();
		}
	}

	protected boolean isAlreadyInstalled() {
		return Files.exists(_destinationDirectoryPath);
	}

	private static final Logger _logger = LogManager.getLogger(
		ElasticsearchInstaller.class);

	private static final Path _temporaryDownloadDirectoryPath =
		getTemporaryDownloadDirectoryPath();

	private final Path _destinationDirectoryPath;
	private final Distribution _distribution;

}