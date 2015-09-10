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
import sys

import Models
import Data
import ConfigParser
import pandas as pd
from sklearn.metrics import log_loss
import numpy as np


def main(args=None):
    if(args):
        ConfigurationString = args[0]
    else:
        ConfigurationString = 'Configuration/default.conf'

    Config = ConfigParser.ConfigParser()
    Config.read(ConfigurationString)

    # TODO: read number of folds and number of minutes from config

    storedmodel_directory = Config.get("Directories", "dir_storedmodel")

    # TODO: READ THESE PARAMETERS FROM SOMEWHERE
    InitClassFile = 'InitClass_RF'
    OnlineClassFile = 'OnlineClass_RF'
    JourneyClusterFile = 'KMeans_JourneyClusters'
    # TODO: add this to config

    # Load models
    InitClassModel = Models.Model.load(storedmodel_directory + InitClassFile)
    OnlineClassModel = Models.Model.load(storedmodel_directory +
                                         OnlineClassFile)

    # Load information from historical data
    HistoricalJourneyClusters = Data.JourneyCluster.load(storedmodel_directory
                                                         + JourneyClusterFile)

    # Filter out journeyClusters with only one journey
    clusterIDs = [journeyCluster.clusterID for
                  journeyCluster in HistoricalJourneyClusters if
                  journeyCluster.clusterID != -1 and
                  len(journeyCluster.journeys) >= 2]

    HistoricalJourneyClusters = [journeyCluster for
                                 journeyCluster in HistoricalJourneyClusters
                                 if journeyCluster.clusterID in clusterIDs]

    journeys = [journey.data for journeyCluster in HistoricalJourneyClusters for journey in journeyCluster.journeys]

    journey_attributes_list = [journeyCluster.allAttributes for journeyCluster in HistoricalJourneyClusters]
    journey_attributes = pd.DataFrame(journey_attributes_list[0])
    for item in journey_attributes_list[1:]:
        journey_attributes = journey_attributes.append(item)
    journey_attributes['journeyClusterID'] = journey_attributes.index

    n_minutes = 11
    # Leave-one-out cross-validation for now
    n_folds = len(journeys)
    mixing_length = 11  # number of minutes to factor in initial prediction
    # # Create list of data frames
    accuracies = pd.DataFrame(columns=['fold', 'minute', 'log_loss'])

    print len(journey_attributes)
    print len(journeys)
    print journey_attributes.columns

    # Do cross-validation TODO: factor out into a function
    for fold in range(n_folds):
        # Online model
        test_online = journeys[fold]
        train_online_journeys = journeys[:fold] + journeys[(fold + 1):]
        train_online = pd.DataFrame(journeys[0])
        for journey in train_online_journeys[1:]:
            train_online = train_online.append(journey)

        relevant_columns = OnlineClassModel.featureNames + ['journeyClusterID']
        train_online = train_online[relevant_columns].dropna()
        start_time = test_online['Time_GPS'].dropna().irow(0)
        test_online['Journey_Duration'] = test_online['Time_GPS'] - start_time
        test_online['Journey_Duration_Minutes'] = test_online['Journey_Duration'].apply(lambda x: np.floor(x/np.timedelta64(1,'m')))
        test_online = test_online.groupby('Journey_Duration_Minutes', as_index=False).first()

        if (len(test_online) < n_minutes):
            continue

        online_model = OnlineClassModel.model.fit(train_online[OnlineClassModel.featureNames].values, train_online['journeyClusterID'].values)

        # Offline model
        test_init = journey_attributes.iloc[fold]
        train_init = journey_attributes.iloc[:fold].append(journey_attributes.iloc[(fold + 1):])

        init_model = InitClassModel.model.fit(train_init[InitClassModel.featureNames].values, train_init['journeyClusterID'].values)

        mixing_factor = np.array(range(n_minutes))/float(mixing_length)
        mixing_factor[mixing_factor > 1] = 1
        mixing_factor = np.repeat(mixing_factor, len(clusterIDs), 0).reshape(n_minutes, len(clusterIDs))

        init_preds = np.tile(init_model.predict_proba(test_init[InitClassModel.featureNames].values), (n_minutes, 1))
        online_preds = online_model.predict_proba(test_online[OnlineClassModel.featureNames].values)[0:n_minutes,]
        pred = (1 - mixing_factor) * init_preds + mixing_factor * online_preds
        true_clusterID = test_online['journeyClusterID'].ix[0]
        response_dict = dict(zip(clusterIDs, range(len(clusterIDs))))
        response_matrix = np.zeros(n_minutes * len(clusterIDs)).reshape(n_minutes, len(clusterIDs))
        response_matrix[:,response_dict[true_clusterID]] = 1

        for minute in range(n_minutes):
            loss = log_loss(response_matrix[minute].reshape(1, len(clusterIDs)), pred[minute].reshape(1,len(clusterIDs)))
            accuracies = accuracies.append({'fold': fold, 'minute': minute, 'log_loss': loss}, ignore_index=True)

    accuracies.to_csv(storedmodel_directory + 'modelscore.csv')


if __name__ == "__main__":
    main(sys.argv[1:])
