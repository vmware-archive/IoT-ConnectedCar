/*
 * Copyright 2015 the original author or authors.
 *
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
package com.acmemotors.rest;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Collections;

import com.acmemotors.rest.configuration.GemfirePoolProperties;
import com.gemstone.gemfire.cache.client.ClientCache;
import com.gemstone.gemfire.cache.client.ClientCacheFactory;
import com.gemstone.gemfire.cache.client.ClientRegionShortcut;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.client.PoolFactoryBean;
import org.springframework.data.gemfire.config.GemfireConstants;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;

/**
 * Spring Boot bootstrap class to configure the Spring Data Repositories in
 * IoT-GemFireCommons to be exposed as Spring Data REST endpoints.
 *
 * @author Michael Minella
 */
@Configuration
@EnableConfigurationProperties(GemfirePoolProperties.class)
@EnableGemfireRepositories(basePackages = "com.acmemotors.rest")
@SpringBootApplication
public class Main {
	@Autowired
	GemfirePoolProperties config;

//	@Bean
//	@Profile("cloud")
//	public ClientCacheFactoryBean clientCache(Pool pool) {
//		ClientCacheFactoryBean clientCacheFactoryBean = new ClientCacheFactoryBean();
//		clientCacheFactoryBean.setUseBeanFactoryLocator(false);
//		clientCacheFactoryBean.setPool(pool);
//		clientCacheFactoryBean.setPoolName("gemfirePool");
//		return clientCacheFactoryBean;
//	}

	@Bean
	@Profile("cloud")
	PoolFactoryBean gemfirePool() {
		PoolFactoryBean poolFactoryBean = new PoolFactoryBean();

		switch (config.getConnectType()) {
			case locator:
				poolFactoryBean.setLocators(Arrays.asList(config.getHostAddresses()));
				break;
			case server:
				poolFactoryBean.setServers(Arrays.asList(config.getHostAddresses()));
				break;
			default:
				throw new IllegalArgumentException("connectType " + config.getConnectType() + " is not supported.");
		}
		poolFactoryBean.setSubscriptionEnabled(config.isSubscriptionEnabled());
		poolFactoryBean.setName(GemfireConstants.DEFAULT_GEMFIRE_POOL_NAME);
		return poolFactoryBean;
	}

	@Bean
	@Profile("!cloud")
	ClientCache cache() {
		return new ClientCacheFactory().create();
	}

	@Bean
	@Profile("!cloud")
	PoolFactoryBean poolFactoryBean(@Value("${gf.server.port}") int serverPort,
			@Value("${gf.server.host}") String serverHost) throws Exception {
		PoolFactoryBean factoryBean = new PoolFactoryBean();
		factoryBean.setName("my-pool");
		factoryBean.setServers(
				Collections.singletonList(new InetSocketAddress(serverHost, serverPort)));
		factoryBean.afterPropertiesSet();
		return factoryBean;
	}

	@Bean
	@SuppressWarnings("rawtypes")
	ClientRegionFactoryBean journeyRegion(ClientCache cache) {

		ClientRegionFactoryBean exampleRegion = new
				ClientRegionFactoryBean<>();

		exampleRegion.setName("journeys");
		exampleRegion.setCache(cache);
		exampleRegion.setShortcut(ClientRegionShortcut.PROXY);

		return exampleRegion;
	}

	@Bean
	@SuppressWarnings("rawtypes")
	ClientRegionFactoryBean carPositionRegion(ClientCache cache) {

		ClientRegionFactoryBean exampleRegion = new
				ClientRegionFactoryBean<>();

		exampleRegion.setName("car-position");
		exampleRegion.setCache(cache);
		exampleRegion.setShortcut(ClientRegionShortcut.PROXY);

		return exampleRegion;
	}

	public static void main(String[] args) throws IOException {
		SpringApplication.run(Main.class, args);
	}
}
