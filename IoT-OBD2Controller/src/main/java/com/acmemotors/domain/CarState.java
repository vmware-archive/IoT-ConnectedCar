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
package com.acmemotors.domain;

/**
 * @author mminella
 */
public class CarState {

	private final int speed;
	private final int oilTemp;
	private final int fuelLevel;
	private final int throttlePosition;
	private final int rpm;
	private final FuelSystemStatus fuelSystemStatus;

	public CarState(FuelSystemStatus fuelSystemStatus, int speed, int oilTemp, int fuelLevel, int throttlePosition, int rpm) {
		this.speed = speed;
		this.oilTemp = oilTemp;
		this.fuelLevel = fuelLevel;
		this.throttlePosition = throttlePosition;
		this.rpm = rpm;
		this.fuelSystemStatus = fuelSystemStatus;
	}

	public int getSpeed() {
		return speed;
	}

	public int getOilTemp() {
		return oilTemp;
	}

	public int getFuelLevel() {
		return fuelLevel;
	}

	public int getThrottlePosition() {
		return throttlePosition;
	}

	public int getRpm() {
		return rpm;
	}

	public FuelSystemStatus getFuelSystemStatus() {
		return fuelSystemStatus;
	}

	@Override
	public String toString() {
		return String.format("speed = %d\noil temperature = %d\nfuel level = %d\nthrottle level = %d\nrpm = %d", speed, oilTemp, fuelLevel, throttlePosition, rpm);
	}
}
