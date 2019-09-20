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

package com.liferay.portal.search.elasticsearch7.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Michael C. Han
 */
@ExtendedObjectClassDefinition(category = "search")
@Meta.OCD(
	id = "com.liferay.portal.search.elasticsearch7.configuration.ElasticsearchConfiguration",
	localization = "content/Language",
	name = "elasticsearch7-configuration-name"
)
public interface ElasticsearchConfiguration {

	@Meta.AD(
		deflt = "LiferayElasticsearchCluster",
		description = "cluster-name-help", name = "cluster-name",
		required = false
	)
	public String clusterName(); //embedded only

	@Meta.AD(
		deflt = "EMBEDDED", description = "operation-mode-help",
		name = "operation-mode", required = false
	)
	public OperationMode operationMode();

	@Meta.AD(
		deflt = "liferay-", description = "index-name-prefix-help",
		name = "index-name-prefix", required = false
	)
	public String indexNamePrefix();

	@Meta.AD(
		deflt = "", description = "number-of-index-replicas-help",
		name = "number-of-index-replicas", required = false
	)
	public String indexNumberOfReplicas();

	@Meta.AD(
		deflt = "", description = "number-of-index-shards-help",
		name = "number-of-index-shards", required = false
	)
	public String indexNumberOfShards();

	@Meta.AD(
		deflt = "false", description = "bootstrap-mlockall-help",
		name = "bootstrap-mlockall", required = false
	)
	public boolean bootstrapMlockAll(); //embedded only

	@Meta.AD(
		deflt = "true", description = "log-exceptions-only-help",
		name = "log-exceptions-only", required = false
	)
	public boolean logExceptionsOnly();

	@Meta.AD(
		deflt = "5", description = "retry-on-conflict-help",
		name = "retry-on-conflict", required = false
	)
	public int retryOnConflict();

	@Meta.AD(
		deflt = "9300-9400",
		description = "discovery-zen-ping-unicast-hosts-port-help",
		name = "discovery-zen-ping-unicast-hosts-port", required = false
	)
	public String discoveryZenPingUnicastHostsPort();

	@Meta.AD(
		deflt = "", description = "network-host-help", name = "network-host",
		required = false
	)
	public String networkHost(); //embedded only

	@Meta.AD(
		deflt = "9200", description = "embedded-http-port-help",
		name = "embedded-http-port-host", required = false
	)
	public int embeddedHttpPort(); //embedded only

	@Meta.AD(
		deflt = "", description = "network-bind-host-help",
		name = "network-bind-host", required = false
	)
	public String networkBindHost(); //embedded only

	@Meta.AD(
		deflt = "", description = "network-publish-host-help",
		name = "network-publish-host", required = false
	)
	public String networkPublishHost(); //embedded only

	/**
	 * @deprecated As of Judson (7.1.x), with no direct replacement
	 */
	@Deprecated
	@Meta.AD(
		deflt = "", description = "transport-tcp-port-help",
		name = "transport-tcp-port", required = false
	)
	public String transportTcpPort(); //embedded only

	@Meta.AD(
		deflt = "http://localhost:9201", description = "transport-addresses-help",
		name = "transport-addresses", required = false
	)
	public String[] networkHostAddresses();

	@Meta.AD(
		deflt = "localhost:9300", description = "transport-addresses-help",
		name = "transport-addresses", required = false
	)
	public String[] transportAddresses();

	/**
	 * @deprecated As of Judson (7.1.x), with no direct replacement
	 */
	@Deprecated
	@Meta.AD(
		deflt = "true", description = "client-transport-sniff-help",
		name = "client-transport-sniff", required = false
	)
	public boolean clientTransportSniff();

	/**
	 * @deprecated As of Judson (7.1.x), with no direct replacement
	 */
	@Deprecated
	@Meta.AD(
		deflt = "false",
		description = "client-transport-ignore-cluster-name-help",
		name = "client-transport-ignore-cluster-name", required = false
	)
	public boolean clientTransportIgnoreClusterName();

	/**
	 * @deprecated As of Judson (7.1.x), with no direct replacement
	 */
	@Deprecated
	@Meta.AD(
		deflt = "", description = "client-transport-ping-timeout-help",
		name = "client-transport-ping-timeout", required = false
	)
	public String clientTransportPingTimeout();

	/**
	 * @deprecated As of Judson (7.1.x), with no direct replacement
	 */
	@Deprecated
	@Meta.AD(
		deflt = "",
		description = "client-transport-nodes-sampler-interval-help",
		name = "client-transport-nodes-sampler-interval", required = false
	)
	public String clientTransportNodesSamplerInterval();

	/**
	 * @deprecated As of Judson (7.1.x), with no direct replacement
	 */
	@Deprecated
	@Meta.AD(
		deflt = "true", description = "http-enabled-help",
		name = "http-enabled", required = false
	)
	public boolean httpEnabled(); //embedded only

	@Meta.AD(
		deflt = "true", description = "http-cors-enabled-help",
		name = "http-cors-enabled", required = false
	)
	public boolean httpCORSEnabled(); //embedded only

	@Meta.AD(
		deflt = "/https?:\\/\\/localhost(:[0-9]+)?/",
		description = "http-cors-allow-origin-help",
		name = "http-cors-allow-origin", required = false
	)
	public String httpCORSAllowOrigin(); //embedded only

	@Meta.AD(
		description = "http-cors-configurations-help",
		name = "http-cors-configurations", required = false
	)
	public String httpCORSConfigurations(); //embedded only

	@Meta.AD(
		description = "additional-configurations-help",
		name = "additional-configurations", required = false
	)
	public String additionalConfigurations(); //embedded only

	@Meta.AD(
		description = "additional-index-configurations-help",
		name = "additional-index-configurations", required = false
	)
	public String additionalIndexConfigurations();

	@Meta.AD(
		description = "additional-type-mappings-help",
		name = "additional-type-mappings", required = false
	)
	public String additionalTypeMappings();

	@Meta.AD(
		description = "override-type-mappings-help",
		name = "override-type-mappings", required = false
	)
	public String overrideTypeMappings();

}