# Instructions for PubsubLiteIO usage.

The minimum version you should use for Pub/Sub Lite IO with beam is 2.34.0.

1. Add the following to your `pom.xml` file to download the Pub/Sub Lite I/O 
   included with Beam.
```xml
<dependency>
   <groupId>org.apache.beam</groupId>
   <artifactId>beam-sdks-java-io-google-cloud-platform</artifactId>
   <version>2.34.0</version>
</dependency>
```
1. Create a topic using [`gcloud pubsub lite-topics create`](https://cloud.google.com/sdk/gcloud/reference/pubsub/lite-topics/create)
1. Write some messages using:

    ```java
    import com.google.cloud.pubsublite.proto.PubSubMessage;
    import com.google.cloud.pubsublite.CloudZone;
    import com.google.cloud.pubsublite.ProjectIdOrNumber;
    import com.google.cloud.pubsublite.TopicName;
    import com.google.cloud.pubsublite.TopicPath;
    import org.apache.beam.sdk.io.gcp.pubsublite.PubsubLiteIO;
    import org.apache.beam.sdk.io.gcp.pubsublite.PublisherOptions;
    
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
    import com.google.cloud.pubsublite.proto.SequencedMessage;
    import com.google.cloud.pubsublite.CloudZone;
    import com.google.cloud.pubsublite.ProjectIdOrNumber;
    import com.google.cloud.pubsublite.SubscriptionName;
    import com.google.cloud.pubsublite.SubscriptionPath;
    import org.apache.beam.sdk.io.gcp.pubsublite.PubsubLiteIO;
    import org.apache.beam.sdk.io.gcp.pubsublite.SubscriberOptions;
    
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
1. Drain does not work on the default dataflow runner. You must set the option
`--experiments=use_runner_v2` for draining to function correctly.
