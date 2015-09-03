#
# Copyright 2014-2015 the original author or authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#


# Generic class to hold the log data
import glob
import json
from collections import OrderedDict
from datetime import datetime

import numpy as np
import pandas as pd
from pandas import parser


class FileException(Exception):
    pass


class Journey:
    # Initialise all vars
    def __init__(self):
        self.data = pd.DataFrame()
        self.attributes = dict()
        self.datadict = dict()
        self.journeyID = -1
        self.definedict()

    def definedict(self):
        self.datadict["Longitude"] = "Longitude"
        self.datadict["Latitude"] = "Latitude"
        self.datadict["LONGITUDE"] = "Longitude"
        self.datadict["longitude"] = "Longitude"
        self.datadict["LATITUDE"] = "Latitude"
        self.datadict["latitude"] = "Latitude"
        self.datadict[u"GPS Longitude(\xe5\xa1)"] = "GPSLongitude"
        self.datadict[u"GPS Longitude(\xc2\xb0)"] = "GPSLongitude"
        self.datadict[u"GPS Longitude(\xb0)"] = "GPSLongitude"
        self.datadict[u"GPS Longitude(\xc2)"] = "GPSLongitude"
        self.datadict[u"GPS Latitude(\xe5\xa1)"] = "GPSLatitude"
        self.datadict[u"GPS Latitude(\xc2\xb0)"] = "GPSLatitude"
        self.datadict[u"GPS Latitude(\xb0)"] = "GPSLatitude"
        self.datadict[u"GPS Latitude(\xc2)"] = "GPSLatitude"
        self.datadict[u"GPS Bearing(\xe5\xa1)"] = "GPSBearing"
        self.datadict[u"GPS Bearing(\xc2\xb0)"] = "GPSBearing"
        self.datadict["GPS Accuracy(m)"] = "GPSAccuracy"
        self.datadict["Altitude"] = "Altitude"
        self.datadict["Altitude"] = "Altitude"
        self.datadict["GPS Altitude(m)"] = "GPSAltitude"
        self.datadict["Bearing"] = "Bearing"
        self.datadict["bearing"] = "Bearing"
        self.datadict["GPS Satellites"] = "GPSSatellites"
        self.datadict["VIN"] = "VIN"

        # Time
        self.datadict["GPS Time"] = "Time_GPS"
        self.datadict["timestamp"] = "Time_GPS"
        self.datadict["Device Time"] = "Time_Device"
        self.datadict["Trip Time(Since journey start)(s)"] = "TripTime"
        self.datadict["Run time since ngine start(s)"] = "RunTime"
        self.datadict["time_since_engine_start"] = "RunTime"
        self.datadict["Trip time(whilst moving)(s)"] = "TripTime_moving"
        self.datadict["Trip time(whilst stationary)(s)"] = "TripTime_stationary"

        # Distance
        self.datadict["Distance travelled with MIL/CEL lit(km)"] = "Distance_MilCel"
        self.datadict["distance_with_mil_on"] = "Distance_MilCel"
        self.datadict["Distance travelled since codes cleared(km)"] = "TotalDistance"
        self.datadict["Trip distance (stored in vehicle profile)(km)"] = "TripDistance"
        self.datadict["Trip Distance(km)"] = "TripDistanceKm"

        # Speed
        self.datadict["Average trip speed(whilst stopped or moving)(km/h)"] = "Speed_av"
        self.datadict["GPS Speed (Meters/second)"] = "Speed_GPS_m"
        self.datadict["SPEED(MPH)"] = "Speed_mph"      
        self.datadict["GPS Speed(km/h)"] = "Speed_GPS_km1"
        self.datadict["Speed (GPS)(km/h)"] = "Speed_GPS_km2"
        self.datadict["Speed (OBD)(km/h)"] = "Speed_OBD_km"
        self.datadict["vehicle_speed"] = "Speed_OBD_km"
        self.datadict["GPS vs OBD Speed difference(km/h)"] = "Speed_GPSOBD_diff"

        # Acceleration
        self.datadict["Trip average Litres/100 KM(l/100km)"] = "TripAvLPerKm"
        self.datadict["G(x)"] = "Gx"
        self.datadict["G(y)"] = "Gy"
        self.datadict["G(z)"] = "Gz"
        self.datadict["G(calibrated)"] = "Gcal"
        self.datadict["Acceleration Sensor(Total)(g)"] = "Accel"
        self.datadict["acceleration"] = "Accel"
        self.datadict["Acceleration Sensor(X axis)(g)"] = "Accel_x"
        self.datadict["Acceleration Sensor(Y axis)(g)"] = "Accel_y"
        self.datadict["Acceleration Sensor(Z axis)(g)"] = "Accel_z"
        self.datadict["Accelerator PedalPosition D(%)"] = "PedalPosition_D"
        self.datadict["Accelerator PedalPosition E(%)"] = "PedalPosition_E"
        self.datadict["Engine Load(%)"] = "EngineLoad"
        self.datadict["engine_load"] = "EngineLoad"
        self.datadict["Horsepower (At the wheels)(hp)"] = "Horsepower"
        self.datadict["Torque(Nm)"] = "Torque"
        self.datadict["Relative Throttle Position(%)"] = "ThrottlePos_rel"
        self.datadict["relative_throttle_position"] = "ThrottlePos_rel"
        self.datadict["Throttle Position(Manifold)(%)"] = "ThrottlePos_manifold"
        self.datadict["throttle_position"] = "ThrottlePos_manifold"
        self.datadict["Engine RPM(rpm)"] = "RPM"
        self.datadict["RPM"] = "RPM"
        self.datadict["rpm"] = "RPM"
        self.datadict["absolute_throttle_pos_b"] = "AbsoluteThrottlePos_B"
        self.datadict["accelerator_throttle_pos_d"] = "AcceleratorThrottlePos_D"
        self.datadict["accelerator_throttle_pos_e"] = "AcceleratorThrottlePos_E"

        # Fuel consumption rate
        self.datadict["Fuel flow rate/minute(cc/min)"] = "FuelFlow_permin"
        self.datadict["Fuel flow rate/hour(l/hr)"] = "FuelFlow_perhr"
        self.datadict["Miles Per Gallon(Long Term Average)(mpg)"] = "MPG_av"
        self.datadict["Miles Per Gallon(Instant)(mpg)"] = "MPG_inst"
        self.datadict["Trip average KPL(kpl)"] = "KPL_av_trip"
        self.datadict["Trip average MPG(mpg)"] = "MPG_av_trip"
        self.datadict["Fuel Rail Pressure(psi)"] = "FuelPressure"
        self.datadict["Kilometers Per Litre(Long Term Average)(kpl)"] = "KmPerL"
        self.datadict["Kilometers Per Litre(Instant)(kpl)"] = "KmPerL_inst"
        self.datadict["Fuel cost (trip)(cost)"] = "FuelCost"
        self.datadict["Litres Per 100 Kilometer(Long Term Average)(l/100km)"] = "LPerKm"
        self.datadict["short_term_fuel"] = "ShortTermFuelTrim"
        self.datadict["long_term_fuel"] = "LongTermFuelTrim"
        self.datadict["fuel_system_status"] = "FuelSystemStatus"

        # Remaining fuel or distance
        self.datadict["Fuel used (trip)(l)"] = "FuelUsed"
        self.datadict["Distance to empty (Estimated)(km)"] = "DistanceRemaining"
        self.datadict["Fuel Remaining (Calculated from vehicle profile)(%)"] = "FuelRemaining"
        self.datadict["fuel_level_input"] = "FuelRemaining"
        self.datadict["Engine kW (At the wheels)(kW)"] = "EnginePower"

        # Misc
        self.datadict["Horizontal Dilution of Precision"] = "PrecisionDilution"
        self.datadict["Barometer (on Android device)(mb)"] = "Barometer"
        self.datadict["Barometric pressure (from vehicle)(psi)"] = "BarometricPressure"
        self.datadict["barometric_pressure"] = "BarometricPressure"
        self.datadict["EGR Commanded(%)"] = "EGR"
        self.datadict["EGR Error(%)"] = "EGR_err"
        self.datadict["Mass Air Flow Rate(g/s)"] = "AirFlow"
        self.datadict["maf_airflow"] = "AirFlow"
        self.datadict["Air Fuel Ratio(Measured)(:1)"] = "AirFuelRatio"
        self.datadict["Turbo Boost & Vacuum Gauge(psi)"] = "TurboGauge"
        self.datadict[u"Ambient air temp(\xe5\xa1)"] = "AirTemp"
        self.datadict[u"Ambient air temp(\xc2\xb0)"] = "AirTemp"
        self.datadict[u"Ambient air temp(\xc2\xb0C)"] = "AirTemp"
        self.datadict[u"Ambient air temp(\xb0C)"] = "AirTemp"
        self.datadict[u"Intake Air Temperature(\xb0C)"] = "IntakeAirTemp"
        self.datadict[u"Intake Air Temperature(\xe5\xa1)"] = "IntakeAirTemp"
        self.datadict[u"Intake Air Temperature(\xc2\xb0)"] = "IntakeAirTemp"
        self.datadict[u"Intake Air Temperature(\xc2\xb0C)"] = "IntakeAirTemp"
        self.datadict[u"intake_air_temp"] = "IntakeAirTemp"
        self.datadict["Intake Manifold Pressure(psi)"] = "IntakePressure"
        self.datadict["intake_manifold_pressure"] = "IntakePressure"
        self.datadict[u"Engine Coolant Temperature(\xe5\xa1)"] = "EngineCoolantTemp"
        self.datadict[u"Engine Coolant Temperature(\xc2\xb0)"] = "EngineCoolantTemp"
        self.datadict[u"Engine Coolant Temperature(\xc2\xb0C)"] = "EngineCoolantTemp"
        self.datadict[u"coolant_temp"] = "EngineCoolantTemp"
        self.datadict["Volumetric Efficiency (Calculated)(%)"] = "VolumetricEfficiency"
        self.datadict["Voltage (OBD Adapter)(V)"] = "Voltage_OBD"
        self.datadict["Voltage (Control Module)(V)"] = "Voltage_Control"
        self.datadict["control_module_voltage"] = "Voltage_Control"
        self.datadict["O2 Sensor1 wide-range Voltage"] = "O2Voltage"
        self.datadict["O2 Sensor1 Equivalence Ratio"] = "O2Ratio"
        self.datadict["catalyst_temp"] = "CatalystTemp"
        self.datadict["obd_standards"] = "OBDStandards"

    def replace_columns(self, df):
        df = df.rename(columns=lambda x: x.strip())
        for i in range(0, len(df.columns.values)):
            if (df.columns.values[i] in self.datadict.keys()):
                newname = self.datadict.get(df.columns.values[i])
                df = df.rename(columns={df.columns.values[i]: newname})
        return df

    def transform_data(self, stream=False):
        if len(np.where(self.data.Time_GPS.notnull())[0]) == 0:
            raise FileException("Warning: GPS time column empty. Object discarded.")

        firstIndex = np.where(self.data.Time_GPS.notnull())[0][0]
        lastIndex = np.where(self.data.Time_GPS.notnull())[0][-1]

        # create and transform columns

        # time columns
        if stream:
            self.data["Time_GPS"].iloc[-1] = pd.to_datetime(self.data["Time_GPS"].iloc[-1], unit="ms")
        else:
            self.data["Time_GPS"] = pd.to_datetime(self.data["Time_GPS"], unit="ms")

        self.data["Day"] = self.data.Time_GPS.apply(datetime.weekday)
        if (self.data.columns.values == "Speed_OBD_km").any():
            self.data["Speed_OBD_km"] = self.data.Speed_OBD_km.convert_objects(convert_numeric=True)
        if (self.data.columns.values == "AirFlow").any():
            self.data["AirFlow"] = self.data.AirFlow.convert_objects(convert_numeric=True)
        self.data["MPG_from_MAF"] = self.maf_to_mpg(self.data["AirFlow"], self.data["Speed_OBD_km"])
        self.data["Latitude"] = self.data.Latitude.convert_objects(convert_numeric=True)
        self.data["Longitude"] = self.data.Longitude.convert_objects(convert_numeric=True)

        self.attributes["StartTime"] = self.data.Time_GPS.irow(firstIndex)
        self.attributes["EndTime"] = self.data.Time_GPS.irow(lastIndex)
        midnight = self.attributes["StartTime"].replace(hour=0, minute=0, second=0, microsecond=0)
        self.data["MinutesSinceMidnight"] = (self.data["Time_GPS"] - midnight) / np.timedelta64(1, "m")
        self.attributes["Day"] = self.data.Time_GPS.irow(firstIndex).weekday()
        self.attributes["Month"] = self.data.Time_GPS.irow(firstIndex).month
        self.attributes["StartMinutesSinceMidnight"] = (self.attributes["StartTime"] - midnight).seconds / 60
        self.attributes["JourneyTime"] = (self.attributes["EndTime"] - self.attributes["StartTime"]).seconds
        self.data["TravelTime"] = self.data["Time_GPS"] - self.attributes["StartTime"]

        # location columns
        self.attributes["StartLat"] = self.data.Latitude.convert_objects(convert_numeric=True).irow(firstIndex)
        self.attributes["StartLong"] = self.data.Longitude.convert_objects(convert_numeric=True).irow(firstIndex)
        self.attributes["EndLat"] = self.data.Latitude.convert_objects(convert_numeric=True).irow(lastIndex)
        self.attributes["EndLong"] = self.data.Longitude.convert_objects(convert_numeric=True).irow(lastIndex)
        self.data["StartLong"] = self.data.Longitude.convert_objects(convert_numeric=True).irow(firstIndex)
        self.data["StartLat"] = self.data.Latitude.convert_objects(convert_numeric=True).irow(firstIndex)

        # # This code returns the number of journeys in a file, however all files loaded thus far have contained only 1 journey, with a gap between journeys of 30 minutes.
        # time_gap = 30
        # TD = self.data["Time_GPS"].diff()
        # count = np.size(np.where(TD >=  np.timedelta64(time_gap, "m")))
        # print "The number of journeys in this file is: " + str(count+1)

        if (self.data.columns.values == "TripDistance").any():
            self.attributes["JourneyDist"] = self.data.TripDistance.convert_objects(convert_numeric=True).irow(lastIndex)
        if (self.data.columns.values == "Altitude").any():
            self.attributes["AltitudeDiff"] = float(
                self.data.Altitude.convert_objects(convert_numeric=True).irow(lastIndex)) - float(
                self.data.Altitude.convert_objects(convert_numeric=True).irow(firstIndex))
        if (self.data.columns.values == "Speed_av").any():
            self.attributes["AvSpeed"] = float(self.data.Speed_av.convert_objects(convert_numeric=True).irow(lastIndex))
        if (self.data.columns.values == "Speed_GPS_m").any():
            self.attributes["MaxSpeed"] = max(self.data.Speed_GPS_m.convert_objects(convert_numeric=True))
        if (self.data.columns.values == "TripTime_stationary").any():
            self.attributes["TimeStopped"] = self.data.TripTime_stationary.convert_objects(convert_numeric=True).irow(lastIndex)
        if (self.data.columns.values == "TripTime_moving").any():
            self.attributes["TimeMoving"] = self.data.TripTime_moving.convert_objects(convert_numeric=True).irow(lastIndex)
        if (self.data.columns.values == "Accel").any():
            self.attributes["AvAccel"] = self.data.Accel.convert_objects(convert_numeric=True).mean()
            self.attributes["MaxAccel"] = self.data.Accel.convert_objects(convert_numeric=True).max()

        # TODO: what happens when we are refuelling??
        if (self.data.columns.values == "FuelUsed").any() and self.data.FuelUsed.convert_objects(convert_numeric=True).dtype != "object":
            self.attributes["FuelStart"] = self.data.FuelUsed.convert_objects(convert_numeric=True).irow(firstIndex)
            self.attributes["FuelEnd"] = self.data.FuelUsed.convert_objects(convert_numeric=True).irow(lastIndex)
            self.attributes["FuelConsumption"] = self.attributes["FuelEnd"] - self.attributes["FuelStart"]
        # if (self.data.columns.values == "FuelFlow_permin").any():
        #     self.attributes["AvFuelRate"] = self.data.FuelFlow_permin.convert_objects(convert_numeric=True).mean()
        #     self.attributes["MaxFuelRate"] = self.data.FuelFlow_permin.convert_objects(convert_numeric=True).max()

        if (self.data.columns.values == "MPG_from_MAF").any():
            self.attributes["MPG_from_MAF"] = self.data.MPG_from_MAF.convert_objects(convert_numeric=True).mean()
        else:
            print "MPG_from_MAF is empty! :("

    # Calculate MPG inst. from MAF and velocity
    def maf_to_mpg(self, maf, velocity):
        mpg_inst = 7.718 * velocity/maf
        return mpg_inst

        # Read in data and assign variables and remove anomalies.
    def load_data(self, raw_data, id):
        self.journeyID = id
        self.data = pd.DataFrame()
        for blob in raw_data:
            self.data = self.data.append(blob, ignore_index=True)

        self.data["timestamp"] = self.data["timestamp"].astype(int)
        self.data.sort(columns="timestamp", inplace=True)

        # replace column names with standardized column names
        self.data = self.replace_columns(self.data)

        # removing column headers in the middle of the data, by checking for data type of "Latitude" column.
        def isfloat(value):
            try:
                float(value)
                return True
            except ValueError:
                return False

        if self.data["Latitude"].dtype != "float64":
            IsFloat = self.data["Latitude"].apply(isfloat)
            print "Dropping rows " + str(self.data.index[np.where(IsFloat == False)[0]])
            self.data = self.data.drop(self.data.index[np.where(IsFloat == False)[0]])

        # Defining a list of column headers
        columns = list(self.data.columns.values)

        # Add columns to this list that you want to convert to numeric.
        columns_to_convert = ["Speed_OBD_km", "KmPerL_inst", "LPerKm", "Speed_GPS_m", "FuelRemaining"]

        for column in columns_to_convert:
            if (self.data.columns.values == column).any():
                self.data[column] = self.data[column].convert_objects(convert_numeric=True)

        # filtering anomalous data.
        for y in columns:
            if (y != "Time_GPS" and ((self.data[y].dtype == "float64") or (self.data[y].dtype == "int64"))):
                self.data[y][self.data[y] >= 1e+10] = float("NaN")
        if (self.data.columns.values == "MPG_inst").any():
            self.data["MPG_inst"][self.data["MPG_inst"] >= 250] = float("NaN")
        # dropping anomalous rows from dataframe.
        self.data = self.data.dropna(how="all")

        # extract attributes and add some data transformations
        self.transform_data()

    # Read in data and assign variables
    def load_streaming_data(self, input_json):
        for k in input_json:
            if input_json[k] == "":
                input_json[k] = np.nan
        if ("fuel_system_status" in input_json):
            input_json["fuel_system_status"] = str(input_json["fuel_system_status"])
        self.data = self.data.append(self.replace_columns(pd.DataFrame(input_json, index=[0])))

        # Add columns to this list that you want to convert to numeric.
        columns_to_convert = ["FuelRemaining"]

        for column in columns_to_convert:
            if (self.data.columns.values == column).any():
                self.data[column] = self.data[column].convert_objects(convert_numeric=True)

        # extract attributes and add some data transformations
        self.transform_data(stream=True)


