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

import com.google.auth.oauth2.GoogleCredentials;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.auth.MoreCallCredentials;
import io.grpc.stub.AbstractStub;
import java.io.IOException;
import java.util.function.Function;

public class Stubs {
  public static <StubT extends AbstractStub<StubT>> StubT defaultStub(
      String target, Function<Channel, StubT> stubFactory) throws IOException {
    return stubFactory
        .apply(ManagedChannelBuilder.forTarget(target).build())
        .withCallCredentials(
            MoreCallCredentials.from(
                GoogleCredentials.getApplicationDefault()
                    .createScoped("https://www.googleapis.com/auth/cloud-platform")));
  }

  private Stubs() {}
}
