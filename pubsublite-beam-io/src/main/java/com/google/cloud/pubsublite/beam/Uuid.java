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

package com.google.cloud.pubsublite.beam;

import com.google.auto.value.AutoValue;
import com.google.protobuf.ByteString;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;
import org.apache.beam.sdk.coders.ByteArrayCoder;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.DelegateCoder;

@AutoValue
public abstract class Uuid {
  public static final String DEFAULT_ATTRIBUTE = "x-goog-pubsublite-dataflow-uuid";

  public abstract ByteString value();

  public static Uuid of(ByteString value) {
    return new AutoValue_Uuid(value);
  }

  public static Uuid random() {
    UUID uuid = UUID.randomUUID();
    ByteString.Output output = ByteString.newOutput(16);
    DataOutputStream stream = new DataOutputStream(output);
    try {
      stream.writeLong(uuid.getMostSignificantBits());
      stream.writeLong(uuid.getLeastSignificantBits());
    } catch (IOException e) {
      throw new RuntimeException("Should never have an IOException since there is no io.", e);
    }
    return Uuid.of(output.toByteString());
  }

  // TODO(dpcollins): Change this to use ByteStringCoder when importing to the beam repo.
  public static Coder<Uuid> getCoder() {
    return DelegateCoder.of(
        ByteArrayCoder.of(),
        (Uuid uuid) -> uuid.value().toByteArray(),
        (byte[] bytes) -> Uuid.of(ByteString.copyFrom(bytes)));
  }
}
