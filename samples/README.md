How to run samples and tests?

1. Set environment variables:
    ```sh
    export GOOGLE_APPLICATION_CREDENTIALS=path/to/json/key
    export GOOGLE_CLOUD_PROJECT_NUMBER=123456789
    ```
1. To run a specific test:
   ```sh
   cd java-pubsublite/
   mvn clean install
   cd samples/
   mvn clean install -DskipTests=true && mvn -Dtest=UpdateTopicExampleIT test
   ```
   To run a specific snippet, update the developer's TODO section in the snippet, update the method name to `public static void main(String[] args)`, then:
   ```sh
   cd java-pubsublite/
   mvn clean install
   cd samples/snippets/
   mvn clean verify -DskipTests=true && mvn exec:java -Dexec.mainClass=CreateTopicExample
   ```