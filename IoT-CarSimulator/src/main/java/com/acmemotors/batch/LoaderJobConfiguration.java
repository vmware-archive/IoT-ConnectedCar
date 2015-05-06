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

import static com.acmemotors.batch.domain.Field.ABSOLUTE_THROTTLE_POS_B;
import static com.acmemotors.batch.domain.Field.ACCELERATION;
import static com.acmemotors.batch.domain.Field.ACCELERATOR_THROTTLE_POS_D;
import static com.acmemotors.batch.domain.Field.ACCELERATOR_THROTTLE_POS_E;
import static com.acmemotors.batch.domain.Field.BAROMETRIC_PRESSURE;
import static com.acmemotors.batch.domain.Field.BEARING;
import static com.acmemotors.batch.domain.Field.CATALYST_TEMP;
import static com.acmemotors.batch.domain.Field.CONTROL_MODULE_VOLTAGE;
import static com.acmemotors.batch.domain.Field.COOLANT_TEMP;
import static com.acmemotors.batch.domain.Field.DISTANCE_WITH_MIL_ON;
import static com.acmemotors.batch.domain.Field.ENGINE_LOAD;
import static com.acmemotors.batch.domain.Field.FUEL_LEVEL_INPUT;
import static com.acmemotors.batch.domain.Field.FUEL_SYSTEM_STATUS;
import static com.acmemotors.batch.domain.Field.INTAKE_AIR_TEMP;
import static com.acmemotors.batch.domain.Field.INTAKE_MANIFOLD_PRESSURE;
import static com.acmemotors.batch.domain.Field.JOURNEY_ID;
import static com.acmemotors.batch.domain.Field.LATITUDE;
import static com.acmemotors.batch.domain.Field.LONGITUDE;
import static com.acmemotors.batch.domain.Field.LONG_TERM_FUEL;
import static com.acmemotors.batch.domain.Field.MAF_AIRFLOW;
import static com.acmemotors.batch.domain.Field.OBD_STANDARDS;
import static com.acmemotors.batch.domain.Field.RELATIVE_THROTTLE_POS;
import static com.acmemotors.batch.domain.Field.RPM;
import static com.acmemotors.batch.domain.Field.SHORT_TERM_FUEL;
import static com.acmemotors.batch.domain.Field.THROTTLE_POSITION;
import static com.acmemotors.batch.domain.Field.TIME_SINCE_ENGINE_START;
import static com.acmemotors.batch.domain.Field.VEHICLE_SPEED;
import static com.acmemotors.batch.domain.Field.VIN;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.sql.DataSource;

import com.acmemotors.batch.domain.Field;
import com.acmemotors.batch.support.GatewayItemWriter;
import com.acmemotors.service.RequestGateway;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DefaultFieldSet;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpMethod;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.gateway.GatewayProxyFactoryBean;
import org.springframework.integration.http.outbound.HttpRequestExecutingMessageHandler;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.messaging.MessageChannel;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;

/**
 * This is a Spring Batch job that reads in a specified CSV file, processes it into JSON and then
 * writes it to the specified Spring channel.  The commit interval on this job is hardcoded to 1
 * and should stay that way due to the fact that the delay between each write occurs in the
 * {@link org.springframework.batch.item.ItemProcessor}.  If we want to change the commit interval,
 * this logic would need to be moved to the {@link org.springframework.batch.item.ItemWriter}
 *
 * @author Michael Minella
 */
@Configuration
@EnableBatchProcessing
@EnableIntegration
public class LoaderJobConfiguration {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	/**
	 * Configures the embedded data source used by this Spring Batch job.
	 *
	 * @return the {@link javax.sql.DataSource} to be used by Spring Batch for the job
	 * 		repository
	 */
	@Bean
	public DataSource dataSource() {
		EmbeddedDatabaseBuilder embeddedDatabaseBuilder = new EmbeddedDatabaseBuilder();
		return embeddedDatabaseBuilder
					.addScript("classpath:org/springframework/batch/core/schema-drop-hsqldb.sql")
					.addScript("classpath:org/springframework/batch/core/schema-hsqldb.sql")
					.setType(EmbeddedDatabaseType.HSQL)
					.build();
	}

