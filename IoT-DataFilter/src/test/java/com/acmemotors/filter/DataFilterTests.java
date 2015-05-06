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
package com.acmemotors.filter;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import org.springframework.core.io.ClassPathResource;
import org.springframework.integration.groovy.GroovyScriptExecutingMessageProcessor;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.scripting.support.ResourceScriptSource;

/**
 * @author Michael Minella
 */
public class DataFilterTests {

	private GroovyScriptExecutingMessageProcessor processor;

	@Before
	public void setUp() {
		processor = new GroovyScriptExecutingMessageProcessor(
				new ResourceScriptSource(new ClassPathResource("DataFilter.groovy")));
	}

	@Test
	public void testEmptyString() throws Exception {
		testScript("", false);
	}

	@Test
	public void testEmptyJson() throws Exception {
		testScript("{}", false);
	}

	@Test
	public void testNoFuelLevelInput() throws Exception {
		testScript("{\"vehicle_speed\":0,\"obd_standards\":2," +
				"\"intake_manifold_pressure\":\"\",\"accelerator_throttle_pos_e\":8," +
				"\"engine_load\":30,\"maf_airflow\":7,\"latitude\":\"32.984979\"," +
				"\"vin\":\"SCEDT26T0BD007019\",\"bearing\":\"319.492374\"," +
				"\"catalyst_temp\":446,\"relative_throttle_pos\":1," +
				"\"fuel_level_input\":\"\",\"fuel_system_status\":[2,0]," +
				"\"accelerator_throttle_pos_d\":16,\"acceleration\":\"0.992\"," +
				"\"throttle_position\":14,\"barometric_pressure\":95," +
				"\"control_module_voltage\":13,\"longitude\":\"-96.709578\"," +
				"\"distance_with_mil_on\":0,\"coolant_temp\":92,\"intake_air_temp\":60," +
				"\"rpm\":659,\"short_term_fuel\":-1,\"time_since_engine_start\":217," +
				"\"absolute_throttle_pos_b\":18,\"long_term_fuel\":2," +
				"\"timestamp\":1408670439897}", false);
		testScript("{\"vehicle_speed\":0,\"obd_standards\":2," +
				"\"intake_manifold_pressure\":\"\",\"accelerator_throttle_pos_e\":8," +
				"\"engine_load\":30,\"maf_airflow\":7,\"latitude\":\"32.984979\"," +
				"\"vin\":\"SCEDT26T0BD007019\",\"bearing\":\"319.492374\"," +
				"\"catalyst_temp\":446,\"relative_throttle_pos\":1," +
				"\"fuel_system_status\":[2,0]," +
				"\"accelerator_throttle_pos_d\":16,\"acceleration\":\"0.992\"," +
				"\"throttle_position\":14,\"barometric_pressure\":95," +
				"\"control_module_voltage\":13,\"longitude\":\"-96.709578\"," +
				"\"distance_with_mil_on\":0,\"coolant_temp\":92,\"intake_air_temp\":60," +
				"\"rpm\":659,\"short_term_fuel\":-1,\"time_since_engine_start\":217," +
				"\"absolute_throttle_pos_b\":18,\"long_term_fuel\":2," +
				"\"timestamp\":1408670439897}", false);
		testScript("{\"vehicle_speed\":0,\"obd_standards\":2," +
				"\"intake_manifold_pressure\":\"\",\"accelerator_throttle_pos_e\":8," +
				"\"engine_load\":30,\"maf_airflow\":7,\"latitude\":\"32.984979\"," +
				"\"vin\":\"SCEDT26T0BD007019\",\"bearing\":\"319.492374\"," +
				"\"catalyst_temp\":446,\"relative_throttle_pos\":1," +
				"\"fuel_level_input\":5,\"fuel_system_status\":[2,0]," +
				"\"accelerator_throttle_pos_d\":16,\"acceleration\":\"0.992\"," +
				"\"throttle_position\":14,\"barometric_pressure\":95," +
				"\"control_module_voltage\":13,\"longitude\":\"-96.709578\"," +
				"\"distance_with_mil_on\":0,\"coolant_temp\":92,\"intake_air_temp\":60," +
				"\"rpm\":659,\"short_term_fuel\":-1,\"time_since_engine_start\":217," +
				"\"absolute_throttle_pos_b\":18,\"long_term_fuel\":2," +
				"\"timestamp\":1408670439897}", true);
	}

