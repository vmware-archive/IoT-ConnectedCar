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

import com.acmemotors.obd2.OBD2Controller;
import org.junit.Test;

/**
 * @author mminella
 */
public class BinaryTest {

	@Test
	public void testConversions() {
		String response = "01 00\n" +
			  "18 DA F1 11 06 41 00 BE 3F A8 13 \n" +
			  "18 DA F1 1D 06 41 00 98 3A 80 01 \n" +
			  "\n" +
			  ">";

		OBD2Controller controller = new OBD2Controller();

		controller.decodePidsSupported(response, 0);
	}
}
