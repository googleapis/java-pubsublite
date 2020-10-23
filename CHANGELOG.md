# Changelog

### [0.5.1](https://www.github.com/googleapis/java-pubsublite/compare/v0.5.0...v0.5.1) (2020-10-23)


### Bug Fixes

* Change Kafka{Producer/Consumer} to fix the number of partitions instead of looking it up. ([#322](https://www.github.com/googleapis/java-pubsublite/issues/322)) ([3f27c86](https://www.github.com/googleapis/java-pubsublite/commit/3f27c86003d7662a2c040f9637a0302743b36552))


### Documentation

* Add a basic readme for kafka client ([#320](https://www.github.com/googleapis/java-pubsublite/issues/320)) ([6073810](https://www.github.com/googleapis/java-pubsublite/commit/607381044ca8542e73aebb4ce9c6d4d57030ed6a))


### Dependencies

* Increment beam version on pubsublite-io ([#317](https://www.github.com/googleapis/java-pubsublite/issues/317)) ([d0db6c2](https://www.github.com/googleapis/java-pubsublite/commit/d0db6c2d5dd94679d6fb32655c26794af24b6be5))
* update dependency com.google.truth:truth to v1.1 ([#307](https://www.github.com/googleapis/java-pubsublite/issues/307)) ([1a556ef](https://www.github.com/googleapis/java-pubsublite/commit/1a556efbda5d911d18f10e2784fcd8f2b1ca1d51))
* update dependency com.google.truth.extensions:truth-java8-extension to v1.1 ([#306](https://www.github.com/googleapis/java-pubsublite/issues/306)) ([21cf834](https://www.github.com/googleapis/java-pubsublite/commit/21cf8346967744aa2978ceffe130f87c28dedcf6))

## [0.5.0](https://www.github.com/googleapis/java-pubsublite/compare/v0.4.1...v0.5.0) (2020-10-21)


### Features

* Add nextOffset method to BufferingPullSubscriber ([#272](https://www.github.com/googleapis/java-pubsublite/issues/272)) ([5c0e7cc](https://www.github.com/googleapis/java-pubsublite/commit/5c0e7ccad577e06459d01b16e3e936baf86f7a90))
* Add pubsublite-kafka-shim directory and disable deployment ([#273](https://www.github.com/googleapis/java-pubsublite/issues/273)) ([8bf29f8](https://www.github.com/googleapis/java-pubsublite/commit/8bf29f8db1fea7fbcd15571ade985d901b2ad16b))
* Add SharedBehavior class which handles the PartitionsFor method on consumers and producers ([#278](https://www.github.com/googleapis/java-pubsublite/issues/278)) ([b42da5f](https://www.github.com/googleapis/java-pubsublite/commit/b42da5fbcfe4e727265b4d604da8e771e493667f))
* Implement interfaces and utilities needed for Pub/Sub Lite Kafka shim ([#276](https://www.github.com/googleapis/java-pubsublite/issues/276)) ([3c43ef3](https://www.github.com/googleapis/java-pubsublite/commit/3c43ef38870f06f6e04aaa39df717e248cdd7008))
* Implement PubsubLiteConsumer. ([#287](https://www.github.com/googleapis/java-pubsublite/issues/287)) ([eed9656](https://www.github.com/googleapis/java-pubsublite/commit/eed9656f6add63a82598e1c64c8e8631b3ccb108))
* Implement PubsubLiteProducer ([#280](https://www.github.com/googleapis/java-pubsublite/issues/280)) ([1879470](https://www.github.com/googleapis/java-pubsublite/commit/187947033fadc6e624552fa1857137408047bd0f))
* Implement SingleSubscriptionConsumerImpl ([#281](https://www.github.com/googleapis/java-pubsublite/issues/281)) ([0e409c2](https://www.github.com/googleapis/java-pubsublite/commit/0e409c213c19c3ef715d569ac7f811ec638e743b))


### Bug Fixes

* Change internals to throw StatusException instead of return Status ([#300](https://www.github.com/googleapis/java-pubsublite/issues/300)) ([96ad02c](https://www.github.com/googleapis/java-pubsublite/commit/96ad02ccdfc076bc548cfc74aca4479499b6afb8))
* Fix PubsubLiteUnboundedSource to create n partitions not partitions of n size ([#313](https://www.github.com/googleapis/java-pubsublite/issues/313)) ([dbebc4b](https://www.github.com/googleapis/java-pubsublite/commit/dbebc4ba4fc718d2f2fcfeea17787dcf2bae9662))
* Implement ApiResourceAggregation to deduplicate logic for BackgroundResources. ([#301](https://www.github.com/googleapis/java-pubsublite/issues/301)) ([09578b5](https://www.github.com/googleapis/java-pubsublite/commit/09578b58c6051a4f4d5cd65df366ba939ff45b3b))
* Implement TrivialProxyService to remove duplicate ProxyService logic for the trivial case. ([#302](https://www.github.com/googleapis/java-pubsublite/issues/302)) ([ed74c6f](https://www.github.com/googleapis/java-pubsublite/commit/ed74c6fa37e64656e0c08d294873bf46818d19f1))
* Make connections start up asynchronously ([#289](https://www.github.com/googleapis/java-pubsublite/issues/289)) ([27b1fec](https://www.github.com/googleapis/java-pubsublite/commit/27b1fecfde7a323f5b1f7f86874d0ee1f33d24a6))


### Dependencies

* Move common dependencies into parent dependency management section ([#275](https://www.github.com/googleapis/java-pubsublite/issues/275)) ([d72bea8](https://www.github.com/googleapis/java-pubsublite/commit/d72bea8f85367a59d3b228ca795bef83a5a154da))
* update dependency com.google.cloud:google-cloud-pubsub to v1.108.3 ([#282](https://www.github.com/googleapis/java-pubsublite/issues/282)) ([cd9d943](https://www.github.com/googleapis/java-pubsublite/commit/cd9d9435a7450c39ded1dd35144d5829e931a7e7))
* update dependency com.google.cloud:google-cloud-pubsub to v1.108.4 ([#291](https://www.github.com/googleapis/java-pubsublite/issues/291)) ([b18ba85](https://www.github.com/googleapis/java-pubsublite/commit/b18ba856b17ebc225963ac4ba76f5fcdcbfc8521))
* update dependency com.google.cloud:google-cloud-pubsub to v1.108.5 ([#303](https://www.github.com/googleapis/java-pubsublite/issues/303)) ([50857a0](https://www.github.com/googleapis/java-pubsublite/commit/50857a0184188a12e9b59dbaabc233cc982fa8b9))
* update dependency com.google.cloud:google-cloud-pubsub-bom to v1.108.3 ([#283](https://www.github.com/googleapis/java-pubsublite/issues/283)) ([e98aa25](https://www.github.com/googleapis/java-pubsublite/commit/e98aa256f38063465a04addfd48acf30b2120cc4))
* update dependency com.google.cloud:google-cloud-pubsub-bom to v1.108.4 ([#292](https://www.github.com/googleapis/java-pubsublite/issues/292)) ([ed876b7](https://www.github.com/googleapis/java-pubsublite/commit/ed876b7f53997192b2120101d07f7e550e66f589))
* update dependency com.google.cloud:google-cloud-pubsub-bom to v1.108.5 ([#304](https://www.github.com/googleapis/java-pubsublite/issues/304)) ([58e55da](https://www.github.com/googleapis/java-pubsublite/commit/58e55da1f0e0f0d46da1e12747f5851f4cc47718))
* update dependency com.google.cloud:google-cloud-shared-dependencies to v0.10.1 ([#284](https://www.github.com/googleapis/java-pubsublite/issues/284)) ([d4be4a3](https://www.github.com/googleapis/java-pubsublite/commit/d4be4a3e0900f6f6fd634999ddb9f1c5803ea118))
* update dependency com.google.cloud:google-cloud-shared-dependencies to v0.10.2 ([#285](https://www.github.com/googleapis/java-pubsublite/issues/285)) ([30c6f83](https://www.github.com/googleapis/java-pubsublite/commit/30c6f839fcce589d6c2293aa48e24029b3c97ba3))
* update dependency com.google.cloud:google-cloud-shared-dependencies to v0.12.1 ([#296](https://www.github.com/googleapis/java-pubsublite/issues/296)) ([273a07a](https://www.github.com/googleapis/java-pubsublite/commit/273a07a98f900e29829ada73d84dd227b156c0ab))
* update dependency com.google.cloud:google-cloud-shared-dependencies to v0.13.0 ([#308](https://www.github.com/googleapis/java-pubsublite/issues/308)) ([ed9d961](https://www.github.com/googleapis/java-pubsublite/commit/ed9d961aa7b79c18eb877a69203b042b0cebae86))
* update dependency joda-time:joda-time to v2.10.7 ([#310](https://www.github.com/googleapis/java-pubsublite/issues/310)) ([d8e319c](https://www.github.com/googleapis/java-pubsublite/commit/d8e319ca3f8e53f961eb19af204a032e79bd87bf))
* update dependency junit:junit to v4.13.1 ([#290](https://www.github.com/googleapis/java-pubsublite/issues/290)) ([578bc73](https://www.github.com/googleapis/java-pubsublite/commit/578bc73dd8f7971d8691ca9fcf5803a8a133d3fe))
* update dependency org.apache.kafka:kafka-clients to v2.6.0 ([#279](https://www.github.com/googleapis/java-pubsublite/issues/279)) ([1985946](https://www.github.com/googleapis/java-pubsublite/commit/1985946348a02fd84476b762631ec6b6c5ab970a))
* update dependency org.mockito:mockito-core to v3.5.15 ([#299](https://www.github.com/googleapis/java-pubsublite/issues/299)) ([a9baa99](https://www.github.com/googleapis/java-pubsublite/commit/a9baa99cc9ce7584decb6ec2678e98c77effce7d))

### [0.4.1](https://www.github.com/googleapis/java-pubsublite/compare/v0.4.0...v0.4.1) (2020-09-28)


### Bug Fixes

* Make BufferingPullSubscriber use List instead of ImmutableList so it is beam friendly. ([#256](https://www.github.com/googleapis/java-pubsublite/issues/256)) ([a23e26f](https://www.github.com/googleapis/java-pubsublite/commit/a23e26f52fcdb4524c134d9b6d13b90a3a8527d3))
* Make flogger a runtime dependency by default. ([#265](https://www.github.com/googleapis/java-pubsublite/issues/265)) ([747bc66](https://www.github.com/googleapis/java-pubsublite/commit/747bc66d2b4ba2e7ef2a868e2341b30b7b2da153)), closes [#213](https://www.github.com/googleapis/java-pubsublite/issues/213)


### Dependencies

* update dependency com.google.cloud:google-cloud-resourcemanager to v0.118.2-alpha ([#259](https://www.github.com/googleapis/java-pubsublite/issues/259)) ([634116c](https://www.github.com/googleapis/java-pubsublite/commit/634116c6666aa151f1c8b7b1354fde27fe5e18fc))
* update dependency com.google.cloud:google-cloud-shared-dependencies to v0.10.0 ([#260](https://www.github.com/googleapis/java-pubsublite/issues/260)) ([f4442a0](https://www.github.com/googleapis/java-pubsublite/commit/f4442a04044c6e690f9bbbf062a063a43b3e9680))
* update dependency io.grpc:grpc-testing to v1.32.1 ([#261](https://www.github.com/googleapis/java-pubsublite/issues/261)) ([2f73e2f](https://www.github.com/googleapis/java-pubsublite/commit/2f73e2f5f77f592b834b11677fa1c0e23634f8ab))
* update dependency org.mockito:mockito-core to v3.5.13 ([#262](https://www.github.com/googleapis/java-pubsublite/issues/262)) ([43bdd1e](https://www.github.com/googleapis/java-pubsublite/commit/43bdd1e32a95160544aee3868a0dde14b8e44dc2))

## [0.4.0](https://www.github.com/googleapis/java-pubsublite/compare/v0.3.1...v0.4.0) (2020-09-24)


### Features

* Implement internal CursorClient which will be used by kafka shim. ([#252](https://www.github.com/googleapis/java-pubsublite/issues/252)) ([eabe900](https://www.github.com/googleapis/java-pubsublite/commit/eabe9003b91367add9b30caaeabbd589b14c8149))
* Use TopicStats to implement getSplitBacklog ([#228](https://www.github.com/googleapis/java-pubsublite/issues/228)) ([9a889a9](https://www.github.com/googleapis/java-pubsublite/commit/9a889a92b41802d5d150b5abbb7427fca0d42ec8))


### Bug Fixes

* Assorted warnings. ([#246](https://www.github.com/googleapis/java-pubsublite/issues/246)) ([a65da39](https://www.github.com/googleapis/java-pubsublite/commit/a65da39d4c6fcf92d84a15436e9d67e1d634632f))
* Fix BufferingPullSubscriber to not seek after sending flow control tokens. ([#253](https://www.github.com/googleapis/java-pubsublite/issues/253)) ([0e20d80](https://www.github.com/googleapis/java-pubsublite/commit/0e20d80f02532dce35e6dda72547e55fda0575a1))
* Fix warning due to unchecked cast. ([#241](https://www.github.com/googleapis/java-pubsublite/issues/241)) ([a3a21b4](https://www.github.com/googleapis/java-pubsublite/commit/a3a21b4355a21979e84eb95f1cfa2049a044e151))
* Move settings for the topic backlog reader to TopicBacklogReaderSettings ([#254](https://www.github.com/googleapis/java-pubsublite/issues/254)) ([0ee60eb](https://www.github.com/googleapis/java-pubsublite/commit/0ee60eb43ac4ee52a548138ecf137df10c539c83))

### [0.3.1](https://www.github.com/googleapis/java-pubsublite/compare/v0.3.0...v0.3.1) (2020-09-10)


### Bug Fixes

* Create a pool of Channels for each target. ([36b472b](https://www.github.com/googleapis/java-pubsublite/commit/36b472b95b39c6bd4af6514c2eae2c38254987e6))
* Split retryable codes for streams, and retry RESOURCE_EXHAUSTED errors. ([e4efe0b](https://www.github.com/googleapis/java-pubsublite/commit/e4efe0b10c55df4be66842ec3bad7055db579df5))


### Documentation

* Update samples for v0.3.0 ([#235](https://www.github.com/googleapis/java-pubsublite/issues/235)) ([61b1981](https://www.github.com/googleapis/java-pubsublite/commit/61b1981f528c0a3302b65be2746983b852abe95e))

## [0.3.0](https://www.github.com/googleapis/java-pubsublite/compare/v0.2.0...v0.3.0) (2020-09-03)


### Features

* Enable project id usage by performing lookups where necessary. ([#223](https://www.github.com/googleapis/java-pubsublite/issues/223)) ([e30896e](https://www.github.com/googleapis/java-pubsublite/commit/e30896e3a9418801fb099edf2d6a27199193e073))


### Bug Fixes

* Add Documentation ([#221](https://www.github.com/googleapis/java-pubsublite/issues/221)) ([6c430da](https://www.github.com/googleapis/java-pubsublite/commit/6c430da5fad6f20146c0aa0d2d6645dc7e341c45))
* Add more coverage ([#220](https://www.github.com/googleapis/java-pubsublite/issues/220)) ([f92f828](https://www.github.com/googleapis/java-pubsublite/commit/f92f828d0a1afe69c02021c8f859c3f181330426))
* add test coverage for cloudpubsub folder. ([#218](https://www.github.com/googleapis/java-pubsublite/issues/218)) ([a70a60f](https://www.github.com/googleapis/java-pubsublite/commit/a70a60f70e37587428817c4d035d7ce73af03525))
* change toString to value in TopicStatsClient ([#210](https://www.github.com/googleapis/java-pubsublite/issues/210)) ([30f0b10](https://www.github.com/googleapis/java-pubsublite/commit/30f0b10e27f1998f084fac8ae49dd8fd2a959856))
* Embed builder in path classes and make components directly accessible ([#231](https://www.github.com/googleapis/java-pubsublite/issues/231)) ([e5e7d7d](https://www.github.com/googleapis/java-pubsublite/commit/e5e7d7df6d7b9bce83796a046a94608a2d8094d6))
* Implement DefaultRoutingPolicyTest ([#188](https://www.github.com/googleapis/java-pubsublite/issues/188)) ([fa49931](https://www.github.com/googleapis/java-pubsublite/commit/fa499311de3cab4a7838af7255c7aa137ed20049))
* Improve test coverage for internal/wire folder. ([#214](https://www.github.com/googleapis/java-pubsublite/issues/214)) ([db2bc7a](https://www.github.com/googleapis/java-pubsublite/commit/db2bc7acb1ee64a0b5406098790432ef1e982d09))
* temporarily disable reporting to unblock releases ([#225](https://www.github.com/googleapis/java-pubsublite/issues/225)) ([2b66535](https://www.github.com/googleapis/java-pubsublite/commit/2b66535ac6160fd36e3f7cdab375ac28e0e74ea6))


### Reverts

* Revert "samples: update samples for automatic subscriber assignment (#189)" (#198) ([eefe717](https://www.github.com/googleapis/java-pubsublite/commit/eefe71725d345e75ded336ce13829d5623d79803)), closes [#189](https://www.github.com/googleapis/java-pubsublite/issues/189) [#198](https://www.github.com/googleapis/java-pubsublite/issues/198)


### Dependencies

* update dependency io.grpc:grpc-testing to v1.31.1 ([#205](https://www.github.com/googleapis/java-pubsublite/issues/205)) ([219c832](https://www.github.com/googleapis/java-pubsublite/commit/219c832766924498e036ba9a7773530b80dfc8dc))
* update dependency org.mockito:mockito-core to v3.5.0 ([#206](https://www.github.com/googleapis/java-pubsublite/issues/206)) ([7b3d297](https://www.github.com/googleapis/java-pubsublite/commit/7b3d29795ef1cf4b8b91aa114c903bf949122a33))
* update dependency org.mockito:mockito-core to v3.5.2 ([#209](https://www.github.com/googleapis/java-pubsublite/issues/209)) ([a4ca349](https://www.github.com/googleapis/java-pubsublite/commit/a4ca3497de0efc3a27a5c6e1cae82a57460673d7))
* update dependency org.mockito:mockito-core to v3.5.5 ([#217](https://www.github.com/googleapis/java-pubsublite/issues/217)) ([a6afc60](https://www.github.com/googleapis/java-pubsublite/commit/a6afc60b5f3a48e6baa6fec404214a013b33c89b))
* update dependency org.mockito:mockito-core to v3.5.6 ([#222](https://www.github.com/googleapis/java-pubsublite/issues/222)) ([f25077d](https://www.github.com/googleapis/java-pubsublite/commit/f25077d7cdbca31ded869aecef6fea27da0b0fd8))
* update dependency org.mockito:mockito-core to v3.5.7 ([#224](https://www.github.com/googleapis/java-pubsublite/issues/224)) ([e4061ba](https://www.github.com/googleapis/java-pubsublite/commit/e4061bae7883044161719bda423959141d1d79ad))
* update shared-dependencies to a version with a working GRPC version. ([89f6582](https://www.github.com/googleapis/java-pubsublite/commit/89f6582465a5fa4541fe9d7108118d11178ec05e))

## [0.2.0](https://www.github.com/googleapis/java-pubsublite/compare/v0.1.8...v0.2.0) (2020-08-11)


### Features

* add api client headers to outbound requests ([#182](https://www.github.com/googleapis/java-pubsublite/issues/182)) ([0b47e06](https://www.github.com/googleapis/java-pubsublite/commit/0b47e060972676f6295a07bda5ef813ab18fcb6c))
* Add the ability to use automatic subscriber assignment to the subscriber settings ([#163](https://www.github.com/googleapis/java-pubsublite/issues/163)) ([a396f24](https://www.github.com/googleapis/java-pubsublite/commit/a396f2474ac515f287ec0a1dab63d5d7061203a9))
* Add the TopicStats client ([#179](https://www.github.com/googleapis/java-pubsublite/issues/179)) ([7eb7861](https://www.github.com/googleapis/java-pubsublite/commit/7eb78616809345d0ca7f32645210a4b03f704c7f))
* Implement Assigner, which delivers partition assignments to a PartitionAssignmentReceiver ([#133](https://www.github.com/googleapis/java-pubsublite/issues/133)) ([a4485d9](https://www.github.com/googleapis/java-pubsublite/commit/a4485d91d54a0ac8d475aea29ee7e327fccf465e))


### Bug Fixes

* add clirr ignored diffs files ([#184](https://www.github.com/googleapis/java-pubsublite/issues/184)) ([0122757](https://www.github.com/googleapis/java-pubsublite/commit/012275744a3b97b171065c8828ef6553bf852a9e))
* Add missing monitor from startSubscriber. ([#142](https://www.github.com/googleapis/java-pubsublite/issues/142)) ([d2a90d8](https://www.github.com/googleapis/java-pubsublite/commit/d2a90d8bab74036bd3db0f3f54bdc8c0036c8dec))
* Fix assorted lint errors. ([#143](https://www.github.com/googleapis/java-pubsublite/issues/143)) ([403efb6](https://www.github.com/googleapis/java-pubsublite/commit/403efb655ba8e674ae97f7f9f2395393eb230578))
* Update SingleConnection to not hold the monitor on upcalls ([#151](https://www.github.com/googleapis/java-pubsublite/issues/151)) ([8274753](https://www.github.com/googleapis/java-pubsublite/commit/8274753de9d3fd9cf9358ab7a36535c21d42741e))
* Update synth.py and run synthtool to get new pubsublite sources. ([#171](https://www.github.com/googleapis/java-pubsublite/issues/171)) ([90bb70d](https://www.github.com/googleapis/java-pubsublite/commit/90bb70d07a554cffa070f4843d5558394b4c7ac5))


### Dependencies

* update dependency com.google.auto.value:auto-value to v1.7.3 ([#129](https://www.github.com/googleapis/java-pubsublite/issues/129)) ([c9492a0](https://www.github.com/googleapis/java-pubsublite/commit/c9492a019789861b8dca881e6c895db3758a60cf))
* update dependency com.google.auto.value:auto-value to v1.7.4 ([#156](https://www.github.com/googleapis/java-pubsublite/issues/156)) ([cb4cd9f](https://www.github.com/googleapis/java-pubsublite/commit/cb4cd9f1037bedd029f48f44f2aac0b5b3038226))
* update dependency com.google.cloud:google-cloud-pubsub to v1.107.0 ([#123](https://www.github.com/googleapis/java-pubsublite/issues/123)) ([5c3e977](https://www.github.com/googleapis/java-pubsublite/commit/5c3e977211cf36184f9bd48879afa9af90676ebc))
* update dependency com.google.cloud:google-cloud-pubsub to v1.108.0 ([#158](https://www.github.com/googleapis/java-pubsublite/issues/158)) ([924052b](https://www.github.com/googleapis/java-pubsublite/commit/924052b9445dd00c0a429ca11d5b203a5051740b))
* update dependency com.google.cloud:google-cloud-pubsub to v1.108.1 ([#186](https://www.github.com/googleapis/java-pubsublite/issues/186)) ([c466e2f](https://www.github.com/googleapis/java-pubsublite/commit/c466e2fc5445c77ceb86c9732a5ab80081bd8cc3))
* update dependency com.google.cloud:google-cloud-pubsub-bom to v1.107.0 ([#124](https://www.github.com/googleapis/java-pubsublite/issues/124)) ([b7e9e91](https://www.github.com/googleapis/java-pubsublite/commit/b7e9e91200a0e2117402ce908461a138b745af99))
* update dependency com.google.cloud:google-cloud-pubsub-bom to v1.108.0 ([#157](https://www.github.com/googleapis/java-pubsublite/issues/157)) ([56a8003](https://www.github.com/googleapis/java-pubsublite/commit/56a8003a437a101faf6d0a29fbf6afc6218b3790))
* update dependency com.google.cloud:google-cloud-pubsub-bom to v1.108.1 ([#187](https://www.github.com/googleapis/java-pubsublite/issues/187)) ([24ab8e2](https://www.github.com/googleapis/java-pubsublite/commit/24ab8e2279d863746f66d881c4a1fa55632eb7b4))
* update dependency com.google.cloud:google-cloud-shared-dependencies to v0.8.1 ([#138](https://www.github.com/googleapis/java-pubsublite/issues/138)) ([176101e](https://www.github.com/googleapis/java-pubsublite/commit/176101e305828e763ec74cfe5a2cce2bb33abdf7))
* update dependency com.google.cloud:google-cloud-shared-dependencies to v0.8.2 ([#154](https://www.github.com/googleapis/java-pubsublite/issues/154)) ([6739d11](https://www.github.com/googleapis/java-pubsublite/commit/6739d11d105fe1f3dd8a7b83646f4ec31b60dc1d))
* update dependency com.google.cloud:google-cloud-shared-dependencies to v0.8.3 ([#159](https://www.github.com/googleapis/java-pubsublite/issues/159)) ([8d3e0de](https://www.github.com/googleapis/java-pubsublite/commit/8d3e0def69316f5efb6fbe256fe4f45736d9f761))
* update dependency com.google.cloud:google-cloud-shared-dependencies to v0.8.5 ([#192](https://www.github.com/googleapis/java-pubsublite/issues/192)) ([9894260](https://www.github.com/googleapis/java-pubsublite/commit/98942601980624be9867889e1163785629dbd0b7))
* update dependency com.google.cloud:google-cloud-shared-dependencies to v0.8.6 ([#195](https://www.github.com/googleapis/java-pubsublite/issues/195)) ([8effb81](https://www.github.com/googleapis/java-pubsublite/commit/8effb8182aa3b37efd4f70abf8f53f09dbcb5003))
* update dependency io.grpc:grpc-testing to v1.30.2 ([#145](https://www.github.com/googleapis/java-pubsublite/issues/145)) ([ff77de9](https://www.github.com/googleapis/java-pubsublite/commit/ff77de91c7b0bbab945215a1e3ab865a21f985a9))
* update dependency io.grpc:grpc-testing to v1.31.0 ([#177](https://www.github.com/googleapis/java-pubsublite/issues/177)) ([f3c4f79](https://www.github.com/googleapis/java-pubsublite/commit/f3c4f79d5c6fe21e8a93e891e446721e9a4c5dff))
* update dependency org.mockito:mockito-core to v3.4.0 ([#161](https://www.github.com/googleapis/java-pubsublite/issues/161)) ([c3c2c6f](https://www.github.com/googleapis/java-pubsublite/commit/c3c2c6f3f2672c0c12b392a8bed7a543730d9db3))
* update dependency org.mockito:mockito-core to v3.4.2 ([#164](https://www.github.com/googleapis/java-pubsublite/issues/164)) ([f0f1e06](https://www.github.com/googleapis/java-pubsublite/commit/f0f1e06ace0e678135ac9507eda35541ce5dae78))
* update dependency org.mockito:mockito-core to v3.4.3 ([#165](https://www.github.com/googleapis/java-pubsublite/issues/165)) ([6d0554a](https://www.github.com/googleapis/java-pubsublite/commit/6d0554a10bdcf881b9421362b46071b480c9d3d5))
* update dependency org.mockito:mockito-core to v3.4.4 ([#167](https://www.github.com/googleapis/java-pubsublite/issues/167)) ([8815e9e](https://www.github.com/googleapis/java-pubsublite/commit/8815e9e6cb933944ea62c7b5b2611c17f496f896))
* update dependency org.mockito:mockito-core to v3.4.6 ([#176](https://www.github.com/googleapis/java-pubsublite/issues/176)) ([5de006f](https://www.github.com/googleapis/java-pubsublite/commit/5de006fc227e83c8b5eb932d36dbf9358a804611))


### Documentation

* update readme samples ([#155](https://www.github.com/googleapis/java-pubsublite/issues/155)) ([28930f8](https://www.github.com/googleapis/java-pubsublite/commit/28930f8fbdaefbf6964b0cc7a04116fbaaaa1b68))

### [0.1.8](https://www.github.com/googleapis/java-pubsublite/compare/v0.1.7...v0.1.8) (2020-06-09)


### Bug Fixes

* Return an error on publish if the publisher's state is not RUNNING ([#81](https://www.github.com/googleapis/java-pubsublite/issues/81)) ([74d61fd](https://www.github.com/googleapis/java-pubsublite/commit/74d61fd6c1491f4827813a2339088bd38fc48679))


### Dependencies

* update dependency com.google.auto.service:auto-service-annotations to v1.0-rc7 ([#102](https://www.github.com/googleapis/java-pubsublite/issues/102)) ([081c4d7](https://www.github.com/googleapis/java-pubsublite/commit/081c4d76fe6615fd7028f5e3cd17be1b3b2d4fee))
* update dependency com.google.auto.value:auto-value to v1.7.2 ([#103](https://www.github.com/googleapis/java-pubsublite/issues/103)) ([ee6bad1](https://www.github.com/googleapis/java-pubsublite/commit/ee6bad1a888f34e2f759717222ffe3575cf97a14))
* update dependency com.google.cloud:google-cloud-pubsub-bom to v1.106.0 ([#104](https://www.github.com/googleapis/java-pubsublite/issues/104)) ([dd31d70](https://www.github.com/googleapis/java-pubsublite/commit/dd31d706f80672e318f03863ec672c8aadfcbc39))
* update dependency com.google.cloud:google-cloud-shared-dependencies to v0.7.0 ([#105](https://www.github.com/googleapis/java-pubsublite/issues/105)) ([27cf47c](https://www.github.com/googleapis/java-pubsublite/commit/27cf47c6c2bedaf82ba8c91534f3269b5c4e54d7))
* update dependency com.google.errorprone:error_prone_annotations to v2.4.0 ([#107](https://www.github.com/googleapis/java-pubsublite/issues/107)) ([8c8666f](https://www.github.com/googleapis/java-pubsublite/commit/8c8666fc976a23d9e2ff4d6205373f55a29ee987))
* update dependency io.grpc:grpc-testing to v1.29.0 ([#108](https://www.github.com/googleapis/java-pubsublite/issues/108)) ([45e019a](https://www.github.com/googleapis/java-pubsublite/commit/45e019a4fcc04decac883266b883bca60adff1ac))
* update dependency io.grpc:grpc-testing to v1.30.0 ([#119](https://www.github.com/googleapis/java-pubsublite/issues/119)) ([eb1fee5](https://www.github.com/googleapis/java-pubsublite/commit/eb1fee5927cecdd098ce7263beb46b4af4f8d032))
* update dependency joda-time:joda-time to v2.10.6 ([#109](https://www.github.com/googleapis/java-pubsublite/issues/109)) ([9dd6266](https://www.github.com/googleapis/java-pubsublite/commit/9dd6266ba3ce83f75972a968f1a763133cb1e52f))
* update dependency org.apache.beam:beam-runners-direct-java to v2.22.0 ([#117](https://www.github.com/googleapis/java-pubsublite/issues/117)) ([a19619f](https://www.github.com/googleapis/java-pubsublite/commit/a19619f9b8ad7d24d47e9acb6c714988a8f3a058))
* update dependency org.apache.beam:beam-sdks-java-core to v2.22.0 ([#118](https://www.github.com/googleapis/java-pubsublite/issues/118)) ([395f02f](https://www.github.com/googleapis/java-pubsublite/commit/395f02f91ea9a1a2dc221aba1733cdec366abb00))
* update dependency org.mockito:mockito-core to v3.3.3 ([#110](https://www.github.com/googleapis/java-pubsublite/issues/110)) ([118d48c](https://www.github.com/googleapis/java-pubsublite/commit/118d48ca1dbfddf5774086425673be33461fffad))

### [0.1.7](https://www.github.com/googleapis/java-pubsublite/compare/v0.1.6...v0.1.7) (2020-06-05)


### Bug Fixes

* Modify CommitterImpl to exit early in case of permanent failure to avoid waiting on its own actions to complete. ([#90](https://www.github.com/googleapis/java-pubsublite/issues/90)) ([8858d58](https://www.github.com/googleapis/java-pubsublite/commit/8858d58c068bacf033c053329ddb12fe7cddfd78))

### [0.1.6](https://www.github.com/googleapis/java-pubsublite/compare/v0.1.5...v0.1.6) (2020-05-27)


### Bug Fixes

* keep track of internal seek ([#87](https://www.github.com/googleapis/java-pubsublite/issues/87)) ([a7e09ff](https://www.github.com/googleapis/java-pubsublite/commit/a7e09ff5f0423f672a829fac4ddeeeef7feff57c))

### [0.1.5](https://www.github.com/googleapis/java-pubsublite/compare/v0.1.4...v0.1.5) (2020-05-21)


### Bug Fixes

* Implement ChannelCache, which aggregates stubs to a single channel, and properly cleans them up on teardown. ([#72](https://www.github.com/googleapis/java-pubsublite/issues/72)) ([502484a](https://www.github.com/googleapis/java-pubsublite/commit/502484a71b51d2304a19ef629eb6fb85b6b35246))
* Modify UnboundedRreader to correctly initialize committers. ([#74](https://www.github.com/googleapis/java-pubsublite/issues/74)) ([5fa68c7](https://www.github.com/googleapis/java-pubsublite/commit/5fa68c7e5501b8c6754403eb7d84f3fb92b92450))
* use appropriate x-version-update in POM files ([#68](https://www.github.com/googleapis/java-pubsublite/issues/68)) ([12edb04](https://www.github.com/googleapis/java-pubsublite/commit/12edb048159482b64843e0d48667164b11680fbd))


### Documentation

* Update sample code snippets in the main readme ([#71](https://www.github.com/googleapis/java-pubsublite/issues/71)) ([603b4a7](https://www.github.com/googleapis/java-pubsublite/commit/603b4a750bfddf782fa4ffca3e35fe2f70108355))

### [0.1.4](https://www.github.com/googleapis/java-pubsublite/compare/v0.1.3...v0.1.4) (2020-05-20)


### Bug Fixes

* Propogate context in SinglePartitionPublisherBuilder. ([#63](https://www.github.com/googleapis/java-pubsublite/issues/63)) ([8b2d525](https://www.github.com/googleapis/java-pubsublite/commit/8b2d525f0dd0178093dff21ecbe639ba7cd563cb))

### [0.1.3](https://www.github.com/googleapis/java-pubsublite/compare/v0.1.2...v0.1.3) (2020-05-20)


### Bug Fixes

* fix the regex in Versions.java to actually split on '.' ([#57](https://www.github.com/googleapis/java-pubsublite/issues/57)) ([b9371b1](https://www.github.com/googleapis/java-pubsublite/commit/b9371b1ef1c7f4ff9b3d86f00d899a1ba3bfd85c))

### [0.1.2](https://www.github.com/googleapis/java-pubsublite/compare/v0.1.1...v0.1.2) (2020-05-19)


### Documentation

* Add disclaimer to readme. ([#52](https://www.github.com/googleapis/java-pubsublite/issues/52)) ([0d90ef1](https://www.github.com/googleapis/java-pubsublite/commit/0d90ef1adf03f0283080290849ecdafd3407b148))

### [0.1.1](https://www.github.com/googleapis/java-pubsublite/compare/v0.1.0...v0.1.1) (2020-05-19)


### Bug Fixes

* bump shared config version to trigger a release ([#48](https://www.github.com/googleapis/java-pubsublite/issues/48)) ([7f2fdef](https://www.github.com/googleapis/java-pubsublite/commit/7f2fdef8993be75ede9962cae3c12fe4d459bd14))

## 0.1.0 (2020-05-18)


### Features

* add exponential backoff to stream retries ([#34](https://www.github.com/googleapis/java-pubsublite/issues/34)) ([bdb0995](https://www.github.com/googleapis/java-pubsublite/commit/bdb09953dbd46ecd40162c023ccd9b6dc736139a))


### Bug Fixes

* add scope codes to the stub provider ([12d2a0e](https://www.github.com/googleapis/java-pubsublite/commit/12d2a0e13f241df71a6f1134961d12dcfa721f19))
* add wait in retry test ([#41](https://www.github.com/googleapis/java-pubsublite/issues/41)) ([2e211ce](https://www.github.com/googleapis/java-pubsublite/commit/2e211cef205a65687b4c72003bbf275a69955a3d))
* clean up lint issues ([#39](https://www.github.com/googleapis/java-pubsublite/issues/39)) ([e51093d](https://www.github.com/googleapis/java-pubsublite/commit/e51093d3c68519231456547135769ec147846e14))
* create inFlightSeek on stream reinitialize ([#35](https://www.github.com/googleapis/java-pubsublite/issues/35)) ([ba4f5b8](https://www.github.com/googleapis/java-pubsublite/commit/ba4f5b88ff25771a2e35ccb5112f9241d2d06b0f)), closes [/github.com/googleapis/java-pubsublite/blob/master/google-cloud-pubsublite/src/main/java/com/google/cloud/pubsublite/internal/wire/SubscriberImpl.java#L211](https://www.github.com/googleapis//github.com/googleapis/java-pubsublite/blob/master/google-cloud-pubsublite/src/main/java/com/google/cloud/pubsublite/internal/wire/SubscriberImpl.java/issues/L211)
* logging stream reconnect attempts at FINE ([#37](https://www.github.com/googleapis/java-pubsublite/issues/37)) ([8fd747a](https://www.github.com/googleapis/java-pubsublite/commit/8fd747ae66765d76d62ffd3cf62b1a555526a01a))


### Reverts

* Revert "chore: release 0.1.0 (#36)" (#43) ([66313ae](https://www.github.com/googleapis/java-pubsublite/commit/66313ae5bcbbba889654c1db0ca994adcca0a36e)), closes [#36](https://www.github.com/googleapis/java-pubsublite/issues/36) [#43](https://www.github.com/googleapis/java-pubsublite/issues/43)


### Documentation

* fix broken link in readme ([#40](https://www.github.com/googleapis/java-pubsublite/issues/40)) ([5be8343](https://www.github.com/googleapis/java-pubsublite/commit/5be83438a59677c0d433659e0c224f4d94316cb5))
