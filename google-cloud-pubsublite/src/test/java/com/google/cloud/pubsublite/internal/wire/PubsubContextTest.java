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

package com.google.cloud.pubsublite.internal.wire;

import static com.google.common.truth.Truth.assertThat;

import com.google.cloud.pubsublite.internal.wire.PubsubContext.Framework;
import com.google.protobuf.Struct;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class PubsubContextTest {
  @Test
  public void withFramework_hasFramework() {
    Struct contextStruct = PubsubContext.of(Framework.of("ABC_FRAMEWORK")).getStruct();
    assertThat(contextStruct.getFieldsOrThrow(PubsubContext.LANGUAGE_KEY).getStringValue())
        .isEqualTo(PubsubContext.LANGUAGE_VALUE);
    assertThat(contextStruct.getFieldsOrThrow(PubsubContext.FRAMEWORK_KEY).getStringValue())
        .isEqualTo("ABC_FRAMEWORK");
    assertThat(contextStruct.getFieldsOrThrow(PubsubContext.MAJOR_VERSION_KEY).getNumberValue())
        .isEqualTo(Versions.MAJOR_VERSION);
    assertThat(contextStruct.getFieldsOrThrow(PubsubContext.MINOR_VERSION_KEY).getNumberValue())
        .isEqualTo(Versions.MINOR_VERSION);
  }

  @Test
  public void noFramework_noFramework() {
    Struct contextStruct = PubsubContext.of().getStruct();
    assertThat(contextStruct.getFieldsOrThrow(PubsubContext.LANGUAGE_KEY).getStringValue())
        .isEqualTo(PubsubContext.LANGUAGE_VALUE);
    assertThat(contextStruct.containsFields(PubsubContext.FRAMEWORK_KEY)).isFalse();
    assertThat(contextStruct.getFieldsOrThrow(PubsubContext.MAJOR_VERSION_KEY).getNumberValue())
        .isEqualTo(Versions.MAJOR_VERSION);
    assertThat(contextStruct.getFieldsOrThrow(PubsubContext.MINOR_VERSION_KEY).getNumberValue())
        .isEqualTo(Versions.MINOR_VERSION);
  }
}
