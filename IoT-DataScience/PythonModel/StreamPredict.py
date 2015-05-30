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

# Usage:
# Ipython StreamPredict.py Configuration/default.conf  TODO: replace with docopt

import json
import logging
import sys

import ConfigParser
import Data
import numpy as np
import pandas as pd

from datetime import datetime
from datetime import timedelta
from pandas import Series
from pandas import concat
from sklearn.externals import joblib
from stream import Processor
from toolz import dicttoolz

if len(sys.argv) > 1:
    configuration_string = sys.argv[1]
else:
    configuration_string = "Configuration/default.conf"

# Disable warnings
pd.set_option('mode.chained_assignment', None)

# Set Job Params
config = ConfigParser.ConfigParser()
config.read(configuration_string)

storedmodel_directory = config.get("Directories", "dir_storedmodel")
units = config.get("Streaming", "units")
fuel_capacity = config.get("Car", "fuel_capacity_liters")
mixing_length = float(config.get("Streaming", "mixing_length"))
smoothing_length = float(config.get("Streaming", "smoothing_length"))
cutoff = timedelta(minutes=int(config.get("Streaming", "cutoff")))

init_class_file = config.get("Batch", "init_class_file")
online_class_file = config.get("Batch", "online_class_file")
journey_cluster_file = config.get("Batch", "journey_cluster_file")

logging.basicConfig(level=logging.ERROR) # Log to the Spring XD console window

# Load models
init_class_models = joblib.load(storedmodel_directory + init_class_file)
online_class_models = joblib.load(storedmodel_directory + online_class_file)

# Load information from historical data
historical_journey_clusters = joblib.load(storedmodel_directory + journey_cluster_file)

end_locations = dicttoolz.valmap(lambda x: [journey_cluster.averages[["EndLat", "EndLong"]].values.tolist() for
                                 journey_cluster in x if journey_cluster.clusterID != -1], historical_journey_clusters)

mpg_insts = dicttoolz.valmap(lambda x: [journey_cluster.averages["MPG_from_MAF"] if "MPG_from_MAF" in journey_cluster.averages.keys() else float("NaN") for
                                        journey_cluster in x if journey_cluster.clusterID != -1], historical_journey_clusters)

cluster_ids = dicttoolz.valmap(lambda x: [str(journey_cluster.clusterID) for
                                          journey_cluster in x if journey_cluster.clusterID != -1], historical_journey_clusters)

journeys = {}
initial_predictions = {}
prob_dict = {}
time_dict = {}


def callback(body):
    try:
        output = json.loads(body)
        vin = output["vin"]

        # impute maf if missing
        if output["maf_airflow"] == "":
            # engine displacement in l
            ed = 1.8
            # volumetric efficiency
            ve = 0.9
            imap = output["rpm"] * output["intake_manifold_pressure"] / (output["intake_air_temp"] + 273.15)
            # 28.97 = mass of air
            # 8.314 = gas constant
            output["maf_airflow"] = (imap/120.) * ve * ed * (28.97/8.314)

        # impute fuel level if missing
        if output["fuel_level_input"] == "":  # TODO: take last value if it exists
            output["fuel_level_input"] = 50

        if vin in journeys:
            journey = journeys[vin]
            journey.load_streaming_data(body)
            time_dict[vin] = concat([time_dict[vin], Series(datetime.now())])
            if (journey.data["Time_GPS"].iloc[-1] - journey.data["Time_GPS"].iloc[-2]) > cutoff \
               or (time_dict[vin].iloc[-1] - time_dict[vin].iloc[-2]) > cutoff:
                journey = Data.Journey()
                journey.load_streaming_data(body)
                journeys[vin] = journey
        else:
            journey = Data.Journey()
            journey.load_streaming_data(body)
            journeys[vin] = journey

        # create initial prediction:
        counter = len(journey.data.index)
        if counter == 1:
            initial_predictions[vin] = init_class_models[vin].predict(journey)
            prob_dict[vin] = initial_predictions[vin]
            time_dict[vin] = Series(datetime.now())

        # create online prediction:
        online_prediction = online_class_models[vin].predict(journey)

        # mix initial and online prediction
        mixing_factor = min(counter/mixing_length, 1)
        probabilities = (1 - mixing_factor) * initial_predictions[vin] + mixing_factor * online_prediction

        if counter > 1:
            prob_dict[vin] = np.vstack((prob_dict[vin], probabilities))

        if counter > smoothing_length:
            smoothed_probabilities = np.mean(prob_dict[vin][(counter - smoothing_length + 1): (counter + 1), :], axis=0, keepdims=True)
        else:
            smoothed_probabilities = probabilities

        # calculate remaining range
        mpg = np.nansum(smoothed_probabilities * mpg_insts[vin])
        remaining_fuel_gallons = ((np.array(journey.data.FuelRemaining.tail(1), dtype="float64")/100 * float(fuel_capacity)) * 0.264172052)[0]

        if (units == "miles"):
            remaining_range = int(remaining_fuel_gallons * mpg)
        else:
            remaining_range = (remaining_fuel_gallons * mpg) * 1.609344

        # create cluster information
        container = zip(cluster_ids[vin], smoothed_probabilities[0], end_locations[vin], mpg_insts[vin])
        cluster_predictions = dict()
        for jid, prob, endloc, mpg in container:
            cluster_predictions[jid] = {"Probability": round(prob, 3), "EndLocation": endloc, "MPG_Journey": round(mpg, 5)}

        # put together final dict
        predictions = dict()
        predictions["ClusterPredictions"] = cluster_predictions
        predictions["RemainingRange"] = remaining_range

        output["Predictions"] = predictions
        output["mpg_instantaneous"] = journey.data.MPG_from_MAF.tail(1).values[0]
        if (units == "miles"):
            output["vehicle_speed"] = int(output["vehicle_speed"] * 0.621371)

        return json.dumps(output, ensure_ascii=False, encoding="utf-8")

    except ValueError as e:
        logging.exception("Got error %s with payload %s and output %s ", e, body, output)
    except TypeError as e:
        logging.exception("Got error %s with payload %s and output %s", e, body, output)
    except Exception as e:
        logging.exception("Got error %s with payload %s and output %s", e, body, output)

process = Processor()
process.start(callback)
