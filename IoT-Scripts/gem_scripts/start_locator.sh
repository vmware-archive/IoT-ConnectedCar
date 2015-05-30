#!/bin/bash

source ./gemfire_env.sh

echo "Starting Locator: host = $LOCATOR_HOST, port = $LOCATOR_PORT"
gfsh -e "start locator --name=$LOCATOR_NAME --port=$LOCATOR_PORT --mcast-port=0"

