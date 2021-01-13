# Apache Spark SQL Streaming connector for Google PubSub Lite (Unreleased)

The connector is a custom implementation of [Spark Structured Streaming](https://spark.apache.org/docs/latest/structured-streaming-programming-guide.html) 
that supports reading messages from [Google PubSub Lite](https://cloud.google.com/pubsub/lite/docs) subscriptions into Spark.

## Disclaimer

This connector is still being worked on. Backwards-incompatible changes may be made until version 1.0.0 is released.

## Requirements

### Enable the PubSub Lite API

Follow [these instructions](https://cloud.google.com/pubsub/lite/docs/quickstart#before-you-begin).

### Create a new subscription or use existing subscription

Follow [the instruction](https://cloud.google.com/pubsub/lite/docs/quickstart#create_a_lite_subscription) to create a new
subscription or use existing subscription. If using existing subscription, the connector will read message from the 
oldest unacknowledged.

### Create a Google Cloud Dataproc cluster (Optional)

If you do not have an Apache Spark environment you can create a Cloud Dataproc cluster with pre-configured auth. The following examples assume you are using Cloud Dataproc, but you can use `spark-submit` on any cluster.

```
MY_CLUSTER=...
gcloud dataproc clusters create "$MY_CLUSTER"
```

## Downloading and Using the Connector

<!--- TODO(jiangmichael): Add jar link for spark-pubsublite-latest.jar -->
The latest version connector of the connector (Scala 2.11) is publicly available in
gs://spark-lib/pubsublite/spark-pubsublite-latest.jar.

<!--- TODO(jiangmichael): Release on Maven Central and add Maven Central link -->
The connector is also available from the Maven Central
repository. It can be used using the `--packages` option or the
`spark.jars.packages` configuration property. Use the following value

| Scala version | Connector Artifact |
| --- | --- |
| Scala 2.11 | `com.google.cloud.pubsublite.spark:pubsublite-spark-sql-streaming-with-dependencies_2.11:0.1.0` |

<!--- TODO(jiangmichael): Add exmaple code and brief description here -->

## Usage

### Reading data from PubSub Lite

```
df = spark.readStream \
  .option("pubsublite.subscription", "projects/123456789/locations/us-central1-a/subscriptions/test-spark-subscription")
  .format("pubsublite") \
  .load
```

Note that the connector supports both MicroBatch Processing and [Continuous Processing](https://spark.apache.org/docs/latest/structured-streaming-programming-guide.html#continuous-processing).

### Properties

The connector supports a number of options to configure the read:

| Option | Type | Required | Meaning |
| ------ | ---- | -------- | ------- |
| pubsublite.subscription | String | Y | Full subscription path that the connector will read from. |
| pubsublite.flowcontrol.byteoutstandingperpartition | Long | N | Max number of bytes per partition that will be cached in workers before Spark processes the messages. Default to 50000000 bytes. |
| pubsublite.flowcontrol.messageoutstandingperpartition | Long | N | Max number of messages per partition that will be cached in workers before Spark processes the messages. Default to Long.MAX_VALUE. |
| gcp.credentials.key | String | N | Service account JSON in base64. Default to [Application Default Credentials](https://cloud.google.com/docs/authentication/production#automatically). |

### Data Schema

The connector has fixed data schema as follows:

| Data Field | Spark Data Type | Notes |
| ---------- | --------------- | ----- |
| subscription | StringType | Full subscription path |
| partition | LongType | |
| offset | LongType | |
| key | BinaryType | |
| data | BinaryType | |
| attributes | MapType\[StringType, ArrayType\[BinaryType\]\] | |
| publish_timestamp | TimestampType | |
| event_timestamp | TimestampType | Nullable |

## Compiling with the connector

To include the connector in your project:

### Maven

```xml
<dependency>
  <groupId>com.google.cloud.pubsublite.spark</groupId>
  <artifactId>pubsublite-spark-sql-streaming-with-dependencies_2.11</artifactId>
  <version>0.1.0</version>
</dependency>
```

### SBT

```sbt
libraryDependencies += "com.google.cloud.pubsublite.spark" %% "pubsublite-spark-sql-streaming-with-dependencies_2.11" % "0.1.0"
```

## Building the Connector

The connector is built using Maven. Following command creates a jar with shaded dependencies:

```
mvn package
```

## FAQ

### What is the Pricing for the PubSub Lite?

See the [PubSub Lite pricing documentation](https://cloud.google.com/pubsub/lite/pricing).

### Can I configure the number of spark partitions?

No, the number of spark partitions is set to be the number of PubSub Lite partitions of the topic that the supplied subscription is for.

### How do I authenticate outside GCE / Dataproc?

Use a service account JSON key and `GOOGLE_APPLICATION_CREDENTIALS` as described [here](https://cloud.google.com/docs/authentication/getting-started).

Credentials can be provided with `gcp.credentials.key` option, it needs be passed in as a base64-encoded string directly.

Example:
```
spark.readStream.format("pubsublite").option("gcp.credentials.key", "<SERVICE_ACCOUNT_JSON_IN_BASE64>")
```
