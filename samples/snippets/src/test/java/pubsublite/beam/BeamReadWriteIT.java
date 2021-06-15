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

package pubsublite.beam;

import static com.google.common.truth.Truth.assertThat;
import static junit.framework.Assert.fail;
import static junit.framework.TestCase.assertNotNull;

import com.google.cloud.pubsublite.proto.SequencedMessage;
import com.google.common.collect.ImmutableList;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.annotation.concurrent.GuardedBy;
import org.apache.beam.runners.direct.DirectOptions;
import org.apache.beam.runners.direct.DirectRunner;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.options.StreamingOptions;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.SimpleFunction;
import org.apache.beam.sdk.values.PCollection;
import org.joda.time.Duration;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import pubsublite.CreateSubscriptionExample;
import pubsublite.CreateTopicExample;
import pubsublite.DeleteSubscriptionExample;
import pubsublite.DeleteTopicExample;

public class BeamReadWriteIT {
  private ByteArrayOutputStream bout;
  private PrintStream out;
  Random rand = new Random();

  private static final Long projectNumber =
      Long.parseLong(System.getenv("GOOGLE_CLOUD_PROJECT_NUMBER"));
  private String cloudRegion = "us-central1";
  private final char zoneId = (char) (rand.nextInt(3) + 'a');
  private static final String suffix = UUID.randomUUID().toString();
  private static final String topicId = "lite-topic-" + suffix;
  private static final String subscriptionId = "lite-subscription-" + suffix;
  private static final int partitions = 2;
  private static final int messageCount = 50;

  private static void requireEnvVar(String varName) {
    assertNotNull(
        "Environment variable " + varName + " is required to perform these tests.",
        System.getenv(varName));
  }

  @Rule public Timeout globalTimeout = Timeout.seconds(300); // 5 minute timeout

  @BeforeClass
  public static void checkRequirements() {
    requireEnvVar("GOOGLE_CLOUD_PROJECT_NUMBER");
  }

  @Before
  public void setUp() throws Exception {
    bout = new ByteArrayOutputStream();
    out = new PrintStream(bout);
    System.setOut(out);

    // Create a topic.
    CreateTopicExample.createTopicExample(cloudRegion, zoneId, projectNumber, topicId, partitions);
    assertThat(bout.toString()).contains("created successfully");

    // Create a subscription.
    CreateSubscriptionExample.createSubscriptionExample(
        cloudRegion, zoneId, projectNumber, topicId, subscriptionId);
    assertThat(bout.toString()).contains("created successfully");
  }

  @After
  public void tearDown() throws Exception {
    // Delete a topic.
    DeleteTopicExample.deleteTopicExample(cloudRegion, zoneId, projectNumber, topicId);
    assertThat(bout.toString()).contains("deleted successfully");

    // Delete a subscription.
    DeleteSubscriptionExample.deleteSubscriptionExample(
        cloudRegion, zoneId, projectNumber, subscriptionId);
    assertThat(bout.toString()).contains("deleted successfully");

    System.setOut(null);
  }

  // This static out of band communication is needed to retain serializability.
  @GuardedBy("this")
  private static final List<SequencedMessage> testQuickstartReceived = new ArrayList<>();

  private static synchronized void addMessageTestQuickstartReceived(SequencedMessage message) {
    testQuickstartReceived.add(message);
  }

  private static synchronized List<SequencedMessage> getTestQuickstartReceived() {
    return ImmutableList.copyOf(testQuickstartReceived);
  }

  private static PTransform<PCollection<? extends SequencedMessage>, PCollection<Void>>
      collectTestQuickstart() {
    return MapElements.via(
        new SimpleFunction<SequencedMessage, Void>() {
          @Override
          public Void apply(SequencedMessage input) {
            BeamReadWriteIT.addMessageTestQuickstartReceived(input);
            return null;
          }
        });
  }

  @Test
  public void testQuickstart() throws Exception {
    StreamingOptions options = PipelineOptionsFactory.as(StreamingOptions.class);
    options.setRunner(DirectRunner.class);
    options.setStreaming(false);
    options.as(DirectOptions.class).setBlockOnRun(false);
    // Publish some messages
    {
      Pipeline p = Pipeline.create(options);
      WriteMessagesExample.writeMessagesExample(
          cloudRegion, zoneId, projectNumber, topicId, messageCount, p);
      // Message writing is bounded.
      assertThat(p.run().waitUntilFinish(Duration.standardMinutes(2)))
          .isEqualTo(PipelineResult.State.DONE);
    }

    // Read some messages. They should be deduplicated by the time we see them, so there should be
    // exactly numMessages, one for every index in [0,50).
    options.setStreaming(true);
    Pipeline p = Pipeline.create(options);
    PCollection<SequencedMessage> messages =
        ReadMessagesExample.readMessagesExample(
            cloudRegion, zoneId, projectNumber, subscriptionId, p);
    messages.apply("messageReceiver", collectTestQuickstart());
    p.run();
    System.err.println("Running!");
    for (int round = 0; round < 120; ++round) {
      Thread.sleep(1000);
      Map<Integer, Integer> receivedCounts = new HashMap<>();
      for (SequencedMessage message : getTestQuickstartReceived()) {
        int id = Integer.parseInt(message.getMessage().getData().toStringUtf8());
        receivedCounts.put(id, receivedCounts.getOrDefault(id, 0) + 1);
      }
      System.err.printf("Performing comparison round %s.\n", round);
      boolean done = true;
      for (int id = 0; id < messageCount; id++) {
        int idCount = receivedCounts.getOrDefault(id, 0);
        if (idCount != 1) {
          System.err.printf("Still missing message %s.\n", id);
          done = false;
        }
      }
      if (done) {
        return;
      }
    }
    fail(
        String.format(
            "Failed to receive all messages after 2 minutes. Received %s messages.",
            getTestQuickstartReceived().size()));
  }
}
