#!/bin/bash
# Derek Beauregard #
# Pivotal 2014 #

source /etc/profile.d/pivotal.sh
sudo $XD_HOME/../shell/bin/xd-shell

set retcode = $?
#read -p "Done. Press [Enter] to close."
exit $retcode

