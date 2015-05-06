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

import java.io.Serializable;

/**
 * Represented in Gemfire by the following JSON:
 *
 * <code>
 * {
 *  "id": "4",
 *  "name": "Flying Saucer",
 *  "lat": "32.952859446844826",
 *  "long": "-96.81975846420114",
 *  "address": "Flying Saucer, 14999 Montfort Dr, Dallas, TX 75254"
 * }
 * </code>
 *
 * @author Michael Minella
 */
public class JourneyDestination implements Serializable {

	private static final long serialVersionUID = 1;
	private String name;
	private double latitude;
	private double longitude;
	private String address;

	public JourneyDestination(String name, double latitude, double longitude, String address) {
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.address = address;
	}

	/**
	 * The name of the destination.  May be null
	 *
	 * @return The name of the destination
	 */
	public String getName() {
		return name;
	}

	/**
	 * The lattitude of the destination
	 *
	 * @return latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * The longitude of the destination
	 *
	 * @return longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * The address of the destination.  May be null
	 *
	 * @return address
	 */
	public String getAddress() {
		return address;
	}
}
