/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.pubsublite.spark;

import com.google.cloud.pubsublite.internal.wire.PubsubContext;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

public class Constants {
  public static int DEFAULT_BYTES_OUTSTANDING = 10000;
  public static int DEFAULT_MESSAGES_OUTSTANDING = 100;
  public static int DEFAULT_BATCH_OFFSET_RANGE = 100;
  public static StructType DEFAULT_SCHEMA =
      new StructType(
          new StructField[] {
            new StructField("subscription", DataTypes.StringType, false, Metadata.empty()),
            new StructField("partition", DataTypes.LongType, false, Metadata.empty()),
            new StructField("offset", DataTypes.LongType, false, Metadata.empty()),
            new StructField("key", DataTypes.BinaryType, false, Metadata.empty()),
            new StructField("data", DataTypes.BinaryType, false, Metadata.empty()),
            new StructField("publish_timestamp", DataTypes.TimestampType, false, Metadata.empty()),
            new StructField("event_timestamp", DataTypes.TimestampType, true, Metadata.empty()),
            new StructField(
                "attributes",
                DataTypes.createMapType(
                    DataTypes.StringType, DataTypes.createArrayType(DataTypes.BinaryType)),
                true,
                Metadata.empty())
          });

  public static final PubsubContext.Framework FRAMEWORK = PubsubContext.Framework.of("SPARK");

  public static String BYTES_OUTSTANDING_CONFIG_KEY = "pubsublite.flowcontrol.byteoutstanding";
  public static String MESSAGES_OUTSTANDING_CONFIG_KEY =
      "pubsublite.flowcontrol.messageoutstanding";
  public static String SUBSCRIPTION_CONFIG_KEY = "pubsublite.subscription";
  public static String CONSUME_FROM_HEAD_CONFIG_KEY = "pubsublite.consumefromhead";
  public static String BATCH_OFFSET_RANGE_CONFIG_KEY = "pubsublite.maxbatchoffsetrange";
  public static String CREDENTIALS_KEY_CONFIG_KEY = "gcp.credentials.key";
  public static String CREDENTIALS_FILE_CONFIG_KEY = "gcp.credentials.file";
  public static String CREDENTIALS_ACCESS_TOKEN_CONFIG_KEY = "gcp.credentials.accesstoken";
}
