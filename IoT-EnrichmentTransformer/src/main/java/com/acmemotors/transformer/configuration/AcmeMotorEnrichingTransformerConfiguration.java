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
package com.acmemotors.transformer.configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.integration.annotation.Transformer;

/**
 * Performs the domain transformation into an acme motors specific domain model.
 * 
 * @author gfoster
 * @author Michael Minella
 * 
 */
@EnableBinding(Processor.class)
public class AcmeMotorEnrichingTransformerConfiguration {
    private static final Logger logger =
            LoggerFactory.getLogger(AcmeMotorEnrichingTransformerConfiguration.class);

    private final ObjectMapper mapper = new ObjectMapper();

    @Transformer(inputChannel = Processor.INPUT, outputChannel = Processor.OUTPUT)
    public String transform(String payload) throws IOException {

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

        logger.info("value transformed");

        return mapper.writeValueAsString(map);
    }
}
