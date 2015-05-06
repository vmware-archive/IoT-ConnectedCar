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
package com.acmemotors.rest.domain;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Test;

/**
 * @author Michael Minella
 */
public class CarPositionTests {

	private static final String EXAMPLE_JSON = " {\n" +
			"\"Predictions\": {\n" +
			"    \"ClusterPredictions\": {\n" +
			"        \"0\": {\n" +
			"            \"EndLocation\": [\n" +
			"                41.97768290811704,\n" +
			"                -87.66936619865827\n" +
			"            ],\n" +
			"            \"MPG_Journey\": 20.2776,\n" +
			"            \"Probability\": 0.09\n" +
			"        }\n" +
			"    },\n" +
			"    \"RemainingRange\": 267\n" +
			"},\n" +
			"\"absolute_throttle_pos_b\": 30.0,\n" +
			"\"acceleration\": 1.014,\n" +
			"\"accelerator_throttle_pos_d\": 20.0,\n" +
			"\"accelerator_throttle_pos_e\": 10.0,\n" +
			"\"barometric_pressure\": 100,\n" +
			"\"bearing\": 85.850507,\n" +
			"\"catalyst_temp\": 596.0,\n" +
			"\"control_module_voltage\": 14.0,\n" +
			"\"coolant_temp\": 88,\n" +
			"\"distance_with_mil_on\": 0,\n" +
			"\"engine_load\": 24.0,\n" +
			"\"fuel_level_input\": 43,\n" +
			"\"fuel_system_status\": [\n" +
			"    2,\n" +
			"    0\n" +
			"],\n" +
			"\"intake_air_temp\": 3,\n" +
			"\"intake_manifold_pressure\": 34,\n" +
			"\"journey_id\": \"ab18d293-57ed-497f-8b25-f587asf0c75\",\n" +
			"\"latitude\": 41.798128,\n" +
			"\"long_term_fuel\": 5.0,\n" +
			"\"longitude\": -87.58763,\n" +
			"\"maf_airflow\": 3.0,\n" +
			"\"mpg_instantaneous\": 0.0,\n" +
			"\"obd_standards\": \"1\",\n" +
			"\"relative_throttle_pos\": 2.0,\n" +
			"\"rpm\": 678.0,\n" +
			"\"short_term_fuel\": 4.0,\n" +
			"\"throttle_position\": 13,\n" +
			"\"time_since_engine_start\": 1760,\n" +
			"\"timestamp\": 1421876463415,\n" +
			"\"vehicle_speed\": 0,\n" +
			"\"vin\": \"WP0AD2A71FL040367\"\n" +
			"}";

	private static final String INTEGER_TIME_JSON = " {\n" +
			"\"Predictions\": {\n" +
			"    \"ClusterPredictions\": {\n" +
			"        \"0\": {\n" +
			"            \"EndLocation\": [\n" +
			"                41.97768290811704,\n" +
			"                -87.66936619865827\n" +
			"            ],\n" +
			"            \"MPG_Journey\": 20.2776,\n" +
			"            \"Probability\": 0.09\n" +
			"        }\n" +
			"    },\n" +
			"    \"RemainingRange\": 267\n" +
			"},\n" +
			"\"absolute_throttle_pos_b\": 30.0,\n" +
			"\"acceleration\": 1.014,\n" +
			"\"accelerator_throttle_pos_d\": 20.0,\n" +
			"\"accelerator_throttle_pos_e\": 10.0,\n" +
			"\"barometric_pressure\": 100,\n" +
			"\"bearing\": 85.850507,\n" +
			"\"catalyst_temp\": 596.0,\n" +
			"\"control_module_voltage\": 14.0,\n" +
			"\"coolant_temp\": 88,\n" +
			"\"distance_with_mil_on\": 0,\n" +
			"\"engine_load\": 24.0,\n" +
			"\"fuel_level_input\": 43,\n" +
			"\"fuel_system_status\": [\n" +
			"    2,\n" +
			"    0\n" +
			"],\n" +
			"\"intake_air_temp\": 3,\n" +
			"\"intake_manifold_pressure\": 34,\n" +
			"\"journey_id\": \"ab18d293-57ed-497f-8b25-f587asf0c75\",\n" +
			"\"latitude\": 41.798128,\n" +
			"\"long_term_fuel\": 5.0,\n" +
			"\"longitude\": -87.58763,\n" +
			"\"maf_airflow\": 3.0,\n" +
			"\"mpg_instantaneous\": 0.0,\n" +
			"\"obd_standards\": \"1\",\n" +
			"\"relative_throttle_pos\": 2.0,\n" +
			"\"rpm\": 678.0,\n" +
			"\"short_term_fuel\": 4.0,\n" +
			"\"throttle_position\": 13,\n" +
			"\"time_since_engine_start\": 170,\n" +
			"\"timestamp\": 1421876463415,\n" +
			"\"vehicle_speed\": 0,\n" +
			"\"vin\": \"WP0AD2A71FL040367\"\n" +
			"}";

