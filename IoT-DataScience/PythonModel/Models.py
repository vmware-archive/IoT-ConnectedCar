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
from collections import Counter

import numpy as np
import pandas as pd
from sklearn import cross_validation
from sklearn import metrics
from sklearn import neighbors
from sklearn.cluster import KMeans as KMeans
from sklearn.ensemble import RandomForestClassifier
from sklearn.externals import joblib
from sklearn.grid_search import GridSearchCV


class Model:
    def __init__(self):
        self.data = None
        self.model = None
        self.feature_names = None
        self.input_data = None
        self.output_data = None

    # go through batch data object and extract relevant features
    def create_train_features(self, data):
        self.data = data
        self.input_data = pd.DataFrame()

        for journey in self.data:
            self.input_data = self.input_data.append(self.create_journey_features(journey))

    def create_journey_features(self, journey):
        return pd.DataFrame(dict([(k, journey.attributes.get(k)) for k in self.feature_names]),
                            index=[journey.journeyID])


###########################################################
# HERE BEGINS DEFINITION OF INITIAL CLASSIFICATION MODELS #
###########################################################
def train_init_class_model(model_type, feature_names, data):
    if model_type == "RandomForest":
        init_model = InitClass_RF()
    else:
        raise Exception("NotImplementedException")
    init_model.train(data, feature_names)
    return init_model


# Interface to be implemented by every initial classification class
class InitClass(Model):
    def __init__(self):
        Model.__init__(self)

    def predict(self, journey):
        input = self.create_journey_features(journey)
        return self.model.predict_proba(input.values)

    def train(self, data, feature_names):
        raise Exception("NotImplementedException")

    def create_train_label(self):
        self.output_data = []
        for journey in self.data:
            self.output_data.append(journey.journey_cluster_id)

    def score(self):
        cnt = Counter(self.output_data)
        indexer = [cnt[x] > 2 for x in self.output_data]
        features = self.input_data[indexer].values
        labels = [x[0] for x in zip(self.output_data, indexer) if x[1]]
        loo = cross_validation.KFold(len(labels), len(labels))
        self.xval = cross_validation.cross_val_score(self.model,
                                                     features,
                                                     np.array(labels),
                                                     cv=loo)


class InitClass_RF(InitClass):
    def __init__(self):
        InitClass.__init__(self)

    def train(self, data, feature_names):
        print "Creating InitClass_RF"
        self.feature_names = feature_names
        self.create_train_features(data)
        self.create_train_label()

        self.model = RandomForestClassifier(n_estimators=100,
                                            max_depth=None,
                                            random_state=0)
        self.model.fit(self.input_data.values, self.output_data)


########################################################
# HERE BEGINS DEFINITION OF ONLINE CLASSIFICATION MODELS
########################################################
def train_online_class_model(model_type, feature_names, data):
    if model_type == "RandomForest":
        online_model = OnlineClass_RF()
    else:
        raise Exception("NotImplementedException")
    online_model.train(data, feature_names)
    return online_model


# interface to be implemented by every online classification class
class OnlineClass(Model):
    def __init__(self):
        Model.__init__(self)

    def create_journey_features(self, journey):
        journey.NArows = pd.isnull(journey.data[self.feature_names]).any(1).nonzero()[0]
        return journey.data.drop(journey.data.index[journey.NArows])[self.feature_names]

    def create_train_label(self):
        self.output_data = pd.Series()
        for journey in self.data:
            self.output_data = self.output_data.append(journey.data.drop(journey.data.index[journey.NArows]).journey_cluster_id)

    def train(self, data, feature_names, params):
        raise Exception("NotImplementedException")

    def predict(self, journey):
        input = self.create_journey_features(journey)
        return self.model.predict_proba(input.tail(1))


# interface to be implemented by every online classification class
class OnlineClass_RF(OnlineClass):
    def __init__(self):
        OnlineClass.__init__(self)

    # def __init__(self):
    #     OnlineClass.__init__(self)
    #     print "creating online rf class"

    def train(self, data, feature_names):
        print "Creating OnlineClass_RF"
        self.feature_names = feature_names
        self.create_train_features(data)
        self.create_train_label()

        self.model = RandomForestClassifier(n_estimators=100, max_depth=None, random_state=0, max_features=None)
        self.model.fit(self.input_data.values, self.output_data)


################################################
# HERE BEGINS DEFINITION OF CLUSTERING MODELS
################################################

# interface to be implemented by every clustering model class
class Clustering(Model):
    def __init__(self):
        Model.__init__(self)

    def train(self, data, feature_names, params):
        raise Exception("NotImplementedException")

    def assign(self):
        raise Exception("NotImplementedException")


# KMeans Clustering
class Clust_KMeans(Clustering):
    def __init__(self):
        Clustering.__init__(self)

    def train(self, data, feature_names, params):
        # parameter extraction
        max_clusters = min(params["max_clusters"], len(data) - 1)
        n_init = params["n_init"]

        self.feature_names = feature_names
        self.create_train_features(data)

        kmeans_objects = []

        # try different numbers of clusters
        for i in range(2, max_clusters + 1):
            kmeans = KMeans(init="k-means++", n_clusters=i, n_init=n_init, random_state=0)
            kmeans.fit(self.input_data)
            kmeans_objects.append(kmeans)

        # get silhouette coefficients of all different cluster models
        silhouette_score = [metrics.silhouette_score(self.input_data, model.labels_, metric="euclidean") for model in
                            kmeans_objects]

        # save kmeans model with highest silhouette
        self.model = kmeans_objects[silhouette_score.index(max(silhouette_score))]

    def assign(self, journeys):
        predictions = self.model.predict(self.input_data)
        for i, journey in enumerate(journeys):
            journey.journey_cluster_id = predictions[i]
            journey.data["journey_cluster_id"] = predictions[i]
            journeys[i] = journey
        return journeys


def cluster(model_type, feature_names, params, processed_journeys):
    if model_type == "KMeans":
        cluster_model = Clust_KMeans()
    else:
        raise Exception("NotImplementedException")

    cluster_model.train(processed_journeys, feature_names, params)
    assigned_journeys = cluster_model.assign(processed_journeys)
    return assigned_journeys
