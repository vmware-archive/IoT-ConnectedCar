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

import copy
import glob
import json
import random

from operator import add
from operator import sub
from datetime import datetime
from uuid import uuid4

import numpy as np
import pandas as pd

from IPython import embed

n_sim = 20
directory = "/Users/ronert/Dropbox/Pivotal/IoT-ConnectedCar/data/Munich/" # INPUT
write_directory = "/Users/ronert/Dropbox/Pivotal/IoT-ConnectedCar/data/Munich_Simulated/" # OUTPUT
origin = "out" # Filename pattern: try "out"
origin_long = 11.589479 # Set to first line on the drive you will simulate
origin_lat = 48.129505

filenames = glob.glob(directory + "*.out")


def smear(journey):
    days = np.random.poisson(lam=10)
    minutes = np.random.poisson(lam=720)
    ops = (add, sub)
    op = random.choice(ops)

    longs = np.array([float(line["longitude"]) for line in journey])
    diff_long = np.diff(longs)
    sd_long = diff_long.std()

    lats = np.array([float(line["latitude"]) for line in journey])
    diff_lat = np.diff(lats)
    sd_lat = diff_lat.std()

    for c, obs in enumerate(journey):
        if c == 0 and origin in filename:
            # Make all journeys start from the same coordinates for demo purposes
            obs["longitude"] = origin_long
            obs["latitude"] = origin_lat
        else:
            # Smear GPS coordinates
            obs["longitude"] = float(obs["longitude"]) + np.random.uniform(-0.002, sd_long*0.002)
            obs["latitude"] = float(obs["latitude"]) + np.random.uniform(-0.002, sd_lat*0.002)

        # Add or subtract random number of days and minutes from timestamp
        obs["timestamp"] = op(pd.to_datetime(obs["timestamp"], unit="ms"), np.timedelta64(minutes, "m")) - np.timedelta64(days, "D")
        epoch = unix_time(obs["timestamp"])
        obs["timestamp"] = int(epoch)

        # Remove Predictions
        if "Predictions" in obs:
            del obs["Predictions"]

        # Add journey_id
        if c == 0:
            journey_id = uuid4().hex

        obs['journey_id'] = journey_id

        journey[c] = obs

    return journey


def sim_n_times(orig_journey, filename):
    for n in range(0, n_sim):
        journey = copy.deepcopy(orig_journey)
        journey = smear(journey)
        new_filename = filename.split("/")[-1].split(".")[0]+"("+str(n+1)+")"+"_Simulated.out"
        dlist = [json.dumps(record) + "\n" for record in journey]
        open(write_directory + new_filename, "w").writelines(dlist)
        print "%s of %s journeys simulated" % (n + 1, n_sim)


def unix_time(dt):
    epoch = datetime.utcfromtimestamp(0)
    delta = dt - epoch
    return delta.total_seconds() * 1000.0


for c, filename in enumerate(filenames):
    with open(filename, "r") as file:
        journey = [json.loads(line) for line in file]
    sim_n_times(journey, filename)
    print "%s of %s files simulated" % (c + 1, len(filenames))
