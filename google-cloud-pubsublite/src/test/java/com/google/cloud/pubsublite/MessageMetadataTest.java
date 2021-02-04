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

package com.google.cloud.pubsublite;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

import com.google.api.gax.rpc.ApiException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class MessageMetadataTest {
  @Test
  public void roundTripThroughString() {
    MessageMetadata metadata = MessageMetadata.of(Partition.of(10), Offset.of(20));
    MessageMetadata metadata2 = MessageMetadata.decode(metadata.encode());
    assertThat(metadata2).isEqualTo(metadata);
  }

  @Test
  public void invalidString() {
    assertThrows(ApiException.class, () -> MessageMetadata.decode("999"));
  }

  @Test
  public void invalidPartition() {
    assertThrows(ApiException.class, () -> MessageMetadata.decode("abc:999"));
  }

  @Test
  public void invalidOffset() {
    assertThrows(ApiException.class, () -> MessageMetadata.decode("999:abc"));
  }
}
