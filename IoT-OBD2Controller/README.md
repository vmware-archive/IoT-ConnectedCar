# OBD2 Controller
At the beginning of the Connected Car project, we needed to prove out the ability for our
software to communicate with a car via an OBD 2 dongle.  This module is made up of that 
POC code.

**Note: This module's code is not used in the execution of this demo.  It was developed 
to provide a proof of concept and is here purely for historical reference.**

## History
Before the development of the associated iOS application used in the actual demos (Herbie),
the Spring Boot application was developed to prove our understanding of the the 
communication protocol between our software and the OBD 2 dongle.  This code was written
and tested to communicate with an inexpensive ELM 327 based dongle 
(http://www.amazon.com/gp/product/B00FQ7O88M/ref=s9_psimh_gw_p263_d0_i1).  The protocol 
this POC application was adapted into the iOS application (Herbie) used in our future 
drives.

## Use
The code in this module is proof of concept code only.  It has not been validated or 
tested beyond basic POC level "does it work?" testing from one or two personal vehicles. 
We leave it in the project purely as an illustration of where things began as well as an 
illustration of how one might develop an edge application using Spring.

The project itself is packaged as a Spring Boot based application.  It uses Spring 
Integration to communicate via a TCP socket over WiFi with the dongle referenced above.  
It then uses a RestTemplate to post the required JSON to Spring XD deployed on a server.

In order to execute this code, you need to network interfaces, one WiFi to communicate with
the dongle referenced above, the other to communicate with the server.  A form of cellular
connectivity is typically required to be able to drive.  With two network interfaces, 
you'll need to configure your network routes to send the correct communication to the 
correct endpoint (the TCP socket to the dongle and the REST POSTs to the server).  With 
the network connectivity configured, a developer can execute the code like any other Spring
Boot application:

```
$ java -jar IoT-OBD2Controller.jar --dongleHost=<OBD2_HOST> --donglePort=<OBD2_PORT> --gpsHost=<GPS_HOST>
```

where OBD2_HOST and OBD2_PORT are the host and port for the dongle (default to 
192.168.0.10 and 35000 respectively) and GPS_HOST is a URL that the current GPS can be 
located at (the iOS application GPS Receiver HD was used to provide this functionality in
testing: https://itunes.apple.com/us/app/gps-receiver-hd/id397928381?mt=8).

## References:
* [Wikipedia's OBD 2 document](http://en.wikipedia.org/wiki/OBD-II_PIDs)
* [ELM 327 data sheet](http://elmelectronics.com/DSheets/ELM327DS.pdf)
* [Spring Integration](http://spring.io/projects/spring-integration)
* [Spring Boot](http://spring.io/projects/spring-boot)
* [Multiple network interfaces on OS X](http://www.mac-forums.com/forums/internet-networking-wireless/166668-two-network-interfaces-how-separate-them.html)
