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
package com.acmemotors.transformer;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.messaging.MessageChannel;

/**
 * Performs the domain transformation into an acme motors specific domain model.
 * 
 * @author gfoster
 * @author Michael Minella
 * 
 */
@Configuration
@EnableIntegration
public class AcmeMotorEnrichingTransformerConfiguration {
    private static final Logger logger =
            LoggerFactory.getLogger(AcmeMotorEnrichingTransformerConfiguration.class);

    @Autowired
    private AcmeEnrichingTransformer transformer;

    @Bean
    public MessageChannel input() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel output() {
        return new DirectChannel();
    }

    @MessageEndpoint
    public static class AcmeEnrichingTransformer {
        private final ObjectMapper mapper = new ObjectMapper();

        @Transformer(inputChannel = "input", outputChannel = "output")
        @SuppressWarnings({"unchecked", "rawtypes"})
        public Map transform(String payload) {

            Map<String, Object> map = new HashMap<>();
            try {
                if (payload != null) {
                    map = mapper.readValue(payload, Map.class);
                    map.put("timestamp", System.currentTimeMillis());
                    if(map.containsKey("vin")) {
                        map.put("vin", ((String) map.get("vin")).toUpperCase());
                    }
                }
            } catch (final Exception e) {
                logger.error("Error adding a timestamp and upper casing VIN in the message",
                        e);
                if (logger.isDebugEnabled()) {
                    logger.debug("Errored payload=[" + payload + "]");
                }
            }

            return map;
        }
    }
}
