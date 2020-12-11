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

import java.util.function.Supplier;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;

/** A thread-safe, lazily constructed instance of an object. */
public class Lazy<T> {
  @GuardedBy("this")
  private @Nullable T instance = null;

  private final Supplier<T> supplier;

  public Lazy(Supplier<T> supplier) {
    this.supplier = supplier;
  }

  public synchronized T get() {
    if (instance == null) instance = supplier.get();
    return instance;
  }
}
