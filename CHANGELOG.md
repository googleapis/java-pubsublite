# Changelog

### [0.1.9](https://www.github.com/googleapis/java-pubsublite/compare/v0.1.8...v0.1.9) (2020-06-13)


### Dependencies

* update dependency com.google.auto.value:auto-value to v1.7.3 ([#129](https://www.github.com/googleapis/java-pubsublite/issues/129)) ([c9492a0](https://www.github.com/googleapis/java-pubsublite/commit/c9492a019789861b8dca881e6c895db3758a60cf))
* update dependency com.google.cloud:google-cloud-pubsub to v1.107.0 ([#123](https://www.github.com/googleapis/java-pubsublite/issues/123)) ([5c3e977](https://www.github.com/googleapis/java-pubsublite/commit/5c3e977211cf36184f9bd48879afa9af90676ebc))
* update dependency com.google.cloud:google-cloud-pubsub-bom to v1.107.0 ([#124](https://www.github.com/googleapis/java-pubsublite/issues/124)) ([b7e9e91](https://www.github.com/googleapis/java-pubsublite/commit/b7e9e91200a0e2117402ce908461a138b745af99))

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