	@Test
	public void testNoVehicleSpeed() throws Exception {
		testScript("{\"obd_standards\":2," +
				"\"intake_manifold_pressure\":\"\",\"accelerator_throttle_pos_e\":8," +
				"\"engine_load\":30,\"maf_airflow\":7,\"latitude\":\"32.984979\"," +
				"\"vin\":\"SCEDT26T0BD007019\",\"bearing\":\"319.492374\"," +
				"\"catalyst_temp\":446,\"relative_throttle_pos\":1," +
				"\"fuel_level_input\":5,\"fuel_system_status\":[2,0]," +
				"\"accelerator_throttle_pos_d\":16,\"acceleration\":\"0.992\"," +
				"\"throttle_position\":14,\"barometric_pressure\":95," +
				"\"control_module_voltage\":13,\"longitude\":\"-96.709578\"," +
				"\"distance_with_mil_on\":0,\"coolant_temp\":92,\"intake_air_temp\":60," +
				"\"rpm\":659,\"short_term_fuel\":-1,\"time_since_engine_start\":217," +
				"\"absolute_throttle_pos_b\":18,\"long_term_fuel\":2," +
				"\"timestamp\":1408670439897}", false);
		testScript("{\"vehicle_speed\":\"\",\"obd_standards\":2," +
				"\"intake_manifold_pressure\":\"\",\"accelerator_throttle_pos_e\":8," +
				"\"engine_load\":30,\"maf_airflow\":7,\"latitude\":\"32.984979\"," +
				"\"vin\":\"SCEDT26T0BD007019\",\"bearing\":\"319.492374\"," +
				"\"catalyst_temp\":446,\"relative_throttle_pos\":1," +
				"\"fuel_level_input\":5,\"fuel_system_status\":[2,0]," +
				"\"accelerator_throttle_pos_d\":16,\"acceleration\":\"0.992\"," +
				"\"throttle_position\":14,\"barometric_pressure\":95," +
				"\"control_module_voltage\":13,\"longitude\":\"-96.709578\"," +
				"\"distance_with_mil_on\":0,\"coolant_temp\":92,\"intake_air_temp\":60," +
				"\"rpm\":659,\"short_term_fuel\":-1,\"time_since_engine_start\":217," +
				"\"absolute_throttle_pos_b\":18,\"long_term_fuel\":2," +
				"\"timestamp\":1408670439897}", false);
		testScript("{\"vehicle_speed\":10,\"obd_standards\":2," +
				"\"intake_manifold_pressure\":\"\",\"accelerator_throttle_pos_e\":8," +
				"\"engine_load\":30,\"maf_airflow\":7,\"latitude\":\"32.984979\"," +
				"\"vin\":\"SCEDT26T0BD007019\",\"bearing\":\"319.492374\"," +
				"\"catalyst_temp\":446,\"relative_throttle_pos\":1," +
				"\"fuel_level_input\":5,\"fuel_system_status\":[2,0]," +
				"\"accelerator_throttle_pos_d\":16,\"acceleration\":\"0.992\"," +
				"\"throttle_position\":14,\"barometric_pressure\":95," +
				"\"control_module_voltage\":13,\"longitude\":\"-96.709578\"," +
				"\"distance_with_mil_on\":0,\"coolant_temp\":92,\"intake_air_temp\":60," +
				"\"rpm\":659,\"short_term_fuel\":-1,\"time_since_engine_start\":217," +
				"\"absolute_throttle_pos_b\":18,\"long_term_fuel\":2," +
				"\"timestamp\":1408670439897}", true);
	}

