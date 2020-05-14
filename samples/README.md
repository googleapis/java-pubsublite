# Getting Started with Pub/Sub Lite Samples

## Running samples and tests using Cloud Shell

[Google Cloud Shell](https://cloud.google.com/shell) has application default credentials from its compute instance which will allow you to run an integration test without having to obtain `GOOGLE_APPLICATION_CREDENTIANS`.

However, tests require an additional environment variable `GOOGLE_CLOUD_PROJECT_NUMBER` to run. For instance, 
 
In [`UpdateTopicExampleIT.java`](snippets/src/test/java/pubsublite/UpdateTopicExampleIT.java):

```java
private static final String GOOGLE_CLOUD_PROJECT_NUMBER =
       System.getenv("GOOGLE_CLOUD_PROJECT_NUMBER");
```
Here, the program needs you to provide your [Google Cloud Project Number](https://cloud.google.com/resource-manager/docs/creating-managing-projects) in an environment variable.

Make sure to set environment variables before running tests and to complete the developer TODOs in the samples before running samples, or else you will get an error asking you to set them.

To run a sample's integration test in Cloud Shell:

1. `cd samples/snippets` - all samples are located in `java-pubsublite/samples/snippets` directory.

1. `mvn -Dtest=UpdateTopicExampleIT test` - this runs the integration test for `UpdateTopicExample.java`.

To run a sample, update the developer's TODO section in the snippet, then:

1. `cd samples/snippets` - all samples are located in `java-pubsublite/samples/snippets` directory.

1. `mvn compile exec:java -Dexec.mainClass=pubsublite.ListTopicsExample` - this should list the topics in your project.