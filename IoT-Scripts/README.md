# Scripts
As part of the demo application that was developed, a portable virtual machine was also 
created.  While the VM itself isn't open sourced as part of this repository, the scripts
used on the VM to launch each of the processes is available in this module.  On a clean 
reboot of the VM (assuming the custom modules for Spring XD have been installed and batch
training of data has been performed), the following startup procedure is executed:

1. Launch Hadoop
2. Execute startup.sh.  This script launches GemFire, loads the journeys from the batch 
training, and starts Spring XD.
3. Execute start_xd-shell.sh.  This script simply launches the Spring XD shell.
4. From inside the Spring XD shell, execute the command where &lt;PATH_TO_SCRIPT&gt; is 
the fully qualified path to the stream-create.xd script:
```
xd:> script --file <PATH_TO_SCRIPT>/stream-create.xd
```
5. Execute start_restapi.sh.  This script launches the IoT-GemFireREST module.
6. Execute start_dashboard.sh.  This script launches the IoT-Dashboard module.
