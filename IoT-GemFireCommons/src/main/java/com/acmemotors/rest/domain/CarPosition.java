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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.gemfire.mapping.Region;
import org.springframework.util.StringUtils;

/**
 * Representated in Gemfire by the following JSON:
 *
 * {
 * "Predictions": {
 *     "ClusterPredictions": {
 *         "0": {
 *             "EndLocation": [
 *                 41.97768290811704,
 *                 -87.66936619865827
 *             ],
 *             "MPG_Journey": 20.2776,
 *             "Probability": 0.0
 *         }
 *     },
 *     "RemainingRange": 267
 * },
 * "absolute_throttle_pos_b": 30.0,
 * "acceleration": 1.014,
 * "accelerator_throttle_pos_d": 20.0,
 * "accelerator_throttle_pos_e": 10.0,
 * "barometric_pressure": 100,
 * "bearing": 85.850507,
 * "catalyst_temp": 596.0,
 * "control_module_voltage": 14.0,
 * "coolant_temp": 88,
 * "distance_with_mil_on": 0,
 * "engine_load": 24.0,
 * "fuel_level_input": 43,
 * "fuel_system_status": [
 *     2,
 *     0
 * ],
 * "intake_air_temp": 3,
 * "intake_manifold_pressure": 34,
 * "journey_id": "ab18d293-57ed-497f-8b25-f587asf0c75",
 * "latitude": 41.798128,
 * "long_term_fuel": 5.0,
 * "longitude": -87.58763,
 * "maf_airflow": 3.0,
 * "mpg_instantaneous": 0.0,
 * "obd_standards": "1",
 * "relative_throttle_pos": 2.0,
 * "rpm": 678.0,
 * "short_term_fuel": 4.0,
 * "throttle_position": 13,
 * "time_since_engine_start": 1760,
 * "timestamp": 1421876463415,
 * "vehicle_speed": 0,
 * "vin": "WP0AD2A71FL040367"
 * }
 *
 * @author Michael Minella
 */
@Region("car-position")
public class CarPosition implements Serializable {

	private static final long serialVersionUID = 1;

	@Id
	private String vin;

	private Double absoluteThrottlePosB;
	private Double acceleration;
	private Double acceleratorThrottlePosD;
	private Double acceleratorThrottlePosE;
	private Integer barometricPressure;
	private Double bearing;
	private Double catalystTemp;
	private Double controlModuleVoltage;
	private Integer coolantTemp;
	private Integer distanceWithMilOn;
	private Double engineLoad;
	private Integer fuelLevelInput;
	private Integer[] fuelSystemStatus;
	private Integer intakeAirTemp;
	private Integer intakeManifoldPressure;
	private Double latitude;
	private Double longTermFuel;
	private Double longitude;
	private Double mafAirflow;
	private Double mpgInstantaneous;
	private Integer obdStandards;
	private Double relativeThrottlePos;
	private Double rpm;
	private Double shortTermFuel;
	private Integer throttlePosition;
	private long timeSinceEngineStart;
	private long timestamp;
	private Double vehicleSpeed;
	private Integer remainingRange;
	private String journeyId;
	private Map<String, PredictedDestination> predictions;

	private Integer toInteger(Object value) {
		if(value instanceof Integer) {
			return (Integer) value;
		} else if(value instanceof String && StringUtils.hasText((String) value)) {
			return Integer.parseInt((String) value);
		} else {
			return null;
		}
	}

	private Long toLong(Object value) {
		if(value instanceof Long) {
			return (Long) value;
		} else if(value instanceof String && StringUtils.hasText((String) value)) {
			return Long.parseLong((String) value);
		} else {
			return null;
		}
	}

