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

import static com.google.cloud.pubsublite.internal.ServiceClients.addDefaultSettings;

import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.SubscriptionPath;
import com.google.cloud.pubsublite.v1.CursorServiceClient;
import com.google.cloud.pubsublite.v1.CursorServiceSettings;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.apache.spark.sql.catalyst.InternalRow;

public class PslSparkUtils {
  public static InternalRow toInternalRow(
      SequencedMessage msg, SubscriptionPath subscription, Partition partition) {
    return InternalRow.apply(
        scala.collection.JavaConverters.asScalaBuffer(
            ImmutableList.of(
                subscription.toString(),
                partition.value(),
                msg.offset().value(),
                msg.message().key(),
                msg.message().data(),
                msg.publishTime(),
                msg.message().eventTime(),
                msg.message().attributes())));
  }

  public interface CursorClientFactory extends Serializable {
    CursorServiceClient newClient(PslDataSourceOptions options);
  }

  public static CursorClientFactory getCursorClientFactory() {
    return (options) -> {
      try {
        return CursorServiceClient.create(
            addDefaultSettings(
                options.subscriptionPath().location().region(),
                CursorServiceSettings.newBuilder()
                    .setCredentialsProvider(new PslCredentialsProvider(options))));
      } catch (IOException e) {
        throw new IllegalStateException("Unable to create CursorServiceClient.");
      }
    };
  }

  public static PslSourceOffset addOne(PslSourceOffset offset) {
    Map<Partition, Offset> map = new HashMap<>(offset.getPartitionOffsetMap());
    map.replaceAll((k, v) -> Offset.of(v.value() + 1));
    return new PslSourceOffset(map);
  }
}
