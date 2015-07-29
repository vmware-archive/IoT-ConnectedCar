#!/bin/bash
# Derek Beauregard #
# Pivotal 2014 #

. /etc/profile.d/pivotal.sh
bash $XD_HOME/../shell/bin/xd-shell

set retcode = $?
#read -p "Done. Press [Enter] to close."
exit $retcode