	private static final String LONG_TIME_JSON = " {\n" +
			"\"Predictions\": {\n" +
			"    \"ClusterPredictions\": {\n" +
			"        \"0\": {\n" +
			"            \"EndLocation\": [\n" +
			"                41.97768290811704,\n" +
			"                -87.66936619865827\n" +
			"            ],\n" +
			"            \"MPG_Journey\": 20.2776,\n" +
			"            \"Probability\": 0.09\n" +
			"        }\n" +
			"    },\n" +
			"    \"RemainingRange\": 267\n" +
			"},\n" +
			"\"absolute_throttle_pos_b\": 30.0,\n" +
			"\"acceleration\": 1.014,\n" +
			"\"accelerator_throttle_pos_d\": 20.0,\n" +
			"\"accelerator_throttle_pos_e\": 10.0,\n" +
			"\"barometric_pressure\": 100,\n" +
			"\"bearing\": 85.850507,\n" +
			"\"catalyst_temp\": 596.0,\n" +
			"\"control_module_voltage\": 14.0,\n" +
			"\"coolant_temp\": 88,\n" +
			"\"distance_with_mil_on\": 0,\n" +
			"\"engine_load\": 24.0,\n" +
			"\"fuel_level_input\": 43,\n" +
			"\"fuel_system_status\": [\n" +
			"    2,\n" +
			"    0\n" +
			"],\n" +
			"\"intake_air_temp\": 3,\n" +
			"\"intake_manifold_pressure\": 34,\n" +
			"\"journey_id\": \"ab18d293-57ed-497f-8b25-f587asf0c75\",\n" +
			"\"latitude\": 41.798128,\n" +
			"\"long_term_fuel\": 5.0,\n" +
			"\"longitude\": -87.58763,\n" +
			"\"maf_airflow\": 3.0,\n" +
			"\"mpg_instantaneous\": 0.0,\n" +
			"\"obd_standards\": \"1\",\n" +
			"\"relative_throttle_pos\": 2.0,\n" +
			"\"rpm\": 678.0,\n" +
			"\"short_term_fuel\": 4.0,\n" +
			"\"throttle_position\": 13,\n" +
			"\"time_since_engine_start\": 1760000000000000,\n" +
			"\"timestamp\": 1421876463415,\n" +
			"\"vehicle_speed\": 0,\n" +
			"\"vin\": \"WP0AD2A71FL040367\"\n" +
			"}";

