# Filter
The OBD II standard provides a set of data that can be available from a car that supports 
the standard.  However, the OBD II standard does not *require* that any given vehicle 
support all the data points.  Because of the potential mismatch between the data a vehicle
provides and the data the data science module of this project this module provides a 
Groovy script to be used by Spring XD to filter out bad records.  This script does not log
the dropped records in any way.

This script is used in the ingestion stream for this project:

```
http | filter --script=file:<PATH_TO_SCRIPT>/DataFilter.groovy | acmeenrich | shell --inputType=application/json --command='/opt/pivotal/anaconda/bin/python ./StreamPredict.py' --workingDir=/opt/pivotal/IoT-ConnectedCar/IoT-Data-Science/PythonModel/ | hdfs
```

where &lt;PATH_TO_SCRIPT&gt; is the fully qualified path to the Groovy script in this 
module.

## References
* [Spring XD](https://spring.io/projects/spring-xd)
* [Groovy](http://groovy-lang.org/)