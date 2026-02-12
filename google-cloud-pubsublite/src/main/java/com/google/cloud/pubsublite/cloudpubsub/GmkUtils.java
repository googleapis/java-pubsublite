/*
 * Copyright 2026 Google LLC
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

package com.google.cloud.pubsublite.cloudpubsub;

import java.net.InetAddress;
import java.util.Map;

/** Utility methods for Google Managed Kafka (GMK) integration. */
public class GmkUtils {

  /**
   * Validates GMK bootstrap server connectivity and configuration.
   *
   * @param kafkaProperties The Kafka properties containing bootstrap.servers
   * @return Validation result with suggestions if issues are found
   */
  public static ValidationResult validateGmkConfiguration(Map<String, Object> kafkaProperties) {
    String bootstrapServers = (String) kafkaProperties.get("bootstrap.servers");

    if (bootstrapServers == null || bootstrapServers.trim().isEmpty()) {
      return ValidationResult.error("bootstrap.servers property is missing or empty");
    }

    // Extract hostname from bootstrap servers
    String[] servers = bootstrapServers.split(",");
    for (String server : servers) {
      String hostname = server.trim().split(":")[0];

      // Check if it looks like a GMK hostname
      if (hostname.contains("managedkafka") && hostname.contains(".cloud.goog")) {
        try {
          // Test DNS resolution
          InetAddress.getByName(hostname);
          return ValidationResult.success(
              "GMK bootstrap server resolved successfully: " + hostname);
        } catch (Exception e) {
          return ValidationResult.error(
              "Failed to resolve GMK hostname: "
                  + hostname
                  + ". Please verify:\n"
                  + "1. The GMK cluster exists and is running\n"
                  + "2. You have the correct project ID, region, and cluster name\n"
                  + "3. Your network allows DNS resolution to *.cloud.goog domains\n"
                  + "Use: gcloud managed-kafka clusters list --location=<region>"
                  + " --project=<project>");
        }
      }
    }

    return ValidationResult.warning(
        "Bootstrap servers don't appear to be GMK endpoints: " + bootstrapServers);
  }

  /**
   * Constructs a GMK bootstrap server URL from cluster details.
   *
   * @param projectId GCP project ID
   * @param region GCP region (e.g., "us-central1")
   * @param clusterId GMK cluster ID
   * @param port Port number
   * @return Formatted bootstrap server URL
   */
  public static String buildGmkBootstrapServer(
      String projectId, String region, String clusterId, int port) {
    return String.format(
        "bootstrap.%s.%s.managedkafka.%s.cloud.goog:%d", clusterId, region, projectId, port);
  }

  /** Constructs a GMK bootstrap server URL with default port 9092. */
  public static String buildGmkBootstrapServer(String projectId, String region, String clusterId) {
    return buildGmkBootstrapServer(projectId, region, clusterId, 9092);
  }

  /** Result of configuration validation. */
  public static class ValidationResult {
    private final boolean success;
    private final String message;
    private final Level level;

    private ValidationResult(boolean success, String message, Level level) {
      this.success = success;
      this.message = message;
      this.level = level;
    }

    public static ValidationResult success(String message) {
      return new ValidationResult(true, message, Level.SUCCESS);
    }

    public static ValidationResult error(String message) {
      return new ValidationResult(false, message, Level.ERROR);
    }

    public static ValidationResult warning(String message) {
      return new ValidationResult(false, message, Level.WARNING);
    }

    public boolean isSuccess() {
      return success;
    }

    public String getMessage() {
      return message;
    }

    public Level getLevel() {
      return level;
    }

    public enum Level {
      SUCCESS,
      WARNING,
      ERROR
    }

    @Override
    public String toString() {
      return level + ": " + message;
    }
  }
}
