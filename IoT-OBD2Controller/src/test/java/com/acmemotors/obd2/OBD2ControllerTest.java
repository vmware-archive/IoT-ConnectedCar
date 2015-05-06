/*
 * Copyright 2014 the original author or authors.
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
package com.acmemotors.obd2;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author mminella
 */
public class OBD2ControllerTest {

	private OBD2Controller controller;

	@Before
	public void setUp() {
		controller = new OBD2Controller();
	}

	/**
	 */
	@Test
	public void testRpmDecoding() {
		int rpm = controller.decodeRPM("01 0C\n" +
										 "18 DA F1 11 04 41 0C 0C C0 \n" +
										 "18 DA F1 1D 04 41 0C 0C B4 \n" +
										 "\n" +
										 ">");

		assertEquals(816, rpm);
	}
}
