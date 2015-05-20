#!/bin/bash

# mem=4G # 4G was the initial value
mem=1G

spark-submit \
  --conf spark.default.parallelism=2 \
  --conf spark.storage.memoryFraction=0.5 \
  --executor-memory $mem \
  --files $IOT_HOME/IoT-DataScience/PythonModel/Configuration/default.conf \
  --py-files $IOT_HOME/IoT-DataScience/PythonModel/Models.py,$IOT_HOME/IoT-DataScience/PythonModel/Data.py \
  $IOT_HOME/IoT-DataScience/PythonModel/BatchTrain.py \
  $IOT_HOME/IoT-DataScience/PythonModel/Configuration/default.conf