	private static final String INTEGER_VEHICLE_SPEED_JSON = " {\n" +
			"\"Predictions\": {\n" +
			"    \"ClusterPredictions\": {\n" +
			"        \"0\": {\n" +
			"            \"EndLocation\": [\n" +
			"                41.97768290811704,\n" +
			"                -87.66936619865827\n" +
			"            ],\n" +
			"            \"MPG_Journey\": 20.2776,\n" +
			"            \"Probability\": 0.09\n" +
			"        }\n" +
			"    },\n" +
			"    \"RemainingRange\": 267\n" +
			"},\n" +
			"\"absolute_throttle_pos_b\": 30.0,\n" +
			"\"acceleration\": 1.014,\n" +
			"\"accelerator_throttle_pos_d\": 20.0,\n" +
			"\"accelerator_throttle_pos_e\": 10.0,\n" +
			"\"barometric_pressure\": 100,\n" +
			"\"bearing\": 85.850507,\n" +
			"\"catalyst_temp\": 596.0,\n" +
			"\"control_module_voltage\": 14.0,\n" +
			"\"coolant_temp\": 88,\n" +
			"\"distance_with_mil_on\": 0,\n" +
			"\"engine_load\": 24.0,\n" +
			"\"fuel_level_input\": 43,\n" +
			"\"fuel_system_status\": [\n" +
			"    2,\n" +
			"    0\n" +
			"],\n" +
			"\"intake_air_temp\": 3,\n" +
			"\"intake_manifold_pressure\": 34,\n" +
			"\"journey_id\": \"ab18d293-57ed-497f-8b25-f587asf0c75\",\n" +
			"\"latitude\": 41.798128,\n" +
			"\"long_term_fuel\": 5.0,\n" +
			"\"longitude\": -87.58763,\n" +
			"\"maf_airflow\": 3.0,\n" +
			"\"mpg_instantaneous\": 0.0,\n" +
			"\"obd_standards\": \"1\",\n" +
			"\"relative_throttle_pos\": 2.0,\n" +
			"\"rpm\": 678.0,\n" +
			"\"short_term_fuel\": 4.0,\n" +
			"\"throttle_position\": 13,\n" +
			"\"time_since_engine_start\": 1760,\n" +
			"\"timestamp\": 1421876463415,\n" +
			"\"vehicle_speed\": 55,\n" +
			"\"vin\": \"WP0AD2A71FL040367\"\n" +
			"}";

	private static final String DOUBLE_VEHICLE_SPEED_JSON = " {\n" +
			"\"Predictions\": {\n" +
			"    \"ClusterPredictions\": {\n" +
			"        \"0\": {\n" +
			"            \"EndLocation\": [\n" +
			"                41.97768290811704,\n" +
			"                -87.66936619865827\n" +
			"            ],\n" +
			"            \"MPG_Journey\": 20.2776,\n" +
			"            \"Probability\": 0.09\n" +
			"        }\n" +
			"    },\n" +
			"    \"RemainingRange\": 267\n" +
			"},\n" +
			"\"absolute_throttle_pos_b\": 30.0,\n" +
			"\"acceleration\": 1.014,\n" +
			"\"accelerator_throttle_pos_d\": 20.0,\n" +
			"\"accelerator_throttle_pos_e\": 10.0,\n" +
			"\"barometric_pressure\": 100,\n" +
			"\"bearing\": 85.850507,\n" +
			"\"catalyst_temp\": 596.0,\n" +
			"\"control_module_voltage\": 14.0,\n" +
			"\"coolant_temp\": 88,\n" +
			"\"distance_with_mil_on\": 0,\n" +
			"\"engine_load\": 24.0,\n" +
			"\"fuel_level_input\": 43,\n" +
			"\"fuel_system_status\": [\n" +
			"    2,\n" +
			"    0\n" +
			"],\n" +
			"\"intake_air_temp\": 3,\n" +
			"\"intake_manifold_pressure\": 34,\n" +
			"\"journey_id\": \"ab18d293-57ed-497f-8b25-f587asf0c75\",\n" +
			"\"latitude\": 41.798128,\n" +
			"\"long_term_fuel\": 5.0,\n" +
			"\"longitude\": -87.58763,\n" +
			"\"maf_airflow\": 3.0,\n" +
			"\"mpg_instantaneous\": 0.0,\n" +
			"\"obd_standards\": \"1\",\n" +
			"\"relative_throttle_pos\": 2.0,\n" +
			"\"rpm\": 678.0,\n" +
			"\"short_term_fuel\": 4.0,\n" +
			"\"throttle_position\": 13,\n" +
			"\"time_since_engine_start\": 1760,\n" +
			"\"timestamp\": 1421876463415,\n" +
			"\"vehicle_speed\": 62.8,\n" +
			"\"vin\": \"WP0AD2A71FL040367\"\n" +
			"}";

