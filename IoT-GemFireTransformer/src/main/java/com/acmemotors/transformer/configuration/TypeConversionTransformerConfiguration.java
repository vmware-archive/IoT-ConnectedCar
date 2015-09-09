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
package com.acmemotors.transformer.configuration;

import java.util.HashMap;
import java.util.Map;

import com.acmemotors.rest.domain.CarPosition;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.integration.annotation.Transformer;

/**
 * Converts the JSON string provided upstream into a
 * {@link com.acmemotors.rest.domain.CarPosition} object to be serialized in Gemfire.
 * This allows Spring Data Gemfire to retrieve POJOs instead of working with
 * {@link com.gemstone.gemfire.pdx.PdxInstance} objects.
 *
 * @author Michael Minella
 */
@EnableBinding(Processor.class)
public class TypeConversionTransformerConfiguration {

	private static final Logger logger =
			LoggerFactory.getLogger(TypeConversionTransformerConfiguration.class);

	private final ObjectMapper mapper;

	public TypeConversionTransformerConfiguration() {
		this.mapper = new ObjectMapper();
		this.mapper.configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true);
	}

	@Transformer(inputChannel = Processor.INPUT, outputChannel = Processor.OUTPUT)
	public CarPosition transform(String payload) {

		CarPosition carPosition = null;

		try {
			if (payload != null) {
				String cleaned = payload.replaceAll("\\\\", "");
				cleaned = cleaned.replaceFirst("\"", "");
				cleaned = cleaned.substring(0, cleaned.length() - 1);

				Map<String, Object> map = mapper.readValue(cleaned,
						new TypeReference<HashMap<String, Object>>(){});
				carPosition = new CarPosition(map);
			}
		} catch (final Exception e) {
			logger.error("Error converting to a CarPosition object", e);

			logger.error("Error payload=[" + payload + "]");

			String cleaned = payload.replaceAll("\\\\", "");
			cleaned = cleaned.replaceFirst("\"", "");
			cleaned = cleaned.substring(0, cleaned.length() - 1);

			logger.error("cleaned: " + cleaned);
		}

		logger.info("Payload has been transformed into a " + carPosition.getClass());

		return carPosition;
	}
}
