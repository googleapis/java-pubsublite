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

import static com.google.cloud.pubsublite.internal.Preconditions.checkArgument;

import com.google.auto.value.AutoValue;
import io.grpc.StatusException;
import java.io.Serializable;
import java.util.Arrays;

/**
 * A string wrapper representing a topic. Should be structured like:
 *
 * <p>projects/&lt;project number&gt;/locations/&lt;cloud zone&gt;/topics/&lt;id&gt;
 */
@AutoValue
public abstract class TopicPath implements Serializable {
  public abstract LocationPath location();

  public abstract TopicName name();

  @Override
  public String toString() {
    return location() + "/topics/" + name();
  }

  public static TopicPath of(LocationPath location, TopicName name) {
    return new AutoValue_TopicPath(location, name);
  }

  public static TopicPath parse(String path) throws StatusException {
    String[] splits = path.split("/");
    checkArgument(splits.length == 6);
    checkArgument(splits[4].equals("topics"));
    LocationPath location = LocationPath.parse(String.join("/", Arrays.copyOf(splits, 4)));
    return TopicPath.of(location, TopicName.of(splits[5]));
  }
}
