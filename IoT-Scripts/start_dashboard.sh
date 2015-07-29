#!/bin/bash
# Derek Beauregard #
# Pivotal 2014 #

. /etc/profile.d/pivotal.sh

java -jar $IOT_HOME/IoT-Dashboard/build/libs/IoT-Dashboard.jar

set retcode = $?
#read -p "Done. Press [Enter] to close."
exit $retcode