	/**
	 * Returns the tokenizer used to parse a line of the input file (each record is a JSON
	 * string).
	 *
	 * @return {@link org.springframework.batch.item.file.transform.LineTokenizer} capable
	 * 		of parsing the input JSON.
	 */
	@Bean
	@SuppressWarnings("unchecked")
	public LineTokenizer jsonLineTokenzier() {
		return line -> {
			List<String> tokens = new ArrayList<>();

			try {
				HashMap<String,Object> result =
						new ObjectMapper().readValue(line, HashMap.class);

				tokens.add((String) result.get(Field.get(0).getHerbieField()));
				tokens.add(String.valueOf(result.get(Field.get(1).getHerbieField())));
				tokens.add(String.valueOf(result.get(Field.get(2).getHerbieField())));
				tokens.add(String.valueOf(result.get(Field.get(3).getHerbieField())));
				tokens.add(result.get(Field.get(4).getHerbieField()).toString());
				tokens.add(String.valueOf(result.get(Field.get(5).getHerbieField())));
				tokens.add(String.valueOf(result.get(Field.get(6).getHerbieField())));
				tokens.add(String.valueOf(result.get(Field.get(7).getHerbieField())));
				tokens.add(String.valueOf(result.get(Field.get(8).getHerbieField())));
				tokens.add(String.valueOf(result.get(Field.get(9).getHerbieField())));
				tokens.add(String.valueOf(result.get(Field.get(10).getHerbieField())));
				tokens.add(String.valueOf(result.get(Field.get(11).getHerbieField())));
				tokens.add(String.valueOf(result.get(Field.get(12).getHerbieField())));
				tokens.add(String.valueOf(result.get(Field.get(13).getHerbieField())));
				tokens.add(String.valueOf(result.get(Field.get(14).getHerbieField())));
				tokens.add(String.valueOf(result.get(Field.get(15).getHerbieField())));
				tokens.add(String.valueOf(result.get(Field.get(16).getHerbieField())));
				tokens.add(String.valueOf(result.get(Field.get(17).getHerbieField())));
				tokens.add(String.valueOf(result.get(Field.get(18).getHerbieField())));
				tokens.add(String.valueOf(result.get(Field.get(19).getHerbieField())));
				tokens.add(String.valueOf(result.get(Field.get(20).getHerbieField())));
				tokens.add(String.valueOf(result.get(Field.get(21).getHerbieField())));
				tokens.add(String.valueOf(result.get(Field.get(22).getHerbieField())));
				tokens.add(String.valueOf(result.get(Field.get(23).getHerbieField())));
				tokens.add(String.valueOf(result.get(Field.get(24).getHerbieField())));
				tokens.add(String.valueOf(result.get(Field.get(25).getHerbieField())));
				tokens.add(String.valueOf(result.get(Field.get(26).getHerbieField())));

			} catch (IOException e) {
				throw new RuntimeException("Unable to parse json: " + line);
			}

			String[] fields = Field.herbieFields();
			return new DefaultFieldSet(tokens.toArray(new String[tokens.size() - 1]),
					Arrays.copyOfRange(fields, 0, fields.length - 1));
		};
	}

