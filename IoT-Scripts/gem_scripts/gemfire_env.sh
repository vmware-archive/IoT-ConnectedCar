#!/bin/bash

# Constants for Gemfire deployment, to share across locator and servers
# (COPY TO EACH OF THE GEMFIRE SERVERS: locator and each server)

# Tailor these to your installation
LOCATOR_HOST="gem-locator"
export PATH=/opt/pivotal/gemfire/Pivotal_GemFire_810/bin:$PATH

# These should be okay as they are
LOCATOR_NAME="${LOCATOR_HOST}-loc"
SERVER_NAME="${HOSTNAME}-srv"
LOCATOR_PORT=9001
CACHE_XML=./cache.xml # TODO: determine how to avoid passing this around cluster
XMS=16384m # For server processes
XMX=16384m # For server processes

