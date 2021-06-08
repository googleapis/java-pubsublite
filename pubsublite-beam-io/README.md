# Instructions for PubsubLiteIO usage.

NOTE: Drain is not currently supported on `PubsubLiteIO`. Once Beam 2.29.0 is released, this will be supported
only when `--experiments=use_runner_v2` is enabled. Please contact dataflow customer support to request adding projects
into the allowlist and launch the pipeline with `--experiments=use_runner_v2`.

1. Add the following to your `pom.xml` file to download the Pub/Sub Lite I/O.
```xml
<dependency>
   <groupId>com.google.cloud</groupId>
   <artifactId>pubsublite-beam-io</artifactId>
   <version>0.14.2</version>
</dependency>
```
1. Create a topic using [`gcloud pubsub lite-topics create`](https://cloud.google.com/sdk/gcloud/reference/pubsub/lite-topics/create)
1. Write some messages using:

    ```java
    import com.google.cloud.pubsublite.beam.PubsubLiteIO;
    import com.google.cloud.pubsublite.beam.PublisherOptions;
    import com.google.cloud.pubsublite.proto.PubSubMessage;
    import com.google.cloud.pubsublite.CloudZone;
    import com.google.cloud.pubsublite.ProjectIdOrNumber;
    import com.google.cloud.pubsublite.TopicName;
    import com.google.cloud.pubsublite.TopicPath;
    
    ...
    
    private final static String LOCATION = "us-central1-b";
    private final static Long PROJECT_NUM = 123L;
    
    ...
    
    PCollection<PubSubMessage> messages = ...;
    messages.apply("Write messages", PubsubLiteIO.write(
        PublisherOptions.newBuilder()
            .setTopicPath(TopicPath.newBuilder()
                .setLocation(CloudZone.parse(ZONE))
                .setProject(ProjectIdOrNumber.of(PROJECT_NUM))
                .setName(TopicName.of("my-topic"))
                .build())
            .build()));
    ```
1. Create a subscription using [`gcloud pubsub lite-subscriptions create`](https://cloud.google.com/sdk/gcloud/reference/pubsub/subscriptions/create)
1. Read some messages using:

    ```java
    import com.google.cloud.pubsublite.beam.PubsubLiteIO;
    import com.google.cloud.pubsublite.beam.SubscriberOptions;
    import com.google.cloud.pubsublite.proto.SequencedMessage;
    import com.google.cloud.pubsublite.CloudZone;
    import com.google.cloud.pubsublite.ProjectIdOrNumber;
    import com.google.cloud.pubsublite.SubscriptionName;
    import com.google.cloud.pubsublite.SubscriptionPath;
    
    ...
    
    private final static String LOCATION = "us-central1-b";
    private final static Long PROJECT_NUM = 123L;
    
    ...
    
    Pipeline pipeline = ...;
    PCollection<SequencedMessage> messages = pipeline.apply("Read messages", PubsubLiteIO.read(
        SubscriberOptions.newBuilder()
            .setSubscriptionPath(SubscriptionPath.newBuilder()
                .setLocation(CloudZone.parse(LOCATION))
                .setProject(ProjectIdOrNumber.of(PROJECT_NUM))
                .setName(SubscriptionName.of("my-sub"))
                .build())
            .build()));
    ```

### Known issues and workarounds
1. If you encounter `IllegalAccessError` as shown below, place the Pub/Sub Lite
 I/O connector as the first dependency before all other Beam dependencies in
  your `pom.xml`.

    >`java.lang.IllegalAccessError: failed to access class com.google.cloud.
    pubsublite.internal.wire.RoutingMetadata from class com.google.cloud.
    pubsublite.beam.SubscriberOptions (com.google.cloud.pubsublite.internal.
    wire.RoutingMetadata and com.google.cloud.pubsublite.beam.SubscriberOptions
    are in unnamed module of loader java.net.URLClassLoader @ff2266c)`

1. Aggregate transforms after fixed windowing will not emit elements in the
DirectRunner for Pub/Sub Lite I/O unless you explicitly fire after watermark
triggers, or use processing-time triggers instead.
