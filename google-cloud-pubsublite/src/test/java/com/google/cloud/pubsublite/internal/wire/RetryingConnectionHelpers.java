package com.google.cloud.pubsublite.internal.wire;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

import com.google.api.core.ApiService.Listener;
import com.google.api.core.SettableApiFuture;
import java.util.concurrent.Future;

class RetryingConnectionHelpers {
  static Future<Void> whenFailed(Listener mockListener) {
    SettableApiFuture<Void> future = SettableApiFuture.create();
    doAnswer(
            args -> {
              future.set(null);
              return null;
            })
        .when(mockListener)
        .failed(any(), any());
    return future;
  }
}
