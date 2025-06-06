/*
 * Copyright 2025 Google LLC
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
// source: google/cloud/pubsublite/v1/cursor.proto

// Protobuf Java Version: 3.25.8
package com.google.cloud.pubsublite.proto;

public final class CursorProto {
  private CursorProto() {}

  public static void registerAllExtensions(com.google.protobuf.ExtensionRegistryLite registry) {}

  public static void registerAllExtensions(com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions((com.google.protobuf.ExtensionRegistryLite) registry);
  }

  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_pubsublite_v1_InitialCommitCursorRequest_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_pubsublite_v1_InitialCommitCursorRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_pubsublite_v1_InitialCommitCursorResponse_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_pubsublite_v1_InitialCommitCursorResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_pubsublite_v1_SequencedCommitCursorRequest_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_pubsublite_v1_SequencedCommitCursorRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_pubsublite_v1_SequencedCommitCursorResponse_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_pubsublite_v1_SequencedCommitCursorResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_pubsublite_v1_StreamingCommitCursorRequest_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_pubsublite_v1_StreamingCommitCursorRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_pubsublite_v1_StreamingCommitCursorResponse_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_pubsublite_v1_StreamingCommitCursorResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_pubsublite_v1_CommitCursorRequest_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_pubsublite_v1_CommitCursorRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_pubsublite_v1_CommitCursorResponse_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_pubsublite_v1_CommitCursorResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_pubsublite_v1_ListPartitionCursorsRequest_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_pubsublite_v1_ListPartitionCursorsRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_pubsublite_v1_PartitionCursor_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_pubsublite_v1_PartitionCursor_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_pubsublite_v1_ListPartitionCursorsResponse_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_pubsublite_v1_ListPartitionCursorsResponse_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor getDescriptor() {
    return descriptor;
  }

  private static com.google.protobuf.Descriptors.FileDescriptor descriptor;

  static {
    java.lang.String[] descriptorData = {
      "\n\'google/cloud/pubsublite/v1/cursor.prot"
          + "o\022\032google.cloud.pubsublite.v1\032\034google/ap"
          + "i/annotations.proto\032\027google/api/client.p"
          + "roto\032\037google/api/field_behavior.proto\032\031g"
          + "oogle/api/resource.proto\032\'google/cloud/p"
          + "ubsublite/v1/common.proto\"E\n\032InitialComm"
          + "itCursorRequest\022\024\n\014subscription\030\001 \001(\t\022\021\n"
          + "\tpartition\030\002 \001(\003\"\035\n\033InitialCommitCursorR"
          + "esponse\"R\n\034SequencedCommitCursorRequest\022"
          + "2\n\006cursor\030\001 \001(\0132\".google.cloud.pubsublit"
          + "e.v1.Cursor\"=\n\035SequencedCommitCursorResp"
          + "onse\022\034\n\024acknowledged_commits\030\001 \001(\003\"\300\001\n\034S"
          + "treamingCommitCursorRequest\022I\n\007initial\030\001"
          + " \001(\01326.google.cloud.pubsublite.v1.Initia"
          + "lCommitCursorRequestH\000\022J\n\006commit\030\002 \001(\01328"
          + ".google.cloud.pubsublite.v1.SequencedCom"
          + "mitCursorRequestH\000B\t\n\007request\"\303\001\n\035Stream"
          + "ingCommitCursorResponse\022J\n\007initial\030\001 \001(\013"
          + "27.google.cloud.pubsublite.v1.InitialCom"
          + "mitCursorResponseH\000\022K\n\006commit\030\002 \001(\01329.go"
          + "ogle.cloud.pubsublite.v1.SequencedCommit"
          + "CursorResponseH\000B\t\n\007request\"r\n\023CommitCur"
          + "sorRequest\022\024\n\014subscription\030\001 \001(\t\022\021\n\tpart"
          + "ition\030\002 \001(\003\0222\n\006cursor\030\003 \001(\0132\".google.clo"
          + "ud.pubsublite.v1.Cursor\"\026\n\024CommitCursorR"
          + "esponse\"\204\001\n\033ListPartitionCursorsRequest\022"
          + ">\n\006parent\030\001 \001(\tB.\340A\002\372A(\n&pubsublite.goog"
          + "leapis.com/Subscription\022\021\n\tpage_size\030\002 \001"
          + "(\005\022\022\n\npage_token\030\003 \001(\t\"X\n\017PartitionCurso"
          + "r\022\021\n\tpartition\030\001 \001(\003\0222\n\006cursor\030\002 \001(\0132\".g"
          + "oogle.cloud.pubsublite.v1.Cursor\"\177\n\034List"
          + "PartitionCursorsResponse\022F\n\021partition_cu"
          + "rsors\030\001 \003(\0132+.google.cloud.pubsublite.v1"
          + ".PartitionCursor\022\027\n\017next_page_token\030\002 \001("
          + "\t2\242\005\n\rCursorService\022\222\001\n\025StreamingCommitC"
          + "ursor\0228.google.cloud.pubsublite.v1.Strea"
          + "mingCommitCursorRequest\0329.google.cloud.p"
          + "ubsublite.v1.StreamingCommitCursorRespon"
          + "se\"\000(\0010\001\022\313\001\n\014CommitCursor\022/.google.cloud"
          + ".pubsublite.v1.CommitCursorRequest\0320.goo"
          + "gle.cloud.pubsublite.v1.CommitCursorResp"
          + "onse\"X\202\323\344\223\002R\"M/v1/cursor/{subscription=p"
          + "rojects/*/locations/*/subscriptions/*}:c"
          + "ommitCursor:\001*\022\336\001\n\024ListPartitionCursors\022"
          + "7.google.cloud.pubsublite.v1.ListPartiti"
          + "onCursorsRequest\0328.google.cloud.pubsubli"
          + "te.v1.ListPartitionCursorsResponse\"S\332A\006p"
          + "arent\202\323\344\223\002D\022B/v1/cursor/{parent=projects"
          + "/*/locations/*/subscriptions/*}/cursors\032"
          + "M\312A\031pubsublite.googleapis.com\322A.https://"
          + "www.googleapis.com/auth/cloud-platformB\317"
          + "\001\n!com.google.cloud.pubsublite.protoB\013Cu"
          + "rsorProtoP\001Z>cloud.google.com/go/pubsubl"
          + "ite/apiv1/pubsublitepb;pubsublitepb\370\001\001\252\002"
          + "\032Google.Cloud.PubSubLite.V1\312\002\032Google\\Clo"
          + "ud\\PubSubLite\\V1\352\002\035Google::Cloud::PubSub"
          + "Lite::V1b\006proto3"
    };
    descriptor =
        com.google.protobuf.Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(
            descriptorData,
            new com.google.protobuf.Descriptors.FileDescriptor[] {
              com.google.api.AnnotationsProto.getDescriptor(),
              com.google.api.ClientProto.getDescriptor(),
              com.google.api.FieldBehaviorProto.getDescriptor(),
              com.google.api.ResourceProto.getDescriptor(),
              com.google.cloud.pubsublite.proto.CommonProto.getDescriptor(),
            });
    internal_static_google_cloud_pubsublite_v1_InitialCommitCursorRequest_descriptor =
        getDescriptor().getMessageTypes().get(0);
    internal_static_google_cloud_pubsublite_v1_InitialCommitCursorRequest_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_pubsublite_v1_InitialCommitCursorRequest_descriptor,
            new java.lang.String[] {
              "Subscription", "Partition",
            });
    internal_static_google_cloud_pubsublite_v1_InitialCommitCursorResponse_descriptor =
        getDescriptor().getMessageTypes().get(1);
    internal_static_google_cloud_pubsublite_v1_InitialCommitCursorResponse_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_pubsublite_v1_InitialCommitCursorResponse_descriptor,
            new java.lang.String[] {});
    internal_static_google_cloud_pubsublite_v1_SequencedCommitCursorRequest_descriptor =
        getDescriptor().getMessageTypes().get(2);
    internal_static_google_cloud_pubsublite_v1_SequencedCommitCursorRequest_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_pubsublite_v1_SequencedCommitCursorRequest_descriptor,
            new java.lang.String[] {
              "Cursor",
            });
    internal_static_google_cloud_pubsublite_v1_SequencedCommitCursorResponse_descriptor =
        getDescriptor().getMessageTypes().get(3);
    internal_static_google_cloud_pubsublite_v1_SequencedCommitCursorResponse_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_pubsublite_v1_SequencedCommitCursorResponse_descriptor,
            new java.lang.String[] {
              "AcknowledgedCommits",
            });
    internal_static_google_cloud_pubsublite_v1_StreamingCommitCursorRequest_descriptor =
        getDescriptor().getMessageTypes().get(4);
    internal_static_google_cloud_pubsublite_v1_StreamingCommitCursorRequest_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_pubsublite_v1_StreamingCommitCursorRequest_descriptor,
            new java.lang.String[] {
              "Initial", "Commit", "Request",
            });
    internal_static_google_cloud_pubsublite_v1_StreamingCommitCursorResponse_descriptor =
        getDescriptor().getMessageTypes().get(5);
    internal_static_google_cloud_pubsublite_v1_StreamingCommitCursorResponse_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_pubsublite_v1_StreamingCommitCursorResponse_descriptor,
            new java.lang.String[] {
              "Initial", "Commit", "Request",
            });
    internal_static_google_cloud_pubsublite_v1_CommitCursorRequest_descriptor =
        getDescriptor().getMessageTypes().get(6);
    internal_static_google_cloud_pubsublite_v1_CommitCursorRequest_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_pubsublite_v1_CommitCursorRequest_descriptor,
            new java.lang.String[] {
              "Subscription", "Partition", "Cursor",
            });
    internal_static_google_cloud_pubsublite_v1_CommitCursorResponse_descriptor =
        getDescriptor().getMessageTypes().get(7);
    internal_static_google_cloud_pubsublite_v1_CommitCursorResponse_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_pubsublite_v1_CommitCursorResponse_descriptor,
            new java.lang.String[] {});
    internal_static_google_cloud_pubsublite_v1_ListPartitionCursorsRequest_descriptor =
        getDescriptor().getMessageTypes().get(8);
    internal_static_google_cloud_pubsublite_v1_ListPartitionCursorsRequest_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_pubsublite_v1_ListPartitionCursorsRequest_descriptor,
            new java.lang.String[] {
              "Parent", "PageSize", "PageToken",
            });
    internal_static_google_cloud_pubsublite_v1_PartitionCursor_descriptor =
        getDescriptor().getMessageTypes().get(9);
    internal_static_google_cloud_pubsublite_v1_PartitionCursor_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_pubsublite_v1_PartitionCursor_descriptor,
            new java.lang.String[] {
              "Partition", "Cursor",
            });
    internal_static_google_cloud_pubsublite_v1_ListPartitionCursorsResponse_descriptor =
        getDescriptor().getMessageTypes().get(10);
    internal_static_google_cloud_pubsublite_v1_ListPartitionCursorsResponse_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_pubsublite_v1_ListPartitionCursorsResponse_descriptor,
            new java.lang.String[] {
              "PartitionCursors", "NextPageToken",
            });
    com.google.protobuf.ExtensionRegistry registry =
        com.google.protobuf.ExtensionRegistry.newInstance();
    registry.add(com.google.api.ClientProto.defaultHost);
    registry.add(com.google.api.FieldBehaviorProto.fieldBehavior);
    registry.add(com.google.api.AnnotationsProto.http);
    registry.add(com.google.api.ClientProto.methodSignature);
    registry.add(com.google.api.ClientProto.oauthScopes);
    registry.add(com.google.api.ResourceProto.resourceReference);
    com.google.protobuf.Descriptors.FileDescriptor.internalUpdateFileDescriptor(
        descriptor, registry);
    com.google.api.AnnotationsProto.getDescriptor();
    com.google.api.ClientProto.getDescriptor();
    com.google.api.FieldBehaviorProto.getDescriptor();
    com.google.api.ResourceProto.getDescriptor();
    com.google.cloud.pubsublite.proto.CommonProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
