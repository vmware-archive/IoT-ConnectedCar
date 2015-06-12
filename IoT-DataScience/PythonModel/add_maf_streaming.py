#!/usr/bin/env python

#
# Add the missing "maf_airflow" field to the Herbie data, based on some other, existing fields.
#
# This is intended to be run as a module within a Spring XD processing pipeline, where this
# reads stdin and writes stdout.
#
# NOTE: since the DataFilter.groovy filter rejects data with invalid values of the fields this
# module depends on, insert this module after that and you should be safe.
#
# Based on this example:
# https://github.com/spring-projects/spring-xd/blob/master/src/docs/asciidoc/Creating-a-Python-Module.asciidoc
#
# Combined with Ronert's IoT code:
# https://github.com/mgoddard-pivotal/IoT-ConnectedCar-EDU/blob/master/IoT-DataScience/PythonModel/impute_maf.py
#

import json
import sys

ed = 3.0 # engine displacement in liters
ve = 0.9 # volumetric efficiency
mass_air = 28.97 # mass of air per unit of ___(?)
gas_constant = 8.314 # some gas constant(?)

def add_maf (line):
  row = json.loads(line)
  if not row["maf_airflow"]:
    imap = row["rpm"] * row["intake_manifold_pressure"] / (row["intake_air_temp"] + 273.15)
    row["maf_airflow"] = imap/120.0 * ve * ed * mass_air/gas_constant
  return json.dumps(row)

# Write data to stdout
def send(data):
  sys.stdout.write(data)
  sys.stdout.flush()

# Terminate a message using the (Spring XD) default CRLF
def eod():
  send("\r\n")

# Main - Echo the input
while True:
  try:
    data = raw_input()
    if data:
      send(add_maf(data))
      eod()
  except EOFError:
      #eod()
      break

