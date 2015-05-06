#
# Copyright 2014-2015 the original author or authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

import json
import sys
import time

if __name__ == "__main__":
    path = sys.argv[1]
    try:
        with open(path) as f:
            names = f.readline().rstrip().split(",")
            for line in f:
                row = line.rstrip().split(",")
                data = dict(zip(names, row))
                print json.dumps(data)
                time.sleep(0.2)
    except Exception as e:
        raise SystemExit(e)
