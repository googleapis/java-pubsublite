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

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth8.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.api.core.ApiFutures;
import com.google.api.core.ApiService;
import com.google.api.core.SettableApiFuture;
import com.google.cloud.pubsublite.CloudRegion;
import com.google.cloud.pubsublite.CloudZone;
import com.google.cloud.pubsublite.Message;
import com.google.cloud.pubsublite.Offset;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.ProjectNumber;
import com.google.cloud.pubsublite.PublishMetadata;
import com.google.cloud.pubsublite.TopicName;
import com.google.cloud.pubsublite.TopicPaths;
import com.google.cloud.pubsublite.internal.ExtractStatus;
import com.google.cloud.pubsublite.internal.Publisher;
import com.google.common.collect.ImmutableList;
import com.google.protobuf.ByteString;
import io.grpc.Status;
import io.grpc.StatusException;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.beam.sdk.Pipeline.PipelineExecutionException;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.ParDo;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.stubbing.Answer;

@RunWith(JUnit4.class)
public class PubsubLiteSinkTest {
  @Rule public final TestPipeline pipeline = TestPipeline.create();

  @SuppressWarnings("unchecked")
  private final Publisher<PublishMetadata> publisher = mock(Publisher.class);

  private final PublisherOptions defaultOptions() {
    try {
      return PublisherOptions.newBuilder()
          .setTopicPath(
              TopicPaths.newBuilder()
                  .setProjectNumber(ProjectNumber.of(9))
                  .setTopicName(TopicName.of("abc"))
                  .setZone(CloudZone.create(CloudRegion.create("us-east1"), 'a'))
                  .build())
          .build();
    } catch (StatusException e) {
      throw e.getStatus().asRuntimeException();
    }
  }

  private final PubsubLiteSink sink = new PubsubLiteSink(defaultOptions());

  // Initialized in setUp.
  private ApiService.Listener listener;

  @Captor
  final ArgumentCaptor<Message> publishedMessageCaptor = ArgumentCaptor.forClass(Message.class);

  private void runWith(Message... messages) {
    pipeline
        .apply(Create.of(ImmutableList.copyOf(messages)).withCoder(new MessageCoder()))
        .apply(ParDo.of(sink));
    pipeline.run();
  }

  @Before
  public void setUp() throws Exception {
    PerServerPublisherCache.cache.set(defaultOptions(), publisher);
    doAnswer(
            (Answer<Void>)
                args -> {
                  listener = args.getArgument(0);
                  return null;
                })
        .when(publisher)
        .addListener(any(), any());
    sink.setup();
    verify(publisher).addListener(any(), any());
  }

  @Test
  public void singleMessagePublishes() throws Exception {
    when(publisher.publish(Message.builder().build()))
        .thenReturn(
            ApiFutures.immediateFuture(
                PublishMetadata.create(Partition.create(1), Offset.create(2))));
    runWith(Message.builder().build());
    verify(publisher).publish(Message.builder().build());
  }

  @Test
  public void manyMessagePublishes() throws Exception {
    Message message1 = Message.builder().build();
    Message message2 = Message.builder().setKey(ByteString.copyFromUtf8("abc")).build();
    when(publisher.publish(message1))
        .thenReturn(
            ApiFutures.immediateFuture(
                PublishMetadata.create(Partition.create(1), Offset.create(2))));
    when(publisher.publish(message2))
        .thenReturn(
            ApiFutures.immediateFuture(
                PublishMetadata.create(Partition.create(85), Offset.create(3))));
    runWith(message1, message2);
    verify(publisher, times(2)).publish(publishedMessageCaptor.capture());
    assertThat(publishedMessageCaptor.getAllValues()).containsExactly(message1, message2);
  }

  @Test
  public void singleExceptionWhenProcessing() {
    Message message1 = Message.builder().build();
    when(publisher.publish(message1))
        .thenReturn(ApiFutures.immediateFailedFuture(Status.INTERNAL.asException()));
    PipelineExecutionException e =
        assertThrows(PipelineExecutionException.class, () -> runWith(message1));
    verify(publisher).publish(message1);
    Optional<Status> statusOr = ExtractStatus.extract(e.getCause());
    assertThat(statusOr).isPresent();
    assertThat(statusOr.get().getCode()).isEqualTo(Status.Code.INTERNAL);
  }

  @Test
  public void exceptionMixedWithOK() throws Exception {
    Message message1 = Message.builder().build();
    Message message2 = Message.builder().setKey(ByteString.copyFromUtf8("abc")).build();
    Message message3 = Message.builder().setKey(ByteString.copyFromUtf8("def")).build();
    when(publisher.publish(message1))
        .thenReturn(
            ApiFutures.immediateFuture(
                PublishMetadata.create(Partition.create(1), Offset.create(2))));
    when(publisher.publish(message2))
        .thenReturn(ApiFutures.immediateFailedFuture(Status.INTERNAL.asException()));
    when(publisher.publish(message3))
        .thenReturn(
            ApiFutures.immediateFuture(
                PublishMetadata.create(Partition.create(1), Offset.create(3))));
    PipelineExecutionException e =
        assertThrows(PipelineExecutionException.class, () -> runWith(message1, message2, message3));
    verify(publisher, times(3)).publish(publishedMessageCaptor.capture());
    assertThat(publishedMessageCaptor.getAllValues()).containsExactly(message1, message2, message3);
    Optional<Status> statusOr = ExtractStatus.extract(e.getCause());
    assertThat(statusOr).isPresent();
    assertThat(statusOr.get().getCode()).isEqualTo(Status.Code.INTERNAL);
  }

  @Test
  public void listenerExceptionOnBundleFinish() throws Exception {
    Message message1 = Message.builder().build();
    SettableApiFuture<PublishMetadata> future = SettableApiFuture.create();

    SettableApiFuture<Void> publishFuture = SettableApiFuture.create();
    when(publisher.publish(message1))
        .thenAnswer(
            args -> {
              publishFuture.set(null);
              return future;
            });
    Future<?> executorFuture =
        Executors.newSingleThreadExecutor()
            .submit(
                () -> {
                  PipelineExecutionException e =
                      assertThrows(PipelineExecutionException.class, () -> runWith(message1));
                  Optional<Status> statusOr = ExtractStatus.extract(e.getCause());
                  assertThat(statusOr).isPresent();
                  assertThat(statusOr.get().getCode()).isEqualTo(Status.Code.INTERNAL);
                });
    publishFuture.get();
    listener.failed(null, Status.INTERNAL.asException());
    future.set(PublishMetadata.create(Partition.create(1), Offset.create(2)));
    executorFuture.get();
  }
}
