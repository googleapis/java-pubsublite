// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.cloud.pubsublite;

import io.grpc.StatusException;

// A MessageTransformer details how to transform a message of one type to another. It is likely that
// either FromT or ToT will be a Message on publish and SequencedMessage on subscribe.
public interface MessageTransformer<FromT, ToT> {
  ToT transform(FromT from) throws StatusException;
}