	private Double toDouble(Object value) {
		if(value instanceof Double) {
			return (Double) value;
		} else if(value instanceof String && StringUtils.hasText((String) value)) {
			return Double.parseDouble((String) value);
		} else if(value instanceof Integer) {
			return ((Integer) value).doubleValue();
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public CarPosition(Map<String, Object> values) {
		this.journeyId = String.valueOf(values.get("journey_id"));
		this.absoluteThrottlePosB = toDouble(values.get("absolute_throttle_pos_b"));
		this.acceleration = toDouble(values.get("acceleration"));
		this.acceleratorThrottlePosD = toDouble(values.get("accelerator_throttle_pos_d"));
		this.acceleratorThrottlePosE = toDouble(values.get("accelerator_throttle_pos_e"));
		this.barometricPressure = toInteger(values.get("barometric_pressure"));
		this.bearing = toDouble(values.get("bearing"));
		this.catalystTemp = toDouble(values.get("catalyst_temp"));
		this.controlModuleVoltage = toDouble(values.get("control_module_voltage"));
		this.coolantTemp = toInteger(values.get("coolant_temp"));
		this.distanceWithMilOn = toInteger(values.get("distance_with_mil_on"));
		this.engineLoad = toDouble(values.get("engine_load"));
		this.fuelLevelInput = toInteger(values.get("fuel_level_input"));

		List<Integer> fuelSystemStatus = (List) values.get("fuel_system_status");
		if(fuelSystemStatus != null) {
			Integer [] fuelSystemValues = new Integer[fuelSystemStatus.size()];
			for (int i = 0; i < fuelSystemStatus.size(); i++) {
				fuelSystemValues[i] = fuelSystemStatus.get(i);
			}

			this.fuelSystemStatus = fuelSystemValues;
		}
		else {
			this.fuelSystemStatus = new Integer[0];
		}

		this.intakeAirTemp = toInteger(values.get("intake_air_temp"));
		this.intakeManifoldPressure = toInteger(values.get("intake_manifold_pressure"));
		this.latitude = toDouble(values.get("latitude"));
		this.longTermFuel = toDouble(values.get("long_term_fuel"));
		this.longitude = toDouble(values.get("longitude"));
		this.mafAirflow = toDouble(values.get("maf_airflow"));
		this.mpgInstantaneous = toDouble(values.get("mpg_instantaneous"));
		this.obdStandards = Integer.parseInt(String.valueOf(values.get("obd_standards")));
		this.relativeThrottlePos = toDouble(values.get("relative_throttle_pos"));
		this.rpm = toDouble(values.get("rpm"));
		this.shortTermFuel = toDouble(values.get("short_term_fuel"));
		this.throttlePosition = toInteger(values.get("throttle_position"));

		Object timeSinceEngineStart = values.get("time_since_engine_start");

		if(timeSinceEngineStart != null) {
			if(timeSinceEngineStart instanceof Long) {
				this.timeSinceEngineStart = (Long) timeSinceEngineStart;
			}
			else {
				this.timeSinceEngineStart = ((Integer) timeSinceEngineStart).longValue();
			}
		}

		this.timestamp = toLong(values.get("timestamp"));
		Object vehicleSpeed = values.get("vehicle_speed");

		if(vehicleSpeed != null) {
			if(vehicleSpeed instanceof Integer) {
				this.vehicleSpeed = ((Integer) vehicleSpeed).doubleValue();
			}
			else if(vehicleSpeed instanceof Double){
				this.vehicleSpeed = (Double) vehicleSpeed;
			}
			else {
				this.vehicleSpeed = Double.parseDouble(String.valueOf(vehicleSpeed));
			}
		}

		this.vin = (String) values.get("vin");

		Map<String, Object> predictions = (Map<String, Object>) values.get("Predictions");
		this.remainingRange = (Integer) predictions.get("RemainingRange");

		Map<String, Object> journeyPredictions = (Map<String, Object>) predictions.get("ClusterPredictions");
		this.predictions = new HashMap<>(journeyPredictions.size());

		for (Map.Entry<String, Object> predictionValues : journeyPredictions.entrySet()) {
			Map<String, Object> prediction = (Map<String, Object>) predictionValues.getValue();

			PredictedDestination curPrediction = new PredictedDestination((Double) ((List)prediction.get("EndLocation")).get(0), (Double) ((List)prediction.get("EndLocation")).get(1), (Double) prediction.get("MPG_Journey"), (Double) prediction.get("Probability"));
			this.predictions.put(predictionValues.getKey(), curPrediction);
		}
	}

	/**
	 * An id that identifies a journey for a given VIN.
	 *
	 * @return a unique string identifying the current journey
	 */
	public String getJourneyId() {
		return journeyId;
	}

	/**
	 * The data point returned for the OBD II PID 01 47
	 *
	 * @return the throttle position
	 */
	public Double getAbsoluteThrottlePosB() {
		return absoluteThrottlePosB;
	}

	/**
	 * The acceleration as determined by the phone
	 *
	 * @return acceleration based on the accelerometer in an iPhone
	 */
	public Double getAcceleration() {
		return acceleration;
	}

	/**
	 * The data point returned for the OBD II PID 01 49
	 *
	 * @return the accelerator throttle position D
	 */
	public Double getAcceleratorThrottlePosD() {
		return acceleratorThrottlePosD;
	}

	/**
	 * The data point returned for the OBD II PID 01 4A
	 *
	 * @return the accelerator throttle position E
	 */
	public Double getAcceleratorThrottlePosE() {
		return acceleratorThrottlePosE;
	}

	/**
	 * The data point returned for the OBD II PID 01 33
	 *
	 * @return the current barometric pressure
	 */
	public Integer getBarometricPressure() {
		return barometricPressure;
	}

	/**
	 * The bearing as determined by the phone
	 *
	 * @return bearing as determined by an iPhone
	 */
	public Double getBearing() {
		return bearing;
	}

	/**
	 * The data point returned for the OBD II PID 01 3C
	 *
	 * @return catalyst temp
	 */
	public Double getCatalystTemp() {
		return catalystTemp;
	}

	/**
	 * The data point returned for the OBD II PID 01 42
	 *
	 * @return control module voltage
	 */
	public Double getControlModuleVoltage() {
		return controlModuleVoltage;
	}

	/**
	 * The data point returned for the OBD II PID 01 05
	 *
	 * @return coolant temp
	 */
	public Integer getCoolantTemp() {
		return coolantTemp;
	}

	/**
	 * The data point returned for the OBD II PID 01 21
	 *
	 * @return distance with the "check engine light" on
	 */
	public Integer getDistanceWithMilOn() {
		return distanceWithMilOn;
	}

	/**
	 * The data point returned for the OBD II PID 01 04
	 *
	 * @return current engine load
	 */
	public Double getEngineLoad() {
		return engineLoad;
	}

	/**
	 * The data point returned for the OBD II PID 01 2F
	 *
	 * @return current amount of fuel in the tank
	 */
	public Integer getFuelLevelInput() {
		return fuelLevelInput;
	}

	/**
	 * The data point returned for the OBD II PID 01 03
	 *
	 * @return statuses of the fuel system
	 */
	public Integer[] getFuelSystemStatus() {
		return fuelSystemStatus;
	}

	/**
	 * The data point returned for the OBD II PID 01 0F
	 *
	 * @return temperature of intake air
	 */
	public Integer getIntakeAirTemp() {
		return intakeAirTemp;
	}

	/**
	 * The data point returned for the OBD II PID 01 0B
	 *
	 * @return intake mainfold pressure
	 */
	public Integer getIntakeManifoldPressure() {
		return intakeManifoldPressure;
	}

	/**
	 * Current latitude as determined by the phone
	 *
	 * @return latitude
	 */
	public Double getLatitude() {
		return latitude;
	}

	/**
	 * The data point returned for the OBD II PID 01 07
	 *
	 * @return long term fuel percent
	 */
	public Double getLongTermFuel() {
		return longTermFuel;
	}

	/**
	 * Current longitude as determined by the phone
	 *
	 * @return longitude
	 */
	public Double getLongitude() {
		return longitude;
	}

	/**
	 * The data point returned for the OBD II PID 01 10
	 *
	 * @return mass airflow rate
	 */
	public Double getMafAirflow() {
		return mafAirflow;
	}

	/**
	 * Calculated instantaneous miles per gallon
	 *
	 * @return current MPG
	 */
	public Double getMpgInstantaneous() {
		return mpgInstantaneous;
	}

	/**
	 * The data point returned for the OBD II PID 01 1C
	 *
	 * @return version of the OBD standard supported by the current vehicle
	 */
	public Integer getObdStandards() {
		return obdStandards;
	}

	/**
	 * The data point returned for the OBD II PID 01 45
	 *
	 * @return relative throttle position
	 */
	public Double getRelativeThrottlePos() {
		return relativeThrottlePos;
	}

	/**
	 * The data point returned for the OBD II PID 01 0C
	 *
	 * @return current engine RPM
	 */
	public Double getRpm() {
		return rpm;
	}

	/**
	 * The data point returned for the OBD II PID 01 06
	 *
	 * @return short term fuel percent
	 */
	public Double getShortTermFuel() {
		return shortTermFuel;
	}

	/**
	 * The data point returned for the OBD II PID 01 11
	 *
	 * @return throttle position
	 */
	public Integer getThrottlePosition() {
		return throttlePosition;
	}

	/**
	 * The data point returned for the OBD II PID 01 1F
	 *
	 * @return time in milliseconds since the vehicle was started
	 */
	public Long getTimeSinceEngineStart() {
		return timeSinceEngineStart;
	}

	/**
	 * Timestamp for the current data point
	 *
	 * @return timestamp in milliseconds from epoch
	 */
	public Long getTimestamp() {
		return timestamp;
	}

	/**
	 * The data point returned for the OBD II PID 01 0D
	 *
	 * @return speed in K/h
	 */
	public Double getVehicleSpeed() {
		return vehicleSpeed;
	}

	/**
	 * The current vehicle's vehicle identification number (VIN)
	 *
	 * @return the vehicle's VIN
	 */
	public String getVin() {
		return vin;
	}

	/**
	 * The calculated remaining range as predicted by the data science module
	 *
	 * @return number of miles
	 */
	public Integer getRemainingRange() {
		return remainingRange;
	}

	/**
	 * Collection of predictions mapped journeyId :
	 * {@link com.acmemotors.rest.domain.CarPosition.PredictedDestination}.
	 *
	 * @return current predictions
	 */
	public Map<String, PredictedDestination> getPredictions() {
		return predictions;
	}

	/**
	 * Represents a predicted end point or "journey".  A "journey" is defined as a start
	 * point and end point and is represented by the end point defined by the latitude and
	 * longitude in this instance.  The start point is defined as the current location of
	 * the vehicle.
	 */
	public static class PredictedDestination implements Serializable {
		private static final long serialVersionUID = 1;
		private Double latitude;
		private Double longitude;
		private Double mpgJourney;
		private Double probability;

		public PredictedDestination(Double latitude, Double longitude, Double mpgJourney, Double probability) {
			this.latitude = latitude;
			this.longitude = longitude;
			this.mpgJourney = mpgJourney;
			this.probability = probability;
		}

		/**
		 * The journey's end point latitude
		 *
		 * @return latitude
		 */
		public Double getLatitude() {
			return latitude;
		}

		/**
		 * The journey's end point longitude
		 *
		 * @return longitude
		 */
		public Double getLongitude() {
			return longitude;
		}

		/**
		 * The journey's historic mpg
		 *
		 * @return MPG
		 */
		public Double getMpgJourney() {
			return mpgJourney;
		}

		/**
		 * The probability this predicted destination is the one the vehicle is traveling
		 * to.
		 *
		 * @return percentage probability.
		 */
		public Double getProbability() {
			return probability;
		}
	}
}
