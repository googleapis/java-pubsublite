/*
 * Copyright 2024 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/cloud/pubsublite/v1/publisher.proto

// Protobuf Java Version: 3.25.5
package com.google.cloud.pubsublite.proto;

public final class PublisherProto {
  private PublisherProto() {}

  public static void registerAllExtensions(com.google.protobuf.ExtensionRegistryLite registry) {}

  public static void registerAllExtensions(com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions((com.google.protobuf.ExtensionRegistryLite) registry);
  }

  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_pubsublite_v1_InitialPublishRequest_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_pubsublite_v1_InitialPublishRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_pubsublite_v1_InitialPublishResponse_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_pubsublite_v1_InitialPublishResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_pubsublite_v1_MessagePublishRequest_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_pubsublite_v1_MessagePublishRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_pubsublite_v1_MessagePublishResponse_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_pubsublite_v1_MessagePublishResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_pubsublite_v1_MessagePublishResponse_CursorRange_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_pubsublite_v1_MessagePublishResponse_CursorRange_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_pubsublite_v1_PublishRequest_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_pubsublite_v1_PublishRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_pubsublite_v1_PublishResponse_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_pubsublite_v1_PublishResponse_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor getDescriptor() {
    return descriptor;
  }

  private static com.google.protobuf.Descriptors.FileDescriptor descriptor;

  static {
    java.lang.String[] descriptorData = {
      "\n*google/cloud/pubsublite/v1/publisher.p"
          + "roto\022\032google.cloud.pubsublite.v1\032\034google"
          + "/api/annotations.proto\032\027google/api/clien"
          + "t.proto\032\'google/cloud/pubsublite/v1/comm"
          + "on.proto\"L\n\025InitialPublishRequest\022\r\n\005top"
          + "ic\030\001 \001(\t\022\021\n\tpartition\030\002 \001(\003\022\021\n\tclient_id"
          + "\030\003 \001(\014\"\030\n\026InitialPublishResponse\"s\n\025Mess"
          + "agePublishRequest\022;\n\010messages\030\001 \003(\0132).go"
          + "ogle.cloud.pubsublite.v1.PubSubMessage\022\035"
          + "\n\025first_sequence_number\030\002 \001(\003\"\232\002\n\026Messag"
          + "ePublishResponse\0228\n\014start_cursor\030\001 \001(\0132\""
          + ".google.cloud.pubsublite.v1.Cursor\022U\n\rcu"
          + "rsor_ranges\030\002 \003(\0132>.google.cloud.pubsubl"
          + "ite.v1.MessagePublishResponse.CursorRang"
          + "e\032o\n\013CursorRange\0228\n\014start_cursor\030\001 \001(\0132\""
          + ".google.cloud.pubsublite.v1.Cursor\022\023\n\013st"
          + "art_index\030\002 \001(\005\022\021\n\tend_index\030\003 \001(\005\"\304\001\n\016P"
          + "ublishRequest\022L\n\017initial_request\030\001 \001(\01321"
          + ".google.cloud.pubsublite.v1.InitialPubli"
          + "shRequestH\000\022T\n\027message_publish_request\030\002"
          + " \001(\01321.google.cloud.pubsublite.v1.Messag"
          + "ePublishRequestH\000B\016\n\014request_type\"\302\001\n\017Pu"
          + "blishResponse\022N\n\020initial_response\030\001 \001(\0132"
          + "2.google.cloud.pubsublite.v1.InitialPubl"
          + "ishResponseH\000\022N\n\020message_response\030\002 \001(\0132"
          + "2.google.cloud.pubsublite.v1.MessagePubl"
          + "ishResponseH\000B\017\n\rresponse_type2\313\001\n\020Publi"
          + "sherService\022h\n\007Publish\022*.google.cloud.pu"
          + "bsublite.v1.PublishRequest\032+.google.clou"
          + "d.pubsublite.v1.PublishResponse\"\000(\0010\001\032M\312"
          + "A\031pubsublite.googleapis.com\322A.https://ww"
          + "w.googleapis.com/auth/cloud-platformB\322\001\n"
          + "!com.google.cloud.pubsublite.protoB\016Publ"
          + "isherProtoP\001Z>cloud.google.com/go/pubsub"
          + "lite/apiv1/pubsublitepb;pubsublitepb\370\001\001\252"
          + "\002\032Google.Cloud.PubSubLite.V1\312\002\032Google\\Cl"
          + "oud\\PubSubLite\\V1\352\002\035Google::Cloud::PubSu"
          + "bLite::V1b\006proto3"
    };
    descriptor =
        com.google.protobuf.Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(
            descriptorData,
            new com.google.protobuf.Descriptors.FileDescriptor[] {
              com.google.api.AnnotationsProto.getDescriptor(),
              com.google.api.ClientProto.getDescriptor(),
              com.google.cloud.pubsublite.proto.CommonProto.getDescriptor(),
            });
    internal_static_google_cloud_pubsublite_v1_InitialPublishRequest_descriptor =
        getDescriptor().getMessageTypes().get(0);
    internal_static_google_cloud_pubsublite_v1_InitialPublishRequest_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_pubsublite_v1_InitialPublishRequest_descriptor,
            new java.lang.String[] {
              "Topic", "Partition", "ClientId",
            });
    internal_static_google_cloud_pubsublite_v1_InitialPublishResponse_descriptor =
        getDescriptor().getMessageTypes().get(1);
    internal_static_google_cloud_pubsublite_v1_InitialPublishResponse_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_pubsublite_v1_InitialPublishResponse_descriptor,
            new java.lang.String[] {});
    internal_static_google_cloud_pubsublite_v1_MessagePublishRequest_descriptor =
        getDescriptor().getMessageTypes().get(2);
    internal_static_google_cloud_pubsublite_v1_MessagePublishRequest_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_pubsublite_v1_MessagePublishRequest_descriptor,
            new java.lang.String[] {
              "Messages", "FirstSequenceNumber",
            });
    internal_static_google_cloud_pubsublite_v1_MessagePublishResponse_descriptor =
        getDescriptor().getMessageTypes().get(3);
    internal_static_google_cloud_pubsublite_v1_MessagePublishResponse_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_pubsublite_v1_MessagePublishResponse_descriptor,
            new java.lang.String[] {
              "StartCursor", "CursorRanges",
            });
    internal_static_google_cloud_pubsublite_v1_MessagePublishResponse_CursorRange_descriptor =
        internal_static_google_cloud_pubsublite_v1_MessagePublishResponse_descriptor
            .getNestedTypes()
            .get(0);
    internal_static_google_cloud_pubsublite_v1_MessagePublishResponse_CursorRange_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_pubsublite_v1_MessagePublishResponse_CursorRange_descriptor,
            new java.lang.String[] {
              "StartCursor", "StartIndex", "EndIndex",
            });
    internal_static_google_cloud_pubsublite_v1_PublishRequest_descriptor =
        getDescriptor().getMessageTypes().get(4);
    internal_static_google_cloud_pubsublite_v1_PublishRequest_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_pubsublite_v1_PublishRequest_descriptor,
            new java.lang.String[] {
              "InitialRequest", "MessagePublishRequest", "RequestType",
            });
    internal_static_google_cloud_pubsublite_v1_PublishResponse_descriptor =
        getDescriptor().getMessageTypes().get(5);
    internal_static_google_cloud_pubsublite_v1_PublishResponse_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_pubsublite_v1_PublishResponse_descriptor,
            new java.lang.String[] {
              "InitialResponse", "MessageResponse", "ResponseType",
            });
    com.google.protobuf.ExtensionRegistry registry =
        com.google.protobuf.ExtensionRegistry.newInstance();
    registry.add(com.google.api.ClientProto.defaultHost);
    registry.add(com.google.api.ClientProto.oauthScopes);
    com.google.protobuf.Descriptors.FileDescriptor.internalUpdateFileDescriptor(
        descriptor, registry);
    com.google.api.AnnotationsProto.getDescriptor();
    com.google.api.ClientProto.getDescriptor();
    com.google.cloud.pubsublite.proto.CommonProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
