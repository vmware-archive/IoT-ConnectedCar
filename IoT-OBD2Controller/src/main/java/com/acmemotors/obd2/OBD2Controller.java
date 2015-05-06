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
package com.acmemotors.obd2;

import com.acmemotors.domain.CarState;
import com.acmemotors.domain.OBDVersion;
import com.acmemotors.integration.OBD2Gateway;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestOperations;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This controller is hard coded to use the CAN protocol which should
 * work on all US cars made after 2008.  PIDs coded for were tested in
 * both a 2009 Acura TSX and a 2014 Acura MDX.
 *
 * For more details read about the various protocols here
 * <a href="https://www.scantool.net/support/index.php?_m=knowledgebase&_a=viewarticle&kbarticleid=3&nav=0">
 *     https://www.scantool.net/support/index.php?_m=knowledgebase&_a=viewarticle&kbarticleid=3&nav=0
 * </a>
 *
 * @author Michael Minella
 */
public class OBD2Controller implements InitializingBean {

	@Autowired
	private OBD2Gateway gateway;

	@Value("${gpsHost:192.168.1.2}")
	private String gpsHost;

	private static final Pattern NO_DATA = Pattern.compile("NO DATA");

	private static final OBD2Command[] CONNECTION_SCRIPT_START = {
		  OBD2Command.ECHO_ON,
		  OBD2Command.LINEFEEDS_OFF,
		  OBD2Command.PRINT_VERSION,
		  OBD2Command.AAAAAAAAAA,
		  OBD2Command.AUTOMATIC_FORMATTING_ON,
		  OBD2Command.HEADERS_ON,
		  OBD2Command.MEMORY_OFF,
		  OBD2Command.TRY_PROTOCOL_7};

	private static final OBD2Command[] PIDS = {
		  OBD2Command.GET_PIDS_SUPPORTED_00,
		  OBD2Command.GET_PIDS_SUPPORTED_20,
		  OBD2Command.GET_PIDS_SUPPORTED_40,
		  OBD2Command.GET_PIDS_SUPPORTED_60,
		  OBD2Command.GET_PIDS_SUPPORTED_80,
		  OBD2Command.GET_PIDS_SUPPORTED_A0,
		  OBD2Command.GET_PIDS_SUPPORTED_C0
	};

	@Autowired
	private RestOperations restTemplate;

	@Override
	public void afterPropertiesSet() throws Exception {

		for(OBD2Command curCommand : CONNECTION_SCRIPT_START) {
			gateway.send(curCommand.getCommand());
		}

		int pidOffset = 0;
		List<String> pids = new ArrayList<>();
		for(OBD2Command curCommand : PIDS) {
			String response = gateway.send(curCommand.getCommand());

			Matcher matcher = NO_DATA.matcher(response);

			if(matcher.find()) {
				break;
			}

			pids.addAll(decodePidsSupported(response, pidOffset));

			pidOffset += 32;
		}

		System.out.println("PIDs supported by this car are:");
		System.out.println("-------------------------------");

		for (String pid : pids) {
			System.out.println(pid);
		}

		System.out.println("-------------------------------");
	}

	public String getVin() {
		String response = gateway.send(OBD2Command.GET_VIN.getCommand());

		String [] responseLines = response.split("\n");

		StringBuilder vin = new StringBuilder(17);

		String[] curLineElements = responseLines[1].split(" ");

		for(int i = 9; i < curLineElements.length; i++) {
			vin.append((char) Integer.parseInt(curLineElements[i], 16));
		}

		for(int i = 2; i < responseLines.length; i++) {
			curLineElements = responseLines[i].split(" ");

			for(int j = 5; j < curLineElements.length; j++) {
				vin.append((char) Integer.parseInt(curLineElements[j], 16));
			}
		}

		return vin.toString();
	}

