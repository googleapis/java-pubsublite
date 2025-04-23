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

package com.google.cloud.pubsublite.internal;

import static com.google.cloud.pubsublite.internal.UncheckedApiPreconditions.checkArgument;

import com.google.api.gax.rpc.ApiException;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.proto.PubSubMessage;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.protobuf.ByteString;
import java.math.BigInteger;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

public class DefaultRoutingPolicy implements RoutingPolicy {
  private final long numPartitions;

  // An incrementing counter, when taken mod(numPartitions), gives the partition choice.
  private final AtomicLong withoutKeyCounter;

  public DefaultRoutingPolicy(long numPartitions) throws ApiException {
    checkArgument(numPartitions > 0, "Must have a positive number of partitions.");
    this.numPartitions = numPartitions;
    this.withoutKeyCounter = new AtomicLong(ThreadLocalRandom.current().nextLong(numPartitions));
  }

  @Override
  public Partition route(PubSubMessage message) {
    return message.getKey().isEmpty() ? routeWithoutKey() : routeWithKey(message.getKey());
  }

  private Partition routeWithoutKey() throws ApiException {
    long index = withoutKeyCounter.incrementAndGet();
    return Partition.of(index % numPartitions);
  }

  private Partition routeWithKey(ByteString messageKey) throws ApiException {
    HashCode code = Hashing.sha256().hashBytes(messageKey.asReadOnlyByteBuffer());
    checkArgument(code.bits() == 256); // sanity check.
    BigInteger bigEndianValue = new BigInteger(/* signum= */ 1, code.asBytes());
    return Partition.of(bigEndianValue.mod(BigInteger.valueOf(numPartitions)).longValueExact());
  }
}
