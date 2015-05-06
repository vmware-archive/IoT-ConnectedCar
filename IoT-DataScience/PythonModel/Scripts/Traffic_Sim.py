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
import numpy as np
import matplotlib.pyplot as plt
import statsmodels.formula.api as sm
import math
from pylab import plot , show


#Function for loading in file.
def LdO(file):
    print "Reading in file " + file + " ..."
    journey = Data.Journey()
    journey.load_data(file, 0)
    return journey

#Function for creating boxplots of mpg by speed in m/s
def Box_plots(journey):
        if (journey.data.columns.values == 'Speed_OBD_km').any():
            if journey.data['Speed_OBD_km'].dtype == 'float64' and journey.data['MPG_inst'].dtype == 'float64':
                plt.ylim([0, 60])
                journey.data.boxplot(column = ['MPG_inst'],by = ['Speed_OBD_km'])
                plt.figure(filename)

    

#Scatter plot funtion with speed in km/h
def line_matrix(filenames):
    c = 0
    for filename in filenames:
        journey = LdO(filename)
        firstIndex = np.where(journey.data.Time_GPS.notnull())[0][0]
        lastIndex = np.where(journey.data.Time_GPS.notnull())[0][-1]
        print 'Starting Latitude: ' + str(journey.data['Latitude'][firstIndex]) + ' Starting Longitude: ' + str(journey.data['Longitude'][firstIndex])
        print 'End Latitude: ' + str(journey.data['Latitude'][lastIndex]) + ' End Longitude: ' + str(journey.data['Longitude'][lastIndex])
        #plt.subplot(math.sqrt(len(filenames)),math.sqrt(len(filenames)), c+1)
        if (journey.data.columns.values == 'MPG_inst').any():
            if (journey.data['MPG_inst'].dtype == 'float64') :
                timediff = max(journey.data['Time_GPS']) - min(journey.data['Time_GPS'])
                print timediff
                #plt.xlim(min(journey.data['Time_GPS']), max(journey.data['Time_GPS']))   
                plt.figure(c)
                journey.data.plot(x =['Time_GPS'], y =['MPG_inst'], style = 'k.', alpha = 0.2, grid = False)
                journey.data['MPGRMeans'] = pandas.stats.moments.rolling_mean(journey.data['MPG_inst'], window = 100, min_periods = 1)
                journey.data.plot(x = ['Time_GPS'],y = ['MPGRMeans'] , style = 'r-', grid = False)
                plt.tick_params(axis = 'x', labelbottom = 'off', which = 'both')
                #plt.subplots_adjust(left = 0.05,right = 0.95, top = 0.95, bottom = 0.05)
                #plt.axes
                new_filename =  filename.split('/')[-1]
                plt.title(new_filename)
                
       # if (journey.data.columns.values == 'Speed_GPS_m').any():
        #    if (journey.data['Speed_GPS_m'].dtype == 'float64'):
         #       plt.figure(c)
          #      journey.data.plot(x = 'Time_GPS', y ='Speed_GPS_m',style = 'b-')
           #    plt.subplots_adjust(left = 0.05,right = 0.95, top = 0.95, bottom = 0.05)
            #    plt.axes
             #   new_filename =  filename.split('/')[-1]
              #  plt.title(new_filename)
        c = c+1
    

#Defining directory to iterate over.
directory ="/Users/jbetts/Documents/DS_ConnectedCar/Data/Ellie/"
filenames = glob.glob(directory+"*.csv") 

#Calculating and plotting Regression.

#overall = [blank data frame]
#coefs = np.ones((6,2)) - 1
#n = 0
#print coefs
#for filename in filenames:
 #   journey = LdO(filename)
  #  if (journey.data.columns.values == 'Speed_GPS_m').any():
   #     if journey.data['Speed_GPS_m'].dtype == 'float64' and journey.data['MPG_inst'].dtype == 'float64':
    #        journey.data['Ones'] = np.ones((len(journey.data), ))
     #       Y = journey.data['MPG_inst'][:-1]
      #      X = journey.data[['Speed_GPS_m', 'Ones']][:-1]
       #     r = sm.OLS(Y,X).fit()
           # coefs = coefs(,n+1) + r.params
        #    n = n+1
line_matrix(filenames)
plt.show()
    
    


        

        
