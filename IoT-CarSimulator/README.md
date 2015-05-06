# Connected Car Simulator
This module consists of a Spring Batch job that can read a recorded journey (recorded in
JSON format) and "replay" it.  The batch job is built as a Spring Boot über jar so all you
need to do to execute the job is build the module and execute the following command:

```
$ java -jar IoT-CarSimulator.jar inputFile=<PATH_TO_INPUT> delay=<DELAY_IN_MILLISECONDS>
```

The `<PATH_TO_INPUT>` is the path to the input file in the same format as the test
input file found in `src/test/resources/data`. The `<DELAY_IN_MILLISECONDS>` is the
delay between sending each record.  The iOS app used in the demo (Herbie) sends data once
a second, but it can be useful to speed things along for testing, etc.

# References
* [Spring Batch](https://spring.io/projects/spring-batch)
* [Spring Boot](https://spring.io/projects/spring-boot)
