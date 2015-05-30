#!/bin/bash

source ./gemfire_env.sh

# Omitting this for now, in favor of manually declaring the regions
# --cache-xml-file=$CACHE_XML

echo "Starting Server"
gfsh -e "start server --name=$SERVER_NAME --locators=${LOCATOR_HOST}[${LOCATOR_PORT}] --server-port=0 --initial-heap=$XMS --max-heap=$XMX --cache-xml-file=$CACHE_XML"

