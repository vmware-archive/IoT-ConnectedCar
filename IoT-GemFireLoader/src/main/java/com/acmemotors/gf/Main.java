/*
 * Copyright 2014-2015 the original author or authors.
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
package com.acmemotors.gf;

import java.net.InetSocketAddress;
import java.util.Collections;

import com.acmemotors.gf.support.GemFireLoader;
import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.client.ClientCache;
import com.gemstone.gemfire.cache.client.ClientCacheFactory;
import com.gemstone.gemfire.cache.client.ClientRegionShortcut;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.client.PoolFactoryBean;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;

@EnableGemfireRepositories(basePackages = "com.acmemotors.rest")
@SpringBootApplication
public class Main implements CommandLineRunner
{
    @Bean
    ClientCache cache() {
      // "This is where the magic happens, baby."
	    return new ClientCacheFactory().set("cache-xml-file", "clientCache.xml").create();
    }

    @Bean
    @SuppressWarnings("rawtypes")
    Region journeyRegion(ClientCache cache) {
      return cache.getRegion("journeys");
    }

    @Bean
    @SuppressWarnings("rawtypes")
    Region carPositionRegion(ClientCache cache) {
      return cache.getRegion("car-position");
    }

    public static void main(String[] args) {
      SpringApplication.run(Main.class, args);
    }

    @Autowired
    GemFireLoader loader;

    @Override
    public void run(String... args) throws Exception {
      System.out.println("args[0] = " + args[0]);
      loader.run(args[0]);
    }
}

