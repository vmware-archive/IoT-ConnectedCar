/*
 * Copyright 2015 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.acmemotors.rest.configuration;


import java.net.InetSocketAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.validator.constraints.NotEmpty;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Copied form Spring Cloud Stream Modules
 *
 * @author David Turanski
 */
@ConfigurationProperties
public class GemfirePoolProperties {
	private static final Pattern HOST_AND_PORT_PATTERN = Pattern.compile("^\\s*(.*?):(\\d+)\\s*$");

	public static enum ConnectType {locator, server};
	/**
	 * Specifies one or more Gemfire locator or server addresses formatted as [host]:[port].
	 */
	private InetSocketAddress[] hostAddresses = {new InetSocketAddress("localhost",10334)};

	/**
	 * Specifies connection type: 'server' or 'locator'.
	 */
	private ConnectType connectType = ConnectType.locator;

	/**
	 * Set to true to enable subscriptions for the client pool. Required to sync updates to the client cache.
	 */
	private boolean subscriptionEnabled;
	

	@NotEmpty
	public InetSocketAddress[] getHostAddresses() {
		return hostAddresses;
	}

	public void setHostAddresses(String[] hostAddresses) {
		this.hostAddresses = new InetSocketAddress[hostAddresses.length];
		for (int i = 0; i < hostAddresses.length; i++) {
			Matcher m = HOST_AND_PORT_PATTERN.matcher(hostAddresses[i]);
			if (m.matches()) {
				String host = m.group(1);
				int port = Integer.parseInt(m.group(2));
				this.hostAddresses[i] = new InetSocketAddress(host, port);
			}
			else {
				throw new IllegalArgumentException(hostAddresses[i] + " is not a valid [host]:[port] value");
			}
		}
	}

	public ConnectType getConnectType() {
		return connectType;
	}

	public void setConnectType(ConnectType connectType) {
		this.connectType = connectType;
	}

	public boolean isSubscriptionEnabled() {
		return subscriptionEnabled;
	}

	public void setSubscriptionEnabled(boolean subscriptionEnabled) {
		this.subscriptionEnabled = subscriptionEnabled;
	}
}
