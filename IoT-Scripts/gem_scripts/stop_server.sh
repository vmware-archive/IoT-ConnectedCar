#!/bin/bash

source ./gemfire_env.sh

gfsh -e "stop locator --name=$LOCATOR_NAME"

