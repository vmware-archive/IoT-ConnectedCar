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
package com.acmemotors.gf.support;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.acmemotors.rest.JourneysRepository;
import com.acmemotors.rest.domain.JourneyDestination;
import com.acmemotors.rest.domain.Journeys;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Provides facilities to load GemFire with the journeys identified by the batch training.
 *
 * @author Derek Beauregard
 * @author Michael Minella
 */
@Component
public class GemFireLoader {

	@Autowired
	private JourneysRepository repository;
	private ObjectMapper mapper = new ObjectMapper();

	/**
	 * Spring Data repository for saving {@link JourneyDestination} instances.
	 *
	 * @param repository Spring Data Repository used for persistence
	 */
	public void setRepository(JourneysRepository repository) {
		this.repository = repository;
	}

	/**
	 * Reads the input file, creates {@link JourneyDestination} instances for each record
	 * and persists them into GemFire.
	 *
	 * @param dataFile location of the file to be imported
	 * @throws IOException if the file cannot be read.
	 */
	@SuppressWarnings("unchecked")
	public void run(String dataFile) throws IOException {
		BufferedReader br =
				new BufferedReader(new InputStreamReader(new FileInputStream(dataFile)));
		String line = null;
		while((line = br.readLine()) != null)
    {
			Journeys curJourneys = mapJourneys(line);
			if(curJourneys != null) {
				repository.save(curJourneys);
			}
		}
		br.close();
	}

	/**
	 * Maps the provided line into a {@link Journeys}
	 *
	 * @param line source JSON
	 * @return a Journeys instance if the line is valid, null it if it's not.
	 * @throws IOException if line cannot be parsed.
	 */
	@SuppressWarnings("unchecked")
	protected Journeys mapJourneys(String line) throws IOException {
		if(StringUtils.hasText(line)) {
			Map<String, Object> map = mapper.readValue(line,
					new TypeReference<HashMap<String,Object>>(){});

			List<JourneyDestination> destinations = new ArrayList<>();

			if(map.containsKey("clusters") && StringUtils.hasText((String) map.get("vin"))) {
				Map<String, Object> jsonDestinations =
						(Map<String, Object>) map.get("clusters");

				for (Map.Entry<String, Object> jsonObject : jsonDestinations.entrySet()) {
					Map<String, Object> destinationValues =
							(Map<String, Object>) jsonObject.getValue();
					JourneyDestination curDestination = new JourneyDestination(
							(String) destinationValues.get("name"),
							(Double) destinationValues.get("lat"),
							(Double) destinationValues.get("long"),
							(String) destinationValues.get("address"));

					destinations.add(curDestination);
				}

				return new Journeys((String) map.get("vin"), destinations);
			}
			else {
				throw new IllegalArgumentException(
						"Line was not able to be parsed: " + line);
			}
		} else {
			return null;
		}
	}
}