	public List<String> decodePidsSupported(String response, int offset) {
		List<String> pids = new ArrayList<>();

		String [] respsonseLines = response.split("\n");

		StringBuilder pidString = new StringBuilder();

		String[] elements = respsonseLines[1].split(" ");

		for(int j = 7; j < elements.length; j++) {
			pidString.append(elements[j]);
		}

		BigInteger bigInt = new BigInteger(pidString.toString(), 16);

		int j = 0;
		for(int i = bigInt.bitLength() - 1; i >= 0; i--) {
			int pidId = j + offset + 1;

			if(bigInt.testBit(i)) {
				pids.add(Integer.toHexString(pidId));
			}

			j++;
		}

		return pids;
	}

	public CarState getCarState() {

		int rpm = decodeRPM(gateway.send(OBD2Command.GET_RPM.getCommand()));
		int speed = decodeSpeed(gateway.send(OBD2Command.GET_SPEED.getCommand()));
		int [] fuelSystemStatus = decodeFuelSystemStatus(gateway.send(OBD2Command.GET_FUEL_SYSTEM_STATUS.getCommand()));
		int load = decodeEngineLoad(gateway.send(OBD2Command.GET_ENGINE_LOAD.getCommand()));
		int coolantTemp = decodeCoolantTemp(gateway.send(OBD2Command.GET_COOLANT_TEMP.getCommand()));
		double shortTermFuelPercent = decodeFuelPercent(gateway.send(OBD2Command.GET_SHORT_TERM_FUEL_PERCENT.getCommand()));
		double longTermFuelPercent = decodeFuelPercent(gateway.send(OBD2Command.GET_LONG_TERM_FUEL_PERCENT.getCommand()));
		int intakePressure = decodeIntakePressure(gateway.send(OBD2Command.GET_INTAKE_ABSOLUTE_PRESSURE.getCommand()));
		int intakeAirTemp = decodeIntakeAirTemp(gateway.send(OBD2Command.GET_INTAKE_AIR_TEMP.getCommand()));
		double mafRate = decodeMafRate(gateway.send(OBD2Command.GET_MAF_FLOW_RATE.getCommand()));
		int throttlePosition = decodeThrottlePosition(gateway.send(OBD2Command.GET_THROTTLE_POSITION.getCommand()));
		OBDVersion standardVersion = decodeObdVersion(gateway.send(OBD2Command.GET_OBD_STANDARD.getCommand()));
		int seconds = decodeRunningTime(gateway.send(OBD2Command.GET_RUN_TIME_SINCE_START.getCommand()));
//		int speed = decodeSpeed(gateway.send(OBD2Command.GET_DISTANCE_WITH_CHECK_ENGINE.getCommand()));
		int fuelLevel = decodePercentage(gateway.send(OBD2Command.GET_FUEL_LEVEL.getCommand()));
//		int speed = decodeSpeed(gateway.send(OBD2Command.GET_BAROMETRIC_PRESSURE.getCommand()));
//		int speed = decodeSpeed(gateway.send(OBD2Command.GET_CATALYST_TEMP.getCommand()));
//		int speed = decodeSpeed(gateway.send(OBD2Command.GET_CONTROL_MODULE_VOLTAGE.getCommand()));
		int relativeThrottlePosition = decodePercentage(gateway.send(OBD2Command.GET_RELATIVE_THROTTLE_POSITION.getCommand()));
		int absoluteThrottlePositionB = decodePercentage(gateway.send(OBD2Command.GET_ABSOLUTE_THROTTLE_POSITION_B.getCommand()));
		int acceleratorPedalD = decodePercentage(gateway.send(OBD2Command.GET_ACCELERATOR_PEDAL_POSITION_D.getCommand()));
		int acceleratorPedalE = decodePercentage(gateway.send(OBD2Command.GET_ACCELERATOR_PEDAL_POSITION_E.getCommand()));

//		CarState result = new CarState(fuelSystemStatus, speed, -1, -1, -1, rpm);

		// REST calls are made to n iPhone running GPS Receiver HD
		// https://itunes.apple.com/us/app/gps-receiver-hd/id397928381?mt=8
		String currentLocation = restTemplate.getForObject("http://" + gpsHost, String.class);
		Map<String, Double> location = formatLocation(currentLocation);
		System.out.println(String.format("%f,%f,%d,%d,%s,%d,%d,%f,%f,%d,%d,%f,%d,%s,%d,%d,%d,%d,%d,%d",
												location.get("Longitude"),
												location.get("Latitude"),
												rpm,
												speed,
												fuelSystemStatus[0], // Currently just outputting the first one since the simulator only consumes one
												load,
												coolantTemp,
												shortTermFuelPercent,
												longTermFuelPercent,
												intakePressure,
												intakeAirTemp,
												mafRate,
												throttlePosition,
												standardVersion,
												seconds,
												fuelLevel,
												relativeThrottlePosition,
												absoluteThrottlePositionB,
												acceleratorPedalD,
												acceleratorPedalE));

		return null;
	}