	@Test
	public void testNoMassAirFlowNoRpm() throws Exception {
		testScript("{\"vehicle_speed\":10,\"obd_standards\":2," +
				"\"intake_manifold_pressure\":\"\",\"accelerator_throttle_pos_e\":8," +
				"\"engine_load\":30,\"latitude\":\"32.984979\"," +
				"\"vin\":\"SCEDT26T0BD007019\",\"bearing\":\"319.492374\"," +
				"\"catalyst_temp\":446,\"relative_throttle_pos\":1," +
				"\"fuel_level_input\":5,\"fuel_system_status\":[2,0]," +
				"\"accelerator_throttle_pos_d\":16,\"acceleration\":\"0.992\"," +
				"\"throttle_position\":14,\"barometric_pressure\":95," +
				"\"control_module_voltage\":13,\"longitude\":\"-96.709578\"," +
				"\"distance_with_mil_on\":0,\"coolant_temp\":92,\"intake_air_temp\":60," +
				"\"short_term_fuel\":-1,\"time_since_engine_start\":217," +
				"\"absolute_throttle_pos_b\":18,\"long_term_fuel\":2," +
				"\"timestamp\":1408670439897}", false);
		testScript("{\"vehicle_speed\":10,\"obd_standards\":2," +
				"\"intake_manifold_pressure\":\"\",\"accelerator_throttle_pos_e\":8," +
				"\"engine_load\":30,\"latitude\":\"32.984979\"," +
				"\"vin\":\"SCEDT26T0BD007019\",\"bearing\":\"319.492374\"," +
				"\"catalyst_temp\":446,\"relative_throttle_pos\":1," +
				"\"fuel_level_input\":5,\"fuel_system_status\":[2,0]," +
				"\"accelerator_throttle_pos_d\":16,\"acceleration\":\"0.992\"," +
				"\"throttle_position\":14,\"barometric_pressure\":95," +
				"\"control_module_voltage\":13,\"longitude\":\"-96.709578\"," +
				"\"distance_with_mil_on\":0,\"coolant_temp\":92,\"intake_air_temp\":60," +
				"\"rpm\":\"\",\"short_term_fuel\":-1,\"time_since_engine_start\":217," +
				"\"absolute_throttle_pos_b\":18,\"long_term_fuel\":2," +
				"\"timestamp\":1408670439897}", false);
		testScript("{\"vehicle_speed\":10,\"obd_standards\":2," +
				"\"intake_manifold_pressure\":\"\",\"accelerator_throttle_pos_e\":8," +
				"\"engine_load\":30,\"maf_airflow\":\"\",\"latitude\":\"32.984979\"," +
				"\"vin\":\"SCEDT26T0BD007019\",\"bearing\":\"319.492374\"," +
				"\"catalyst_temp\":446,\"relative_throttle_pos\":1," +
				"\"fuel_level_input\":5,\"fuel_system_status\":[2,0]," +
				"\"accelerator_throttle_pos_d\":16,\"acceleration\":\"0.992\"," +
				"\"throttle_position\":14,\"barometric_pressure\":95," +
				"\"control_module_voltage\":13,\"longitude\":\"-96.709578\"," +
				"\"distance_with_mil_on\":0,\"coolant_temp\":92,\"intake_air_temp\":60," +
				"\"rpm\":\"\",\"short_term_fuel\":-1,\"time_since_engine_start\":217," +
				"\"absolute_throttle_pos_b\":18,\"long_term_fuel\":2," +
				"\"timestamp\":1408670439897}", false);
		testScript("{\"vehicle_speed\":10,\"obd_standards\":2," +
				"\"intake_manifold_pressure\":\"\",\"accelerator_throttle_pos_e\":8," +
				"\"engine_load\":30,\"maf_airflow\":\"\",\"latitude\":\"32.984979\"," +
				"\"vin\":\"SCEDT26T0BD007019\",\"bearing\":\"319.492374\"," +
				"\"catalyst_temp\":446,\"relative_throttle_pos\":1," +
				"\"fuel_level_input\":5,\"fuel_system_status\":[2,0]," +
				"\"accelerator_throttle_pos_d\":16,\"acceleration\":\"0.992\"," +
				"\"throttle_position\":14,\"barometric_pressure\":95," +
				"\"control_module_voltage\":13,\"longitude\":\"-96.709578\"," +
				"\"distance_with_mil_on\":0,\"coolant_temp\":92,\"intake_air_temp\":60," +
				"\"short_term_fuel\":-1,\"time_since_engine_start\":217," +
				"\"absolute_throttle_pos_b\":18,\"long_term_fuel\":2," +
				"\"timestamp\":1408670439897}", false);
		testScript("{\"vehicle_speed\":10,\"obd_standards\":2," +
				"\"intake_manifold_pressure\":5,\"accelerator_throttle_pos_e\":8," +
				"\"engine_load\":30,\"latitude\":\"32.984979\"," +
				"\"vin\":\"SCEDT26T0BD007019\",\"bearing\":\"319.492374\"," +
				"\"catalyst_temp\":446,\"relative_throttle_pos\":1," +
				"\"fuel_level_input\":5,\"fuel_system_status\":[2,0]," +
				"\"accelerator_throttle_pos_d\":16,\"acceleration\":\"0.992\"," +
				"\"throttle_position\":14,\"barometric_pressure\":95," +
				"\"control_module_voltage\":13,\"longitude\":\"-96.709578\"," +
				"\"distance_with_mil_on\":0,\"coolant_temp\":92,\"intake_air_temp\":60," +
				"\"rpm\":659,\"short_term_fuel\":-1,\"time_since_engine_start\":217," +
				"\"absolute_throttle_pos_b\":18,\"long_term_fuel\":2," +
				"\"timestamp\":1408670439897}", true);
	}

