# GemFire Commons
This application uses Pivotal GemFire as a data store to power the dashboard.  This module
provides the domain objects and Spring Data repositories used to support all functionality
around GemFire.  This includes:

1. **Populate historic journeys -** Based on a VIN's historic data, journeys are loaded 
on startup to power the data science tab in the dashboard.
2. **Query for journeys based on VIN -** The dashboard queries for all the possible 
journeys based on the selected VIN.
3. **Query for the current position of a known vehicle -** The dashboard will query for 
the last known position of the vehicle by VIN.

**Note:** This module does not provide the actual REST API.  That is provided by the 
IoT-GemFireREST module.  This provides the common domain objects and repositories only.

## References
* [Spring Data GemFire](https://projects.spring.io/spring-data-gemfire/)
