import redis
import ConfigParser


configuration_string = "Configuration/default.conf"

config = ConfigParser.ConfigParser()
config.read(configuration_string)
rawdata_directory = config.get("Directories", "dir_rawdata")

# Set up Redis
redis_host = config.get("Redis", "host")
redis_port = int(config.get("Redis", "port"))
redis_password = config.get("Redis", "password")
# Connect to Redis
r = redis.StrictRedis(host=redis_host, port=redis_port, password=redis_password)

path = "/Users/ronert/Dropbox/Pivotal/IoT-ConnectedCar/data/pydata_london_maf/Ian-Bentima.out"

with open(path, "r") as f:
    for line in f:
        r.lpush("queue.transformer.data", line.rstrip("\n"))
