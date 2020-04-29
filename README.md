# Java Client for Google Pub/Sub Lite

Java idiomatic client for Google Pub/Sub Lite.

## Quickstart

If you are using Maven, add this to your pom.xml file

[//]: # ({x-version-update-start:google-cloud-pubsub:released})
```xml
<dependency>
  <groupId>com.google.cloud</groupId>
  <artifactId>google-cloud-pubsublite</artifactId>
  <version>0.0.0</version>
</dependency>
```
If you are using Gradle, add this to your dependencies
```Groovy
compile 'com.google.cloud:google-cloud-pubsublite:0.0.0'
```
If you are using SBT, add this to your dependencies
```Scala
libraryDependencies += "com.google.cloud" % "google-cloud-pubsublite" % "0.0.0"
```
[//]: # ({x-version-update-end})

## Authentication

See the [Authentication][authentication] section in the base directory's README.

## About Google Pub/Sub Lite

[Google Pub/Sub Lite][api-reference] is designed to provide reliable,
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

## Getting Started

### Prerequisites

For this tutorial, you will need a
[Google Developers Console](https://console.developers.google.com/) project with the Pub/Sub Lite API
enabled.
[Follow these instructions](https://cloud.google.com/resource-manager/docs/creating-managing-projects) to get your
project set up. You will also need to set up the local development environment by [installing the
Google Cloud SDK](https://cloud.google.com/sdk/) and running the following commands in command line:
`gcloud auth login` and `gcloud config set project [YOUR PROJECT ID]`.

#### Installation and setup

You'll need to obtain the `google-cloud-pubsublite` library.  See the [Quickstart](#quickstart) section
to add `google-cloud-pubsublite` as a dependency in your code.

#### Creating an authorized service object

To make authenticated requests to Google Pub/Sub Lite, you must create a service object with
credentials. You can then make API calls by calling methods on the service object. The
simplest way to authenticate is to use
[Application Default Credentials](https://developers.google.com/identity/protocols/application-default-credentials).
These credentials are automatically inferred from your environment.

For other authentication options, see the
[Authentication](https://github.com/googleapis/google-cloud-java#authentication) page.

#### Creating a topic

With Pub/Sub Lite you can create topics. A topic is a named resource to which messages are sent by
publishers. Add the following imports at the top of your file:

```java
import com.google.cloud.pubsublite.*;
```
Then, to create the topic, use the following code:

```java
CloudRegion region = CloudRegion.create("us-central1");
TopicPath topicPath =
    TopicPaths.newBuilder()
      .setZone(CloudZone.create(region, 'b'))
      .setProjectNumber(ProjectNumber.of(123))
      .setTopicName(TopicName.of("my-new-topic"))
      .build();
    
Topic topic =
    Topic.newBuilder()
      .setPartitionConfig(
        PartitionConfig.newBuilder()
          .setScale(1) // Set publishing throughput to 1*4 MiB per sec. This must be 1-4.
          .setCount(PARTITIONS))
      .setRetentionConfig(
        RetentionConfig.newBuilder()
          .setPeriod(Durations.fromDays(7))
          .setPerPartitionBytes(100000000000L)) // 100 GiB. This must be 30 GiB-10 TiB.
      .setName(topicPath.value())
      .build();

AdminClient client = AdminClientBuilder.builder()
    .setRegion(region)
    .setExecutor(executor)
    .build();
client.createTopic(topic).get();
```

#### Publishing messages

With Pub/Sub Lite, you can publish messages to a topic. Add the following import at the top of your file:

```java
import com.google.cloud.pubsublite.*;
import com.google.cloud.pubsublite.cloudpubsub.*;
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

  public static List<ApiFuture<String>> runPublisher(PublisherInterface publisher) throws Exception {
    List<ApiFuture<String>> futures = new ArrayList<>();
    for (int i = 0; i < MESSAGE_COUNT; i++) {
      String message = "message-" + i;

      // convert message to bytes
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
    PublisherApiService publisherService =
        PublisherBuilder.newBuilder()
            .setTopicPath(
                TopicPaths.newBuilder()
                    .setProjectNumber(ProjectNumber.of(PROJECT_NUMBER))
                    .setZone(CloudZone.parse(ZONE))
                    .setTopicName(TopicName.of(TOPIC_NAME))
                    .build())
            .build();
    publisherService.startAsync().awaitRunning();
    List<ApiFuture<String>> futureAckIds = runPublisher(publisherService);
    publisherService.stopAsync().awaitTerminated();
    List<String> ackIds = ApiFutures.allAsList(futureAckIds).get();
    ArrayList<PublishMetadata> metadata = new ArrayList<>();
    for (String id : ackIds) {
      metadata.add(PublishMetadata.decode(id));
    }
    for (PublishMetadata one : metadata) {
      System.out.println(one.offset());
    }
  }
}
```

#### Creating a subscription

With Pub/Sub Lite you can create subscriptions. A subscription represents the stream of messages from a
single, specific topic. Add the following imports at the top of your file:

```java
import com.google.cloud.pubsublite.*;
```
Then, to create the subscription, use the following code:

```java
// CloudZone must be equivalent to the topic.
CloudRegion cloudRegion = CloudRegion.create("us-central1");
CloudZone zone = CloudZone.create(cloudRegion, 'b');
// ProjectNumber must the equivalent to the topic.
ProjectNumber projectNum = ProjectNumber.of(123);
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

AdminClient client = AdminClientBuilder.builder()
    .setRegion(region)
    .setExecutor(executor)
    .build();
client.createSubscription(subscription).get();
```

#### Receiving messages

With Pub/Sub Lite you can receive messages from a subscription. Add the
following imports at the top of your file:

```java
import com.google.cloud.pubsublite.*;
import com.google.cloud.pubsublite.cloudpubsub.*;
import com.google.pubsub.v1.MessageReceiver;
import com.google.pubsub.v1.SubscriberInterface;
```
Then, to pull messages asynchronously, use the following code:

```java
SubscriptionPath subscriptionPath =
SubscriptionPaths.newBuilder()
  .setZone(zone)
  .setProjectNumber(projectNum)
  .setSubscriptionName(subscriptionName)
  .build();
// Connect to partition 0.
Partition PARTITION = Partition.create(0);

FlowControlSettings flowControlSettings =
    FlowControlSettings.builder()
      .setBytesOutstanding(10_000_000) // 10 MB per partition.
      .setMessagesOutstanding(Long.MAX_VALUE)
      .build();


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

SubscriberInterface subscriber = null;
try {
  subscriber = SubscriberBuilder.newBuilder()
      .setSubscriptionPath(subscriptionPath)
      .setFlowControlSettings(flowControlSettings)
      .setReceiver(receiver)
      .setPartition(0)
      .build();
  subscriber.addListener(
    new Subscriber.Listener() {
      @Override
      public void failed(Subscriber.State from, Throwable failure) {
        // Handle failure. This is called when the Subscriber encountered a fatal error and is shutting down.
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

## Troubleshooting

To get help, follow the instructions in the [shared Troubleshooting document][troubleshooting].

## Transport

Google Pub/Sub Lite uses gRPC for the transport layer.

## Java Versions

Java 8 or above is required for using this client.

## Versioning

This library follows [Semantic Versioning](http://semver.org/).

It is currently in major version zero (``0.y.z``), which means that anything may change at any time
and the public API should not be considered stable.

## Contributing

Contributions to this library are always welcome and highly encouraged.

See [CONTRIBUTING.md][contributing] documentation for more information on how to get started.

Please note that this project is released with a Contributor Code of Conduct. By participating in
this project you agree to abide by its terms. See [Code of Conduct][code-of-conduct] for more
information.
