# Enrichment Transformer
The transformer (acmeenrich) used in the ingestion of OBD II data for the connected car 
serves two purposes:

1. **To add a timestamp -** The timestamp logic is placed here instead of at the edge (Herbie)
so not to have to address time zone differences based on where the car is driving.
2. **Upper case the VIN -** To allow for consistency, the VIN is upper cased with each POST.

The transformer in context of the stream:

```
http | filter --script=file:<PATH_TO_SCRIPT>/DataFilter.groovy | acmeenrich | shell --inputType=application/json --command='/opt/pivotal/anaconda/bin/python ./StreamPredict.py' --workingDir=/opt/pivotal/IoT-ConnectedCar/IoT-Data-Science/PythonModel/ | hdfs
```

This module is packaged via a Spring Boot über jar and can be deployed to Spring XD by the
XD shell via the following command:

```
xd:> module upload --type processor --name acmeenrich --file <PATH_TO_PROJECT>/IoT-ConnectedCar/IoT-EnrichmentTransformer/build/libs/IoT-EnrichmentTransformer.jar
```

Where &lt;PATH_TO_PROJECT&gt; is the path to the IoT-ConnectedCar project.

## References
* [Spring XD](https://spring.io/projects/spring-xd)
* [Spring Integration](https://spring.io/projects/spring-integration)
* [Spring Boot](https://spring.io/projects/spring-boot)
