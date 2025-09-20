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

package com.google.cloud.pubsublite.cloudpubsub.internal;

import static com.google.cloud.pubsublite.internal.testing.RetryingConnectionHelpers.whenTerminated;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.google.api.core.ApiService.State;
import com.google.cloud.pubsublite.Partition;
import com.google.cloud.pubsublite.cloudpubsub.ReassignmentHandler;
import com.google.cloud.pubsublite.cloudpubsub.Subscriber;
import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.internal.testing.FakeApiService;
import com.google.cloud.pubsublite.internal.wire.Assigner;
import com.google.cloud.pubsublite.internal.wire.AssignerFactory;
import com.google.cloud.pubsublite.internal.wire.PartitionAssignmentReceiver;
import com.google.common.collect.ImmutableSet;
import io.grpc.Status;
import java.util.Set;
import java.util.concurrent.Future;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.stubbing.Answer;

@RunWith(JUnit4.class)
public class AssigningSubscriberTest {
  @Mock PartitionSubscriberFactory subscriberFactory;
  @Mock ReassignmentHandler reassignmentHandler;
  @Mock AssignerFactory assignerFactory;

  private AssigningSubscriber assigningSubscriber;

  abstract static class FakeAssigner extends FakeApiService implements Assigner {}

  @Spy FakeAssigner assigner;

  abstract static class FakeSubscriber extends FakeApiService implements Subscriber {}

  private PartitionAssignmentReceiver leakedReceiver;

  @Before
  public void setUp() {
    initMocks(this);
    when(assignerFactory.New(any()))
        .then(
            args -> {
              leakedReceiver = args.getArgument(0);
              return assigner;
            });
    assigningSubscriber =
        new AssigningSubscriber(subscriberFactory, reassignmentHandler, assignerFactory);
    verify(assignerFactory).New(any());
    assertThat(leakedReceiver).isNotNull();
    assigningSubscriber.startAsync().awaitRunning();
  }

  @Test
  public void startStop() {
    assigningSubscriber.stopAsync().awaitTerminated();
    verify(assigner).stopAsync();
  }

  @Test
  public void failedCreate() throws CheckedApiException {
    when(subscriberFactory.newSubscriber(Partition.of(1)))
        .thenThrow(new RuntimeException("Arbitrary error."));
    leakedReceiver.handleAssignment(ImmutableSet.of(Partition.of(1)));
    verify(subscriberFactory).newSubscriber(Partition.of(1));
    assertThrows(IllegalStateException.class, assigningSubscriber::awaitTerminated);
  }

  @Test
  public void assignmentHandlerFailure() throws Exception {
    Subscriber sub1 = spy(FakeSubscriber.class);
    when(subscriberFactory.newSubscriber(Partition.of(1))).thenReturn(sub1);
    doThrow(new RuntimeException("Arbitrary error."))
        .when(reassignmentHandler)
        .handleReassignment(ImmutableSet.of(), ImmutableSet.of(Partition.of(1)));
    leakedReceiver.handleAssignment(ImmutableSet.of(Partition.of(1)));
    assertThrows(IllegalStateException.class, assigningSubscriber::awaitTerminated);
  }

  @Test
  public void assignmentHandlerReentrantSafe() throws Exception {
    Subscriber sub1 = spy(FakeSubscriber.class);
    when(subscriberFactory.newSubscriber(Partition.of(1))).thenReturn(sub1);
    doAnswer(
            (Answer<Void>)
                args -> {
                  assigningSubscriber.stopAsync().awaitTerminated();
                  return null;
                })
        .when(reassignmentHandler)
        .handleReassignment(ImmutableSet.of(), ImmutableSet.of(Partition.of(1)));
    leakedReceiver.handleAssignment(ImmutableSet.of(Partition.of(1)));
    assigningSubscriber.awaitTerminated();
  }

