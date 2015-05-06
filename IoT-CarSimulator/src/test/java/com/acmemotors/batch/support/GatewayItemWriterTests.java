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
package com.acmemotors.batch.support;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.util.ArrayList;
import java.util.List;

import com.acmemotors.service.RequestGateway;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author Michael Minella
 */
public class GatewayItemWriterTests {

	private GatewayItemWriter writer;
	@Mock
	private RequestGateway gateway;
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		writer = new GatewayItemWriter();
		writer.setGateway(gateway);
		writer.afterPropertiesSet();
	}

	@After
	public void tearDown() {
		exception = ExpectedException.none();
	}

	@Test
	public void testNoGateway() throws Exception {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("A RequestGateway is required");

		GatewayItemWriter writer = new GatewayItemWriter();
		writer.afterPropertiesSet();
	}

	@Test
	public void testGatewaySet() throws Exception {
		GatewayItemWriter writer = new GatewayItemWriter();
		writer.setGateway(gateway);
		writer.afterPropertiesSet();
	}

	@Test
	public void testWritingEmptyList() throws Exception {
		List<String> items = new ArrayList<>();
		writer.write(items);
		verifyZeroInteractions(gateway);
	}

	@Test
	public void testWritingOneItem() throws Exception {
		List<String> items = new ArrayList<>();
		items.add("foo");

		writer.write(items);

		verify(gateway).send("foo");
	}

	@Test
	public void testWritingMultipleItems() throws Exception {
		List<String> items = new ArrayList<>();
		items.add("foo");
		items.add("bar");
		items.add("baz");

		writer.write(items);

		verify(gateway).send(eq("foo"));
		verify(gateway).send(eq("bar"));
		verify(gateway).send(eq("baz"));
	}

	@Test
	public void testExceptionIsBubbled() throws Exception {
		RuntimeException toBeThrown = new RuntimeException("Cannot connect");

		doThrow(toBeThrown).when(gateway).send("bar");

		exception.expect(RuntimeException.class);
		exception.expectMessage("Cannot connect");

		List<String> items = new ArrayList<>();
		items.add("foo");
		items.add("bar");
		items.add("baz");

		writer.write(items);
	}
}
