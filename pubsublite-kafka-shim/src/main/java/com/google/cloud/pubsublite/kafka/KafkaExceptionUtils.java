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

package com.google.cloud.pubsublite.kafka;

import com.google.cloud.pubsublite.internal.CheckedApiException;
import com.google.cloud.pubsublite.internal.ExtractStatus;
import java.util.concurrent.TimeoutException;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.AuthenticationException;
import org.apache.kafka.common.errors.AuthorizationException;
import org.apache.kafka.common.errors.BrokerNotAvailableException;
import org.apache.kafka.common.errors.InterruptException;
import org.apache.kafka.common.errors.InvalidRequestException;

class KafkaExceptionUtils {
  private KafkaExceptionUtils() {}

  static KafkaException toKafkaException(CheckedApiException source) {
    switch (source.code()) {
      case ABORTED:
        return new BrokerNotAvailableException("Aborted.", source);
      case ALREADY_EXISTS:
        return new KafkaException("Already exists.", source);
      case CANCELLED:
        return new BrokerNotAvailableException("Cancelled.", source);
      case DATA_LOSS:
        return new KafkaException("Data loss.", source);
      case DEADLINE_EXCEEDED:
        return new BrokerNotAvailableException("Deadline exceeded.", source);
      case FAILED_PRECONDITION:
        return new InvalidRequestException("Failed precondition.", source);
      case INTERNAL:
        return new BrokerNotAvailableException("Internal.", source);
      case INVALID_ARGUMENT:
        return new InvalidRequestException("Invalid argument.", source);
      case NOT_FOUND:
        return new KafkaException("Not found.", source);
      case OUT_OF_RANGE:
        return new KafkaException("Out of range.", source);
      case PERMISSION_DENIED:
        return new AuthorizationException("Permission denied.", source);
      case RESOURCE_EXHAUSTED:
        return new KafkaException("Resource exhausted.", source);
      case UNAUTHENTICATED:
        return new AuthenticationException("Unauthenticated.", source);
      case UNAVAILABLE:
        return new BrokerNotAvailableException("Unavailable.", source);
      case UNIMPLEMENTED:
        return new KafkaException("Unimplemented.", source);
      case UNKNOWN:
        return new KafkaException("Unknown.", source);
    }
    return new KafkaException("No case.", source);
  }

  /**
   * Transform an exception into a kind that is likely to be thrown through kafka interfaces.
   *
   * @param t A throwable to transform.
   * @return The transformed exception suitable for re-throwing.
   */
  static RuntimeException toKafka(Throwable t) {
    try {
      throw t;
    } catch (KafkaException | UnsupportedOperationException | IllegalStateException e) {
      return e;
    } catch (InterruptedException e) {
      return new InterruptException(e);
    } catch (TimeoutException e) {
      return new org.apache.kafka.common.errors.TimeoutException(e);
    } catch (Throwable e) {
      return KafkaExceptionUtils.toKafkaException(ExtractStatus.toCanonical(t));
    }
  }
}
