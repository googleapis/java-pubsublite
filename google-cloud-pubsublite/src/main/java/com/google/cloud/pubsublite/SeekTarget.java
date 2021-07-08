/*
 * Copyright 2021 Google LLC
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

import com.google.auto.value.AutoOneOf;
import com.google.protobuf.Timestamp;
import java.io.Serializable;

/** The target location to seek a subscription to. */
@AutoOneOf(SeekTarget.Kind.class)
public abstract class SeekTarget implements Serializable {
  public enum Kind {
    BACKLOG_LOCATION,
    PUBLISH_TIME,
    EVENT_TIME,
  }

  public abstract SeekTarget.Kind getKind();

  public abstract BacklogLocation backlogLocation();

  public abstract Timestamp publishTime();

  public abstract Timestamp eventTime();

  /** Seek to a named backlog location. */
  public static SeekTarget of(BacklogLocation location) {
    return AutoOneOf_SeekTarget.backlogLocation(location);
  }

  /** Seek to a message publish timestamp. */
  public static SeekTarget ofPublishTime(Timestamp time) {
    return AutoOneOf_SeekTarget.publishTime(time);
  }

  /** Seek to a message event timestamp. */
  public static SeekTarget ofEventTime(Timestamp time) {
    return AutoOneOf_SeekTarget.eventTime(time);
  }
}
