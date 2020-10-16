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

import static com.google.cloud.pubsublite.internal.UncheckedApiPreconditions.checkArgument;

import com.google.api.gax.rpc.ApiException;
import com.google.auto.value.AutoValue;
import java.io.Serializable;

/**
 * A wrapper class for the <a
 * href="https://cloud.google.com/resource-manager/docs/creating-managing-projects#before_you_begin">project
 * number</a>.
 */
@AutoValue
public abstract class ProjectNumber implements Serializable {
  /** The long value of this project number. */
  public abstract long value();

  @Override
  public String toString() {
    return Long.toString(value());
  }

  /** Construct a ProjectNumber from its long value. */
  public static ProjectNumber of(long value) throws ApiException {
    checkArgument(value > 0);
    return new AutoValue_ProjectNumber(value);
  }
}
