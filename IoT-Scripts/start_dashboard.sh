#!/bin/bash
# Derek Beauregard #
# Pivotal 2014 #

source /etc/profile.d/pivotal.sh
cd $IOT_HOME/IoT-Dashboard
java -jar /opt/pivotal/IoT-ConnectedCar/IoT-Dashboard/build/libs/IoT-Dashboard.jar

set retcode = $?
#read -p "Done. Press [Enter] to close."
exit $retcode

