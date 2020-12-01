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

package com.google.cloud.pubsublite.spark;

import com.google.auto.value.AutoValue;
import com.google.cloud.pubsublite.SubscriptionPath;
import java.io.Serializable;
import java.util.Optional;
import javax.annotation.Nullable;
import org.apache.spark.sql.sources.v2.DataSourceOptions;

@AutoValue
public abstract class PslDataSourceOptions implements Serializable {

  @Nullable
  public abstract String credentialsAccessToken();

  @Nullable
  public abstract String credentialsKey();

  @Nullable
  public abstract String credentialsFile();

  public abstract SubscriptionPath subscriptionPath();

  public abstract long maxBytesOutstanding();

  public abstract long maxMessagesOutstanding();

  public abstract boolean consumeFromHead();

  public abstract long maxBatchOffsetRange();

  public static Builder builder() {
    return new AutoValue_PslDataSourceOptions.Builder()
        .credentialsAccessToken(null)
        .credentialsKey(null)
        .credentialsFile(null)
        .maxBytesOutstanding(Constants.DEFAULT_BYTES_OUTSTANDING)
        .maxMessagesOutstanding(Constants.DEFAULT_MESSAGES_OUTSTANDING)
        .maxBatchOffsetRange(Constants.DEFAULT_BATCH_OFFSET_RANGE)
        .consumeFromHead(true);
  }

  public static PslDataSourceOptions fromSparkDataSourceOptions(DataSourceOptions options) {
    if (!options.get(Constants.SUBSCRIPTION_CONFIG_KEY).isPresent()) {
      throw new IllegalArgumentException(Constants.SUBSCRIPTION_CONFIG_KEY + " is required.");
    }

    Builder builder = builder();
    Optional<String> value;
    if ((value = options.get(Constants.CREDENTIALS_ACCESS_TOKEN_CONFIG_KEY)).isPresent()) {
      builder.credentialsAccessToken(value.get());
    } else if ((value = options.get(Constants.CREDENTIALS_KEY_CONFIG_KEY)).isPresent()) {
      builder.credentialsKey(value.get());
    } else if ((value = options.get(Constants.CREDENTIALS_FILE_CONFIG_KEY)).isPresent()) {
      builder.credentialsFile(value.get());
    }
    builder.subscriptionPath(
        SubscriptionPath.parse(options.get(Constants.SUBSCRIPTION_CONFIG_KEY).get()));
    builder.maxBytesOutstanding(
        options.getLong(
            Constants.BYTES_OUTSTANDING_CONFIG_KEY, Constants.DEFAULT_BYTES_OUTSTANDING));
    builder.maxMessagesOutstanding(
        options.getLong(
            Constants.MESSAGES_OUTSTANDING_CONFIG_KEY, Constants.DEFAULT_MESSAGES_OUTSTANDING));
    builder.consumeFromHead(options.getBoolean(Constants.CONSUME_FROM_HEAD_CONFIG_KEY, true));
    builder.maxBatchOffsetRange(
        options.getLong(
            Constants.BATCH_OFFSET_RANGE_CONFIG_KEY, Constants.DEFAULT_BATCH_OFFSET_RANGE));
    return builder.build();
  }

  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder credentialsAccessToken(String credentialsAccessToken);

    public abstract Builder credentialsKey(String credentialsKey);

    public abstract Builder credentialsFile(String credentialsFile);

    public abstract Builder subscriptionPath(SubscriptionPath subscriptionPath);

    public abstract Builder maxBytesOutstanding(long maxBytesOutstanding);

    public abstract Builder maxMessagesOutstanding(long maxMessagesOutstanding);

    public abstract Builder consumeFromHead(boolean consumeFromHead);

    public abstract Builder maxBatchOffsetRange(long maxBatchOffsetRange);

    public abstract PslDataSourceOptions build();
  }
}
