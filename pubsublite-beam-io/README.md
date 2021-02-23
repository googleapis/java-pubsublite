# Instructions for PubsubLiteIO usage.

1. Add the following to your POM file to download the Pub/Sub Lite I/O.
```xml
<dependency>
   <groupId>com.google.cloud.pubsublite</groupId>
   <artifactId>pubsublite-beam-io</artifactId>
   <version>0.11.0</version>
</dependency>
```
1. Create a topic using `gcloud pubsub lite-topics create`
1. Write some messages using:

    ```java
    import com.google.cloud.pubsublite.beam.PubsubLiteIO;
    import com.google.cloud.pubsublite.beam.PublisherOptions;
    import com.google.cloud.pubsublite.proto.PubSubMessage;
    
    ...
    
    private final static String ZONE = "us-central1-b";
    private final static Long PROJECT_NUM = 123L;
    
    ...
    
    PCollection<PubSubMessage> messages = ...;
    messages.apply("Write messages", PubsubLiteIO.write(
        PublisherOptions.newBuilder()
            .setTopicPath(TopicPath.newBuilder()
                .setLocation(CloudZone.parse(ZONE))
                .setProject(ProjectNumber.of(PROJECT_NUM))
                .setName(TopicName.of("my-topic"))
                .build())
            .build()));
    ```
1. Create a subscription using `gcloud pubsub lite-subscriptions create`
1. Read some messages using:

    ```java
    import com.google.cloud.pubsublite.beam.PubsubLiteIO;
    import com.google.cloud.pubsublite.beam.SubscriberOptions;
    import com.google.cloud.pubsublite.proto.SequencedMessage;
    import com.google.cloud.pubsublite.*;
    
    ...
    
    private final static String ZONE = "us-central1-b";
    private final static Long PROJECT_NUM = 123L;
    
    ...
    
    Pipeline pipeline = ...;
    PCollection<SequencedMessage> messages = pipeline.apply("Read messages", PubsubLiteIO.read(
        SubscriberOptions.newBuilder()
            .setSubscriptionPath(SubscriptionPath.newBuilder()
                .setLocation(CloudZone.parse(ZONE))
                .setProject(ProjectNumber.of(PROJECT_NUM))
                .setName(SubscriptionName.of("my-sub"))
                .build())
            .build()));
    ```
