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
package com.acmemotors.integration;

import org.springframework.core.serializer.Deserializer;
import org.springframework.core.serializer.Serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author mminella
 */
public class OBD2Serializer implements Serializer<String>, Deserializer<String>{

	/**
	 * Convert a CustomOrder object into a byte-stream
	 *
	 * @param outputStream
	 * @throws IOException
	 */
	@Override
	public void serialize(String command, OutputStream outputStream) throws IOException {
		outputStream.write(command.getBytes());
		outputStream.write("\r\n".getBytes());
		outputStream.flush();
	}

	/**
	 * Convert a raw byte stream into a CustomOrder
	 *
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	@Override
	public String deserialize(InputStream inputStream) throws IOException {
		StringBuilder builder = new StringBuilder();

		int c;
		while(true) {
			c = inputStream.read();
			checkClosure(c);

			if(c == '\r') {
				c = '\n';
			}

			builder.append((char)c);

			if(c == '>') {
				break;
			}
		}

		return builder.toString();
	}

	/**
	 * Check whether the byte passed in is the "closed socket" byte
	 * Note, I put this in here just as an example, but you could just extend the
	 * {@link org.springframework.integration.ip.tcp.serializer.AbstractByteArraySerializer} class
	 * which has this method
	 *
	 * @param bite
	 * @throws IOException
	 */
	protected void checkClosure(int bite) throws IOException {
		if (bite < 0) {
			throw new IOException("Socket closed during message assembly");
		}
	}
}
