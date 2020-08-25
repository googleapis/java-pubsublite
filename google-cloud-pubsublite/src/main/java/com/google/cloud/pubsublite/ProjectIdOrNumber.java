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

import com.google.auto.value.AutoOneOf;
import java.io.Serializable;

@AutoOneOf(ProjectIdOrNumber.Kind.class)
public abstract class ProjectIdOrNumber implements Serializable {
  enum Kind {
    NAME,
    NUMBER
  }

  public abstract Kind getKind();

  public abstract ProjectId name();

  public abstract ProjectNumber number();

  public static ProjectIdOrNumber of(ProjectId name) {
    return AutoOneOf_ProjectIdOrNumber.name(name);
  }

  public static ProjectIdOrNumber of(ProjectNumber number) {
    return AutoOneOf_ProjectIdOrNumber.number(number);
  }

  @Override
  public String toString() {
    switch (getKind()) {
      case NAME:
        return name().toString();
      case NUMBER:
        return number().toString();
      default:
        throw new RuntimeException("Unknown case for ProjectIdOrNumber.");
    }
  }
}
