#
# Copyright 2014-2015 the original author or authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

import pandas
import Data
import glob

#Function for reading in file
def Load(file):
    print "Reading in file " + file + " ..."
    journey = Data.Journey()
    journey.load_data(file, 0)
    return journey

#Define directory to check for column headings.
directory ="/Users/jbetts/Documents/DS_ConnectedCar/Data/*/" 
filenames = glob.glob(directory +"*.csv") 

#Checking files for column headings
ColumnsToCheck = ['MPG_inst']
#['MPG_inst'],['KmPerL_inst'],['LPerKm']

for column_name in ColumnsToCheck:
    count = 0
    for file in filenames:
        journey = Load(file)
        columns = journey.data.columns.values
        if journey.data[column_name].isnull().all():
        #if (journey.data[column_name].dtype) != 'float64':
            print journey.data[column_name]
            #print 'THIS ONE'
           # count = count + 1
    #print (str(count) + '/' + str(len(filenames)) + ' FILES HAVE THE VARIABLE ' + str(column_name))
    
            
    
    
    
