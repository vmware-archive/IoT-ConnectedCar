#!/bin/bash

source ./gemfire_env.sh

gfsh -e "stop locator --dir=$LOCATOR_NAME"