	@Bean
	@StepScope
	public FlatFileItemReader<Map<String, Object>> reader(
			@Value("#{jobParameters['inputFile']}")String fileName) throws Exception {

		DefaultLineMapper<Map<String, Object>> mapper = new DefaultLineMapper<>();
		mapper.setLineTokenizer(jsonLineTokenzier());
		mapper.setFieldSetMapper(new FieldSetMapper<Map<String,Object>>() {

			private UUID journeyId = UUID.randomUUID();

			private Double readDouble(String value) {
				if(StringUtils.hasText(value)) {
					return Double.valueOf(value);
				} else {
					return null;
				}
			}

			private Integer readInteger(String value) {
				if(StringUtils.hasText(value)) {
					return Integer.valueOf(value);
				} else {
					return null;
				}
			}

			@Override
			public Map<String, Object> mapFieldSet(FieldSet fieldSet) throws BindException {

				Map<String, Object> map = new HashMap<>();

				map.put(VIN.getHerbieField(),
						fieldSet.readString(VIN.getHerbieField()));
				map.put(LONGITUDE.getHerbieField(),
						readDouble(fieldSet.readString(LONGITUDE.getHerbieField())));
				map.put(LATITUDE.getHerbieField(),
						readDouble(fieldSet.readString(LATITUDE.getHerbieField())));
				map.put(RPM.getHerbieField(),
						readDouble(fieldSet.readString(RPM.getHerbieField())));
				map.put(VEHICLE_SPEED.getHerbieField(),
						readInteger(fieldSet.readString(VEHICLE_SPEED.getHerbieField())));
				String statuses =
						fieldSet.readString(FUEL_SYSTEM_STATUS.getHerbieField());
				String [] statusPieces =
						statuses.substring(1,statuses.length() - 1).split(",");
				Integer [] integerStatuses = {readInteger(statusPieces[0].trim()),
												readInteger(statusPieces[1].trim())};
				map.put(FUEL_SYSTEM_STATUS.getHerbieField(), integerStatuses);
				map.put(ENGINE_LOAD.getHerbieField(),
						readDouble(fieldSet.readString(ENGINE_LOAD.getHerbieField())));
				map.put(COOLANT_TEMP.getHerbieField(),
						readInteger(fieldSet.readString(COOLANT_TEMP.getHerbieField())));
				map.put(SHORT_TERM_FUEL.getHerbieField(),
						readDouble(fieldSet.readString(SHORT_TERM_FUEL.getHerbieField())));
				map.put(LONG_TERM_FUEL.getHerbieField(),
						readDouble(fieldSet.readString(LONG_TERM_FUEL.getHerbieField())));
				map.put(INTAKE_MANIFOLD_PRESSURE.getHerbieField(),
						readInteger(fieldSet.readString(INTAKE_MANIFOLD_PRESSURE.getHerbieField())));
				map.put(INTAKE_AIR_TEMP.getHerbieField(),
						readInteger(fieldSet.readString(INTAKE_AIR_TEMP.getHerbieField())));
				map.put(MAF_AIRFLOW.getHerbieField(),
						readDouble(fieldSet.readString(MAF_AIRFLOW.getHerbieField())));
				map.put(THROTTLE_POSITION.getHerbieField(),
						readInteger(fieldSet.readString(THROTTLE_POSITION.getHerbieField())));
				map.put(OBD_STANDARDS.getHerbieField(),
						fieldSet.readString(OBD_STANDARDS.getHerbieField()));
				map.put(TIME_SINCE_ENGINE_START.getHerbieField(),
						readInteger(fieldSet.readString(TIME_SINCE_ENGINE_START.getHerbieField())));
				map.put(FUEL_LEVEL_INPUT.getHerbieField(),
						readInteger(fieldSet.readString(FUEL_LEVEL_INPUT.getHerbieField())));
				map.put(RELATIVE_THROTTLE_POS.getHerbieField(),
						readDouble(fieldSet.readString(RELATIVE_THROTTLE_POS.getHerbieField())));
				map.put(ABSOLUTE_THROTTLE_POS_B.getHerbieField(),
						readDouble(fieldSet.readString(ABSOLUTE_THROTTLE_POS_B.getHerbieField())));
				map.put(ACCELERATOR_THROTTLE_POS_D.getHerbieField(),
						readDouble(fieldSet.readString(ACCELERATOR_THROTTLE_POS_D.getHerbieField())));
				map.put(ACCELERATOR_THROTTLE_POS_E.getHerbieField(),
						readDouble(fieldSet.readString(ACCELERATOR_THROTTLE_POS_E.getHerbieField())));
				map.put(DISTANCE_WITH_MIL_ON.getHerbieField(),
						readInteger(fieldSet.readString(DISTANCE_WITH_MIL_ON.getHerbieField())));
				map.put(CATALYST_TEMP.getHerbieField(),
						readDouble(fieldSet.readString(CATALYST_TEMP.getHerbieField())));
				map.put(BAROMETRIC_PRESSURE.getHerbieField(),
						readInteger(fieldSet.readString(BAROMETRIC_PRESSURE.getHerbieField())));
				map.put(CONTROL_MODULE_VOLTAGE.getHerbieField(),
						readDouble(fieldSet.readString(CONTROL_MODULE_VOLTAGE.getHerbieField())));
				map.put(ACCELERATION.getHerbieField(),
						readDouble(fieldSet.readString(ACCELERATION.getHerbieField())));
				map.put(BEARING.getHerbieField(),
						readDouble(fieldSet.readString(BEARING.getHerbieField())));
				map.put(JOURNEY_ID.getHerbieField(), journeyId.toString());

				return map;
			}
		});

		mapper.afterPropertiesSet();

		FlatFileItemReader<Map<String, Object>> reader = new FlatFileItemReader<>();
		reader.setLineMapper(mapper);
		reader.setResource(new FileSystemResource(fileName));
		reader.afterPropertiesSet();

		return reader;
	}

