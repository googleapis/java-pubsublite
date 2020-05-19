# Instructions for PubsubLiteIO usage.

1. Acquire PubsubLiteIO sources. This is currently through downloading the
sources and including them in your project, but a release jar will be available
soon.
1. Create a topic using `gcloud pubsub lite-topics create`
1. Write some messages using:

    ```java
    import com.google.cloud.pubsublite.beam.PubsubLiteIO;
    import com.google.cloud.pubsublite.beam.PublisherOptions;
    import com.google.cloud.pubsublite.*;
    
    ...
    
    private final static String ZONE = "us-central1-b";
    private final static Long PROJECT_NUM = 123L;
    
    ...
    
    PCollection<Message> messages = ...;
    messages.apply("Write messages", PubsubLiteIO.write(
        PublisherOptions.newBuilder()
            .setTopicPath(TopicPaths.newBuilder()
                .setZone(CloudZone.parse(ZONE))
                .setProject(ProjectNumber.of(PROJECT_NUM))
                .setTopicName(TopicName.of("my-topic"))
                .build())
            .build()));
    ```
1. Create a subscription using `gcloud pubsub lite-subscriptions create`
1. Read some messages using:

    ```java
    import com.google.cloud.pubsublite.beam.PubsubLiteIO;
    import com.google.cloud.pubsublite.beam.SubscriberOptions;
    import com.google.cloud.pubsublite.*;
    import com.google.cloud.pubsublite.cloudpubsub.FlowControlSettings;
    
    ...
    
    private final static String ZONE = "us-central1-b";
    private final static Long PROJECT_NUM = 123L;
    
    ...
    
    Pipeline pipeline = ...;
    PCollection<Message> messages = pipeline.apply("Read messages", PubsubLiteIO.read(
        SubscriberOptions.newBuilder()
            .setSubscriptionPath(SubscriptionPaths.newBuilder()
                .setZone(CloudZone.parse(ZONE))
                .setProject(ProjectNumber.of(PROJECT_NUM))
                .setTopicName(SubscriptionName.of("my-sub"))
                .build())
            .setFlowControlSettings(FlowControlSettings.builder()
                .setBytesOutstanding(100_000_000)  // 100 MB
                .setMessagesOutstanding(Long.MAX_VALUE)
                .build())
            .build()));
    ```
