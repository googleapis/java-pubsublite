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

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.atomic.AtomicInteger;
import org.mockito.stubbing.Answer;

public class TestUtil {

  public static <T> Answer<Void> answerWith(T response) {
    return invocation -> {
      StreamObserver<T> responseObserver = invocation.getArgument(1);
      responseObserver.onNext(response);
      responseObserver.onCompleted();
      return null;
    };
  }

  public static Answer<Void> answerWith(Status status) {
    return invocation -> {
      StreamObserver<?> responseObserver = invocation.getArgument(1);
      responseObserver.onError(status.asRuntimeException());
      return null;
    };
  }

  public static Answer<Void> inOrder(Answer<Void>... answers) {
    AtomicInteger count = new AtomicInteger(0);
    return invocation -> {
      int index = count.getAndIncrement();

      return answers[index].answer(invocation);
    };
  }
}