def load_batch_data(raw_journey):
    try:
        id = next(x["journey_id"] for x in raw_journey)
        journey = Journey()
        journey.load_data(raw_journey, id)
        return journey
    except FileException:
        print "Discarding corrupt journey:" + id
        return None


def create_journey_clusters(journeys):
    # find all clusterIDs which are available
    clusterIDs = [journey.journey_cluster_id for journey in journeys]
    clusterIDset = set(clusterIDs)
    # build journey cluster from all journeys
    journey_clusters = []
    # build journey clusters from found clusters
    for clusterID in clusterIDset:
        journeys_in_cluster = [journey for journey in journeys if journey.journey_cluster_id == clusterID]
        journey_cluster = JourneyCluster(journeys_in_cluster, clusterID)
        journey_clusters.append(journey_cluster)

    return journey_clusters


class JourneyCluster:
    # Initialise all vars
    def __init__(self, journeys, clusterID):
        self.journeyIDs = [journey.journeyID for journey in journeys]
        self.journeys = journeys
        self.clusterID = clusterID
        self.averages = pd.DataFrame()
        self.create_journey_stats(journeys)

    def create_journey_stats(self, journeys):
        self.all_attributes = pd.DataFrame()
        for journey in journeys:
            self.all_attributes = self.all_attributes.append(pd.DataFrame(journey.attributes, index=[self.clusterID]))
            self.averages = self.all_attributes.mean(axis=0)


def extract_journey_json(tup):
    vin, clusters = tup
    result = OrderedDict()
    clusters_dict = OrderedDict()
    for cluster in clusters:
        cluster_dict = OrderedDict()
        cluster_dict["name"] = ""
        cluster_dict["lat"] = cluster.averages["EndLat"]
        cluster_dict["long"] = cluster.averages["EndLong"]
        cluster_dict["address"] = ""
        clusters_dict[str(cluster.clusterID)] = cluster_dict
    result["vin"] = str(vin)
    result["clusters"] = clusters_dict
    return json.dumps(result)
