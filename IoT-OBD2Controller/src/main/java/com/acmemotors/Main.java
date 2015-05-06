/*
 * Copyright 2014-2015 the original author or authors.
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
package com.acmemotors;

import com.acmemotors.integration.OBD2Serializer;
import com.acmemotors.obd2.OBD2Controller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.ip.tcp.TcpOutboundGateway;
import org.springframework.integration.ip.tcp.connection.TcpNioClientConnectionFactory;
import org.springframework.integration.transformer.ObjectToStringTransformer;
import org.springframework.messaging.MessageChannel;
import org.springframework.web.client.RestTemplate;

/**
 * To execute the simulator run the following command:
 * java -jar IoT-OBD2Controller.jar --dongleHost=&lt;OBD2_HOST&gt; --donglePort=&lt;OBD2_PORT&gt; --gpsHost=&lt;GPS_HOST&gt;
 *
 * @author Michael Minella
 */
@Configuration
@ComponentScan
@IntegrationComponentScan
@EnableAutoConfiguration
public class Main {

	@Bean
	public TcpNioClientConnectionFactory connectionFactory(@Value("${dongleHost:192.168.0.10}") String dongleHost,
														   @Value("${donglePort:35000}") int donglePort) {
		TcpNioClientConnectionFactory factory;

		factory = new TcpNioClientConnectionFactory(dongleHost, donglePort);

		OBD2Serializer odb2Serializer = new OBD2Serializer();
		factory.setSerializer(odb2Serializer);
		factory.setDeserializer(odb2Serializer);
		factory.setSingleUse(false);
		factory.afterPropertiesSet();

		return factory;
	}

	@Bean
	public MessageChannel requests() {
		return new DirectChannel();
	}

	@Bean
	public MessageChannel replies() {
		return new DirectChannel();
	}

	@Bean
	public MessageChannel results() {
		return new DirectChannel();
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	@ServiceActivator(inputChannel = "requests")
	public TcpOutboundGateway tcpOutboundGateway(TcpNioClientConnectionFactory connectionFactory) {
		TcpOutboundGateway gateway = new TcpOutboundGateway();
		gateway.setConnectionFactory(connectionFactory);
		gateway.setOutputChannel(replies());
		gateway.afterPropertiesSet();
		return gateway;
	}

	@Bean
	@Transformer(inputChannel = "replies", outputChannel = "results")
	public ObjectToStringTransformer transformer() {
		ObjectToStringTransformer transformer = new ObjectToStringTransformer();

		return transformer;
	}

	@Bean
	public OBD2Controller controller() {
		return new OBD2Controller();
	}

    public static void main(String[] args) throws Exception {
		ApplicationContext context = SpringApplication.run(Main.class, args);
		OBD2Controller controller = context.getBean(OBD2Controller.class);

		System.out.println("The car's VIN: " + controller.getVin());

		System.out.println("LONGITUDE,LATITUDE,RPM,SPEED(MPH),FUEL SYSTEM STATUS,ENGINE LOAD,COOLANT TEMP,SHORT TERM FUEL PERCENT,LONG TERM FUEL PERCENT,INTAKE PRESSURE,INTAKE AIR TEMP,MAF RATE,THROTTLE POSITION,OBD VERSION,RUNNING TIME(SECONDS),FUEL LEVEL,RELATIVE THROTTLE POSITION,ABSOLUTE THROTTLE POSITION B,ACCELERATOR POSITION D,ACCELERATOR POSITION E");

		while(true) {

			controller.getCarState();
			Thread.sleep(1000);
		}
    }
}
