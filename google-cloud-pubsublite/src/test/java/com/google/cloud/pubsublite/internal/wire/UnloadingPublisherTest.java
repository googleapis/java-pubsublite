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

package com.google.cloud.pubsublite.internal.wire;

import static com.google.cloud.pubsublite.internal.testing.RetryingConnectionHelpers.whenFailed;
import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import com.google.api.core.ApiFuture;
import com.google.api.core.SettableApiFuture;
import com.google.cloud.pubsublite.Message;
import com.google.cloud.pubsublite.MessageMetadata;
import com.google.cloud.pubsublite.internal.AlarmFactory;
import com.google.cloud.pubsublite.internal.Publisher;
import com.google.cloud.pubsublite.internal.testing.FakeApiService;
import com.google.protobuf.ByteString;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.function.Supplier;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

@RunWith(JUnit4.class)
public class UnloadingPublisherTest {
  private static final Message MESSAGE =
      Message.builder().setData(ByteString.copyFromUtf8("abc")).build();

  @Rule public MockitoRule mockito = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

  @Mock private Supplier<Publisher<MessageMetadata>> supplier;

  abstract static class FakePublisher extends FakeApiService
      implements Publisher<MessageMetadata> {};

  @Spy private FakePublisher underlying1;
  @Spy private FakePublisher underlying2;

  @Mock private AlarmFactory alarmFactory;
  private Runnable onAlarm;

  private UnloadingPublisher publisher;

  @Before
  public void setUp() {
    when(alarmFactory.newAlarm(any()))
        .thenAnswer(
            args -> {
              onAlarm = args.getArgument(0);
              return SettableApiFuture.create();
            });
    publisher = new UnloadingPublisher(supplier, alarmFactory);
    Objects.requireNonNull(onAlarm);
    publisher.startAsync().awaitRunning();
  }

  @Test
  public void loadOnPublish() {
    doReturn(underlying1).when(supplier).get();
    SettableApiFuture<MessageMetadata> future = SettableApiFuture.create();
    doReturn(future).when(underlying1).publish(MESSAGE);

    assertThat(publisher.publish(MESSAGE)).isSameInstanceAs(future);
  }

  @Test
  public void underlyingFailureFails() throws Exception {
    doReturn(underlying1).when(supplier).get();
    doReturn(SettableApiFuture.create()).when(underlying1).publish(MESSAGE);
    ApiFuture<?> unused = publisher.publish(MESSAGE);

    Future<Void> failed = whenFailed(publisher);
    underlying1.fail(new IllegalStateException("bad"));
    failed.get();
  }

  @Test
  public void twoAlarmsWithoutPublishShutsDown() throws Exception {
    doReturn(underlying1).when(supplier).get();
    doReturn(SettableApiFuture.create()).when(underlying1).publish(MESSAGE);
    ApiFuture<?> future1 = publisher.publish(MESSAGE);

    onAlarm.run();

    assertThat(underlying1.isRunning()).isTrue();

    ApiFuture<?> future2 = publisher.publish(MESSAGE);
    onAlarm.run();
    assertThat(underlying1.isRunning()).isTrue();

    onAlarm.run();
    assertThat(underlying1.isRunning()).isFalse();
  }
}
