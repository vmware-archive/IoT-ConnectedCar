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
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.gemfire.mapping.Region;

/**
 * A domain object that represents the journeys for a given VIN.
 *
 * @author Michael Minella
 */
@Region("journeys")
public class Journeys implements Serializable {

	private static final long serialVersionUID = 1;

	@Id
	private String vin;
	private List<JourneyDestination> destinations;

	@PersistenceConstructor
	public Journeys(String vin, List<JourneyDestination> destinations) {
		this.vin = vin;
		this.destinations = destinations;
	}

	/**
	 * The VIN the journeys are associated with
	 *
	 * @return the vehicle's VIN
	 */
	public String getVin() {
		return vin;
	}

	/**
	 * The list of {@link JourneyDestination} values, one per possible destination for
	 * the associated VIN.
	 *
	 * @return the possible destinations
	 */
	public List<JourneyDestination> getDestinations() {
		return destinations;
	}
}
