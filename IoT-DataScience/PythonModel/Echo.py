#!/usr/bin/env python

import sys

#=====================
# Write data to stdout
#=====================
def send(data):
  sys.stdout.write(data)
  sys.stdout.flush()

#===========================================
# Terminate a message using the default CRLF
#===========================================
def eod():
  send("\r\n")

#===========================
# Main - Echo the input
#===========================
while True:
  try:
    data = raw_input()
    if data:
      send(data)
      eod()
  except EOFError:
      eod()
      break

