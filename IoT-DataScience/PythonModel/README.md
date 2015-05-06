# Data Science for Journey and Fuel Consumption Prediction

### Overview

The Data Science part of the Connected Car Demo predicts which journey you are likely to take and based on that, how much fuel you will likely consume.

The code runs in 2 steps:

1. *Batch Training:* Train the models on all available data in batch to condense all important information into the model objects.
2. *Stream Prediction:* Point a RabbitMQ JSON stream to the stream prediction function to get updated online predictions of journey and fuel consumption.

---

### How do I run this stuff?

1. Edit the default configuration file (or create your own) so the [Directories] point to your working environment
2. cd to inside the PythonModel directory
3. Call the python Scripts files.


The python scripts can be called like so:

#### *Batch Training* is done by BatchTrain.py

*Usage*:
`python BatchTrain.py [path to configuration file]`

*Example*:
`python BatchTrain.py Configuration/default.conf`


#### *Stream Prediction* is done by StreamPredict.py

In order for stream prediction to work, you have to point a RabbitMQ JSON stream at it. You can use the example in ../StreamMockup, which is being run like this (note this requires a running RabbitMQ server):

`python StreamMockup/rabbitmq.py Data/Historical/trackLog-2014-Mai-23_22-02-57.csv localhost receive`

The stream predict function emits the predictions to another RabbitMQ queue you can specificy in the configuration.

When this is streaming data via RabbitMQ, you can call the stream prediction function like this:

*Usage*:
`python StreamPredict.py [path to configuration file]`

*Example*:
`python StreamPredict.py Configuration/default.conf`

---

### Configuration

Configuration/default.conf
Default configuration which contains the following parameters for running which can be modified for the current environment

Directories:
- dir_rawdata: Path to the raw files
- dir_storedmodel: Path to the location of the stored model (output of training and input of prediction)
- source: JSON or CSV

Batch:
cluster_class_file: Cluster model name
init_class_file: Init model name
online_class_file: Online model name
journey_cluster_file: Clustering model name

Streaming:
- host: The RabbitMQ host
- receiver_queue: The queue that streams raw json data into StreamPredict.py
- emitter_queue: The queue that StreamPredict.py sends the predictions to
- units: Units to emit (miles or km)
- mixing_length: Length of time steps to blend initial model with online model

Car:
- fuel_type: petrol or diesel
- fuel_capacity_liters: fuel tank capacity in liters


*DEPRECATED:*
- dir_newdata: Path to incoming streaming data
- dir_predictions: Path to where the output of the prediction script will be written
- file_newdata: Name of incoming streaming data file
- file_predictions: Name of output prediction file
- ModelName: Name of model (when written to dir_storedmodel)
- ModelType: Type of model (linear regression, constant, etc…)

---

### Code Structure

#### Data.BatchData
Class to handle data loading from a directory. Contains all journeys collected from that directory.


#### Data.Journey
Class to represent a single journey. Holds the raw data as well as some other characteristics of a single journey, such as:

- JourneyID
- Day
- Month
- StartTime
- EndTime
- JourneyTime
- StartLoc
- EndLoc
- JourneyDist
- AltitudeDiff
- AvSpeed
- MaxSpeed
- TimeStopped
- TimeMoving
- AvAccel
- MaxAccel
- FuelConsumption
- AvFuelRate
- MaxFuelRate
- KmPerL


#### Models.Model
This is the generic base class for any model. The functionality is to

- Write the trained model to a specified location
- Load a model from a specified location
- Generate the feature set


#### Models.Clustering

This inherits from the base model and is an interface to all implemented clustering models. Clusters journeys and assigns a cluster ID to every journey.


#### Models.InitClass

This inherits from the base model and is an interface to all initial classification models. Gives an initial prediction as to where this journey is going.


#### Models.OnlineClass

This inherits from the base model and is an interface to all online classification models. Gives updated predictions during driving as to where the current journey is going.

---

### Todo
- Error handling for the read in
  - missing columns? currently just checked by hand there are none but it will crash if one appears
- Need to feed in the engine capacity somehow (have a look in Torque profile)
- The streaming part does not predict anything at the moment.
- Online Classification not implemented yet.
- does one journey really correspond to one file?
- There's some hardcoded parameters in the code at the moment. Need to move these out of the code.
- actually send a stream back of the online prediction.

---

### Included Python libraries

- sklearn
- pandas
- pika
- numpy
- patsy

*Pro Tip:* Just use the Anaconda distribution.

### Getting started
Run batch training `ipython BatchTrain.py`
Start `rabbitmq-server`
Start rabbitmq listener (the receiver of the predictions) `ipython StreamMockup/rabbitmq_consumer.py localhost emit`
Start the streaming prediction `ipython Model/StreamPredict.py`
Start streaming data into the stream predict function `ipython StreamMockup/rabbitmq.py Data/Alex/trackLog-2014-Mai-23_22-02-57.csv localhost receive csv`


### Spark
In spark-env.sh set:

SPARK_YARN_USER_ENV=/opt/anaconda/bin/python
PYSPARK_PYTHON=/opt/anaconda/bin/python

Run:

/usr/local/spark/bin/spark-submit --executor-memory 4G --files /IoT-ConnectedCar/IoT-Data-Science/PythonModel/Configuration/default.conf --py-files /IoT-ConnectedCar/IoT-Data-Science/PythonModel/Models.py,/IoT-ConnectedCar/IoT-Data-Science/PythonModel/Data.py  /IoT-ConnectedCar/IoT-Data-Science/PythonModel/BatchTrain.py "/IoT-ConnectedCar/IoT-Data-Science/PythonModel/Configuration/default.conf"

