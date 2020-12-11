/*
 * Copyright 2020 Google LLC
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
// source: google/cloud/pubsublite/v1/subscriber.proto

package com.google.cloud.pubsublite.proto;

public final class SubscriberProto {
  private SubscriberProto() {}

  public static void registerAllExtensions(com.google.protobuf.ExtensionRegistryLite registry) {}

  public static void registerAllExtensions(com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions((com.google.protobuf.ExtensionRegistryLite) registry);
  }

  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_pubsublite_v1_InitialSubscribeRequest_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_pubsublite_v1_InitialSubscribeRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_pubsublite_v1_InitialSubscribeResponse_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_pubsublite_v1_InitialSubscribeResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_pubsublite_v1_SeekRequest_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_pubsublite_v1_SeekRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_pubsublite_v1_SeekResponse_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_pubsublite_v1_SeekResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_pubsublite_v1_FlowControlRequest_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_pubsublite_v1_FlowControlRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_pubsublite_v1_SubscribeRequest_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_pubsublite_v1_SubscribeRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_pubsublite_v1_MessageResponse_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_pubsublite_v1_MessageResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_pubsublite_v1_SubscribeResponse_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_pubsublite_v1_SubscribeResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_pubsublite_v1_InitialPartitionAssignmentRequest_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_pubsublite_v1_InitialPartitionAssignmentRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_pubsublite_v1_PartitionAssignment_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_pubsublite_v1_PartitionAssignment_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_pubsublite_v1_PartitionAssignmentAck_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_pubsublite_v1_PartitionAssignmentAck_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_pubsublite_v1_PartitionAssignmentRequest_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_pubsublite_v1_PartitionAssignmentRequest_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor getDescriptor() {
    return descriptor;
  }

  private static com.google.protobuf.Descriptors.FileDescriptor descriptor;

  static {
    java.lang.String[] descriptorData = {
      "\n+google/cloud/pubsublite/v1/subscriber."
          + "proto\022\032google.cloud.pubsublite.v1\032\027googl"
          + "e/api/client.proto\032\'google/cloud/pubsubl"
          + "ite/v1/common.proto\"B\n\027InitialSubscribeR"
          + "equest\022\024\n\014subscription\030\001 \001(\t\022\021\n\tpartitio"
          + "n\030\002 \001(\003\"N\n\030InitialSubscribeResponse\0222\n\006c"
          + "ursor\030\001 \001(\0132\".google.cloud.pubsublite.v1"
          + ".Cursor\"\347\001\n\013SeekRequest\022K\n\014named_target\030"
          + "\001 \001(\01623.google.cloud.pubsublite.v1.SeekR"
          + "equest.NamedTargetH\000\0224\n\006cursor\030\002 \001(\0132\".g"
          + "oogle.cloud.pubsublite.v1.CursorH\000\"K\n\013Na"
          + "medTarget\022\034\n\030NAMED_TARGET_UNSPECIFIED\020\000\022"
          + "\010\n\004HEAD\020\001\022\024\n\020COMMITTED_CURSOR\020\002B\010\n\006targe"
          + "t\"B\n\014SeekResponse\0222\n\006cursor\030\001 \001(\0132\".goog"
          + "le.cloud.pubsublite.v1.Cursor\"E\n\022FlowCon"
          + "trolRequest\022\030\n\020allowed_messages\030\001 \001(\003\022\025\n"
          + "\rallowed_bytes\030\002 \001(\003\"\346\001\n\020SubscribeReques"
          + "t\022F\n\007initial\030\001 \001(\01323.google.cloud.pubsub"
          + "lite.v1.InitialSubscribeRequestH\000\0227\n\004see"
          + "k\030\002 \001(\0132\'.google.cloud.pubsublite.v1.See"
          + "kRequestH\000\022F\n\014flow_control\030\003 \001(\0132..googl"
          + "e.cloud.pubsublite.v1.FlowControlRequest"
          + "H\000B\t\n\007request\"Q\n\017MessageResponse\022>\n\010mess"
          + "ages\030\001 \003(\0132,.google.cloud.pubsublite.v1."
          + "SequencedMessage\"\343\001\n\021SubscribeResponse\022G"
          + "\n\007initial\030\001 \001(\01324.google.cloud.pubsublit"
          + "e.v1.InitialSubscribeResponseH\000\0228\n\004seek\030"
          + "\002 \001(\0132(.google.cloud.pubsublite.v1.SeekR"
          + "esponseH\000\022?\n\010messages\030\003 \001(\0132+.google.clo"
          + "ud.pubsublite.v1.MessageResponseH\000B\n\n\010re"
          + "sponse\"L\n!InitialPartitionAssignmentRequ"
          + "est\022\024\n\014subscription\030\001 \001(\t\022\021\n\tclient_id\030\002"
          + " \001(\014\")\n\023PartitionAssignment\022\022\n\npartition"
          + "s\030\001 \003(\003\"\030\n\026PartitionAssignmentAck\"\274\001\n\032Pa"
          + "rtitionAssignmentRequest\022P\n\007initial\030\001 \001("
          + "\0132=.google.cloud.pubsublite.v1.InitialPa"
          + "rtitionAssignmentRequestH\000\022A\n\003ack\030\002 \001(\0132"
          + "2.google.cloud.pubsublite.v1.PartitionAs"
          + "signmentAckH\000B\t\n\007request2\322\001\n\021SubscriberS"
          + "ervice\022n\n\tSubscribe\022,.google.cloud.pubsu"
          + "blite.v1.SubscribeRequest\032-.google.cloud"
          + ".pubsublite.v1.SubscribeResponse\"\000(\0010\001\032M"
          + "\312A\031pubsublite.googleapis.com\322A.https://w"
          + "ww.googleapis.com/auth/cloud-platform2\357\001"
          + "\n\032PartitionAssignmentService\022\201\001\n\020AssignP"
          + "artitions\0226.google.cloud.pubsublite.v1.P"
          + "artitionAssignmentRequest\032/.google.cloud"
          + ".pubsublite.v1.PartitionAssignment\"\000(\0010\001"
          + "\032M\312A\031pubsublite.googleapis.com\322A.https:/"
          + "/www.googleapis.com/auth/cloud-platformB"
          + "\331\001\n!com.google.cloud.pubsublite.protoB\017S"
          + "ubscriberProtoP\001ZDgoogle.golang.org/genp"
          + "roto/googleapis/cloud/pubsublite/v1;pubs"
          + "ublite\370\001\001\252\002\032Google.Cloud.PubSubLite.V1\312\002"
          + "\032Google\\Cloud\\PubSubLite\\V1\352\002\035Google::Cl"
          + "oud::PubSubLite::V1b\006proto3"
    };
    descriptor =
        com.google.protobuf.Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(
            descriptorData,
            new com.google.protobuf.Descriptors.FileDescriptor[] {
              com.google.api.ClientProto.getDescriptor(),
              com.google.cloud.pubsublite.proto.CommonProto.getDescriptor(),
            });
    internal_static_google_cloud_pubsublite_v1_InitialSubscribeRequest_descriptor =
        getDescriptor().getMessageTypes().get(0);
    internal_static_google_cloud_pubsublite_v1_InitialSubscribeRequest_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_pubsublite_v1_InitialSubscribeRequest_descriptor,
            new java.lang.String[] {
              "Subscription", "Partition",
            });
    internal_static_google_cloud_pubsublite_v1_InitialSubscribeResponse_descriptor =
        getDescriptor().getMessageTypes().get(1);
    internal_static_google_cloud_pubsublite_v1_InitialSubscribeResponse_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_pubsublite_v1_InitialSubscribeResponse_descriptor,
            new java.lang.String[] {
              "Cursor",
            });
    internal_static_google_cloud_pubsublite_v1_SeekRequest_descriptor =
        getDescriptor().getMessageTypes().get(2);
    internal_static_google_cloud_pubsublite_v1_SeekRequest_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_pubsublite_v1_SeekRequest_descriptor,
            new java.lang.String[] {
              "NamedTarget", "Cursor", "Target",
            });
    internal_static_google_cloud_pubsublite_v1_SeekResponse_descriptor =
        getDescriptor().getMessageTypes().get(3);
    internal_static_google_cloud_pubsublite_v1_SeekResponse_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_pubsublite_v1_SeekResponse_descriptor,
            new java.lang.String[] {
              "Cursor",
            });
    internal_static_google_cloud_pubsublite_v1_FlowControlRequest_descriptor =
        getDescriptor().getMessageTypes().get(4);
    internal_static_google_cloud_pubsublite_v1_FlowControlRequest_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_pubsublite_v1_FlowControlRequest_descriptor,
            new java.lang.String[] {
              "AllowedMessages", "AllowedBytes",
            });
    internal_static_google_cloud_pubsublite_v1_SubscribeRequest_descriptor =
        getDescriptor().getMessageTypes().get(5);
    internal_static_google_cloud_pubsublite_v1_SubscribeRequest_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_pubsublite_v1_SubscribeRequest_descriptor,
            new java.lang.String[] {
              "Initial", "Seek", "FlowControl", "Request",
            });
    internal_static_google_cloud_pubsublite_v1_MessageResponse_descriptor =
        getDescriptor().getMessageTypes().get(6);
    internal_static_google_cloud_pubsublite_v1_MessageResponse_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_pubsublite_v1_MessageResponse_descriptor,
            new java.lang.String[] {
              "Messages",
            });
    internal_static_google_cloud_pubsublite_v1_SubscribeResponse_descriptor =
        getDescriptor().getMessageTypes().get(7);
    internal_static_google_cloud_pubsublite_v1_SubscribeResponse_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_pubsublite_v1_SubscribeResponse_descriptor,
            new java.lang.String[] {
              "Initial", "Seek", "Messages", "Response",
            });
    internal_static_google_cloud_pubsublite_v1_InitialPartitionAssignmentRequest_descriptor =
        getDescriptor().getMessageTypes().get(8);
    internal_static_google_cloud_pubsublite_v1_InitialPartitionAssignmentRequest_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_pubsublite_v1_InitialPartitionAssignmentRequest_descriptor,
            new java.lang.String[] {
              "Subscription", "ClientId",
            });
    internal_static_google_cloud_pubsublite_v1_PartitionAssignment_descriptor =
        getDescriptor().getMessageTypes().get(9);
    internal_static_google_cloud_pubsublite_v1_PartitionAssignment_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_pubsublite_v1_PartitionAssignment_descriptor,
            new java.lang.String[] {
              "Partitions",
            });
    internal_static_google_cloud_pubsublite_v1_PartitionAssignmentAck_descriptor =
        getDescriptor().getMessageTypes().get(10);
    internal_static_google_cloud_pubsublite_v1_PartitionAssignmentAck_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_pubsublite_v1_PartitionAssignmentAck_descriptor,
            new java.lang.String[] {});
    internal_static_google_cloud_pubsublite_v1_PartitionAssignmentRequest_descriptor =
        getDescriptor().getMessageTypes().get(11);
    internal_static_google_cloud_pubsublite_v1_PartitionAssignmentRequest_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_pubsublite_v1_PartitionAssignmentRequest_descriptor,
            new java.lang.String[] {
              "Initial", "Ack", "Request",
            });
    com.google.protobuf.ExtensionRegistry registry =
        com.google.protobuf.ExtensionRegistry.newInstance();
    registry.add(com.google.api.ClientProto.defaultHost);
    registry.add(com.google.api.ClientProto.oauthScopes);
    com.google.protobuf.Descriptors.FileDescriptor.internalUpdateFileDescriptor(
        descriptor, registry);
    com.google.api.ClientProto.getDescriptor();
    com.google.cloud.pubsublite.proto.CommonProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
