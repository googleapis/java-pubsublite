// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.cloud.pubsublite.internal.wire;

import com.google.cloud.pubsublite.SequencedMessage;
import com.google.cloud.pubsublite.proto.SeekRequest;
import com.google.common.collect.Ordering;
import java.util.Comparator;

public final class Predicates {
  public static boolean isOrdered(Iterable<SequencedMessage> messages) {
    return Ordering.from(Comparator.comparingLong((SequencedMessage m) -> m.offset().value()))
        .isStrictlyOrdered(messages);
  }

  public static boolean isValidSeekRequest(SeekRequest request) {
    switch (request.getTargetCase()) {
      case CURSOR:
        return request.getCursor().getOffset() >= 0;
      case NAMED_TARGET:
        switch (request.getNamedTarget()) {
          case HEAD:
          case COMMITTED_CURSOR:
            return true;
          default:
            return false;
        }
      default:
        return false;
    }
  }

  private Predicates() {}
}
