/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pubsublite;

import static com.google.common.truth.Truth.assertThat;
import static junit.framework.TestCase.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.UUID;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class UpdateTopicExampleIT {
  private ByteArrayOutputStream bout;
  private PrintStream out;

  private static final String GOOGLE_CLOUD_PROJECT_NUMBER =
      System.getenv("GOOGLE_CLOUD_PROJECT_NUMBER");
  private static final String CLOUD_REGION = "us-central1";
  private static final char ZONE_ID = 'b';
  private static final Long PROJECT_NUMBER = Long.parseLong(GOOGLE_CLOUD_PROJECT_NUMBER);
  private static final String SUFFIX = UUID.randomUUID().toString();
  private static final String TOPIC_NAME = "lite-topic-" + SUFFIX;
  private static final int PARTITIONS = 1;

  private static void requireEnvVar(String varName) {
    assertNotNull(
        "Environment variable " + varName + " is required to perform these tests.",
        System.getenv(varName));
  }

  @BeforeClass
  public static void checkRequirements() {
    requireEnvVar("GOOGLE_CLOUD_PROJECT_NUMBER");
  }

  @Before
  public void setUp() throws Exception {
    bout = new ByteArrayOutputStream();
    out = new PrintStream(bout);
    System.setOut(out);

    // Set up
    CreateTopicExample.createTopicExample(
        CLOUD_REGION, ZONE_ID, PROJECT_NUMBER, TOPIC_NAME, PARTITIONS);
  }

  @After
  public void tearDown() throws Exception {
    DeleteTopicExample.deleteTopicExample(CLOUD_REGION, ZONE_ID, PROJECT_NUMBER, TOPIC_NAME);
    System.setOut(null);
  }

  @Test
  public void testUpdateTopicExample() throws Exception {
    UpdateTopicExample.updateTopicExample(CLOUD_REGION, ZONE_ID, PROJECT_NUMBER, TOPIC_NAME);
    assertThat(bout.toString()).contains("seconds: 604800");
    assertThat(bout.toString()).contains("per_partition_bytes: 214748364800");
    assertThat(bout.toString()).contains("scale: 4");
  }
}
