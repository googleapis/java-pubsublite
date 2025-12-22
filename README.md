# Google Cloud Pub/Sub Lite Client for Java

Java idiomatic client for [Cloud Pub/Sub Lite][product-docs].

[![Maven][maven-version-image]][maven-version-link]
![Stability][stability-image]

- [Product Documentation][product-docs]
- [Client Library Documentation][javadocs]


## Quickstart


If you are using Maven, add this to your pom.xml file:


```xml
<dependency>
  <groupId>com.google.cloud</groupId>
  <artifactId>google-cloud-pubsublite</artifactId>
  <version>1.15.6</version>
</dependency>
<dependency>
  <groupId>com.google.cloud</groupId>
  <artifactId>google-cloud-pubsub</artifactId>
  <version>1.144.0</version>
</dependency>

```

If you are using Gradle without BOM, add this to your dependencies:

```Groovy
implementation 'com.google.cloud:google-cloud-pubsublite:1.15.20'
```

If you are using SBT, add this to your dependencies:

```Scala
libraryDependencies += "com.google.cloud" % "google-cloud-pubsublite" % "1.15.20"
```

## Authentication

See the [Authentication][authentication] section in the base directory's README.

## Authorization

The client application making API calls must be granted [authorization scopes][auth-scopes] required for the desired Cloud Pub/Sub Lite APIs, and the authenticated principal must have the [IAM role(s)][predefined-iam-roles] required to access GCP resources using the Cloud Pub/Sub Lite API calls.

## Getting Started

### Prerequisites

You will need a [Google Cloud Platform Console][developer-console] project with the Cloud Pub/Sub Lite [API enabled][enable-api].
You will need to [enable billing][enable-billing] to use Google Cloud Pub/Sub Lite.
[Follow these instructions][create-project] to get your project set up. You will also need to set up the local development environment by
[installing the Google Cloud Command Line Interface][cloud-cli] and running the following commands in command line:
`gcloud auth login` and `gcloud config set project [YOUR PROJECT ID]`.

### Installation and setup

You'll need to obtain the `google-cloud-pubsublite` library.  See the [Quickstart](#quickstart) section
to add `google-cloud-pubsublite` as a dependency in your code.

## About Cloud Pub/Sub Lite

[Google Pub/Sub Lite][product-docs] is designed to provide reliable,
many-to-many, asynchronous messaging between applications. Publisher
applications can send messages to a topic and other applications can
subscribe to that topic to receive the messages. By decoupling senders and
receivers, Google Cloud Pub/Sub allows developers to communicate between
independently written applications.

Compared to Google Pub/Sub, Pub/Sub Lite provides partitioned zonal data
storage with predefined capacity. Both products present a similar API, but
Pub/Sub Lite has more usage caveats.

