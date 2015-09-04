import redis
import cPickle as pickle
from sklearn.externals import joblib


redis_host = "10.68.52.181"
redis_port = 6379
redis_password = "acab8cbe-3a88-4e47-b1b8-31e692631148"

r = redis.StrictRedis(host=redis_host, port=redis_port, password=redis_password)

# Write models and clusters
journey_clusters = joblib.load("/Users/ronert/Dropbox/Pivotal/IoT-ConnectedCar/data/pcf_models/KMeans_JourneyClusters")
r.set("journey_clusters", pickle.dumps(journey_clusters))

# journey_clusters_loaded = pickle.loads(r.get("journey_clusters"))

init_models = joblib.load("/Users/ronert/Dropbox/Pivotal/IoT-ConnectedCar/data/pcf_models/InitClass_RF")
r.set("init_models", pickle.dumps(init_models))

online_models = joblib.load("/Users/ronert/Dropbox/Pivotal/IoT-ConnectedCar/data/pcf_models/OnlineClass_RF")
r.set("online_models", pickle.dumps(online_models))

with open("/Users/ronert/Dropbox/Pivotal/IoT-ConnectedCar/data/pcf_models/clusters.json", "r") as f:
    clusters_json = f.read().rstrip("\n")
r.set("clusters_json", clusters_json)