	private int decodePercentage(String response) {
		String [] respsonseLines = response.split("\n");

		StringBuilder pidString = new StringBuilder();

		String[] elements = respsonseLines[1].split(" ");

		BigInteger a = new BigInteger(elements[7], 16);

		return a.intValue() * 100 / 255;
	}

	protected int decodeRunningTime(String response) {
		String [] respsonseLines = response.split("\n");

		StringBuilder pidString = new StringBuilder();

		String[] elements = respsonseLines[1].split(" ");

		BigInteger a = new BigInteger(elements[7], 16);
		BigInteger b = new BigInteger(elements[8], 16);

		return (a.intValue()*256)+b.intValue();
	}

	protected OBDVersion decodeObdVersion(String response) {
		String [] respsonseLines = response.split("\n");

		// Need to verify position
		String[] elements = respsonseLines[1].split(" ");
		int status = new BigInteger(elements[7], 16).intValue();

		switch (status) {
			case 1: return OBDVersion.OBD_II;
			case 2: return OBDVersion.OBD;
			case 3: return OBDVersion.OBD_AND_OBD_II;
			case 4: return OBDVersion.OBD_I;
			case 5: return OBDVersion.NOT_OBD;
			case 6: return OBDVersion.EOBD;
			case 7: return OBDVersion.EOBD_AND_OBD_II;
			case 8: return OBDVersion.EOBD_AND_OBD;
			case 9: return OBDVersion.EOBD_OBD_AND_OBD_II;
			case 10: return OBDVersion.JOBD;
			case 11: return OBDVersion.JOBD_AND_OBD_II;
			case 12: return OBDVersion.JOBD_AND_EOBD;
			case 13: return OBDVersion.JOBD_EOBD_AND_OBD_II;
			case 17: return OBDVersion.EMD;
			case 18: return OBDVersion.EMD_PLUS;
			case 19: return OBDVersion.HD_OBD_C;
			case 20: return OBDVersion.HD_OBD;
			case 21: return OBDVersion.WWH_OBD;
			case 23: return OBDVersion.HD_EOBD_I;
			case 24: return OBDVersion.HD_EOBD_I_N;
			case 25: return OBDVersion.HD_EOBD_II;
			case 26: return OBDVersion.HD_EOBD_II_N;
			case 28: return OBDVersion.OBDBr_1;
			case 29: return OBDVersion.OBDBr_2;
			case 30: return OBDVersion.KOBD;
			case 31: return OBDVersion.IOBD_I;
			case 32: return OBDVersion.IOBD_II;
			case 33: return OBDVersion.HD_EOBD_IV;
			default : throw new RuntimeException("Invalid value returned");
		}
	}

	private int decodeThrottlePosition(String response) {
		String [] respsonseLines = response.split("\n");

		StringBuilder pidString = new StringBuilder();

		String[] elements = respsonseLines[1].split(" ");

		return Math.round(new BigInteger(elements[7], 16).intValue() * 100 / 255);
	}

