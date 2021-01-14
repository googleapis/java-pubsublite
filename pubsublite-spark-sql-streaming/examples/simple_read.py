#!/usr/bin/env python
# Copyright 2020 Google Inc. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#       http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

from pyspark.sql import SparkSession
import sys

full_subscription_path = sys.argv[1]

spark = SparkSession.builder.appName('Simple PubSub Lite Read').master('yarn').getOrCreate()

spark \
    .readStream \
    .format('pubsublite') \
    .option('pubsublite.subscription', full_subscription_path) \
    .load() \
    .writeStream \
    .format('console') \
    .outputMode('append') \
    .trigger(processingTime='1 second') \
    .start() \
    .awaitTermination()