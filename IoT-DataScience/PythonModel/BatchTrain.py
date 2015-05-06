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
import sys
import json
from operator import add

import Models
import Data
from pyspark import SparkContext, SparkConf
from sklearn.externals import joblib


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
    storedmodel_directory = config.get("Directories", "dir_storedmodel")
    cluster_json_directory = config.get("Directories", "dir_clusters")

    # Parameters TODO: set these in config file
    # Clustering
    cluster_feature_names = ["StartLat", "StartLong", "EndLat", "EndLong"]  # Which features to cluster over
    clustering_alg = "KMeans"                                    # Which Clustering Algorithm to use
    cluster_model_file = config.get("Batch", "cluster_class_file")
    cluster_params = {"max_clusters": 10, "n_init": 10}           # Parameters for KMeans

    # Initial Classification
    init_class_alg = "RandomForest"
    init_class_model_file = config.get("Batch", "init_class_file")
    init_class_feature_names = ["StartLat", "StartLong"]  # Which features to cluster over

    # Online Classification
    online_class_alg = "RandomForest"
    online_class_model_file = config.get("Batch", "online_class_file")
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
    joblib.dump(journey_clusters_local, storedmodel_directory + cluster_model_file + "_JourneyClusters")
    cluster_json = journey_clusters.map(Data.extract_journey_json).collect()
    with open(cluster_json_directory + "clusters.json", "w") as f:
        for cluster in cluster_json:
            f.write(cluster + "\n")
    journey_clusters.unpersist()

    # Build initial classification models
    init_class_models = journeys_with_id.mapValues(lambda data: Models.train_init_class_model(init_class_alg, init_class_feature_names, data)).collectAsMap()
    joblib.dump(init_class_models, storedmodel_directory + init_class_model_file)

    # Build online classification models
    online_class_models = journeys_with_id.mapValues(lambda data: Models.train_online_class_model(online_class_alg, online_class_feature_names, data)).collectAsMap()
    joblib.dump(online_class_models, storedmodel_directory + online_class_model_file)

    sc.stop()

if __name__ == "__main__":
    main(sys.argv[1:])
