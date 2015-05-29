#!/bin/bash

. /etc/profile.d/pivotal.sh

# This corresponds with the Hadoop distribution in use
# (ref. http://docs.spring.io/spring-xd/docs/1.2.0.M1/reference/html/)
hd_distro=phd30 # "phd30" for PHD 3.0, "phd21" for PHD 2.x

# Setup the gemfire installation 
#export GEMFIRE_IOT_CONF_DIR=$IOT_HOME/IoT-Scripts/conf

# Start gemfire (MIKE: added $XD_HOME/../gemfire/bin/ since, without it, CLASSPATH wasn't being set)
#JAVA_OPTS="-Dgemfire.jmx-manager-start=true -Dgemfire.jmx-manager=true -Dgemfire.jmx-manager-http-port=0"
JAVA_OPTS="$JAVA_OPTS -Xmx2048m -XX:MaxPermSize=256m"
#export JAVA_OPTS
#bash $XD_HOME/../gemfire/bin/gemfire-server $GEMFIRE_IOT_CONF_DIR/iot-demo.xml &
#gemfire_pid=$!
#sleep 10;

# Load the journeys into GemFire
java -jar $IOT_HOME/IoT-GemFireLoader/build/libs/IoT-GemFireLoader.jar $IOT_HOME/../data/model/clusters.json

# Fix issue with the log file
dir=$XD_HOME/logs
sudo mkdir -pm0775 $dir
sudo chown $USER: $dir
touch $XD_HOME/logs/singlenode.log

# Fix the issue with the lock file
dir=$XD_HOME/data/jobs
sudo mkdir -pm0775 $dir
sudo chown $USER: $dir

# Ensure we can upload a Spring XD processor module
dir=$XD_HOME/modules
sudo chown -R $USER: $dir

# Try to resolve the issue with uploading a custom module
base_dir=$XD_HOME/custom-modules
for dir in source common job processor sink
do
  sudo mkdir -pm0775 $base_dir/$dir
done
sudo chown -R $USER: $base_dir

# Start XD Singlenode
bash $XD_HOME/bin/xd-singlenode --hadoopDistro $hd_distro

set retcode = $?
read -p "Done. Press [Enter] to close."
kill $gemfire_pid # Kill backgrounded Gemfire process
exit $retcode