	@Bean
	@StepScope
	public ItemProcessor<Map<String, Object>, String> processor(
			@Value("#{jobParameters['delay']}")final long delay) {
		return item -> {
			DefaultSerializerProvider provider = new DefaultSerializerProvider.Impl();

			provider.setNullValueSerializer(new JsonSerializer<Object>() {
				@Override
				public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
					if(value == null) {
						jgen.writeString("");
					} else {
						if(value instanceof String) {
							jgen.writeString((String) value);
						} else {
							if(value instanceof Integer) {
								jgen.writeNumber((Integer) value);
							} else {
								jgen.writeNumber((Double) value);
							}
						}
					}
				}
			});

			ObjectMapper mapper = new ObjectMapper();
			mapper.setSerializerProvider(provider);

			String processedItem = mapper.writeValueAsString(item);

			Thread.sleep(delay);

			return processedItem;
		};
	}

	@Bean
	public MessageChannel requestChannel() {
		return new DirectChannel();
	}

	@Bean
	@ServiceActivator(inputChannel = "requestChannel")
	public HttpRequestExecutingMessageHandler messageHandler(
			@Value("${serverUrl}") String serverUrl	) {
		HttpRequestExecutingMessageHandler handler =
				new HttpRequestExecutingMessageHandler(serverUrl);
		handler.setHttpMethod(HttpMethod.POST);
		handler.setExpectedResponseType(String.class);

		return handler;
	}

	@Bean
	public GatewayProxyFactoryBean gatewayProxyFactoryBean() {
		GatewayProxyFactoryBean gatewayProxyFactoryBean = new GatewayProxyFactoryBean();
		gatewayProxyFactoryBean.setServiceInterface(RequestGateway.class);
		gatewayProxyFactoryBean.setDefaultRequestChannel(requestChannel());
		gatewayProxyFactoryBean.setDefaultReplyTimeout(5000l);
		return gatewayProxyFactoryBean;
	}

	@Bean
	public GatewayItemWriter writer() {
		return new GatewayItemWriter();
	}

	@Bean
	public Step step1() throws Exception {
		return stepBuilderFactory.get("step1")
					   .<Map<String, Object>, String> chunk(1)
					   .reader(reader(null))
					   .processor(processor(-1l))
					   .writer(writer())
					   .build();
	}

	@Bean
	public Job simulatorJob(Step step1) throws Exception {
		return jobBuilderFactory.get("simulatorJob")
					   .incrementer(new RunIdIncrementer())
					   .flow(step1)
					   .end()
					   .build();
	}
}
