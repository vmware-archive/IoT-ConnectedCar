#!/bin/bash

source /etc/profile.d/pivotal.sh

# Setup the gemfire installation 
export GEMFIRE_IOT_CONF_DIR=/opt/pivotal/IoT-ConnectedCar/IoT-Scripts/conf

# Start gemfire
export JAVA_OPTS="-Dgemfire.jmx-manager-start=true -Dgemfire.jmx-manager=true -Dgemfire.jmx-manager-http-port=0"
gemfire-server $GEMFIRE_IOT_CONF_DIR/iot-demo.xml &

sleep 10;

# Load the journeys into GemFire
java -jar /opt/pivotal/IoT-ConnectedCar/IoT-GemFireLoader/build/libs/IoT-GemFireLoader.jar /opt/pivotal/IoT-ConnectedCar/data/model/clusters.json

# Start XD Singlenode
sudo HADOOP_USER_NAME=hdfs $XD_HOME/bin/xd-singlenode --hadoopDistro hdp22

set retcode = $?
read -p "Done. Press [Enter] to close."
exit $retcode

