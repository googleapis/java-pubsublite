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

import static com.google.cloud.pubsublite.internal.testing.UnitTestExamples.example;
import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.cloud.pubsublite.AdminClient;
import com.google.cloud.pubsublite.TopicPath;
import java.time.Duration;
import java.util.List;
import org.apache.kafka.common.PartitionInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;

@RunWith(JUnit4.class)
public class SharedBehaviorTest {
  @Mock AdminClient admin;

  @Before
  public void setUp() {
    initMocks(this);
  }

  @Test
  public void partitionsForSuccess() throws Exception {
    ApiFuture<Long> future = ApiFutures.immediateFuture(2L);
    when(admin.getTopicPartitionCount(example(TopicPath.class))).thenReturn(future);
    List<PartitionInfo> result =
        SharedBehavior.partitionsFor(admin, example(TopicPath.class), Duration.ofDays(1));
    assertThat(result.size()).isEqualTo(2);
    assertThat(result.get(0).topic()).isEqualTo(example(TopicPath.class).toString());
    assertThat(result.get(0).partition()).isEqualTo(0);
    assertThat(result.get(0).leader()).isEqualTo(PubsubLiteNode.NODE);
    assertThat(result.get(1).topic()).isEqualTo(example(TopicPath.class).toString());
    assertThat(result.get(1).partition()).isEqualTo(1);
    assertThat(result.get(1).leader()).isEqualTo(PubsubLiteNode.NODE);
  }
}
