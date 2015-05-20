#!/bin/bash
# Derek Beauregard #
# Pivotal 2014-2015 #

. /etc/profile.d/pivotal.sh

java -jar $IOT_HOME/IoT-GemFireREST/build/libs/IoT-GemFireREST.jar

set retcode = $?
#read -p "Done. Press [Enter] to close."
exit $retcode

