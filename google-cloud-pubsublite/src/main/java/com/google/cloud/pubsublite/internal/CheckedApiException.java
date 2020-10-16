/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.pubsublite.internal;

import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.StatusCode;
import javax.annotation.Nullable;

public class CheckedApiException extends Exception {
  public final ApiException underlying;

  public CheckedApiException(ApiException underlying) {
    super(underlying);
    this.underlying = underlying;
  }

  public static StatusCode fromCode(StatusCode.Code code) {
    return new StatusCode() {
      @Override
      public Code getCode() {
        return code;
      }

      @Override
      public Object getTransportCode() {
        return null;
      }
    };
  }

  public CheckedApiException(String message, @Nullable Throwable cause, StatusCode.Code code) {
    this(new ApiException(message, cause, fromCode(code), false));
  }

  public CheckedApiException(@Nullable Throwable cause, StatusCode.Code code) {
    this("", cause, code);
  }

  public CheckedApiException(String message, StatusCode.Code code) {
    this(message, null, code);
  }

  public CheckedApiException(StatusCode.Code code) {
    this("", code);
  }

  public StatusCode.Code code() {
    return underlying.getStatusCode().getCode();
  }
}