	private static final String NULL_FUEL_SYSTEM_STATUSES_JSON = " {\n" +
			"\"Predictions\": {\n" +
			"    \"ClusterPredictions\": {\n" +
			"        \"0\": {\n" +
			"            \"EndLocation\": [\n" +
			"                41.97768290811704,\n" +
			"                -87.66936619865827\n" +
			"            ],\n" +
			"            \"MPG_Journey\": 20.2776,\n" +
			"            \"Probability\": 0.09\n" +
			"        }\n" +
			"    },\n" +
			"    \"RemainingRange\": 267\n" +
			"},\n" +
			"\"absolute_throttle_pos_b\": 30.0,\n" +
			"\"acceleration\": 1.014,\n" +
			"\"accelerator_throttle_pos_d\": 20.0,\n" +
			"\"accelerator_throttle_pos_e\": 10.0,\n" +
			"\"barometric_pressure\": 100,\n" +
			"\"bearing\": 85.850507,\n" +
			"\"catalyst_temp\": 596.0,\n" +
			"\"control_module_voltage\": 14.0,\n" +
			"\"coolant_temp\": 88,\n" +
			"\"distance_with_mil_on\": 0,\n" +
			"\"engine_load\": 24.0,\n" +
			"\"fuel_level_input\": 43,\n" +
			"\"intake_air_temp\": 3,\n" +
			"\"intake_manifold_pressure\": 34,\n" +
			"\"journey_id\": \"ab18d293-57ed-497f-8b25-f587asf0c75\",\n" +
			"\"latitude\": 41.798128,\n" +
			"\"long_term_fuel\": 5.0,\n" +
			"\"longitude\": -87.58763,\n" +
			"\"maf_airflow\": 3.0,\n" +
			"\"mpg_instantaneous\": 0.0,\n" +
			"\"obd_standards\": \"1\",\n" +
			"\"relative_throttle_pos\": 2.0,\n" +
			"\"rpm\": 678.0,\n" +
			"\"short_term_fuel\": 4.0,\n" +
			"\"throttle_position\": 13,\n" +
			"\"time_since_engine_start\": 1760,\n" +
			"\"timestamp\": 1421876463415,\n" +
			"\"vehicle_speed\": 0,\n" +
			"\"vin\": \"WP0AD2A71FL040367\"\n" +
			"}";

	@Test
	public void testMapConstructor() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = mapper.readValue(EXAMPLE_JSON,
				new TypeReference<HashMap<String,Object>>(){});

