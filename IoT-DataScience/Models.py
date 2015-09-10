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
        self.model = None
        self.feature_names = None

    # go through batch data object and extract relevant features
    def create_train_features(self, data):
        features = pd.DataFrame()
        for journey in data:
            features = features.append(self.create_journey_features(journey))
        return features

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
        new_data = self.create_journey_features(journey)
        return self.model.predict_proba(new_data.values)

    def train(self, data, feature_names):
        raise Exception("NotImplementedException")

    def create_train_labels(self, data):
        labels = []
        for journey in data:
            labels.append(journey.journey_cluster_id)
        return labels

    def score(self, data, labels):
        cnt = Counter(labels)
        indexer = [cnt[x] > 2 for x in labels]
        features = create_train_features(data)[indexer].values
        labels = [x[0] for x in zip(labels, indexer) if x[1]]
        loo = cross_validation.KFold(len(labels), len(labels))
        xval = cross_validation.cross_val_score(self.model,
                                                features,
                                                np.array(labels),
                                                cv=loo)
        return xval


class InitClass_RF(InitClass):
    def __init__(self):
        InitClass.__init__(self)

    def train(self, data, feature_names):
        print "Creating InitClass_RF"
        self.feature_names = feature_names
        features = self.create_train_features(data)
        labels = self.create_train_labels(data)

        self.model = RandomForestClassifier(n_estimators=100,
                                            max_depth=None,
                                            random_state=0)
        self.model.fit(features, labels)


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

    def create_train_labels(self, data):
        labels = pd.Series()
        for journey in data:
            labels = labels.append(journey.data.drop(journey.data.index[journey.NArows]).journey_cluster_id)
        return labels

    def train(self, data, feature_names, params):
        raise Exception("NotImplementedException")

    def predict(self, journey):
        new_data = self.create_journey_features(journey)
        return self.model.predict_proba(new_data.tail(1))


# interface to be implemented by every online classification class
class OnlineClass_RF(OnlineClass):
    def __init__(self):
        OnlineClass.__init__(self)

    def train(self, data, feature_names):
        print "Creating OnlineClass_RF"
        self.feature_names = feature_names
        train_data = self.create_train_features(data)
        train_labels = self.create_train_labels(data)

        self.model = RandomForestClassifier(n_estimators=100, max_depth=None, random_state=0, max_features=None)
        self.model.fit(train_data, train_labels)


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
        features = self.create_train_features(data)

        kmeans_objects = []

        # try different numbers of clusters
        for i in range(2, max_clusters + 1):
            kmeans = KMeans(init="k-means++", n_clusters=i, n_init=n_init, random_state=0)
            kmeans.fit(features)
            kmeans_objects.append(kmeans)

        # get silhouette coefficients of all different cluster models
        silhouette_score = [metrics.silhouette_score(features, model.labels_, metric="euclidean") for model in
                            kmeans_objects]

        # save kmeans model with highest silhouette
        self.model = kmeans_objects[silhouette_score.index(max(silhouette_score))]

    def assign(self, data):
        features = self.create_train_features(data)
        predictions = self.model.predict(features)
        for i, journey in enumerate(data):
            journey.journey_cluster_id = predictions[i]
            journey.data["journey_cluster_id"] = predictions[i]
            data[i] = journey
        return data


def cluster(model_type, feature_names, params, processed_journeys):
    if model_type == "KMeans":
        cluster_model = Clust_KMeans()
    else:
        raise Exception("NotImplementedException")
    cluster_model.train(processed_journeys, feature_names, params)
    assigned_journeys = cluster_model.assign(processed_journeys)
    return assigned_journeys
