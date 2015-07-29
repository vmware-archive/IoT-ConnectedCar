package com.acmemotors.batch.domain;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Michael Minella
 */
public enum Field {

	VIN("vin", "vin"),
	LONGITUDE("longitude", "longitude"),
	LATITUDE("latitude", "latitude"),
	VEHICLE_SPEED("vehicle_speed", "vehicleSpeed"),
	FUEL_SYSTEM_STATUS("fuel_system_status", "fuelSystemStatus"),
	ENGINE_LOAD("engine_load", "engineLoad"),
	COOLANT_TEMP("coolant_temp", "coolantTemp"),
	SHORT_TERM_FUEL("short_term_fuel", "shortTermFuel"),
	LONG_TERM_FUEL("long_term_fuel", "longTermFuel"),
	INTAKE_MANIFOLD_PRESSURE("intake_manifold_pressure", "intakeManifoldPressure"),
	INTAKE_AIR_TEMP("intake_air_temp", "intakeAirTemp"),
	MAF_AIRFLOW("maf_airflow", "mafAirflow"),
	THROTTLE_POSITION("throttle_position", "throttlePosition"),
	OBD_STANDARDS("obd_standards", "obdStandards"),
	TIME_SINCE_ENGINE_START("time_since_engine_start", "timeSinceEngineStart"),
	FUEL_LEVEL_INPUT("fuel_level_input", "fuelLevelInput"),
	RELATIVE_THROTTLE_POS("relative_throttle_pos", "relativeThrottlePos"),
	ABSOLUTE_THROTTLE_POS_B("absolute_throttle_pos_b", "absoluteThrottlePosB"),
	ACCELERATOR_THROTTLE_POS_D("accelerator_throttle_pos_d", "acceleratorThrottlePosD"),
	ACCELERATOR_THROTTLE_POS_E("accelerator_throttle_pos_e", "acceleratorThrottlePosE"),
	DISTANCE_WITH_MIL_ON("distance_with_mil_on", "distanceWithMilOn"),
	CATALYST_TEMP("catalyst_temp", "catalystTemp"),
	BAROMETRIC_PRESSURE("barometric_pressure", "barometricPressure"),
	CONTROL_MODULE_VOLTAGE("control_module_voltage", "controlModuleVoltage"),
	ACCELERATION("acceleration", "acceleration"),
	BEARING("bearing", "bearing"),
	RPM ("rpm", "rpm"),
	JOURNEY_ID("journey_id", "journeyId");

	private String herbieField;
	private String gemfireField;

	Field(String herbieField, String gemfireField) {
		this.herbieField = herbieField;
		this.gemfireField = gemfireField;
	}

	public static Field get(int index) {
		return values()[index];
	}

	public String getHerbieField() {
		return herbieField;
	}

	public String getGemfireField() {
		return gemfireField;
	}

	private static String[] getFields(String type) {
    List<String> rv = new ArrayList<String>();
    for (Field f : values())
    {
      if ("herbie".equals(type)) {
        rv.add(f.getHerbieField());
      } else if ("gemfire".equals(type)) {
        rv.add(f.getGemfireField());
      }
    }
		return rv.toArray(new String[rv.size()]);
  }

	public static String[] herbieFields() {
		return getFields("herbie");
	}

	public static String[] gemfireFields() {
		return getFields("gemfire");
	}
}