  @Test
  public void createSubscribers() throws CheckedApiException {
    Subscriber sub1 = spy(FakeSubscriber.class);
    when(subscriberFactory.newSubscriber(Partition.of(1))).thenReturn(sub1);
    leakedReceiver.handleAssignment(ImmutableSet.of(Partition.of(1)));
    InOrder order = inOrder(reassignmentHandler, subscriberFactory);
    order.verify(subscriberFactory).newSubscriber(Partition.of(1));
    order
        .verify(reassignmentHandler)
        .handleReassignment(ImmutableSet.of(), ImmutableSet.of(Partition.of(1)));
    verify(sub1).startAsync();
    reset(sub1);

    Subscriber sub2 = spy(FakeSubscriber.class);
    when(subscriberFactory.newSubscriber(Partition.of(2))).thenReturn(sub2);
    Set<Partition> newAssignment = ImmutableSet.of(Partition.of(1), Partition.of(2));
    leakedReceiver.handleAssignment(newAssignment);
    verify(subscriberFactory).newSubscriber(Partition.of(2));
    verify(reassignmentHandler).handleReassignment(ImmutableSet.of(Partition.of(1)), newAssignment);
    verify(sub2).startAsync();
    verifyNoMoreInteractions(sub1);
  }

  @Test
  public void createAndEvict() throws CheckedApiException {
    Subscriber sub1 = spy(FakeSubscriber.class);
    when(subscriberFactory.newSubscriber(Partition.of(1))).thenReturn(sub1);
    leakedReceiver.handleAssignment(ImmutableSet.of(Partition.of(1)));
    verify(subscriberFactory).newSubscriber(Partition.of(1));
    verify(reassignmentHandler)
        .handleReassignment(ImmutableSet.of(), ImmutableSet.of(Partition.of(1)));
    verify(sub1).startAsync();
    State sub1State = sub1.state();
    assertThat(State.STARTING.equals(sub1State) || State.RUNNING.equals(sub1State)).isTrue();
    reset(sub1);

    Subscriber sub2 = spy(FakeSubscriber.class);
    when(subscriberFactory.newSubscriber(Partition.of(2))).thenReturn(sub2);
    doAnswer(
            (Answer<Void>)
                args -> {
                  // sub1 should already be stopped by the time the reassignmentHandler is called
                  assertThat(sub1.state()).isEqualTo(State.TERMINATED);
                  return null;
                })
        .when(reassignmentHandler)
        .handleReassignment(ImmutableSet.of(Partition.of(1)), ImmutableSet.of(Partition.of(2)));
    leakedReceiver.handleAssignment(ImmutableSet.of(Partition.of(2)));
    verify(subscriberFactory).newSubscriber(Partition.of(2));
    verify(reassignmentHandler)
        .handleReassignment(ImmutableSet.of(Partition.of(1)), ImmutableSet.of(Partition.of(2)));
    verify(sub2).startAsync();
    verify(sub1).stopAsync();
    verify(sub1).awaitTerminated();
  }

  private Subscriber initSub1() throws CheckedApiException {
    Subscriber sub1 = spy(FakeSubscriber.class);
    when(subscriberFactory.newSubscriber(Partition.of(1))).thenReturn(sub1);
    leakedReceiver.handleAssignment(ImmutableSet.of(Partition.of(1)));
    verify(subscriberFactory).newSubscriber(Partition.of(1));
    verify(sub1).startAsync();
    reset(sub1);
    return sub1;
  }

  @Test
  public void stopStopsSubs() throws CheckedApiException {
    Subscriber sub1 = initSub1();

    assigningSubscriber.stopAsync().awaitTerminated();
    verify(sub1).stopAsync();
    verify(sub1).awaitTerminated();
  }

  @Ignore
  @Test
  public void assignerErrorFailsSubs() throws Exception {
    Subscriber sub1 = initSub1();
    Future<Void> terminated = whenTerminated(sub1);

    assigner.fail(Status.INVALID_ARGUMENT.asException());
    terminated.get();
    verify(sub1).stopAsync();
    verify(sub1).awaitTerminated();
  }
}
