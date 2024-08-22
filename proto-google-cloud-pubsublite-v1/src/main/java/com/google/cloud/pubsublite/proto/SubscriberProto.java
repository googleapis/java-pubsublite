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
// source: google/cloud/pubsublite/v1/subscriber.proto

// Protobuf Java Version: 3.25.4
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
          + "proto\022\032google.cloud.pubsublite.v1\032\034googl"
          + "e/api/annotations.proto\032\027google/api/clie"
          + "nt.proto\032\037google/api/field_behavior.prot"
          + "o\032\'google/cloud/pubsublite/v1/common.pro"
          + "to\"\212\001\n\027InitialSubscribeRequest\022\024\n\014subscr"
          + "iption\030\001 \001(\t\022\021\n\tpartition\030\002 \001(\003\022F\n\020initi"
          + "al_location\030\004 \001(\0132\'.google.cloud.pubsubl"
          + "ite.v1.SeekRequestB\003\340A\001\"N\n\030InitialSubscr"
          + "ibeResponse\0222\n\006cursor\030\001 \001(\0132\".google.clo"
          + "ud.pubsublite.v1.Cursor\"\347\001\n\013SeekRequest\022"
          + "K\n\014named_target\030\001 \001(\01623.google.cloud.pub"
          + "sublite.v1.SeekRequest.NamedTargetH\000\0224\n\006"
          + "cursor\030\002 \001(\0132\".google.cloud.pubsublite.v"
          + "1.CursorH\000\"K\n\013NamedTarget\022\034\n\030NAMED_TARGE"
          + "T_UNSPECIFIED\020\000\022\010\n\004HEAD\020\001\022\024\n\020COMMITTED_C"
          + "URSOR\020\002B\010\n\006target\"B\n\014SeekResponse\0222\n\006cur"
          + "sor\030\001 \001(\0132\".google.cloud.pubsublite.v1.C"
          + "ursor\"E\n\022FlowControlRequest\022\030\n\020allowed_m"
          + "essages\030\001 \001(\003\022\025\n\rallowed_bytes\030\002 \001(\003\"\346\001\n"
          + "\020SubscribeRequest\022F\n\007initial\030\001 \001(\01323.goo"
          + "gle.cloud.pubsublite.v1.InitialSubscribe"
          + "RequestH\000\0227\n\004seek\030\002 \001(\0132\'.google.cloud.p"
          + "ubsublite.v1.SeekRequestH\000\022F\n\014flow_contr"
          + "ol\030\003 \001(\0132..google.cloud.pubsublite.v1.Fl"
          + "owControlRequestH\000B\t\n\007request\"Q\n\017Message"
          + "Response\022>\n\010messages\030\001 \003(\0132,.google.clou"
          + "d.pubsublite.v1.SequencedMessage\"\343\001\n\021Sub"
          + "scribeResponse\022G\n\007initial\030\001 \001(\01324.google"
          + ".cloud.pubsublite.v1.InitialSubscribeRes"
          + "ponseH\000\0228\n\004seek\030\002 \001(\0132(.google.cloud.pub"
          + "sublite.v1.SeekResponseH\000\022?\n\010messages\030\003 "
          + "\001(\0132+.google.cloud.pubsublite.v1.Message"
          + "ResponseH\000B\n\n\010response\"L\n!InitialPartiti"
          + "onAssignmentRequest\022\024\n\014subscription\030\001 \001("
          + "\t\022\021\n\tclient_id\030\002 \001(\014\")\n\023PartitionAssignm"
          + "ent\022\022\n\npartitions\030\001 \003(\003\"\030\n\026PartitionAssi"
          + "gnmentAck\"\274\001\n\032PartitionAssignmentRequest"
          + "\022P\n\007initial\030\001 \001(\0132=.google.cloud.pubsubl"
          + "ite.v1.InitialPartitionAssignmentRequest"
          + "H\000\022A\n\003ack\030\002 \001(\01322.google.cloud.pubsublit"
          + "e.v1.PartitionAssignmentAckH\000B\t\n\007request"
          + "2\322\001\n\021SubscriberService\022n\n\tSubscribe\022,.go"
          + "ogle.cloud.pubsublite.v1.SubscribeReques"
          + "t\032-.google.cloud.pubsublite.v1.Subscribe"
          + "Response\"\000(\0010\001\032M\312A\031pubsublite.googleapis"
          + ".com\322A.https://www.googleapis.com/auth/c"
          + "loud-platform2\357\001\n\032PartitionAssignmentSer"
          + "vice\022\201\001\n\020AssignPartitions\0226.google.cloud"
          + ".pubsublite.v1.PartitionAssignmentReques"
          + "t\032/.google.cloud.pubsublite.v1.Partition"
          + "Assignment\"\000(\0010\001\032M\312A\031pubsublite.googleap"
          + "is.com\322A.https://www.googleapis.com/auth"
          + "/cloud-platformB\323\001\n!com.google.cloud.pub"
          + "sublite.protoB\017SubscriberProtoP\001Z>cloud."
          + "google.com/go/pubsublite/apiv1/pubsublit"
          + "epb;pubsublitepb\370\001\001\252\002\032Google.Cloud.PubSu"
          + "bLite.V1\312\002\032Google\\Cloud\\PubSubLite\\V1\352\002\035"
          + "Google::Cloud::PubSubLite::V1b\006proto3"
    };
    descriptor =
        com.google.protobuf.Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(
            descriptorData,
            new com.google.protobuf.Descriptors.FileDescriptor[] {
              com.google.api.AnnotationsProto.getDescriptor(),
              com.google.api.ClientProto.getDescriptor(),
              com.google.api.FieldBehaviorProto.getDescriptor(),
              com.google.cloud.pubsublite.proto.CommonProto.getDescriptor(),
            });
    internal_static_google_cloud_pubsublite_v1_InitialSubscribeRequest_descriptor =
        getDescriptor().getMessageTypes().get(0);
    internal_static_google_cloud_pubsublite_v1_InitialSubscribeRequest_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_pubsublite_v1_InitialSubscribeRequest_descriptor,
            new java.lang.String[] {
              "Subscription", "Partition", "InitialLocation",
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
    registry.add(com.google.api.FieldBehaviorProto.fieldBehavior);
    registry.add(com.google.api.ClientProto.oauthScopes);
    com.google.protobuf.Descriptors.FileDescriptor.internalUpdateFileDescriptor(
        descriptor, registry);
    com.google.api.AnnotationsProto.getDescriptor();
    com.google.api.ClientProto.getDescriptor();
    com.google.api.FieldBehaviorProto.getDescriptor();
    com.google.cloud.pubsublite.proto.CommonProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
