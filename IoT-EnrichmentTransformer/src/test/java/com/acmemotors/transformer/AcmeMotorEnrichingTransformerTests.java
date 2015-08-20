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
package com.acmemotors.transformer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import com.acmemotors.transformer.configuration.AcmeMotorEnrichingTransformerConfiguration;
import org.junit.Before;
import org.junit.Test;

/**
 * @author mminella
 */
@SuppressWarnings("unchecked")
public class AcmeMotorEnrichingTransformerTests {

	private AcmeMotorEnrichingTransformerConfiguration transformer;

	@Before
	public void setUp() {
		transformer =
				new AcmeMotorEnrichingTransformerConfiguration();
	}

	@Test
	public void testNoVin() {
		Map<String, Object> payload = (Map<String, Object>) transformer.transform("{}");

		assertEquals(1, payload.size());
		assertNotNull("timestamp", payload.get("timestamp"));
	}

	@Test
	public void testWithVin() {
		Map<String, Object> payload =
				(Map<String, Object>) transformer.transform("{\"vin\":\"my vin\"}");

		assertEquals(2, payload.size());
		assertNotNull("timestamp", payload.get("timestamp"));
		assertEquals("MY VIN", payload.get("vin"));
	}
}
