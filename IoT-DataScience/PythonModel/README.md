# Data Science for Journey and Fuel Consumption Prediction

### Overview

The Data Science part of the Connected Car Demo predicts which journey you are likely to take and based on that, how much fuel you will likely consume.

The code runs in 2 steps:

1. *Batch Training:* Train the models on all available data in batch to condense all important information into the model objects.
2. *Stream Prediction:* Point a Redis stream to the stream prediction function to get updated online predictions of journey and fuel consumption.

The batch training runs on top of Spark using the pyspark API and the streaming prediction runs as a microservice within Cloud Foundry.

---

### How do I run this stuff?

1. Edit the default configuration file (or create your own)
2. Edit the manifest.yml file
3. Run the batch training (see below)
4. `cd` into PythonModel and run `cf push`

#### *Batch Training* is done by BatchTrain.py running on  Spark

*Requirements*

Spark 1.3+, Hadoop and YARN.

Anaconda Python should be installed on all of the cluster nodes and in the user's
`$PATH`. Alternatively, you can set Spark to use Anaconda in `spark-env.sh`:

```
SPARK_YARN_USER_ENV=/opt/anaconda/bin/python
PYSPARK_PYTHON=/opt/anaconda/bin/python
```

Run `conda env update -f environment.yml` on all of the nodes to install the Python requirements.

*Configuration*

Edit `Configuration/default.conf`. You need to point it to the location of your data on HDFS and pass it your Redis credentials to store the models.


*Usage*:
`spark-submit --files <path to configuration file> --py-files <path to Models.Py, path to Data.py> <path to BatchTrain.py> "<path to configuration file>"`

*Example*:
`spark-submit --driver-memory 4G --executor-memory 4G --num-executors 2 --executor-cores 1 --files /home/pyspark/PythonModel/Configuration/default.conf --py-files /home/pyspark/PythonModel/Models.py,/home/pyspark/PythonModel/Data.py /home/pyspark/PythonModel/BatchTrain.py "/home/pyspark/PythonModel/Configuration/default.conf"`


#### *Stream Prediction* is done by StreamPredict.py on Cloud Foundry

The stream predict function emits the predictions to another RabbitMQ queue you can specificy in the configuration.

*Usage*:
1. Edit `manifest.yml`.
2. `cd IoT-DataScience/PythonModel`
3. `cf push`

---

### Code Structure

#### Data.py

Defines the various data structures and data munging functions used

#### Models.py

Specifies the machine learning models used


#### BatchTraining.py

Runs the machine learning models on top of Spark

#### Various Helper Scripts

Within the `PythonModel` directory you can find various other helper
scripts you can use to impute broken/missing data in the journeys you
recorded.

---

### TODOs
- Move some of the hardcoded options into the config file
- Automate data imputation and simulation (when too few trips were
  recorded, e.g. in a demo situation)
- Run an API on top of Cloud Foundry that can kick of the model
  training and monitor its progress


