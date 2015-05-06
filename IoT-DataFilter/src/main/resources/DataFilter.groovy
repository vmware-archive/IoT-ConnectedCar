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
import groovy.json.JsonSlurper

def result = true
def slurper = new JsonSlurper()
def jsonPayload = null;

try {
	jsonPayload = slurper.parseText(payload)
} catch (Exception e) {
	return false;
}

def fuelLevelInput = jsonPayload.containsKey('fuel_level_input') ? jsonPayload?.fuel_level_input : null
def massAirFlow = jsonPayload.containsKey('maf_airflow') ? jsonPayload?.maf_airflow : null
def rpm = jsonPayload.containsKey('rpm') ? jsonPayload?.rpm : null
def intakeManifoldPressure = jsonPayload.containsKey('intake_manifold_pressure') ? jsonPayload?.intake_manifold_pressure : null
def intakeAirTemp = jsonPayload.containsKey('intake_air_temp') ? jsonPayload?.intake_air_temp : null
def vehicleSpeed = jsonPayload.containsKey('vehicle_speed') ? jsonPayload?.vehicle_speed : null

if((fuelLevelInput == null || fuelLevelInput instanceof String) || (vehicleSpeed == null || vehicleSpeed instanceof String)) {
	println "Rejecting $payload because either fuelLevelInput or vehicleSpeed are empty"
	result = false
} else {
	if((massAirFlow == null || massAirFlow instanceof String)) {
		if((rpm == null || rpm instanceof String) || (intakeAirTemp == null || intakeAirTemp instanceof String) || (intakeManifoldPressure == null || intakeManifoldPressure instanceof String)) {
			println "Rejecting $payload because maf related fields are not available"
			result = false
		}
	}
}

return result