	@Test
	public void testNoMassAirFlowNoIntakeAirTemp() throws Exception {
		testScript("{\"vehicle_speed\":10,\"obd_standards\":2," +
				"\"intake_manifold_pressure\":\"\",\"accelerator_throttle_pos_e\":8," +
				"\"engine_load\":30,\"latitude\":\"32.984979\"," +
				"\"vin\":\"SCEDT26T0BD007019\",\"bearing\":\"319.492374\"," +
				"\"catalyst_temp\":446,\"relative_throttle_pos\":1," +
				"\"fuel_level_input\":5,\"fuel_system_status\":[2,0]," +
				"\"accelerator_throttle_pos_d\":16,\"acceleration\":\"0.992\"," +
				"\"throttle_position\":14,\"barometric_pressure\":95," +
				"\"control_module_voltage\":13,\"longitude\":\"-96.709578\"," +
				"\"distance_with_mil_on\":0,\"coolant_temp\":92," +
				"\"rpm\":685,\"short_term_fuel\":-1,\"time_since_engine_start\":217," +
				"\"absolute_throttle_pos_b\":18,\"long_term_fuel\":2," +
				"\"timestamp\":1408670439897}", false);
		testScript("{\"vehicle_speed\":10,\"obd_standards\":2," +
				"\"intake_manifold_pressure\":\"\",\"accelerator_throttle_pos_e\":8," +
				"\"engine_load\":30,\"latitude\":\"32.984979\"," +
				"\"vin\":\"SCEDT26T0BD007019\",\"bearing\":\"319.492374\"," +
				"\"catalyst_temp\":446,\"relative_throttle_pos\":1," +
				"\"fuel_level_input\":5,\"fuel_system_status\":[2,0]," +
				"\"accelerator_throttle_pos_d\":16,\"acceleration\":\"0.992\"," +
				"\"throttle_position\":14,\"barometric_pressure\":95," +
				"\"control_module_voltage\":13,\"longitude\":\"-96.709578\"," +
				"\"distance_with_mil_on\":0,\"coolant_temp\":92,\"intake_air_temp\":\"\"," +
				"\"rpm\":685,\"short_term_fuel\":-1,\"time_since_engine_start\":217," +
				"\"absolute_throttle_pos_b\":18,\"long_term_fuel\":2," +
				"\"timestamp\":1408670439897}", false);
		testScript("{\"vehicle_speed\":10,\"obd_standards\":2," +
				"\"intake_manifold_pressure\":\"\",\"accelerator_throttle_pos_e\":8," +
				"\"engine_load\":30,\"maf_airflow\":\"\",\"latitude\":\"32.984979\"," +
				"\"vin\":\"SCEDT26T0BD007019\",\"bearing\":\"319.492374\"," +
				"\"catalyst_temp\":446,\"relative_throttle_pos\":1," +
				"\"fuel_level_input\":5,\"fuel_system_status\":[2,0]," +
				"\"accelerator_throttle_pos_d\":16,\"acceleration\":\"0.992\"," +
				"\"throttle_position\":14,\"barometric_pressure\":95," +
				"\"control_module_voltage\":13,\"longitude\":\"-96.709578\"," +
				"\"distance_with_mil_on\":0,\"coolant_temp\":92," +
				"\"rpm\":685,\"short_term_fuel\":-1,\"time_since_engine_start\":217," +
				"\"absolute_throttle_pos_b\":18,\"long_term_fuel\":2," +
				"\"timestamp\":1408670439897}", false);
		testScript("{\"vehicle_speed\":10,\"obd_standards\":2," +
				"\"intake_manifold_pressure\":\"\",\"accelerator_throttle_pos_e\":8," +
				"\"engine_load\":30,\"maf_airflow\":\"\",\"latitude\":\"32.984979\"," +
				"\"vin\":\"SCEDT26T0BD007019\",\"bearing\":\"319.492374\"," +
				"\"catalyst_temp\":446,\"relative_throttle_pos\":1," +
				"\"fuel_level_input\":5,\"fuel_system_status\":[2,0]," +
				"\"accelerator_throttle_pos_d\":16,\"acceleration\":\"0.992\"," +
				"\"throttle_position\":14,\"barometric_pressure\":95," +
				"\"control_module_voltage\":13,\"longitude\":\"-96.709578\"," +
				"\"distance_with_mil_on\":0,\"coolant_temp\":92,\"intake_air_temp\":\"\"," +
				"\"rpm\":685,\"short_term_fuel\":-1,\"time_since_engine_start\":217," +
				"\"absolute_throttle_pos_b\":18,\"long_term_fuel\":2," +
				"\"timestamp\":1408670439897}", false);
		testScript("{\"vehicle_speed\":10,\"obd_standards\":2," +
				"\"intake_manifold_pressure\":5,\"accelerator_throttle_pos_e\":8," +
				"\"engine_load\":30,\"latitude\":\"32.984979\"," +
				"\"vin\":\"SCEDT26T0BD007019\",\"bearing\":\"319.492374\"," +
				"\"catalyst_temp\":446,\"relative_throttle_pos\":1," +
				"\"fuel_level_input\":5,\"fuel_system_status\":[2,0]," +
				"\"accelerator_throttle_pos_d\":16,\"acceleration\":\"0.992\"," +
				"\"throttle_position\":14,\"barometric_pressure\":95," +
				"\"control_module_voltage\":13,\"longitude\":\"-96.709578\"," +
				"\"distance_with_mil_on\":0,\"coolant_temp\":92,\"intake_air_temp\":60," +
				"\"rpm\":659,\"short_term_fuel\":-1,\"time_since_engine_start\":217," +
				"\"absolute_throttle_pos_b\":18,\"long_term_fuel\":2," +
				"\"timestamp\":1408670439897}", true);
	}

