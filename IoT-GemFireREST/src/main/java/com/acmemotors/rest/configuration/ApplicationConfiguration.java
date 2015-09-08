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
package com.acmemotors.rest.configuration;

import com.gemstone.gemfire.cache.client.ClientCache;
import com.gemstone.gemfire.cache.client.ClientRegionShortcut;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;

/**
 * @author Michael Minella
 */
@Configuration
//@EnableConfigurationProperties(GemfirePoolProperties.class)
@EnableGemfireRepositories(basePackages = "com.acmemotors.rest")
public class ApplicationConfiguration {
//	@Autowired
//	GemfirePoolProperties config;
//
//	@Bean
//	@Profile("cloud")
//	PoolFactoryBean gemfirePool() {
//		System.out.println("Configuring based on the following");
//		System.out.println(config.toString());
//
//		PoolFactoryBean poolFactoryBean = new PoolFactoryBean();
//
//		switch (config.getConnectType()) {
//			case locator:
//				poolFactoryBean.setLocators(Arrays.asList(config.getHostAddresses()));
//				break;
//			case server:
//				poolFactoryBean.setServers(Arrays.asList(config.getHostAddresses()));
//				break;
//			default:
//				throw new IllegalArgumentException("connectType " + config.getConnectType() + " is not supported.");
//		}
//		poolFactoryBean.setSubscriptionEnabled(config.isSubscriptionEnabled());
//		poolFactoryBean.setName(GemfireConstants.DEFAULT_GEMFIRE_POOL_NAME);
//		return poolFactoryBean;
//	}

//	@Bean
//	@Profile("!cloud")
//	ClientCache cache() {
//		return new ClientCacheFactory().create();
//	}
//
//	@Bean
//	@Profile("!cloud")
//	PoolFactoryBean poolFactoryBean(@Value("${gf.server.port}") int serverPort,
//			@Value("${gf.server.host}") String serverHost) throws Exception {
//		PoolFactoryBean factoryBean = new PoolFactoryBean();
//		factoryBean.setName("my-pool");
//		factoryBean.setServers(
//				Collections.singletonList(new InetSocketAddress(serverHost, serverPort)));
//		factoryBean.afterPropertiesSet();
//		return factoryBean;
//	}
//
//	@Bean
//	@SuppressWarnings("rawtypes")
//	ClientRegionFactoryBean journeyRegion(ClientCache cache) {
//
//		ClientRegionFactoryBean exampleRegion = new
//				ClientRegionFactoryBean<>();
//
//		exampleRegion.setName("journeys");
//		exampleRegion.setCache(cache);
//		exampleRegion.setShortcut(ClientRegionShortcut.PROXY);
//
//		return exampleRegion;
//	}

	@Bean
	@SuppressWarnings("rawtypes")
	public ClientRegionFactoryBean carPositionRegion(ClientCache cache) {

		ClientRegionFactoryBean exampleRegion = new
				ClientRegionFactoryBean<>();

		exampleRegion.setName("car-position");
		exampleRegion.setCache(cache);
		exampleRegion.setShortcut(ClientRegionShortcut.PROXY);

		return exampleRegion;
	}
}
