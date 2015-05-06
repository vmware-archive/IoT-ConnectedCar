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
package com.acmemotors.batch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.acmemotors.batch.support.GatewayItemWriter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;

/**
 * @author mminella
 */
public class LoadJobConfigurationTests {

	@Mock
	private static GatewayItemWriter writer;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testJsonLineTokenizer() throws Exception {
		LoaderJobConfiguration config = new LoaderJobConfiguration();

		LineTokenizer tokenizer = config.jsonLineTokenzier();

		FieldSet fieldSet = tokenizer.tokenize("{\"vehicle_speed\":0,\"obd_standards\":2,\"intake_manifold_pressure\":\"\",\"accelerator_throttle_pos_e\":8,\"engine_load\":30,\"maf_airflow\":7,\"latitude\":\"32.984979\",\"vin\":\"SCEDT26T0BD007019\",\"bearing\":\"319.492374\",\"catalyst_temp\":446,\"relative_throttle_pos\":1,\"fuel_level_input\":99,\"fuel_system_status\":[2,0],\"accelerator_throttle_pos_d\":16,\"acceleration\":\"0.992\",\"throttle_position\":14,\"barometric_pressure\":95,\"control_module_voltage\":13,\"longitude\":\"-96.709578\",\"distance_with_mil_on\":0,\"coolant_temp\":92,\"intake_air_temp\":60,\"rpm\":659,\"short_term_fuel\":-1,\"time_since_engine_start\":217,\"absolute_throttle_pos_b\":18,\"long_term_fuel\":2,\"timestamp\":1408670439897}");
		Map<String, Object> expectedResults = new HashMap<>();

		expectedResults.put("vehicle_speed", "0");
		expectedResults.put("obd_standards", "2");
		expectedResults.put("intake_manifold_pressure", "");
		expectedResults.put("accelerator_throttle_pos_e", "8");
		expectedResults.put("engine_load", "30");
		expectedResults.put("maf_airflow", "7");
		expectedResults.put("latitude", "32.984979");
		expectedResults.put("vin", "SCEDT26T0BD007019");
		expectedResults.put("bearing", "319.492374");
		expectedResults.put("catalyst_temp", "446");
		expectedResults.put("relative_throttle_pos", "1");
		expectedResults.put("fuel_level_input", "99");
		expectedResults.put("fuel_system_status", "[2, 0]");
		expectedResults.put("accelerator_throttle_pos_d", "16");
		expectedResults.put("acceleration", "0.992");
		expectedResults.put("throttle_position", "14");
		expectedResults.put("barometric_pressure", "95");
		expectedResults.put("control_module_voltage", "13");
		expectedResults.put("longitude", "-96.709578");
		expectedResults.put("distance_with_mil_on", "0");
		expectedResults.put("coolant_temp", "92");
		expectedResults.put("intake_air_temp", "60");
		expectedResults.put("rpm", "659");
		expectedResults.put("short_term_fuel", "-1");
		expectedResults.put("time_since_engine_start", "217");
		expectedResults.put("absolute_throttle_pos_b", "18");
		expectedResults.put("long_term_fuel", "2");

		validateFieldSet(fieldSet, expectedResults);
	}

	@Test
	public void testProcessor() throws Exception {
		LoaderJobConfiguration config = new LoaderJobConfiguration();

		ItemProcessor<Map<String, Object>, String> processor = config.processor(500);
		Map<String, Object> item = new HashMap<>();
		item.put("foo", "bar");

		Date startTime = new Date();
		String stringItem = processor.process(item);
		assertTrue(new Date().getTime() - startTime.getTime() > 500);
		assertEquals("{\"foo\":\"bar\"}", stringItem);
	}

	@Test
	public void integrationTest() throws Exception {
		GenericApplicationContext context = new AnnotationConfigApplicationContext(TestJobConfiguration.class);

		JobLauncher launcher = context.getBean(JobLauncher.class);
		Job job = context.getBean(Job.class);

		JobParameters jobParameters = new JobParametersBuilder().addLong("delay", 50l)
				.addString("inputFile", new ClassPathResource("/data/sampleJourney.json").getFile().getAbsolutePath())
				.toJobParameters();
		JobExecution execution = launcher.run(job, jobParameters);
		assertEquals(execution.getStatus(), BatchStatus.COMPLETED);
		verify(writer, times(3)).write(anyListOf(String.class));
	}

	@Configuration
	@Import(LoaderJobConfiguration.class)
	public static class TestJobConfiguration {

		@Bean
		public GatewayItemWriter writer() {
			return writer;
		}
	}

	private void validateFieldSet(FieldSet fieldSet, Map<String, Object> expectedValues) {

		assertEquals(expectedValues.get("vin"), fieldSet.readString("vin"));
		assertEquals(expectedValues.get("longitude"), fieldSet.readString("longitude"));
		assertEquals(expectedValues.get("latitude"), fieldSet.readString("latitude"));
		assertEquals(expectedValues.get("rpm"), fieldSet.readString("rpm"));
		assertEquals(expectedValues.get("vehicle_speed"), fieldSet.readString("vehicle_speed"));
		assertEquals(expectedValues.get("fuel_system_status"), fieldSet.readString("fuel_system_status"));
		assertEquals(expectedValues.get("engine_load"), fieldSet.readString("engine_load"));
		assertEquals(expectedValues.get("coolant_temp"), fieldSet.readString("coolant_temp"));
		assertEquals(expectedValues.get("short_term_fuel"), fieldSet.readString("short_term_fuel"));
		assertEquals(expectedValues.get("long_term_fuel"), fieldSet.readString("long_term_fuel"));
		assertEquals(expectedValues.get("intake_manifold_pressure"), fieldSet.readString("intake_manifold_pressure"));
		assertEquals(expectedValues.get("intake_air_temp"), fieldSet.readString("intake_air_temp"));
		assertEquals(expectedValues.get("maf_airflow"), fieldSet.readString("maf_airflow"));
		assertEquals(expectedValues.get("throttle_position"), fieldSet.readString("throttle_position"));
		assertEquals(expectedValues.get("obd_standards"), fieldSet.readString("obd_standards"));
		assertEquals(expectedValues.get("time_since_engine_start"), fieldSet.readString("time_since_engine_start"));
		assertEquals(expectedValues.get("fuel_level_input"), fieldSet.readString("fuel_level_input"));
		assertEquals(expectedValues.get("relative_throttle_pos"), fieldSet.readString("relative_throttle_pos"));
		assertEquals(expectedValues.get("absolute_throttle_pos_b"), fieldSet.readString("absolute_throttle_pos_b"));
		assertEquals(expectedValues.get("accelerator_throttle_pos_d"), fieldSet.readString("accelerator_throttle_pos_d"));
		assertEquals(expectedValues.get("accelerator_throttle_pos_e"), fieldSet.readString("accelerator_throttle_pos_e"));
		assertEquals(expectedValues.get("distance_with_mil_on"), fieldSet.readString("distance_with_mil_on"));
		assertEquals(expectedValues.get("catalyst_temp"), fieldSet.readString("catalyst_temp"));
		assertEquals(expectedValues.get("barometric_pressure"), fieldSet.readString("barometric_pressure"));
		assertEquals(expectedValues.get("control_module_voltage"), fieldSet.readString("control_module_voltage"));
		assertEquals(expectedValues.get("acceleration"), fieldSet.readString("acceleration"));
		assertEquals(expectedValues.get("bearing"), fieldSet.readString("bearing"));
	}

}
