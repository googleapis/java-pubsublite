/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.pubsublite.v1;

import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.httpjson.testing.MockHttpService;
import com.google.cloud.pubsublite.v1.stub.HttpJsonPartitionAssignmentServiceStub;
import java.io.IOException;
import javax.annotation.Generated;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

@Generated("by gapic-generator-java")
public class PartitionAssignmentServiceClientHttpJsonTest {
  private static MockHttpService mockService;
  private static PartitionAssignmentServiceClient client;

  @BeforeClass
  public static void startStaticServer() throws IOException {
    mockService =
        new MockHttpService(
            HttpJsonPartitionAssignmentServiceStub.getMethodDescriptors(),
            PartitionAssignmentServiceSettings.getDefaultEndpoint());
    PartitionAssignmentServiceSettings settings =
        PartitionAssignmentServiceSettings.newHttpJsonBuilder()
            .setTransportChannelProvider(
                PartitionAssignmentServiceSettings.defaultHttpJsonTransportProviderBuilder()
                    .setHttpTransport(mockService)
                    .build())
            .setCredentialsProvider(NoCredentialsProvider.create())
            .build();
    client = PartitionAssignmentServiceClient.create(settings);
  }

  @AfterClass
  public static void stopServer() {
    client.close();
  }

  @Before
  public void setUp() {}

  @After
  public void tearDown() throws Exception {
    mockService.reset();
  }

  @Test
  public void assignPartitionsUnsupportedMethodTest() throws Exception {
    // The assignPartitions() method is not supported in REST transport.
    // This empty test is generated for technical reasons.
  }
}
