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
package com.acmemotors.gf.support;

/**
 * @author Michael Minella
 */
public class GemfireLoaderTests {
//
//	private GemFireLoader loader;
//
////	@Mock
////	public JourneysRepository repository;
//
//
//	private static final String CORRECT_JSON = "{\"vin\": \"SCEDT26T0BD007019\", " +
//			"\"clusters\": {\"0\": {\"name\": \"\", \"lat\": 32.952136134697362, " +
//			"\"long\": -96.820293184407973, \"address\": \"\"}, \"1\": " +
//			"{\"name\": \"\", \"lat\": 32.775359756851508, " +
//			"\"long\": -96.804928235059336, \"address\": \"\"}, \"2\": {\"name\": \"\"," +
//			" \"lat\": 32.943392560883851, \"long\": -96.649327721702406, " +
//			"\"address\": \"\"}, \"3\": {\"name\": \"\", \"lat\": 32.968117331986988, " +
//			"\"long\": -96.927546300480572, \"address\": \"\"}}}";
//	private static final String EMPTY_VIN_JSON = "{\"vin\": \"\", \"clusters\": {\"0\": " +
//			"{\"name\": \"\", \"lat\": 32.952136134697362, " +
//			"\"long\": -96.820293184407973, \"address\": \"\"}, \"1\": " +
//			"{\"name\": \"\", \"lat\": 32.775359756851508, " +
//			"\"long\": -96.804928235059336, \"address\": \"\"}, \"2\": {\"name\": \"\"," +
//			" \"lat\": 32.943392560883851, \"long\": -96.649327721702406, " +
//			"\"address\": \"\"}, \"3\": {\"name\": \"\", \"lat\": 32.968117331986988, " +
//			"\"long\": -96.927546300480572, \"address\": \"\"}}}";
//	private static final String NO_VIN_JSON = "{\"clusters\": {\"0\": {\"name\": \"\", " +
//			"\"lat\": 32.952136134697362, \"long\": -96.820293184407973, " +
//			"\"address\": \"\"}, \"1\": {\"name\": \"\", \"lat\": 32.775359756851508, " +
//			"\"long\": -96.804928235059336, \"address\": \"\"}, \"2\": " +
//			"{\"name\": \"\", \"lat\": 32.943392560883851, " +
//			"\"long\": -96.649327721702406, \"address\": \"\"}, \"3\": " +
//			"{\"name\": \"\", \"lat\": 32.968117331986988, " +
//			"\"long\": -96.927546300480572, \"address\": \"\"}}}";
//	private static final String EMPTY_CLUSTERS_JSON = "{\"vin\": \"SCEDT26T0BD007019\"," +
//			" \"clusters\": {}}";
//	private static final String NO_CLUSTERS_JSON = "{\"vin\": \"SCEDT26T0BD007019\"}";
//
//	@Before
//	public void setUp() {
//		MockitoAnnotations.initMocks(this);
//		loader = new GemFireLoader();
////		loader.setRepository(repository);
//	}
//
//	@Test
//	public void testMapJourneysEmptyString() throws Exception {
//		assertNull(loader.mapJourneys(""));
//	}
//
//	@Test(expected = IllegalArgumentException.class)
//	public void testMapJourneysEmptyJson() throws Exception {
//		assertNull(loader.mapJourneys("{}"));
//	}
//
//	@Test
//	public void testEmptyClusters() throws Exception {
//		Journeys journeys = loader.mapJourneys(EMPTY_CLUSTERS_JSON);
//
//		// Let's see if she'll go 88 MPH...
//		assertEquals(journeys.getVin(), "SCEDT26T0BD007019");
//		assertEquals(journeys.getDestinations().size(), 0);
//	}
//
//	@Test(expected = IllegalArgumentException.class)
//	public void testNoClusters() throws Exception {
//		loader.mapJourneys(NO_CLUSTERS_JSON);
//	}
//
//	@Test(expected = IllegalArgumentException.class)
//	public void testEmptyVin() throws Exception {
//		loader.mapJourneys(EMPTY_VIN_JSON);
//	}
//
//	@Test(expected = IllegalArgumentException.class)
//	public void testNoVin() throws Exception {
//		loader.mapJourneys(NO_VIN_JSON);
//	}
//
//	@Test
//	public void testValidJson() throws Exception {
//		Journeys journeys = loader.mapJourneys(CORRECT_JSON);
//
//		assertEquals(journeys.getVin(), "SCEDT26T0BD007019");
//		assertEquals(journeys.getDestinations().size(), 4);
//
//		JourneyDestination curDestination = journeys.getDestinations().get(0);
//
//		assertEquals(curDestination.getAddress(), "");
//		assertEquals(curDestination.getName(), "");
//		assertEquals(curDestination.getLatitude(), 32.952136134697362, 0);
//		assertEquals(curDestination.getLongitude(), -96.820293184407973, 0);
//
//		curDestination = journeys.getDestinations().get(1);
//
//		assertEquals(curDestination.getAddress(), "");
//		assertEquals(curDestination.getName(), "");
//		assertEquals(curDestination.getLatitude(), 32.775359756851508, 0);
//		assertEquals(curDestination.getLongitude(), -96.804928235059336, 0);
//
//		curDestination = journeys.getDestinations().get(2);
//
//		assertEquals(curDestination.getAddress(), "");
//		assertEquals(curDestination.getName(), "");
//		assertEquals(curDestination.getLatitude(), 32.943392560883851, 0);
//		assertEquals(curDestination.getLongitude(), -96.649327721702406, 0);
//
//		curDestination = journeys.getDestinations().get(3);
//
//		assertEquals(curDestination.getAddress(), "");
//		assertEquals(curDestination.getName(), "");
//		assertEquals(curDestination.getLatitude(), 32.968117331986988, 0);
//		assertEquals(curDestination.getLongitude(), -96.927546300480572, 0);
//	}
//
//	@Test
//	public void testIntegration() throws Exception {
//		ArgumentCaptor<Journeys> capturedJourneys =
//				ArgumentCaptor.forClass(Journeys.class);
//		ClassPathResource inputFile = new ClassPathResource("/data/clusters.json");
//
//		loader.run(inputFile.getFile().getAbsolutePath());
//
////		verify(repository, times(3)).save(capturedJourneys.capture());
//
//		List<Journeys> results = capturedJourneys.getAllValues();
//
//		assertEquals(3, results.size());
//
//		Journeys curVin = results.get(0);
//
//		// Let's go back...to the FUTURE!
//		assertEquals("SCEDT26T0BD007019", curVin.getVin());
//		assertEquals(4, curVin.getDestinations().size());
//		validateLocation("", "", 32.952136134697362, -96.820293184407973, curVin.getDestinations().get(0));
//		validateLocation("", "", 32.775359756851508, -96.804928235059336, curVin.getDestinations().get(1));
//		validateLocation("", "", 32.943392560883851, -96.649327721702406, curVin.getDestinations().get(2));
//		validateLocation("", "", 32.968117331986988, -96.927546300480572, curVin.getDestinations().get(3));
//
//		curVin = results.get(1);
//
//		// 53
//		assertEquals("4724069", curVin.getVin());
//		assertEquals(1, curVin.getDestinations().size());
//		validateLocation("", "", 33.9336949, -117.2768647, curVin.getDestinations().get(0));
//
//		curVin = results.get(2);
//
//		// Don't hassle the Hoff
//		assertEquals("1G2AW87HXCL527449", curVin.getVin());
//		assertEquals(2, curVin.getDestinations().size());
//		validateLocation("", "", 34.165145, -118.508207, curVin.getDestinations().get(0));
//		validateLocation("", "", 32.775359756851508, -96.804928235059336, curVin.getDestinations().get(1));
//	}
//
//	private void validateLocation(String name, String address, double latitude,
//			double longitude, JourneyDestination destination) {
//
//		assertEquals(destination.getName(), name);
//		assertEquals(destination.getAddress(), address);
//		assertEquals(destination.getLongitude(), longitude, 0);
//		assertEquals(destination.getLatitude(), latitude, 0);
//	}
}
