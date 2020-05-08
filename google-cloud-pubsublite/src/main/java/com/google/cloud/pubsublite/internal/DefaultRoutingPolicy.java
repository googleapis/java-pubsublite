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

import static com.google.cloud.pubsublite.internal.Preconditions.checkArgument;

import com.google.cloud.pubsublite.Partition;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import com.google.protobuf.ByteString;
import io.grpc.StatusException;
import java.math.BigInteger;
import java.util.Random;

public class DefaultRoutingPolicy implements RoutingPolicy {
  private final int numPartitions;
  private final CloseableMonitor monitor = new CloseableMonitor();

  @GuardedBy("monitor.monitor")
  private int nextWithoutKeyPartition;

  public DefaultRoutingPolicy(int numPartitions) throws StatusException {
    checkArgument(numPartitions > 0, "Must have a positive number of partitions.");
    this.numPartitions = numPartitions;
    this.nextWithoutKeyPartition = new Random().nextInt(this.numPartitions);
  }

  @Override
  public Partition routeWithoutKey() throws StatusException {
    try (CloseableMonitor.Hold h = monitor.enter()) {
      Partition toReturn = Partition.of(nextWithoutKeyPartition);
      int next = nextWithoutKeyPartition + 1;
      next = next % numPartitions;
      nextWithoutKeyPartition = next;
      return toReturn;
    }
  }

  @Override
  public Partition route(ByteString messageKey) throws StatusException {
    HashCode code = Hashing.sha256().hashBytes(messageKey.asReadOnlyByteBuffer());
    checkArgument(code.bits() == 256); // sanity check.
    BigInteger bigEndianValue = new BigInteger(/*signum=*/ 1, code.asBytes());
    return Partition.of(bigEndianValue.mod(BigInteger.valueOf(numPartitions)).longValueExact());
  }
}
