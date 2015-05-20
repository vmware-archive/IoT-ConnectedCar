#!/bin/bash
# Derek Beauregard #
# Pivotal 2014 #

. /etc/profile.d/pivotal.sh

function printOptions {
	echo "You are currently at home, the Renaissance Hotel."
	echo "Where do you want to go?"
	echo "1) Chicago Drives (MIKE: CHOOSE THIS ONE)"
	echo "2) Flying Saucer Resturant"
	echo "3) Babe's Chicken Dinner House"
	echo "4) Sandy Lake Amusement Park"
}

function getInput {
DEFAULT=1
echo -n "Enter your Selection and press [ENTER] (default: $DEFAULT): "
read selection

# Set to default if not supplied by user
if [[ -z "$selection" ]]
then
	selection=$DEFAULT
fi

# Validate that it is a number, 1 to 4
if ! [[ $selection =~ ^[1-4]$ ]]
then
	echo "You must enter a number between 1 and 4"
	getInput
fi
}

# Pick the data file to use
function selectInputFile {
	case $selection in
		1)
		#inputfile="$IOT_HOME/../data/Dallas-Drives/Ren-Omni-75.out"
		inputfile="$IOT_HOME/../data/Chicago_Drives/Home-Prom.out"
		;;
		2)
		inputfile="$IOT_HOME/../data/Dallas-Drives/Ren-Fly-BeltlineRoute.out"
		;;
		3)
		inputfile="$IOT_HOME/../data/Dallas-Drives/Ren-Babes-JupiterCampbellRoute.out"
		;;
		4)
		inputfile="$IOT_HOME/../data/Dallas-Drives/Ren-Sandy-GBTollwayRoute.out"
		;;
	esac
}

# Run the simulator
function runSimulation {
	echo "Running simulation #$selection - $inputfile"
	java -jar $IOT_HOME/IoT-CarSimulator/build/libs/IoT-CarSimulator.jar inputFile=$inputfile delay=1000
	return $?
}

printOptions
getInput
selectInputFile
runSimulation

set retcode = $?
read -p "Done. Press [Enter] to close."
exit $retcode

