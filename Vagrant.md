## Setting up the development environment
**Sample data:**  
Extract https://s3.amazonaws.com/iot.connected.car/opt_pivotal_data.tar.gz to root of the IoT-ConnectedCar project 


**Download and Install Hortonworks**  
- Download the Hortonworks VirtualBox OVA from http://hortonworks.com/products/hortonworks-sandbox/#install  
- Open VirtualBox and go to File -> Import Appliance and import the Hortonworks OVA  
- Open the Settings panel for Hortonworks and go to the Network tab  
- Disable Adapter 1 and Enable Adapter 2 as a Bridged Adapter  
- Start Hortonworks VM  
- Get the IP of the Hortonworks box by running `VBoxManage guestproperty get "Hortonworks Sandbox with HDP 2.3.2" "/VirtualBox/GuestInfo/Net/0/V4/IP"`  
- You should be able to see Ambari at `<HORTONWORKS_BRIDGED_IP>:8080`


**Building the project**  
- `vagrant up`
- `vagrant ssh`
- `sudo vim $XD_HOME/config/servers.yml` and modify `spring: hadoop: fsUri: hdfs://<HORTONWORKS_BRIDGED_IP>:8020` with the IP of the Hortonworks box  
- `sudo vim $XD_HOME/config/hadoop.properties`: and modify `fs.default.name=hdfs://<HORTONWORKS_BRIDGED_IP>:8020` with the IP of the Hortonworks box  
- `cp $XD_HOME/../python/src/springxd/stream.py $IOT_HOME/IoT-DataScience/PythonModel`
- `cd $IOT_HOME/IoT-Dashboard` and run `npm install && bower install && grunt clean build`  
- `cd $IOT_HOME` and run `./gradlew clean build`  


## Running from the VM
- `cd $IOT_HOME/IoT-Scripts`  
- Run `./startup.sh`  
- Once startup has finished loading(reached 'scheduling deployments to new containers in 15000 ms'), open another tab  
- In the new tab run `./start_xd-shell.sh`  
- From inside the Spring XD shell, execute these commands:  
- `module upload --type processor --name acmeenrich --file /opt/pivotal/IoT-ConnectedCar/IoT-EnrichmentTransformer/build/libs/IoT-EnrichmentTransformer.jar`  
- `module upload --type processor --name typeconversiontransformer --file /opt/pivotal/IoT-ConnectedCar/IoT-GemFireTransformer/build/libs/IoT-GemFireTransformer.jar`  
- `script --file /opt/pivotal/IoT-ConnectedCar/IoT-Scripts/stream-create.xd`  
- Note that the modules only need to added the first time but the stream command will need to be re-run each time the project is launched  
- In another terminal run `./start_restapi.sh`  
- In another terminal run `./start_dashboard.sh`  
- In another terminal run `./start_simulator.sh`  
- Once the simulator has started select option 1  
- Open `33.33.164.176:9889` in your browser