		CarPosition position = new CarPosition(map);
		assertEquals(position.getAbsoluteThrottlePosB(), 30.0, 0);
		assertEquals(position.getAcceleration(), 1.014, 0);
		assertEquals(position.getAcceleratorThrottlePosD(), 20.0, 0);
		assertEquals(position.getAcceleratorThrottlePosE(), 10.0, 0);
		assertEquals(position.getBarometricPressure().intValue(), 100);
		assertEquals(position.getBearing(), 85.850507, 0);
		assertEquals(position.getCatalystTemp(), 596.0, 0);
		assertEquals(position.getControlModuleVoltage(), 14.0, 0);
		assertEquals(position.getCoolantTemp().intValue(), 88);
		assertEquals(position.getDistanceWithMilOn().intValue(), 0);
		assertEquals(position.getEngineLoad(), 24.0, 0);
		assertEquals(position.getFuelLevelInput().intValue(), 43);
		assertEquals(position.getIntakeAirTemp().intValue(), 3);
		assertEquals(position.getIntakeManifoldPressure().intValue(), 34);
		assertEquals(position.getJourneyId(), "ab18d293-57ed-497f-8b25-f587asf0c75");
		assertEquals(position.getLatitude(), 41.798128, 0);
		assertEquals(position.getLongTermFuel(), 5.0, 0);
		assertEquals(position.getLongitude(), -87.58763, 0);
		assertEquals(position.getMafAirflow(), 3.0, 0);
		assertEquals(position.getMpgInstantaneous(), 0.0, 0);
		assertEquals(position.getObdStandards().intValue(), 1);
		assertEquals(position.getRelativeThrottlePos(), 2.0, 0);
		assertEquals(position.getTimeSinceEngineStart().longValue(), 1760);
		assertEquals(position.getTimestamp().longValue(), 1421876463415l);
		assertEquals(position.getVehicleSpeed(), 0, 0);
		assertEquals(position.getVin(), "WP0AD2A71FL040367");
		assertEquals(position.getFuelSystemStatus()[0].intValue(), 2);
		assertEquals(position.getFuelSystemStatus()[1].intValue(), 0);
		assertEquals(position.getRemainingRange().intValue(), 267);
		assertEquals(position.getPredictions().get("0").getLatitude(), 41.97768290811704, 0);
		assertEquals(position.getPredictions().get("0").getLongitude(), -87.66936619865827, 0);
		assertEquals(position.getPredictions().get("0").getMpgJourney(), 20.2776, 0);
		assertEquals(position.getPredictions().get("0").getProbability(), 0.09, 0);
	}

	@Test
	public void testMapConstructorIntegerSpeed() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = mapper.readValue(INTEGER_VEHICLE_SPEED_JSON,
				new TypeReference<HashMap<String,Object>>(){});

		CarPosition position = new CarPosition(map);
		assertEquals(position.getAbsoluteThrottlePosB(), 30.0, 0);
		assertEquals(position.getAcceleration(), 1.014, 0);
		assertEquals(position.getAcceleratorThrottlePosD(), 20.0, 0);
		assertEquals(position.getAcceleratorThrottlePosE(), 10.0, 0);
		assertEquals(position.getBarometricPressure().intValue(), 100);
		assertEquals(position.getBearing(), 85.850507, 0);
		assertEquals(position.getCatalystTemp(), 596.0, 0);
		assertEquals(position.getControlModuleVoltage(), 14.0, 0);
		assertEquals(position.getCoolantTemp().intValue(), 88);
		assertEquals(position.getDistanceWithMilOn().intValue(), 0);
		assertEquals(position.getEngineLoad(), 24.0, 0);
		assertEquals(position.getFuelLevelInput().intValue(), 43);
		assertEquals(position.getIntakeAirTemp().intValue(), 3);
		assertEquals(position.getIntakeManifoldPressure().intValue(), 34);
		assertEquals(position.getJourneyId(), "ab18d293-57ed-497f-8b25-f587asf0c75");
		assertEquals(position.getLatitude(), 41.798128, 0);
		assertEquals(position.getLongTermFuel(), 5.0, 0);
		assertEquals(position.getLongitude(), -87.58763, 0);
		assertEquals(position.getMafAirflow(), 3.0, 0);
		assertEquals(position.getMpgInstantaneous(), 0.0, 0);
		assertEquals(position.getObdStandards().intValue(), 1);
		assertEquals(position.getRelativeThrottlePos(), 2.0, 0);
		assertEquals(position.getTimeSinceEngineStart().longValue(), 1760);
		assertEquals(position.getTimestamp().longValue(), 1421876463415l);
		assertEquals(position.getVehicleSpeed(), 55, 0);
		assertEquals(position.getVin(), "WP0AD2A71FL040367");
		assertEquals(position.getFuelSystemStatus()[0].intValue(), 2);
		assertEquals(position.getFuelSystemStatus()[1].intValue(), 0);
		assertEquals(position.getRemainingRange().intValue(), 267);
		assertEquals(position.getPredictions().get("0").getLatitude(), 41.97768290811704, 0);
		assertEquals(position.getPredictions().get("0").getLongitude(), -87.66936619865827, 0);
		assertEquals(position.getPredictions().get("0").getMpgJourney(), 20.2776, 0);
		assertEquals(position.getPredictions().get("0").getProbability(), 0.09, 0);
	}

	@Test
	public void testMapConstructorDoubleSpeed() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = mapper.readValue(DOUBLE_VEHICLE_SPEED_JSON,
				new TypeReference<HashMap<String,Object>>(){});

		CarPosition position = new CarPosition(map);
		assertEquals(position.getAbsoluteThrottlePosB(), 30.0, 0);
		assertEquals(position.getAcceleration(), 1.014, 0);
		assertEquals(position.getAcceleratorThrottlePosD(), 20.0, 0);
		assertEquals(position.getAcceleratorThrottlePosE(), 10.0, 0);
		assertEquals(position.getBarometricPressure().intValue(), 100);
		assertEquals(position.getBearing(), 85.850507, 0);
		assertEquals(position.getCatalystTemp(), 596.0, 0);
		assertEquals(position.getControlModuleVoltage(), 14.0, 0);
		assertEquals(position.getCoolantTemp().intValue(), 88);
		assertEquals(position.getDistanceWithMilOn().intValue(), 0);
		assertEquals(position.getEngineLoad(), 24.0, 0);
		assertEquals(position.getFuelLevelInput().intValue(), 43);
		assertEquals(position.getIntakeAirTemp().intValue(), 3);
		assertEquals(position.getIntakeManifoldPressure().intValue(), 34);
		assertEquals(position.getJourneyId(), "ab18d293-57ed-497f-8b25-f587asf0c75");
		assertEquals(position.getLatitude(), 41.798128, 0);
		assertEquals(position.getLongTermFuel(), 5.0, 0);
		assertEquals(position.getLongitude(), -87.58763, 0);
		assertEquals(position.getMafAirflow(), 3.0, 0);
		assertEquals(position.getMpgInstantaneous(), 0.0, 0);
		assertEquals(position.getObdStandards().intValue(), 1);
		assertEquals(position.getRelativeThrottlePos(), 2.0, 0);
		assertEquals(position.getTimeSinceEngineStart().longValue(), 1760);
		assertEquals(position.getTimestamp().longValue(), 1421876463415l);
		assertEquals(position.getVehicleSpeed(), 62.8, 0);
		assertEquals(position.getVin(), "WP0AD2A71FL040367");
		assertEquals(position.getFuelSystemStatus()[0].intValue(), 2);
		assertEquals(position.getFuelSystemStatus()[1].intValue(), 0);
		assertEquals(position.getRemainingRange().intValue(), 267);
		assertEquals(position.getPredictions().get("0").getLatitude(), 41.97768290811704, 0);
		assertEquals(position.getPredictions().get("0").getLongitude(), -87.66936619865827, 0);
		assertEquals(position.getPredictions().get("0").getMpgJourney(), 20.2776, 0);
		assertEquals(position.getPredictions().get("0").getProbability(), 0.09, 0);
	}

	@Test
	public void testMapConstructorIntegerTime() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = mapper.readValue(INTEGER_TIME_JSON,
				new TypeReference<HashMap<String,Object>>(){});

		CarPosition position = new CarPosition(map);
		assertEquals(position.getAbsoluteThrottlePosB(), 30.0, 0);
		assertEquals(position.getAcceleration(), 1.014, 0);
		assertEquals(position.getAcceleratorThrottlePosD(), 20.0, 0);
		assertEquals(position.getAcceleratorThrottlePosE(), 10.0, 0);
		assertEquals(position.getBarometricPressure().intValue(), 100);
		assertEquals(position.getBearing(), 85.850507, 0);
		assertEquals(position.getCatalystTemp(), 596.0, 0);
		assertEquals(position.getControlModuleVoltage(), 14.0, 0);
		assertEquals(position.getCoolantTemp().intValue(), 88);
		assertEquals(position.getDistanceWithMilOn().intValue(), 0);
		assertEquals(position.getEngineLoad(), 24.0, 0);
		assertEquals(position.getFuelLevelInput().intValue(), 43);
		assertEquals(position.getIntakeAirTemp().intValue(), 3);
		assertEquals(position.getIntakeManifoldPressure().intValue(), 34);
		assertEquals(position.getJourneyId(), "ab18d293-57ed-497f-8b25-f587asf0c75");
		assertEquals(position.getLatitude(), 41.798128, 0);
		assertEquals(position.getLongTermFuel(), 5.0, 0);
		assertEquals(position.getLongitude(), -87.58763, 0);
		assertEquals(position.getMafAirflow(), 3.0, 0);
		assertEquals(position.getMpgInstantaneous(), 0.0, 0);
		assertEquals(position.getObdStandards().intValue(), 1);
		assertEquals(position.getRelativeThrottlePos(), 2.0, 0);
		assertEquals(position.getTimeSinceEngineStart().longValue(), 170);
		assertEquals(position.getTimestamp().longValue(), 1421876463415l);
		assertEquals(position.getVehicleSpeed(), 0, 0);
		assertEquals(position.getVin(), "WP0AD2A71FL040367");
		assertEquals(position.getFuelSystemStatus()[0].intValue(), 2);
		assertEquals(position.getFuelSystemStatus()[1].intValue(), 0);
		assertEquals(position.getRemainingRange().intValue(), 267);
		assertEquals(position.getPredictions().get("0").getLatitude(), 41.97768290811704, 0);
		assertEquals(position.getPredictions().get("0").getLongitude(), -87.66936619865827, 0);
		assertEquals(position.getPredictions().get("0").getMpgJourney(), 20.2776, 0);
		assertEquals(position.getPredictions().get("0").getProbability(), 0.09, 0);
	}

	@Test
	public void testMapConstructorLongTime() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = mapper.readValue(LONG_TIME_JSON,
				new TypeReference<HashMap<String,Object>>(){});

		CarPosition position = new CarPosition(map);
		assertEquals(position.getAbsoluteThrottlePosB(), 30.0, 0);
		assertEquals(position.getAcceleration(), 1.014, 0);
		assertEquals(position.getAcceleratorThrottlePosD(), 20.0, 0);
		assertEquals(position.getAcceleratorThrottlePosE(), 10.0, 0);
		assertEquals(position.getBarometricPressure().intValue(), 100);
		assertEquals(position.getBearing(), 85.850507, 0);
		assertEquals(position.getCatalystTemp(), 596.0, 0);
		assertEquals(position.getControlModuleVoltage(), 14.0, 0);
		assertEquals(position.getCoolantTemp().intValue(), 88);
		assertEquals(position.getDistanceWithMilOn().intValue(), 0);
		assertEquals(position.getEngineLoad(), 24.0, 0);
		assertEquals(position.getFuelLevelInput().intValue(), 43);
		assertEquals(position.getIntakeAirTemp().intValue(), 3);
		assertEquals(position.getIntakeManifoldPressure().intValue(), 34);
		assertEquals(position.getJourneyId(), "ab18d293-57ed-497f-8b25-f587asf0c75");
		assertEquals(position.getLatitude(), 41.798128, 0);
		assertEquals(position.getLongTermFuel(), 5.0, 0);
		assertEquals(position.getLongitude(), -87.58763, 0);
		assertEquals(position.getMafAirflow(), 3.0, 0);
		assertEquals(position.getMpgInstantaneous(), 0.0, 0);
		assertEquals(position.getObdStandards().intValue(), 1);
		assertEquals(position.getRelativeThrottlePos(), 2.0, 0);
		assertEquals(position.getTimeSinceEngineStart().longValue(), 1760000000000000l);
		assertEquals(position.getTimestamp().longValue(), 1421876463415l);
		assertEquals(position.getVehicleSpeed(), 0, 0);
		assertEquals(position.getVin(), "WP0AD2A71FL040367");
		assertEquals(position.getFuelSystemStatus()[0].intValue(), 2);
		assertEquals(position.getFuelSystemStatus()[1].intValue(), 0);
		assertEquals(position.getRemainingRange().intValue(), 267);
		assertEquals(position.getPredictions().get("0").getLatitude(), 41.97768290811704, 0);
		assertEquals(position.getPredictions().get("0").getLongitude(), -87.66936619865827, 0);
		assertEquals(position.getPredictions().get("0").getMpgJourney(), 20.2776, 0);
		assertEquals(position.getPredictions().get("0").getProbability(), 0.09, 0);
	}

	@Test
	public void testMapConstructorNullFuelSystemStatuses() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = mapper.readValue(NULL_FUEL_SYSTEM_STATUSES_JSON,
				new TypeReference<HashMap<String,Object>>(){});

		CarPosition position = new CarPosition(map);
		assertEquals(position.getAbsoluteThrottlePosB(), 30.0, 0);
		assertEquals(position.getAcceleration(), 1.014, 0);
		assertEquals(position.getAcceleratorThrottlePosD(), 20.0, 0);
		assertEquals(position.getAcceleratorThrottlePosE(), 10.0, 0);
		assertEquals(position.getBarometricPressure().intValue(), 100);
		assertEquals(position.getBearing(), 85.850507, 0);
		assertEquals(position.getCatalystTemp(), 596.0, 0);
		assertEquals(position.getControlModuleVoltage(), 14.0, 0);
		assertEquals(position.getCoolantTemp().intValue(), 88);
		assertEquals(position.getDistanceWithMilOn().intValue(), 0);
		assertEquals(position.getEngineLoad(), 24.0, 0);
		assertEquals(position.getFuelLevelInput().intValue(), 43);
		assertEquals(position.getIntakeAirTemp().intValue(), 3);
		assertEquals(position.getIntakeManifoldPressure().intValue(), 34);
		assertEquals(position.getJourneyId(), "ab18d293-57ed-497f-8b25-f587asf0c75");
		assertEquals(position.getLatitude(), 41.798128, 0);
		assertEquals(position.getLongTermFuel(), 5.0, 0);
		assertEquals(position.getLongitude(), -87.58763, 0);
		assertEquals(position.getMafAirflow(), 3.0, 0);
		assertEquals(position.getMpgInstantaneous(), 0.0, 0);
		assertEquals(position.getObdStandards().intValue(), 1);
		assertEquals(position.getRelativeThrottlePos(), 2.0, 0);
		assertEquals(position.getTimeSinceEngineStart().longValue(), 1760);
		assertEquals(position.getTimestamp().longValue(), 1421876463415l);
		assertEquals(position.getVehicleSpeed(), 0, 0);
		assertEquals(position.getVin(), "WP0AD2A71FL040367");
		assertEquals(position.getFuelSystemStatus().length, 0);
		assertEquals(position.getRemainingRange().intValue(), 267);
		assertEquals(position.getPredictions().get("0").getLatitude(), 41.97768290811704, 0);
		assertEquals(position.getPredictions().get("0").getLongitude(), -87.66936619865827, 0);
		assertEquals(position.getPredictions().get("0").getMpgJourney(), 20.2776, 0);
		assertEquals(position.getPredictions().get("0").getProbability(), 0.09, 0);
	}
}
