/*
 * Copyright 2023 Google LLC
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

package com.google.cloud.pubsublite.internal.wire;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.google.api.core.ApiFutures;
import com.google.cloud.pubsublite.Message;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.internal.PublishSequenceNumber;
import com.google.cloud.pubsublite.internal.SequencedPublisher;
import com.google.cloud.pubsublite.internal.testing.FakeApiService;
import com.google.protobuf.ByteString;
import java.util.concurrent.Future;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Spy;

@RunWith(JUnit4.class)
public final class SequenceAssigningPublisherTest {

  private static Message makeMessage(String data) {
    return Message.builder().setData(ByteString.copyFromUtf8(data)).build();
  }

  abstract static class FakeSequencedPublisher extends FakeApiService
      implements SequencedPublisher<Offset> {}

  @Spy private FakeSequencedPublisher underlyingPublisher;

  private SequenceAssigningPublisher publisher;

  @Before
  public void setUp() {
    initMocks(this);
    publisher = new SequenceAssigningPublisher(underlyingPublisher);
  }

  @Test
  public void publishAssignsSequenceNumbers() throws Exception {
    Message message1 = makeMessage("msg1");
    Message message2 = makeMessage("msg2");
    Message message3 = makeMessage("msg3");

    when(underlyingPublisher.publish(eq(message1), eq(PublishSequenceNumber.of(0))))
        .thenReturn(ApiFutures.immediateFuture(Offset.of(100)));
    when(underlyingPublisher.publish(eq(message2), eq(PublishSequenceNumber.of(1))))
        .thenReturn(ApiFutures.immediateFuture(Offset.of(200)));
    when(underlyingPublisher.publish(eq(message3), eq(PublishSequenceNumber.of(2))))
        .thenReturn(ApiFutures.immediateFuture(Offset.of(300)));

    Future<Offset> future1 = publisher.publish(message1);
    verify(underlyingPublisher).publish(eq(message1), eq(PublishSequenceNumber.of(0)));
    Future<Offset> future2 = publisher.publish(message2);
    verify(underlyingPublisher).publish(eq(message2), eq(PublishSequenceNumber.of(1)));
    Future<Offset> future3 = publisher.publish(message3);
    verify(underlyingPublisher).publish(eq(message3), eq(PublishSequenceNumber.of(2)));

    assertThat(future1.isDone()).isTrue();
    assertThat(future1.get()).isEqualTo(Offset.of(100));
    assertThat(future2.isDone()).isTrue();
    assertThat(future2.get()).isEqualTo(Offset.of(200));
    assertThat(future3.isDone()).isTrue();
    assertThat(future3.get()).isEqualTo(Offset.of(300));
  }

  @Test
  public void cancelOutstandingPublishesDelegatesToUnderlying() throws Exception {
    publisher.cancelOutstandingPublishes();
    verify(underlyingPublisher).cancelOutstandingPublishes();
  }

  @Test
  public void flushDelegatesToUnderlying() throws Exception {
    publisher.flush();
    verify(underlyingPublisher).flush();
  }
}
