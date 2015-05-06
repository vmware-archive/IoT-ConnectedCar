# Connected Car Dashboard
This is the AngularJS based dashboard.  A tour of the directories here:

 * `app` - The source code for the dashboard
 * `bower_components` - The bower dependencies
 * `config` - The configuration for the application
 * `src` - Java source to execute this module as a Spring Boot application.
 * `test` - The test code for the application

## To build the project:

1. Execute a grunt build from the root of this module: `$ grunt clean build`.
2. From the root of the project (one level up from this module) execute a gradle build:
    `$ ./gradlew clean build`
3. The Spring Boot über jar can be found in the build/lib directory and can be launched
via `$ java -jar IoT-Dashboard.jar`.
4. The dashboard should be able to be viewed via [http://localhost:8080/index.html](http://localhost:8080/index.html)


## To deploy on Pivotal Cloud Foundry:

1. `$ cf push <APP-NAME> -p IoT-Dashboard/build/libs/IoT-Dashboard.jar -b https://github.com/cloudfoundry/java-buildpack.git --no-start`
2. `$ cf set-env <APP-NAME> JBP_CONFIG_OPEN_JDK_JRE '[version: 1.8.0_+, memory_heuristics: {heap: 85, stack: 10}]'`
3. `$ cf start <APP-NAME>`

## References
* [Yeoman](http://yeoman.io/)
* [AngularJS](https://angularjs.org/)
* [Spring Boot](https://spring.io/projects/spring-boot)
* [Pivotal Cloud Foundry](http://pivotal.io/platform-as-a-service/pivotal-cloud-foundry)
