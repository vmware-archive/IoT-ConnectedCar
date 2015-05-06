# GemFire REST API
To expose the data stored in GemFire to the dashboard, this REST API is used.  It simply 
exposes the Spring Data repositories defined in the IoT-GemFireCommons module via Spring
Data REST.

Since this module is just a simple Spring Boot application, running it is done via:

```
$ java -jar IoT-GemFireREST.jar
```

The location of the GemFire server (host and port) are configured via the 
`application.properties` file per normal Spring Boot conventions.
 
## References
* [Spring Data REST](http://projects.spring.io/spring-data-rest/)
* [Pivotal GemFire](http://pivotal.io/big-data/pivotal-gemfire)