	@Test
	public void testNoMassAirFlowNoIntakeMainfoldPressure() throws Exception {
		testScript("{\"vehicle_speed\":10,\"obd_standards\":2," +
				"\"accelerator_throttle_pos_e\":8," +
				"\"engine_load\":30,\"latitude\":\"32.984979\"," +
				"\"vin\":\"SCEDT26T0BD007019\",\"bearing\":\"319.492374\"," +
				"\"catalyst_temp\":446,\"relative_throttle_pos\":1," +
				"\"fuel_level_input\":5,\"fuel_system_status\":[2,0]," +
				"\"accelerator_throttle_pos_d\":16,\"acceleration\":\"0.992\"," +
				"\"throttle_position\":14,\"barometric_pressure\":95," +
				"\"control_module_voltage\":13,\"longitude\":\"-96.709578\"," +
				"\"distance_with_mil_on\":0,\"coolant_temp\":92,\"intake_air_temp\":60," +
				"\"rpm\":685,\"short_term_fuel\":-1,\"time_since_engine_start\":217," +
				"\"absolute_throttle_pos_b\":18,\"long_term_fuel\":2," +
				"\"timestamp\":1408670439897}", false);
		testScript("{\"vehicle_speed\":10,\"obd_standards\":2," +
				"\"intake_manifold_pressure\":\"\",\"accelerator_throttle_pos_e\":8," +
				"\"engine_load\":30,\"latitude\":\"32.984979\"," +
				"\"vin\":\"SCEDT26T0BD007019\",\"bearing\":\"319.492374\"," +
				"\"catalyst_temp\":446,\"relative_throttle_pos\":1," +
				"\"fuel_level_input\":5,\"fuel_system_status\":[2,0]," +
				"\"accelerator_throttle_pos_d\":16,\"acceleration\":\"0.992\"," +
				"\"throttle_position\":14,\"barometric_pressure\":95," +
				"\"control_module_voltage\":13,\"longitude\":\"-96.709578\"," +
				"\"distance_with_mil_on\":0,\"coolant_temp\":92,\"intake_air_temp\":60," +
				"\"rpm\":685,\"short_term_fuel\":-1,\"time_since_engine_start\":217," +
				"\"absolute_throttle_pos_b\":18,\"long_term_fuel\":2," +
				"\"timestamp\":1408670439897}", false);
		testScript("{\"vehicle_speed\":10,\"obd_standards\":2," +
				"\"accelerator_throttle_pos_e\":8," +
				"\"engine_load\":30,\"maf_airflow\":\"\",\"latitude\":\"32.984979\"," +
				"\"vin\":\"SCEDT26T0BD007019\",\"bearing\":\"319.492374\"," +
				"\"catalyst_temp\":446,\"relative_throttle_pos\":1," +
				"\"fuel_level_input\":5,\"fuel_system_status\":[2,0]," +
				"\"accelerator_throttle_pos_d\":16,\"acceleration\":\"0.992\"," +
				"\"throttle_position\":14,\"barometric_pressure\":95," +
				"\"control_module_voltage\":13,\"longitude\":\"-96.709578\"," +
				"\"distance_with_mil_on\":0,\"coolant_temp\":92,\"intake_air_temp\":60," +
				"\"rpm\":685,\"short_term_fuel\":-1,\"time_since_engine_start\":217," +
				"\"absolute_throttle_pos_b\":18,\"long_term_fuel\":2," +
				"\"timestamp\":1408670439897}", false);
		testScript("{\"vehicle_speed\":10,\"obd_standards\":2," +
				"\"intake_manifold_pressure\":\"\",\"accelerator_throttle_pos_e\":8," +
				"\"engine_load\":30,\"maf_airflow\":\"\",\"latitude\":\"32.984979\"," +
				"\"vin\":\"SCEDT26T0BD007019\",\"bearing\":\"319.492374\"," +
				"\"catalyst_temp\":446,\"relative_throttle_pos\":1," +
				"\"fuel_level_input\":5,\"fuel_system_status\":[2,0]," +
				"\"accelerator_throttle_pos_d\":16,\"acceleration\":\"0.992\"," +
				"\"throttle_position\":14,\"barometric_pressure\":95," +
				"\"control_module_voltage\":13,\"longitude\":\"-96.709578\"," +
				"\"distance_with_mil_on\":0,\"coolant_temp\":92,\"intake_air_temp\":60," +
				"\"rpm\":685,\"short_term_fuel\":-1,\"time_since_engine_start\":217," +
				"\"absolute_throttle_pos_b\":18,\"long_term_fuel\":2," +
				"\"timestamp\":1408670439897}", false);
		testScript("{\"vehicle_speed\":10,\"obd_standards\":2," +
				"\"intake_manifold_pressure\":5,\"accelerator_throttle_pos_e\":8," +
				"\"engine_load\":30,\"latitude\":\"32.984979\"," +
				"\"vin\":\"SCEDT26T0BD007019\",\"bearing\":\"319.492374\"," +
				"\"catalyst_temp\":446,\"relative_throttle_pos\":1," +
				"\"fuel_level_input\":5,\"fuel_system_status\":[2,0]," +
				"\"accelerator_throttle_pos_d\":16,\"acceleration\":\"0.992\"," +
				"\"throttle_position\":14,\"barometric_pressure\":95," +
				"\"control_module_voltage\":13,\"longitude\":\"-96.709578\"," +
				"\"distance_with_mil_on\":0,\"coolant_temp\":92,\"intake_air_temp\":60," +
				"\"rpm\":659,\"short_term_fuel\":-1,\"time_since_engine_start\":217," +
				"\"absolute_throttle_pos_b\":18,\"long_term_fuel\":2," +
				"\"timestamp\":1408670439897}", true);
	}

	private void testScript(String payload, boolean result) throws IOException {

		assertEquals(result, processor.processMessage(MessageBuilder.withPayload(payload).build()));
	}
}
