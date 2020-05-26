# Changelog

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