	private double decodeMafRate(String response) {
		String [] respsonseLines = response.split("\n");

		StringBuilder pidString = new StringBuilder();

		String[] elements = respsonseLines[1].split(" ");

		BigInteger a = new BigInteger(elements[7], 16);
		BigInteger b = new BigInteger(elements[8], 16);

		return ((a.intValue()*256)+b.intValue())/100;
	}

	private int decodeIntakeAirTemp(String response) {
		String [] respsonseLines = response.split("\n");

		StringBuilder pidString = new StringBuilder();

		String[] elements = respsonseLines[1].split(" ");

		return (int) Math.round((new BigInteger(elements[7], 16).intValue() - 40) * 1.8 + 32);
	}

	protected int decodeIntakePressure(String response) {
		String [] respsonseLines = response.split("\n");

		StringBuilder pidString = new StringBuilder();

		String[] elements = respsonseLines[1].split(" ");

		return new BigInteger(elements[7], 16).intValue();
	}

	protected double decodeFuelPercent(String response) {
		String [] respsonseLines = response.split("\n");

		StringBuilder pidString = new StringBuilder();

		String[] elements = respsonseLines[1].split(" ");

		BigInteger a = new BigInteger(elements[7], 16);

		return a.intValue() - 40;
	}

	protected int decodeCoolantTemp(String response) {
		String [] respsonseLines = response.split("\n");

		StringBuilder pidString = new StringBuilder();

		String[] elements = respsonseLines[1].split(" ");

		BigInteger a = new BigInteger(elements[7], 16);

		return a.intValue() - 40;
	}

	protected int decodeEngineLoad(String response) {
		String [] respsonseLines = response.split("\n");

		StringBuilder pidString = new StringBuilder();

		String[] elements = respsonseLines[1].split(" ");

		BigInteger a = new BigInteger(elements[7], 16);

		return a.intValue() * 100 / 255;
	}

	/**
	 * Formula taken from http://en.wikipedia.org/wiki/OBD-II_PIDs
	 * @param response Full response for the 01 0C request
	 * @return int value of the current RPMs
	 */
	protected int decodeRPM(String response) {
		String [] respsonseLines = response.split("\n");

		StringBuilder pidString = new StringBuilder();

		String[] elements = respsonseLines[1].split(" ");

		BigInteger a = new BigInteger(elements[7], 16);
		BigInteger b = new BigInteger(elements[8], 16);

		return ((a.intValue()*256)+b.intValue())/4;
	}

	protected int decodeSpeed(String response) {
		String [] respsonseLines = response.split("\n");

		String[] elements = respsonseLines[1].split(" ");

		return (int) Math.round(.621371 * new BigInteger(elements[7], 16).intValue());
	}

	protected int[] decodeFuelSystemStatus(String response) {
		String [] respsonseLines = response.split("\n");

		String[] elements = respsonseLines[1].split(" ");
		int [] statuses = new int[2];
		statuses[0] = new BigInteger(elements[7], 16).intValue();
		statuses[1] = new BigInteger(elements[8], 16).intValue();

		return statuses;
	}

	private Map<String, Double> formatLocation(String locationHtml) {
		Map<String, Double> curLocation = new HashMap<>();

		locationHtml = locationHtml.replaceAll("\n", "").replaceAll("<[^<]+?>", "");
		Pattern pattern = Pattern.compile(".*Latitude(-?\\d+\\.\\d+)Longitude(-?\\d+\\.\\d+).*");
		Matcher matcher = pattern.matcher(locationHtml);

		if(matcher.find()) {
			curLocation.put("Latitude", Double.parseDouble(matcher.group(1)));
			curLocation.put("Longitude", Double.parseDouble(matcher.group(2)));
		}

		return curLocation;
	}
}
