#!/bin/bash
# Derek Beauregard #
# Pivotal 2014 #

source /etc/profile.d/pivotal.sh
sudo HADOOP_USER_NAME=hdfs $XD_HOME/../shell/bin/xd-shell --hadoopDistro hdp22

set retcode = $?
#read -p "Done. Press [Enter] to close."
exit $retcode

