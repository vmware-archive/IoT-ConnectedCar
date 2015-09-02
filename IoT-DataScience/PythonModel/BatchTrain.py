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
# ipython BatchTrain.py Configuration/default.conf

import ConfigParser
import json
import cStringIO
import cPickle as pickle
import sys

import Data
import Models
import redis

from operator import add
from pyspark import SparkContext, SparkConf


conf = SparkConf().setAppName("BatchTraining")
sc = SparkContext(conf=conf)


def main(args=None):
    # Setting up configuration TODO: use docopt
    if(args):
        configuration_string = args[0]
    else:
        configuration_string = "Configuration/default.conf"

    config = ConfigParser.ConfigParser()
    config.read(configuration_string)
    rawdata_directory = config.get("Directories", "dir_rawdata")

    # Set up Redis
    redis_host = config.get("Redis", "host")
    redis_port = int(config.get("Redis", "port"))
    redis_password = config.get("Redis", "password")
    # Connect to Redis
    r = redis.StrictRedis(host=redis_host, port=redis_port, password=redis_password)

    # Parameters TODO: set these in config file
    # Clustering
    cluster_feature_names = ["StartLat", "StartLong", "EndLat", "EndLong"]  # Which features to cluster over
    clustering_alg = "KMeans"                                    # Which Clustering Algorithm to use
    cluster_params = {"max_clusters": 10, "n_init": 10}           # Parameters for KMeans

    # Initial Classification
    init_class_alg = "RandomForest"
    init_class_feature_names = ["StartLat", "StartLong"]  # Which features to cluster over

    # Online Classification
    online_class_alg = "RandomForest"
    online_class_feature_names = ["Latitude", "Longitude", "StartLat", "StartLong"]

    # Read in batch data
    raw_data = sc.textFile(rawdata_directory)
    json_data = raw_data.map(lambda x: json.loads(x))
    pair_rdd = json_data.filter(bool).map(lambda x: (x["journey_id"], x))

    raw_journeys = pair_rdd.combineByKey(lambda value: [value], lambda acc, value: acc + [value], lambda acc1, acc2: add(acc1, acc2))
    processed_journeys = raw_journeys.mapValues(lambda x: Data.load_batch_data(x))

    journeys = processed_journeys.map(lambda x: (x[1].data["vin"][0], x[1]))
    journeys_by_vin = journeys.combineByKey(lambda value: [value], lambda acc, value: acc + [value], lambda acc1, acc2: add(acc1, acc2))

    # Build, assign and save clusters
    journeys_with_id = journeys_by_vin.mapValues(lambda data: Models.cluster(clustering_alg, cluster_feature_names, cluster_params, data))
    journeys_with_id.persist()
    journey_clusters = journeys_with_id.mapValues(lambda journeys: Data.create_journey_clusters(journeys)).persist()
    journey_clusters_local = journey_clusters.collectAsMap()
    r.set("journey_clusters", pickle.dumps(journey_clusters_local))
    cluster_json = journey_clusters.map(Data.extract_journey_json).collect()
    output = cStringIO.StringIO()
    with open(output, "w") as f:
        for cluster in cluster_json:
            f.write(cluster + "\n")
    journey_clusters.unpersist()
    r.set("clusters_json", output.getvalue())
    output.close()

    # Build initial classification models
    init_class_models = journeys_with_id.mapValues(lambda data: Models.train_init_class_model(init_class_alg, init_class_feature_names, data)).collectAsMap()
    r.set("init_models", pickle.dumps(init_class_models))
    output.close()

    # Build online classification models
    online_class_models = journeys_with_id.mapValues(lambda data: Models.train_online_class_model(online_class_alg,
                                                                                                  online_class_feature_names,
                                                                                                  data)).collectAsMap()
    r.set("online_models", pickle.dumps(online_class_models))
    output.close()

    sc.stop()

if __name__ == "__main__":
    main(sys.argv[1:])
