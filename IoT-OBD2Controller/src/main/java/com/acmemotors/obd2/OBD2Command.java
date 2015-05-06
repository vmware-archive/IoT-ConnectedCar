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
package com.acmemotors.obd2;

/**
 * @author mminella
 */
public enum OBD2Command {

	ECHO_ON("AT E1", false),
	ECHO_OFF("AT E0", false),
	LINEFEEDS_ON("AT L1", false),
	LINEFEEDS_OFF("AT L0", false),
	PRINT_VERSION("AT I", false),
	AAAAAAAAAA("ST I", false),
	AUTOMATIC_FORMATTING_ON("AT CAF1", false),
	AUTOMATIC_FORMATTING_OFF("AT CAF0", false),
	HEADERS_ON("AT H1", false),
	HEADERS_OFF("AT H0", false),
	MEMORY_ON("AT M1", false),
	MEMORY_OFF("AT M0", false),
	TRY_PROTOCOL_6("AT TP6", false),
	TRY_PROTOCOL_7("AT TP7", false),
	GET_VIN("09 02", true),
	SET_DEFAULTS("AT D", false),
	GET_PIDS_SUPPORTED_00("01 00", true),
	GET_PIDS_SUPPORTED_20("01 20", true),
	GET_PIDS_SUPPORTED_40("01 40", true),
	GET_PIDS_SUPPORTED_60("01 60", true),
	GET_PIDS_SUPPORTED_80("01 80", true),
	GET_PIDS_SUPPORTED_A0("01 A0", true),
	GET_PIDS_SUPPORTED_C0("01 C0", true),
	DESCRIBE_PROTOCOL_BY_NUMBER("AT DPN", false),
	BBBBBBBBBBB("AT STD7D", false),
	ALLOW_LONG_BYTE_MESSAGE("AT AL", false),
	// Setting headers: Fucntional addressing (DB), receiver (always 33 for functional), transmitter (always F1 for functional)
	SET_HEADERS("AT SHDB33F1", false),
	SET_CAN_PRIORITY("AT CP18", false),
	GET_CALIBRATION_ID("09 04", true),
	GET_CALIBRATION_VERIFICATION_NUMBERS("09 06", true),
	GET_FUEL_SYSTEM_STATUS("01 03", true),
	GET_ENGINE_LOAD("01 04", true),
	GET_COOLANT_TEMP("01 05", true),
	GET_SHORT_TERM_FUEL_PERCENT("01 06", true),
	GET_LONG_TERM_FUEL_PERCENT("01 07", true),
	GET_INTAKE_ABSOLUTE_PRESSURE("01 0B", true),
	GET_RPM("01 0C", true),
	GET_SPEED("01 0D", true),
	GET_INTAKE_AIR_TEMP("01 0F", true),
	GET_MAF_FLOW_RATE("01 10", true),
	GET_THROTTLE_POSITION("01 11", true),
	GET_OBD_STANDARD("01 1C", true),
	GET_RUN_TIME_SINCE_START("01 1F", true),
	GET_DISTANCE_WITH_CHECK_ENGINE("01 21", true),
	GET_FUEL_LEVEL("01 2F", true),
	GET_BAROMETRIC_PRESSURE("01 33", true),
	GET_CATALYST_TEMP("01 3C", true),
	GET_CONTROL_MODULE_VOLTAGE("01 42", true),
	GET_RELATIVE_THROTTLE_POSITION("01 45", true),
	GET_ABSOLUTE_THROTTLE_POSITION_B("01 47", true),
	GET_ACCELERATOR_PEDAL_POSITION_D("01 49", true),
	GET_ACCELERATOR_PEDAL_POSITION_E("01 4A", true);

	private final String command;
	private final boolean odb2;

	private OBD2Command(String command, boolean odb2) {
		this.command = command;
		this.odb2 = odb2;
	}

	public String getCommand() {
		return command;
	}

	public boolean isOdb2() {
		return odb2;
	}
}
