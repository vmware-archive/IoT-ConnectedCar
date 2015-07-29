# Set this to where you install the "miniconda" Python distro, from Continuum Analytics
MINICONDA=/opt/miniconda

# Set this to the root of your IoT install
PIVOTAL_HOME=/opt/pivotal

export IOT_HOME=$PIVOTAL_HOME/IoT-ConnectedCar
export XD_HOME=$PIVOTAL_HOME/spring-xd/xd
export PATH=$MINICONDA/bin:$XD_HOME/bin:$XD_HOME/../gemfire/bin:$PATH
export PYTHONPATH=/usr/phd/3.0.0.0-249/spark/python:/usr/phd/3.0.0.0-249/spark/python/build:$XD_HOME/../python/src/springxd

