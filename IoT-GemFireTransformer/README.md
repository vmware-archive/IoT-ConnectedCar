# GemFire Transformer
This transformer is used to convert the basic JSON String received from the car into a 
POJO to be serialized in GemFire.  We do this to be able to use Spring Data REST in 
combination with Spring Data GemFire to power the HTML dashboard.  This transformer is 
used in the tap below:

```
tap:stream:IoT-HTTP.shell > typeconversiontransformer | gemfire-server --regionName=car-position --keyExpression=payload.vin
```

This module is packaged via a Spring Boot über jar and can be deployed to Spring XD by the
XD shell via the following command:

```
xd:> module upload --type processor --name typeconversiontransformer --file <PATH_TO_PROJECT>/IoT-ConnectedCar/IoT-GemFireTransformer/build/libs/IoT-GemFireTransformer.jar
```

Where &lt;PATH_TO_PROJECT&gt; is the path to the IoT-ConnectedCar project.

## References
* [Spring Data GemFire](http://projects.spring.io/spring-data-gemfire/)
* [Spring Integration](https://spring.io/projects/spring-integration)
* [Spring XD](https://spring.io/projects/spring-xd)
* [Spring Boot](https://spring.io/projects/spring-boot)
* [Pivotal GemFire](http://pivotal.io/big-data/pivotal-gemfire)
