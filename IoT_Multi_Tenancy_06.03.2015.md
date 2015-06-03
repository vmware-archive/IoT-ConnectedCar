# IoT-ConnectedCar: Considerations for Multi-tenancy

## Aspects
1. HAWQ
  * Single instance
  * Multiple roles
  * Multiple DBs
2. Spring XD:
  * Single instance
  * Use distinct stream names
  * Deploy custom modules with distinct names
3. Gemfire
  * Single instance (locator, servers)
  * Distinct _region_ names
  * REST service will be modified to point to appropriate region
  * REST service will need to be deployed to distinct port
4. HDFS
  * Single instance
  * Distinct directories (students have /user/studentID/)
  * Spring XD will, by default, write into /xd/STREAM_NAME, which is ok
5. PCF
  * Single instance
  * Individual login
  * Distinct app name
6. Environment
  * /etc/profile.d/pivotal.sh will need to be abandoned in favor of $HOME/.bashrc
  * Any paths like `$IOT_HOME/IoT-GemFireLoader/build/libs/IoT-GemFireLoader.jar` will
    be ok so long as IOT_HOME is distinct (per above)
  * Any embedded `/opt/pivotal/...` type paths will need to be located and redefined via environment variables


