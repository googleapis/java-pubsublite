# Google Cloud Pub/Sub Lite Client for Java

Java idiomatic client for [Cloud Pub/Sub Lite][product-docs].

[![Maven][maven-version-image]][maven-version-link]
![Stability][stability-image]

- [Product Documentation][product-docs]
- [Client Library Documentation][javadocs]

> Note: This client is a work-in-progress, and may occasionally
> make backwards-incompatible changes.

## Quickstart


If you are using Maven, add this to your pom.xml file:

```xml
<dependency>
  <groupId>com.google.cloud</groupId>
  <artifactId>google-cloud-pubsublite</artifactId>
  <version>0.1.4</version>
</dependency>
<dependency>
  <groupId>com.google.cloud</groupId>
  <artifactId>google-cloud-pubsub</artifactId>
  <version>1.105.0</version>
</dependency>
<!-- A logging dependency used by the underlying library  -->
<dependency>
  <groupId>com.google.flogger</groupId>
  <artifactId>flogger-system-backend</artifactId>
  <version>0.5.1</version>
  <scope>runtime</scope>
</dependency>

```

[//]: # ({x-version-update-start:google-cloud-pubsublite:released})

If you are using Gradle, add this to your dependencies
```Groovy
compile 'com.google.cloud:google-cloud-pubsublite:0.1.4'
```
If you are using SBT, add this to your dependencies
```Scala
libraryDependencies += "com.google.cloud" % "google-cloud-pubsublite" % "0.1.4"
```
[//]: # ({x-version-update-end})

## Authentication

See the [Authentication][authentication] section in the base directory's README.

## Getting Started

### Prerequisites

You will need a [Google Cloud Platform Console][developer-console] project with the Cloud Pub/Sub Lite [API enabled][enable-api].
You will need to [enable billing][enable-billing] to use Google Cloud Pub/Sub Lite.
[Follow these instructions][create-project] to get your project set up. You will also need to set up the local development environment by
[installing the Google Cloud SDK][cloud-sdk] and running the following commands in command line:
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
CloudRegion region = CloudRegion.of("us-central1");
TopicPath topicPath =
    TopicPaths.newBuilder()
      .setZone(CloudZone.of(region, 'b'))
      .setProjectNumber(ProjectNumber.of(123L)) // Your project number.
      .setTopicName(TopicName.of("my-new-topic"))
      .build();

Topic topic =
    Topic.newBuilder()
      .setPartitionConfig(
        PartitionConfig.newBuilder()
          .setScale(1) // Set publishing throughput to 1*4 MiB per sec. This must be 1-4.
          .setCount(2)) // The number of partitions.
      .setRetentionConfig(
        RetentionConfig.newBuilder()
          .setPeriod(Durations.fromDays(7))
          .setPerPartitionBytes(100000000000L)) // 100 GiB. This must be 30 GiB-10 TiB.
      .setName(topicPath.value())
      .build();

AdminClientSettings settings =
    AdminClientSettings.newBuilder().setRegion(region).build();
try (AdminClient client = AdminClient.create(settings)) {
  Topic response = client.createTopic(topic).get();
  System.out.println(response.getAllFields());
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
public class PublisherExample {
  private static final int MESSAGE_COUNT = 10;

  // Load the project number from a commandline flag.
  private static final long PROJECT_NUMBER = 123L;
  // Load the zone from a commandline flag.
  private static final String ZONE = "us-central1-b";
  // Load the topic name from a commandline flag.
  private static final String TOPIC_NAME = "my-new-topic";

  public static List<ApiFuture<String>> runPublisher(Publisher publisher) throws Exception {
    List<ApiFuture<String>> futures = new ArrayList<>();
    for (int i = 0; i < MESSAGE_COUNT; i++) {
      String message = "message-" + i;

      // Convert the message to a byte string.
      ByteString data = ByteString.copyFromUtf8(message);
      PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();

      // Schedule a message to be published. Messages are automatically batched.
      ApiFuture<String> future = publisher.publish(pubsubMessage);
      futures.add(future);
    }
    return futures;
  }

  // Publish messages to a topic.
  public static void main(String[] args) throws Exception {
    PublisherSettings settings =
        PublisherSettings.newBuilder()
            .setTopicPath(
                TopicPaths.newBuilder()
                    .setProjectNumber(ProjectNumber.of(PROJECT_NUMBER))
                    .setZone(CloudZone.parse(ZONE))
                    .setTopicName(TopicName.of(TOPIC_NAME))
                    .build())
            .build();
    Publisher publisher = Publisher.create(settings);
    publisher.startAsync().awaitRunning();
    List<ApiFuture<String>> futureAckIds = runPublisher(publisher);
    publisher.stopAsync().awaitTerminated();

    List<String> ackIds = ApiFutures.allAsList(futureAckIds).get();
    ArrayList<PublishMetadata> metadata = new ArrayList<>();
    for (String id : ackIds) {
      metadata.add(PublishMetadata.decode(id));
    }
    for (PublishMetadata one : metadata) {
      System.out.println(one);
    }
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
// CloudZone must be the zone of the topic.
CloudRegion region = CloudRegion.of("us-central1");
CloudZone zone = CloudZone.of(region, 'b');
ProjectNumber projectNum = ProjectNumber.of(123L);
TopicName topicName = TopicName.of("my-new-topic");
SubscriptionName subscriptionName = SubscriptionName.of("my-new-sub");

TopicPath topicPath =
TopicPaths.newBuilder()
  .setZone(zone)
  .setProjectNumber(projectNum)
  .setTopicName(topicName)
  .build();

SubscriptionPath subscriptionPath =
SubscriptionPaths.newBuilder()
  .setZone(zone)
  .setProjectNumber(projectNum)
  .setSubscriptionName(subscriptionName)
  .build();

Subscription subscription =
Subscription.newBuilder()
  .setDeliveryConfig(
    DeliveryConfig.newBuilder()
      .setDeliveryRequirement(DeliveryRequirement.DELIVER_IMMEDIATELY))
  .setName(subscriptionPath.value())
  .setTopic(topicPath.value())
  .build();

AdminClientSettings settings =
    AdminClientSettings.newBuilder().setRegion(region).build();
try (AdminClient client = AdminClient.create(settings)) {
  Subscription response = client.createSubscription(subscription).get();
  System.out.println(response.getAllFields());
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
CloudZone zone = CloudZone.parse("us-central1-b");
ProjectNumber projectNum = ProjectNumber.of(123L);
SubscriptionName subscriptionName = SubscriptionName.of("my-new-sub");

SubscriptionPath subscriptionPath =
    SubscriptionPaths.newBuilder()
      .setZone(zone)
      .setProjectNumber(projectNum)
      .setSubscriptionName(subscriptionName)
      .build();

FlowControlSettings flowControlSettings =
    FlowControlSettings.builder()
        .setBytesOutstanding(10 * 1024 * 1024L) // 10 MiB per partition.
        .setMessagesOutstanding(Long.MAX_VALUE)
        .build();

// Connect to partitions 0, 1. Note that we configured the topic with 2
// partitions.
List<Partition> partitions = Arrays.asList(Partition.of(0), Partition.of(1));

MessageReceiver receiver =
  new MessageReceiver() {
    @Override
    public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) {
      System.out.println("got message: " + message.getData().toStringUtf8());
      if (isValid(message)) {
        consumer.ack();
      } else {
        // 'nack' is not valid by default and will result in Subscriber failure,
        // but this behavior can be configured.
        consumer.nack();  // Fail the subscriber with a permanent error.
      }
    }
  };

Subscriber subscriber = null;
try {
  subscriber = Subscriber.create(SubscriberSettings.newBuilder()
         .setSubscriptionPath(subscriptionPath)
         .setPerPartitionFlowControlSettings(flowControlSettings)
         .setReceiver(receiver)
         .setPartitions(partitions)
         .build());
  subscriber.addListener(
    new Subscriber.Listener() {
      @Override
      public void failed(Subscriber.State from, Throwable failure) {
        // Handle failure. This is called when the Subscriber encountered a
        // fatal error and is shutting down.
        System.err.println(failure);
      }
    },
    MoreExecutors.directExecutor());
  subscriber.startAsync().awaitRunning();
  //...
} finally {
  if (subscriber != null) {
    subscriber.stopAsync().awaitTerminated();
  }
}
```




## Samples

Samples are in the [`samples/`](https://github.com/googleapis/java-pubsublite/tree/master/samples) directory. The samples' `README.md`
has instructions for running the samples.

| Sample                      | Source Code                       | Try it |
| --------------------------- | --------------------------------- | ------ |
| Create Subscription Example | [source code](https://github.com/googleapis/java-pubsublite/blob/master/samples/snippets/src/main/java/pubsublite/CreateSubscriptionExample.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-pubsublite&page=editor&open_in_editor=samples/snippets/src/main/java/pubsublite/CreateSubscriptionExample.java) |
| Create Topic Example | [source code](https://github.com/googleapis/java-pubsublite/blob/master/samples/snippets/src/main/java/pubsublite/CreateTopicExample.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-pubsublite&page=editor&open_in_editor=samples/snippets/src/main/java/pubsublite/CreateTopicExample.java) |
| Delete Subscription Example | [source code](https://github.com/googleapis/java-pubsublite/blob/master/samples/snippets/src/main/java/pubsublite/DeleteSubscriptionExample.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-pubsublite&page=editor&open_in_editor=samples/snippets/src/main/java/pubsublite/DeleteSubscriptionExample.java) |
| Delete Topic Example | [source code](https://github.com/googleapis/java-pubsublite/blob/master/samples/snippets/src/main/java/pubsublite/DeleteTopicExample.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-pubsublite&page=editor&open_in_editor=samples/snippets/src/main/java/pubsublite/DeleteTopicExample.java) |
| Get Subscription Example | [source code](https://github.com/googleapis/java-pubsublite/blob/master/samples/snippets/src/main/java/pubsublite/GetSubscriptionExample.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-pubsublite&page=editor&open_in_editor=samples/snippets/src/main/java/pubsublite/GetSubscriptionExample.java) |
| Get Topic Example | [source code](https://github.com/googleapis/java-pubsublite/blob/master/samples/snippets/src/main/java/pubsublite/GetTopicExample.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-pubsublite&page=editor&open_in_editor=samples/snippets/src/main/java/pubsublite/GetTopicExample.java) |
| List Subscriptions In Project Example | [source code](https://github.com/googleapis/java-pubsublite/blob/master/samples/snippets/src/main/java/pubsublite/ListSubscriptionsInProjectExample.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-pubsublite&page=editor&open_in_editor=samples/snippets/src/main/java/pubsublite/ListSubscriptionsInProjectExample.java) |
| List Subscriptions In Topic Example | [source code](https://github.com/googleapis/java-pubsublite/blob/master/samples/snippets/src/main/java/pubsublite/ListSubscriptionsInTopicExample.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-pubsublite&page=editor&open_in_editor=samples/snippets/src/main/java/pubsublite/ListSubscriptionsInTopicExample.java) |
| List Topics Example | [source code](https://github.com/googleapis/java-pubsublite/blob/master/samples/snippets/src/main/java/pubsublite/ListTopicsExample.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-pubsublite&page=editor&open_in_editor=samples/snippets/src/main/java/pubsublite/ListTopicsExample.java) |
| Publisher Example | [source code](https://github.com/googleapis/java-pubsublite/blob/master/samples/snippets/src/main/java/pubsublite/PublisherExample.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-pubsublite&page=editor&open_in_editor=samples/snippets/src/main/java/pubsublite/PublisherExample.java) |
| Subscriber Example | [source code](https://github.com/googleapis/java-pubsublite/blob/master/samples/snippets/src/main/java/pubsublite/SubscriberExample.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-pubsublite&page=editor&open_in_editor=samples/snippets/src/main/java/pubsublite/SubscriberExample.java) |
| Update Subscription Example | [source code](https://github.com/googleapis/java-pubsublite/blob/master/samples/snippets/src/main/java/pubsublite/UpdateSubscriptionExample.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-pubsublite&page=editor&open_in_editor=samples/snippets/src/main/java/pubsublite/UpdateSubscriptionExample.java) |
| Update Topic Example | [source code](https://github.com/googleapis/java-pubsublite/blob/master/samples/snippets/src/main/java/pubsublite/UpdateTopicExample.java) | [![Open in Cloud Shell][shell_img]](https://console.cloud.google.com/cloudshell/open?git_repo=https://github.com/googleapis/java-pubsublite&page=editor&open_in_editor=samples/snippets/src/main/java/pubsublite/UpdateTopicExample.java) |



## Troubleshooting

To get help, follow the instructions in the [shared Troubleshooting document][troubleshooting].

## Transport

Cloud Pub/Sub Lite uses gRPC for the transport layer.

## Java Versions

Java 7 or above is required for using this client.

## Versioning


This library follows [Semantic Versioning](http://semver.org/).


It is currently in major version zero (``0.y.z``), which means that anything may change at any time
and the public API should not be considered stable.

## Contributing


Contributions to this library are always welcome and highly encouraged.

See [CONTRIBUTING][contributing] for more information how to get started.

Please note that this project is released with a Contributor Code of Conduct. By participating in
this project you agree to abide by its terms. See [Code of Conduct][code-of-conduct] for more
information.

## License

Apache 2.0 - See [LICENSE][license] for more information.

## CI Status

Java Version | Status
------------ | ------
Java 7 | [![Kokoro CI][kokoro-badge-image-1]][kokoro-badge-link-1]
Java 8 | [![Kokoro CI][kokoro-badge-image-2]][kokoro-badge-link-2]
Java 8 OSX | [![Kokoro CI][kokoro-badge-image-3]][kokoro-badge-link-3]
Java 8 Windows | [![Kokoro CI][kokoro-badge-image-4]][kokoro-badge-link-4]
Java 11 | [![Kokoro CI][kokoro-badge-image-5]][kokoro-badge-link-5]

[product-docs]: https://cloud.google.com/pubsub
[javadocs]: https://googleapis.dev/java/google-cloud-pubsublite/latest/index.html
[kokoro-badge-image-1]: http://storage.googleapis.com/cloud-devrel-public/java/badges/java-pubsublite/java7.svg
[kokoro-badge-link-1]: http://storage.googleapis.com/cloud-devrel-public/java/badges/java-pubsublite/java7.html
[kokoro-badge-image-2]: http://storage.googleapis.com/cloud-devrel-public/java/badges/java-pubsublite/java8.svg
[kokoro-badge-link-2]: http://storage.googleapis.com/cloud-devrel-public/java/badges/java-pubsublite/java8.html
[kokoro-badge-image-3]: http://storage.googleapis.com/cloud-devrel-public/java/badges/java-pubsublite/java8-osx.svg
[kokoro-badge-link-3]: http://storage.googleapis.com/cloud-devrel-public/java/badges/java-pubsublite/java8-osx.html
[kokoro-badge-image-4]: http://storage.googleapis.com/cloud-devrel-public/java/badges/java-pubsublite/java8-win.svg
[kokoro-badge-link-4]: http://storage.googleapis.com/cloud-devrel-public/java/badges/java-pubsublite/java8-win.html
[kokoro-badge-image-5]: http://storage.googleapis.com/cloud-devrel-public/java/badges/java-pubsublite/java11.svg
[kokoro-badge-link-5]: http://storage.googleapis.com/cloud-devrel-public/java/badges/java-pubsublite/java11.html
[stability-image]: https://img.shields.io/badge/stability-beta-yellow
[maven-version-image]: https://img.shields.io/maven-central/v/com.google.cloud/google-cloud-pubsublite.svg
[maven-version-link]: https://search.maven.org/search?q=g:com.google.cloud%20AND%20a:google-cloud-pubsublite&core=gav
[authentication]: https://github.com/googleapis/google-cloud-java#authentication
[developer-console]: https://console.developers.google.com/
[create-project]: https://cloud.google.com/resource-manager/docs/creating-managing-projects
[cloud-sdk]: https://cloud.google.com/sdk/
[troubleshooting]: https://github.com/googleapis/google-cloud-common/blob/master/troubleshooting/readme.md#troubleshooting
[contributing]: https://github.com/googleapis/java-pubsublite/blob/master/CONTRIBUTING.md
[code-of-conduct]: https://github.com/googleapis/java-pubsublite/blob/master/CODE_OF_CONDUCT.md#contributor-code-of-conduct
[license]: https://github.com/googleapis/java-pubsublite/blob/master/LICENSE
[enable-billing]: https://cloud.google.com/apis/docs/getting-started#enabling_billing
[enable-api]: https://console.cloud.google.com/flows/enableapi?apiid=pubsublite.googleapis.com
[libraries-bom]: https://github.com/GoogleCloudPlatform/cloud-opensource-java/wiki/The-Google-Cloud-Platform-Libraries-BOM
[shell_img]: https://gstatic.com/cloudssh/images/open-btn.png