See the [Google Pub/Sub Lite docs](https://cloud.google.com/pubsub/quickstart-console#before-you-begin) for more details on how to activate
Pub/Sub Lite for your project, as well as guidance on how to choose between
Cloud Pub/Sub and Pub/Sub Lite.


#### Creating a topic

With Pub/Sub Lite you can create topics. A topic is a named resource to which messages are sent by
publishers. Add the following imports at the top of your file:

```java
import com.google.cloud.pubsublite.*;
import com.google.cloud.pubsublite.proto.Topic;
import com.google.cloud.pubsublite.proto.Topic.*;
import com.google.protobuf.util.Durations;
```
Then, to create the topic, use the following code:

```java
// TODO(developer): Replace these variables with your own.
long projectNumber = 123L;
String cloudRegion = "us-central1";
char zoneId = 'b';
String topicId = "your-topic-id";
Integer partitions = 1;

TopicPath topicPath =
    TopicPath.newBuilder()
        .setProject(ProjectNumber.of(projectNumber))
        .setLocation(CloudZone.of(CloudRegion.of(cloudRegion), zoneId))
        .setName(TopicName.of(topicId))
        .build();

Topic topic =
    Topic.newBuilder()
        .setPartitionConfig(
            PartitionConfig.newBuilder()
                // Set publishing throughput to 1 times the standard partition
                // throughput of 4 MiB per sec. This must be in the range [1,4]. A
                // topic with `scale` of 2 and count of 10 is charged for 20 partitions.
                .setScale(1)
                .setCount(partitions))
        .setRetentionConfig(
            RetentionConfig.newBuilder()
                // How long messages are retained.
                .setPeriod(Durations.fromDays(1))
                // Set storage per partition to 30 GiB. This must be 30 GiB-10 TiB.
                // If the number of bytes stored in any of the topic's partitions grows
                // beyond this value, older messages will be dropped to make room for
                // newer ones, regardless of the value of `period`.
                .setPerPartitionBytes(30 * 1024 * 1024 * 1024L))
        .setName(topicPath.toString())
        .build();

AdminClientSettings adminClientSettings =
    AdminClientSettings.newBuilder().setRegion(CloudRegion.of(cloudRegion)).build();

try (AdminClient adminClient = AdminClient.create(adminClientSettings)) {
  Topic response = adminClient.createTopic(topic).get();
  System.out.println(response.getAllFields() + "created successfully.");
}
```

#### Publishing messages

With Pub/Sub Lite, you can publish messages to a topic. Add the following import at the top of your file:

```java
import com.google.api.core.*;
import com.google.cloud.pubsublite.*;
import com.google.cloud.pubsublite.cloudpubsub.*;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import java.util.*;
```
Then, to publish messages asynchronously, use the following code:

```java
// TODO(developer): Replace these variables before running the sample.
long projectNumber = 123L;
String cloudRegion = "us-central1";
char zoneId = 'b';
// Choose an existing topic.
String topicId = "your-topic-id";
int messageCount = 100;

TopicPath topicPath =
    TopicPath.newBuilder()
        .setProject(ProjectNumber.of(projectNumber))
        .setLocation(CloudZone.of(CloudRegion.of(cloudRegion), zoneId))
        .setName(TopicName.of(topicId))
        .build();
Publisher publisher = null;
List<ApiFuture<String>> futures = new ArrayList<>();

try {
  PublisherSettings publisherSettings =
      PublisherSettings.newBuilder().setTopicPath(topicPath).build();

  publisher = Publisher.create(publisherSettings);

  // Start the publisher. Upon successful starting, its state will become RUNNING.
  publisher.startAsync().awaitRunning();

  for (int i = 0; i < messageCount; i++) {
    String message = "message-" + i;

    // Convert the message to a byte string.
    ByteString data = ByteString.copyFromUtf8(message);
    PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();

    // Publish a message. Messages are automatically batched.
    ApiFuture<String> future = publisher.publish(pubsubMessage);
    futures.add(future);
  }
} finally {
  ArrayList<MessageMetadata> metadata = new ArrayList<>();
  List<String> ackIds = ApiFutures.allAsList(futures).get();
  for (String id : ackIds) {
    // Decoded metadata contains partition and offset.
    metadata.add(MessageMetadata.decode(id));
  }
  System.out.println(metadata + "\nPublished " + ackIds.size() + " messages.");

  if (publisher != null) {
    // Shut down the publisher.
    publisher.stopAsync().awaitTerminated();
    System.out.println("Publisher is shut down.");
  }
}
```

#### Creating a subscription

With Pub/Sub Lite you can create subscriptions. A subscription represents the stream of messages from a
single, specific topic. Add the following imports at the top of your file:

```java
import com.google.cloud.pubsublite.*;
import com.google.cloud.pubsublite.proto.Subscription;
import com.google.cloud.pubsublite.proto.Subscription.*;
import com.google.cloud.pubsublite.proto.Subscription.DeliveryConfig.*;
```
Then, to create the subscription, use the following code:

```java
// TODO(developer): Replace these variables with your own.
long projectNumber = 123L;
String cloudRegion = "us-central1";
char zoneId = 'b';
// Choose an existing topic.
String topicId = "your-topic-id";
String subscriptionId = "your-subscription-id";

TopicPath topicPath =
    TopicPath.newBuilder()
        .setProject(ProjectNumber.of(projectNumber))
        .setLocation(CloudZone.of(CloudRegion.of(cloudRegion), zoneId))
        .setName(TopicName.of(topicId))
        .build();

SubscriptionPath subscriptionPath =
    SubscriptionPath.newBuilder()
        .setLocation(CloudZone.of(CloudRegion.of(cloudRegion), zoneId))
        .setProject(ProjectNumber.of(projectNumber))
        .setName(SubscriptionName.of(subscriptionId))
        .build();

Subscription subscription =
    Subscription.newBuilder()
        .setDeliveryConfig(
            // The server does not wait for a published message to be successfully
            // written to storage before delivering it to subscribers. As such, a
            // subscriber may receive a message for which the write to storage failed.
            // If the subscriber re-reads the offset of that message later on, there
            // may be a gap at that offset.
            DeliveryConfig.newBuilder()
                .setDeliveryRequirement(DeliveryRequirement.DELIVER_IMMEDIATELY))
        .setName(subscriptionPath.toString())
        .setTopic(topicPath.toString())
        .build();

AdminClientSettings adminClientSettings =
    AdminClientSettings.newBuilder().setRegion(CloudRegion.of(cloudRegion)).build();

try (AdminClient adminClient = AdminClient.create(adminClientSettings)) {
  Subscription response = adminClient.createSubscription(subscription).get();
  System.out.println(response.getAllFields() + "created successfully.");
}
```

#### Receiving messages

With Pub/Sub Lite you can receive messages from a subscription. Add the
following imports at the top of your file:

```java
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsublite.*;
import com.google.cloud.pubsublite.cloudpubsub.*;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.pubsub.v1.PubsubMessage;
import java.util.*;
```
Then, to pull messages asynchronously, use the following code:

```java
// TODO(developer): Replace these variables with your own.
long projectNumber = 123L;
String cloudRegion = "us-central1";
char zoneId = 'b';
// Choose an existing topic.
String topicId = "your-topic-id";
// Choose an existing subscription.
String subscriptionId = "your-subscription-id";

SubscriptionPath subscriptionPath =
    SubscriptionPath.newBuilder()
        .setLocation(CloudZone.of(CloudRegion.of(cloudRegion), zoneId))
        .setProject(ProjectNumber.of(projectNumber))
        .setName(SubscriptionName.of(subscriptionId))
        .build();

// The message stream is paused based on the maximum size or number of messages that the
// subscriber has already received, whichever condition is met first.
FlowControlSettings flowControlSettings =
    FlowControlSettings.builder()
        // 10 MiB. Must be greater than the allowed size of the largest message (1 MiB).
        .setBytesOutstanding(10 * 1024 * 1024L)
        // 1,000 outstanding messages. Must be >0.
        .setMessagesOutstanding(1000L)
        .build();

MessageReceiver receiver =
    (PubsubMessage message, AckReplyConsumer consumer) -> {
      System.out.println("Id : " + message.getMessageId());
      System.out.println("Data : " + message.getData().toStringUtf8());
      consumer.ack();
    };

SubscriberSettings subscriberSettings =
    SubscriberSettings.newBuilder()
        .setSubscriptionPath(subscriptionPath)
        .setReceiver(receiver)
        // Flow control settings are set at the partition level.
        .setPerPartitionFlowControlSettings(flowControlSettings)
        .build();

Subscriber subscriber = Subscriber.create(subscriberSettings);

// Start the subscriber. Upon successful starting, its state will become RUNNING.
subscriber.startAsync().awaitRunning();

System.out.println("Listening to messages on " + subscriptionPath.toString() + "...");

try {
  System.out.println(subscriber.state());
  // Wait 90 seconds for the subscriber to reach TERMINATED state. If it encounters
  // unrecoverable errors before then, its state will change to FAILED and an
  // IllegalStateException will be thrown.
  subscriber.awaitTerminated(90, TimeUnit.SECONDS);
} catch (TimeoutException t) {
  // Shut down the subscriber. This will change the state of the subscriber to TERMINATED.
  subscriber.stopAsync().awaitTerminated();
  System.out.println("Subscriber is shut down: " + subscriber.state());
}
```




## Samples

Samples are in the [`samples/`](https://github.com/googleapis/java-pubsublite/tree/main/samples) directory.

| Sample                      | Source Code                       | Try it |
| --------------------------- | --------------------------------- | ------ |
| Create Pubsub Export Subscription Example | [source code](https://github.com/googleapis/java-pubsublite/blob/main/samples/snippets/src/main/java/pubsublite/CreatePubsubExportSubscriptionExample.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-pubsublite&page=editor&open_in_editor=samples/snippets/src/main/java/pubsublite/CreatePubsubExportSubscriptionExample.java) |
| Create Reservation Example | [source code](https://github.com/googleapis/java-pubsublite/blob/main/samples/snippets/src/main/java/pubsublite/CreateReservationExample.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-pubsublite&page=editor&open_in_editor=samples/snippets/src/main/java/pubsublite/CreateReservationExample.java) |
| Create Subscription Example | [source code](https://github.com/googleapis/java-pubsublite/blob/main/samples/snippets/src/main/java/pubsublite/CreateSubscriptionExample.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-pubsublite&page=editor&open_in_editor=samples/snippets/src/main/java/pubsublite/CreateSubscriptionExample.java) |
| Create Topic Example | [source code](https://github.com/googleapis/java-pubsublite/blob/main/samples/snippets/src/main/java/pubsublite/CreateTopicExample.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-pubsublite&page=editor&open_in_editor=samples/snippets/src/main/java/pubsublite/CreateTopicExample.java) |
| Delete Reservation Example | [source code](https://github.com/googleapis/java-pubsublite/blob/main/samples/snippets/src/main/java/pubsublite/DeleteReservationExample.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-pubsublite&page=editor&open_in_editor=samples/snippets/src/main/java/pubsublite/DeleteReservationExample.java) |
| Delete Subscription Example | [source code](https://github.com/googleapis/java-pubsublite/blob/main/samples/snippets/src/main/java/pubsublite/DeleteSubscriptionExample.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-pubsublite&page=editor&open_in_editor=samples/snippets/src/main/java/pubsublite/DeleteSubscriptionExample.java) |
| Delete Topic Example | [source code](https://github.com/googleapis/java-pubsublite/blob/main/samples/snippets/src/main/java/pubsublite/DeleteTopicExample.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-pubsublite&page=editor&open_in_editor=samples/snippets/src/main/java/pubsublite/DeleteTopicExample.java) |
| Get Reservation Example | [source code](https://github.com/googleapis/java-pubsublite/blob/main/samples/snippets/src/main/java/pubsublite/GetReservationExample.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-pubsublite&page=editor&open_in_editor=samples/snippets/src/main/java/pubsublite/GetReservationExample.java) |
| Get Subscription Example | [source code](https://github.com/googleapis/java-pubsublite/blob/main/samples/snippets/src/main/java/pubsublite/GetSubscriptionExample.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-pubsublite&page=editor&open_in_editor=samples/snippets/src/main/java/pubsublite/GetSubscriptionExample.java) |
| Get Topic Example | [source code](https://github.com/googleapis/java-pubsublite/blob/main/samples/snippets/src/main/java/pubsublite/GetTopicExample.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-pubsublite&page=editor&open_in_editor=samples/snippets/src/main/java/pubsublite/GetTopicExample.java) |
| List Reservations Example | [source code](https://github.com/googleapis/java-pubsublite/blob/main/samples/snippets/src/main/java/pubsublite/ListReservationsExample.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-pubsublite&page=editor&open_in_editor=samples/snippets/src/main/java/pubsublite/ListReservationsExample.java) |
| List Subscriptions In Project Example | [source code](https://github.com/googleapis/java-pubsublite/blob/main/samples/snippets/src/main/java/pubsublite/ListSubscriptionsInProjectExample.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-pubsublite&page=editor&open_in_editor=samples/snippets/src/main/java/pubsublite/ListSubscriptionsInProjectExample.java) |
| List Subscriptions In Topic Example | [source code](https://github.com/googleapis/java-pubsublite/blob/main/samples/snippets/src/main/java/pubsublite/ListSubscriptionsInTopicExample.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-pubsublite&page=editor&open_in_editor=samples/snippets/src/main/java/pubsublite/ListSubscriptionsInTopicExample.java) |
| List Topics Example | [source code](https://github.com/googleapis/java-pubsublite/blob/main/samples/snippets/src/main/java/pubsublite/ListTopicsExample.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-pubsublite&page=editor&open_in_editor=samples/snippets/src/main/java/pubsublite/ListTopicsExample.java) |
| Publish With Batch Settings Example | [source code](https://github.com/googleapis/java-pubsublite/blob/main/samples/snippets/src/main/java/pubsublite/PublishWithBatchSettingsExample.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-pubsublite&page=editor&open_in_editor=samples/snippets/src/main/java/pubsublite/PublishWithBatchSettingsExample.java) |
| Publish With Custom Attributes Example | [source code](https://github.com/googleapis/java-pubsublite/blob/main/samples/snippets/src/main/java/pubsublite/PublishWithCustomAttributesExample.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-pubsublite&page=editor&open_in_editor=samples/snippets/src/main/java/pubsublite/PublishWithCustomAttributesExample.java) |
| Publish With Ordering Key Example | [source code](https://github.com/googleapis/java-pubsublite/blob/main/samples/snippets/src/main/java/pubsublite/PublishWithOrderingKeyExample.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-pubsublite&page=editor&open_in_editor=samples/snippets/src/main/java/pubsublite/PublishWithOrderingKeyExample.java) |
| Publisher Example | [source code](https://github.com/googleapis/java-pubsublite/blob/main/samples/snippets/src/main/java/pubsublite/PublisherExample.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-pubsublite&page=editor&open_in_editor=samples/snippets/src/main/java/pubsublite/PublisherExample.java) |
| Seek Subscription Example | [source code](https://github.com/googleapis/java-pubsublite/blob/main/samples/snippets/src/main/java/pubsublite/SeekSubscriptionExample.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-pubsublite&page=editor&open_in_editor=samples/snippets/src/main/java/pubsublite/SeekSubscriptionExample.java) |
| Subscriber Example | [source code](https://github.com/googleapis/java-pubsublite/blob/main/samples/snippets/src/main/java/pubsublite/SubscriberExample.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-pubsublite&page=editor&open_in_editor=samples/snippets/src/main/java/pubsublite/SubscriberExample.java) |
| Update Reservation Example | [source code](https://github.com/googleapis/java-pubsublite/blob/main/samples/snippets/src/main/java/pubsublite/UpdateReservationExample.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-pubsublite&page=editor&open_in_editor=samples/snippets/src/main/java/pubsublite/UpdateReservationExample.java) |
| Update Subscription Example | [source code](https://github.com/googleapis/java-pubsublite/blob/main/samples/snippets/src/main/java/pubsublite/UpdateSubscriptionExample.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-pubsublite&page=editor&open_in_editor=samples/snippets/src/main/java/pubsublite/UpdateSubscriptionExample.java) |
| Update Topic Example | [source code](https://github.com/googleapis/java-pubsublite/blob/main/samples/snippets/src/main/java/pubsublite/UpdateTopicExample.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-pubsublite&page=editor&open_in_editor=samples/snippets/src/main/java/pubsublite/UpdateTopicExample.java) |



## Troubleshooting

To get help, follow the instructions in the [shared Troubleshooting document][troubleshooting].

## Transport

Cloud Pub/Sub Lite uses gRPC for the transport layer.

## Supported Java Versions

Java 8 or above is required for using this client.

Google's Java client libraries,
[Google Cloud Client Libraries][cloudlibs]
and
[Google Cloud API Libraries][apilibs],
follow the
[Oracle Java SE support roadmap][oracle]
(see the Oracle Java SE Product Releases section).

### For new development

In general, new feature development occurs with support for the lowest Java
LTS version covered by  Oracle's Premier Support (which typically lasts 5 years
from initial General Availability). If the minimum required JVM for a given
library is changed, it is accompanied by a [semver][semver] major release.

Java 11 and (in September 2021) Java 17 are the best choices for new
development.

### Keeping production systems current

Google tests its client libraries with all current LTS versions covered by
Oracle's Extended Support (which typically lasts 8 years from initial
General Availability).

#### Legacy support

Google's client libraries support legacy versions of Java runtimes with long
term stable libraries that don't receive feature updates on a best efforts basis
as it may not be possible to backport all patches.

Google provides updates on a best efforts basis to apps that continue to use
Java 7, though apps might need to upgrade to current versions of the library
that supports their JVM.

#### Where to find specific information

The latest versions and the supported Java versions are identified on
the individual GitHub repository `github.com/GoogleAPIs/java-SERVICENAME`
and on [google-cloud-java][g-c-j].

## Versioning


This library follows [Semantic Versioning](http://semver.org/).



## Contributing


Contributions to this library are always welcome and highly encouraged.

See [CONTRIBUTING][contributing] for more information how to get started.

Please note that this project is released with a Contributor Code of Conduct. By participating in
this project you agree to abide by its terms. See [Code of Conduct][code-of-conduct] for more
information.


## License

Apache 2.0 - See [LICENSE][license] for more information.

Java is a registered trademark of Oracle and/or its affiliates.

[product-docs]: https://cloud.google.com/pubsub/lite/docs
[javadocs]: https://cloud.google.com/java/docs/reference/google-cloud-pubsublite/latest/history
[stability-image]: https://img.shields.io/badge/stability-stable-green
[maven-version-image]: https://img.shields.io/maven-central/v/com.google.cloud/google-cloud-pubsublite.svg
[maven-version-link]: https://central.sonatype.com/artifact/com.google.cloud/google-cloud-pubsublite/1.15.20
[authentication]: https://github.com/googleapis/google-cloud-java#authentication
[auth-scopes]: https://developers.google.com/identity/protocols/oauth2/scopes
[predefined-iam-roles]: https://cloud.google.com/iam/docs/understanding-roles#predefined_roles
[iam-policy]: https://cloud.google.com/iam/docs/overview#cloud-iam-policy
[developer-console]: https://console.developers.google.com/
[create-project]: https://cloud.google.com/resource-manager/docs/creating-managing-projects
[cloud-cli]: https://cloud.google.com/cli
[troubleshooting]: https://github.com/googleapis/google-cloud-java/blob/main/TROUBLESHOOTING.md
[contributing]: https://github.com/googleapis/java-pubsublite/blob/main/CONTRIBUTING.md
[code-of-conduct]: https://github.com/googleapis/java-pubsublite/blob/main/CODE_OF_CONDUCT.md#contributor-code-of-conduct
[license]: https://github.com/googleapis/java-pubsublite/blob/main/LICENSE
[enable-billing]: https://cloud.google.com/apis/docs/getting-started#enabling_billing
[enable-api]: https://console.cloud.google.com/flows/enableapi?apiid=pubsublite.googleapis.com
[libraries-bom]: https://github.com/GoogleCloudPlatform/cloud-opensource-java/wiki/The-Google-Cloud-Platform-Libraries-BOM
[shell_img]: https://gstatic.com/cloudssh/images/open-btn.png

[semver]: https://semver.org/
[cloudlibs]: https://cloud.google.com/apis/docs/client-libraries-explained
[apilibs]: https://cloud.google.com/apis/docs/client-libraries-explained#google_api_client_libraries
[oracle]: https://www.oracle.com/java/technologies/java-se-support-roadmap.html
[g-c-j]: http://github.com/googleapis/google-cloud-java
