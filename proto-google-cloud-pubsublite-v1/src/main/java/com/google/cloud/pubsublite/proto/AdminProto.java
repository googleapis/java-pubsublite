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
// source: google/cloud/pubsublite/v1/admin.proto

package com.google.cloud.pubsublite.proto;

public final class AdminProto {
  private AdminProto() {}

  public static void registerAllExtensions(com.google.protobuf.ExtensionRegistryLite registry) {}

  public static void registerAllExtensions(com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions((com.google.protobuf.ExtensionRegistryLite) registry);
  }

  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_pubsublite_v1_CreateTopicRequest_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_pubsublite_v1_CreateTopicRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_pubsublite_v1_GetTopicRequest_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_pubsublite_v1_GetTopicRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_pubsublite_v1_GetTopicPartitionsRequest_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_pubsublite_v1_GetTopicPartitionsRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_pubsublite_v1_TopicPartitions_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_pubsublite_v1_TopicPartitions_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_pubsublite_v1_ListTopicsRequest_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_pubsublite_v1_ListTopicsRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_pubsublite_v1_ListTopicsResponse_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_pubsublite_v1_ListTopicsResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_pubsublite_v1_UpdateTopicRequest_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_pubsublite_v1_UpdateTopicRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_pubsublite_v1_DeleteTopicRequest_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_pubsublite_v1_DeleteTopicRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_pubsublite_v1_ListTopicSubscriptionsRequest_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_pubsublite_v1_ListTopicSubscriptionsRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_pubsublite_v1_ListTopicSubscriptionsResponse_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_pubsublite_v1_ListTopicSubscriptionsResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_pubsublite_v1_CreateSubscriptionRequest_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_pubsublite_v1_CreateSubscriptionRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_pubsublite_v1_GetSubscriptionRequest_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_pubsublite_v1_GetSubscriptionRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_pubsublite_v1_ListSubscriptionsRequest_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_pubsublite_v1_ListSubscriptionsRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_pubsublite_v1_ListSubscriptionsResponse_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_pubsublite_v1_ListSubscriptionsResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_pubsublite_v1_UpdateSubscriptionRequest_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_pubsublite_v1_UpdateSubscriptionRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
      internal_static_google_cloud_pubsublite_v1_DeleteSubscriptionRequest_descriptor;
  static final com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_google_cloud_pubsublite_v1_DeleteSubscriptionRequest_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor getDescriptor() {
    return descriptor;
  }

  private static com.google.protobuf.Descriptors.FileDescriptor descriptor;

  static {
    java.lang.String[] descriptorData = {
      "\n&google/cloud/pubsublite/v1/admin.proto"
          + "\022\032google.cloud.pubsublite.v1\032\034google/api"
          + "/annotations.proto\032\027google/api/client.pr"
          + "oto\032\037google/api/field_behavior.proto\032\031go"
          + "ogle/api/resource.proto\032\'google/cloud/pu"
          + "bsublite/v1/common.proto\032\033google/protobu"
          + "f/empty.proto\032 google/protobuf/field_mas"
          + "k.proto\032\037google/protobuf/timestamp.proto"
          + "\"\235\001\n\022CreateTopicRequest\0229\n\006parent\030\001 \001(\tB"
          + ")\340A\002\372A#\n!locations.googleapis.com/Locati"
          + "on\0225\n\005topic\030\002 \001(\0132!.google.cloud.pubsubl"
          + "ite.v1.TopicB\003\340A\002\022\025\n\010topic_id\030\003 \001(\tB\003\340A\002"
          + "\"H\n\017GetTopicRequest\0225\n\004name\030\001 \001(\tB\'\340A\002\372A"
          + "!\n\037pubsublite.googleapis.com/Topic\"R\n\031Ge"
          + "tTopicPartitionsRequest\0225\n\004name\030\001 \001(\tB\'\340"
          + "A\002\372A!\n\037pubsublite.googleapis.com/Topic\"*"
          + "\n\017TopicPartitions\022\027\n\017partition_count\030\001 \001"
          + "(\003\"u\n\021ListTopicsRequest\0229\n\006parent\030\001 \001(\tB"
          + ")\340A\002\372A#\n!locations.googleapis.com/Locati"
          + "on\022\021\n\tpage_size\030\002 \001(\005\022\022\n\npage_token\030\003 \001("
          + "\t\"`\n\022ListTopicsResponse\0221\n\006topics\030\001 \003(\0132"
          + "!.google.cloud.pubsublite.v1.Topic\022\027\n\017ne"
          + "xt_page_token\030\002 \001(\t\"\201\001\n\022UpdateTopicReque"
          + "st\0225\n\005topic\030\001 \001(\0132!.google.cloud.pubsubl"
          + "ite.v1.TopicB\003\340A\002\0224\n\013update_mask\030\002 \001(\0132\032"
          + ".google.protobuf.FieldMaskB\003\340A\002\"K\n\022Delet"
          + "eTopicRequest\0225\n\004name\030\001 \001(\tB\'\340A\002\372A!\n\037pub"
          + "sublite.googleapis.com/Topic\"}\n\035ListTopi"
          + "cSubscriptionsRequest\0225\n\004name\030\001 \001(\tB\'\340A\002"
          + "\372A!\n\037pubsublite.googleapis.com/Topic\022\021\n\t"
          + "page_size\030\002 \001(\005\022\022\n\npage_token\030\003 \001(\t\"P\n\036L"
          + "istTopicSubscriptionsResponse\022\025\n\rsubscri"
          + "ptions\030\001 \003(\t\022\027\n\017next_page_token\030\002 \001(\t\"\317\001"
          + "\n\031CreateSubscriptionRequest\0229\n\006parent\030\001 "
          + "\001(\tB)\340A\002\372A#\n!locations.googleapis.com/Lo"
          + "cation\022C\n\014subscription\030\002 \001(\0132(.google.cl"
          + "oud.pubsublite.v1.SubscriptionB\003\340A\002\022\034\n\017s"
          + "ubscription_id\030\003 \001(\tB\003\340A\002\022\024\n\014skip_backlo"
          + "g\030\004 \001(\010\"V\n\026GetSubscriptionRequest\022<\n\004nam"
          + "e\030\001 \001(\tB.\340A\002\372A(\n&pubsublite.googleapis.c"
          + "om/Subscription\"|\n\030ListSubscriptionsRequ"
          + "est\0229\n\006parent\030\001 \001(\tB)\340A\002\372A#\n!locations.g"
          + "oogleapis.com/Location\022\021\n\tpage_size\030\002 \001("
          + "\005\022\022\n\npage_token\030\003 \001(\t\"u\n\031ListSubscriptio"
          + "nsResponse\022?\n\rsubscriptions\030\001 \003(\0132(.goog"
          + "le.cloud.pubsublite.v1.Subscription\022\027\n\017n"
          + "ext_page_token\030\002 \001(\t\"\226\001\n\031UpdateSubscript"
          + "ionRequest\022C\n\014subscription\030\001 \001(\0132(.googl"
          + "e.cloud.pubsublite.v1.SubscriptionB\003\340A\002\022"
          + "4\n\013update_mask\030\002 \001(\0132\032.google.protobuf.F"
          + "ieldMaskB\003\340A\002\"Y\n\031DeleteSubscriptionReque"
          + "st\022<\n\004name\030\001 \001(\tB.\340A\002\372A(\n&pubsublite.goo"
          + "gleapis.com/Subscription2\370\022\n\014AdminServic"
          + "e\022\271\001\n\013CreateTopic\022..google.cloud.pubsubl"
          + "ite.v1.CreateTopicRequest\032!.google.cloud"
          + ".pubsublite.v1.Topic\"W\202\323\344\223\0029\"0/v1/admin/"
          + "{parent=projects/*/locations/*}/topics:\005"
          + "topic\332A\025parent,topic,topic_id\022\233\001\n\010GetTop"
          + "ic\022+.google.cloud.pubsublite.v1.GetTopic"
          + "Request\032!.google.cloud.pubsublite.v1.Top"
          + "ic\"?\202\323\344\223\0022\0220/v1/admin/{name=projects/*/l"
          + "ocations/*/topics/*}\332A\004name\022\304\001\n\022GetTopic"
          + "Partitions\0225.google.cloud.pubsublite.v1."
          + "GetTopicPartitionsRequest\032+.google.cloud"
          + ".pubsublite.v1.TopicPartitions\"J\202\323\344\223\002=\022;"
          + "/v1/admin/{name=projects/*/locations/*/t"
          + "opics/*}/partitions\332A\004name\022\256\001\n\nListTopic"
          + "s\022-.google.cloud.pubsublite.v1.ListTopic"
          + "sRequest\032..google.cloud.pubsublite.v1.Li"
          + "stTopicsResponse\"A\202\323\344\223\0022\0220/v1/admin/{par"
          + "ent=projects/*/locations/*}/topics\332A\006par"
          + "ent\022\273\001\n\013UpdateTopic\022..google.cloud.pubsu"
          + "blite.v1.UpdateTopicRequest\032!.google.clo"
          + "ud.pubsublite.v1.Topic\"Y\202\323\344\223\002?26/v1/admi"
          + "n/{topic.name=projects/*/locations/*/top"
          + "ics/*}:\005topic\332A\021topic,update_mask\022\226\001\n\013De"
          + "leteTopic\022..google.cloud.pubsublite.v1.D"
          + "eleteTopicRequest\032\026.google.protobuf.Empt"
          + "y\"?\202\323\344\223\0022*0/v1/admin/{name=projects/*/lo"
          + "cations/*/topics/*}\332A\004name\022\336\001\n\026ListTopic"
          + "Subscriptions\0229.google.cloud.pubsublite."
          + "v1.ListTopicSubscriptionsRequest\032:.googl"
          + "e.cloud.pubsublite.v1.ListTopicSubscript"
          + "ionsResponse\"M\202\323\344\223\002@\022>/v1/admin/{name=pr"
          + "ojects/*/locations/*/topics/*}/subscript"
          + "ions\332A\004name\022\352\001\n\022CreateSubscription\0225.goo"
          + "gle.cloud.pubsublite.v1.CreateSubscripti"
          + "onRequest\032(.google.cloud.pubsublite.v1.S"
          + "ubscription\"s\202\323\344\223\002G\"7/v1/admin/{parent=p"
          + "rojects/*/locations/*}/subscriptions:\014su"
          + "bscription\332A#parent,subscription,subscri"
          + "ption_id\022\267\001\n\017GetSubscription\0222.google.cl"
          + "oud.pubsublite.v1.GetSubscriptionRequest"
          + "\032(.google.cloud.pubsublite.v1.Subscripti"
          + "on\"F\202\323\344\223\0029\0227/v1/admin/{name=projects/*/l"
          + "ocations/*/subscriptions/*}\332A\004name\022\312\001\n\021L"
          + "istSubscriptions\0224.google.cloud.pubsubli"
          + "te.v1.ListSubscriptionsRequest\0325.google."
          + "cloud.pubsublite.v1.ListSubscriptionsRes"
          + "ponse\"H\202\323\344\223\0029\0227/v1/admin/{parent=project"
          + "s/*/locations/*}/subscriptions\332A\006parent\022"
          + "\354\001\n\022UpdateSubscription\0225.google.cloud.pu"
          + "bsublite.v1.UpdateSubscriptionRequest\032(."
          + "google.cloud.pubsublite.v1.Subscription\""
          + "u\202\323\344\223\002T2D/v1/admin/{subscription.name=pr"
          + "ojects/*/locations/*/subscriptions/*}:\014s"
          + "ubscription\332A\030subscription,update_mask\022\253"
          + "\001\n\022DeleteSubscription\0225.google.cloud.pub"
          + "sublite.v1.DeleteSubscriptionRequest\032\026.g"
          + "oogle.protobuf.Empty\"F\202\323\344\223\0029*7/v1/admin/"
          + "{name=projects/*/locations/*/subscriptio"
          + "ns/*}\332A\004name\032M\312A\031pubsublite.googleapis.c"
          + "om\322A.https://www.googleapis.com/auth/clo"
          + "ud-platformB\321\001\n!com.google.cloud.pubsubl"
          + "ite.protoB\nAdminProtoP\001ZDgoogle.golang.o"
          + "rg/genproto/googleapis/cloud/pubsublite/"
          + "v1;pubsublite\252\002\032Google.Cloud.PubSubLite."
          + "V1\312\002\032Google\\Cloud\\PubSubLite\\V1\352\002\035Google"
          + "::Cloud::PubSubLite::V1b\006proto3"
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
              com.google.protobuf.EmptyProto.getDescriptor(),
              com.google.protobuf.FieldMaskProto.getDescriptor(),
              com.google.protobuf.TimestampProto.getDescriptor(),
            });
    internal_static_google_cloud_pubsublite_v1_CreateTopicRequest_descriptor =
        getDescriptor().getMessageTypes().get(0);
    internal_static_google_cloud_pubsublite_v1_CreateTopicRequest_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_pubsublite_v1_CreateTopicRequest_descriptor,
            new java.lang.String[] {
              "Parent", "Topic", "TopicId",
            });
    internal_static_google_cloud_pubsublite_v1_GetTopicRequest_descriptor =
        getDescriptor().getMessageTypes().get(1);
    internal_static_google_cloud_pubsublite_v1_GetTopicRequest_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_pubsublite_v1_GetTopicRequest_descriptor,
            new java.lang.String[] {
              "Name",
            });
    internal_static_google_cloud_pubsublite_v1_GetTopicPartitionsRequest_descriptor =
        getDescriptor().getMessageTypes().get(2);
    internal_static_google_cloud_pubsublite_v1_GetTopicPartitionsRequest_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_pubsublite_v1_GetTopicPartitionsRequest_descriptor,
            new java.lang.String[] {
              "Name",
            });
    internal_static_google_cloud_pubsublite_v1_TopicPartitions_descriptor =
        getDescriptor().getMessageTypes().get(3);
    internal_static_google_cloud_pubsublite_v1_TopicPartitions_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_pubsublite_v1_TopicPartitions_descriptor,
            new java.lang.String[] {
              "PartitionCount",
            });
    internal_static_google_cloud_pubsublite_v1_ListTopicsRequest_descriptor =
        getDescriptor().getMessageTypes().get(4);
    internal_static_google_cloud_pubsublite_v1_ListTopicsRequest_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_pubsublite_v1_ListTopicsRequest_descriptor,
            new java.lang.String[] {
              "Parent", "PageSize", "PageToken",
            });
    internal_static_google_cloud_pubsublite_v1_ListTopicsResponse_descriptor =
        getDescriptor().getMessageTypes().get(5);
    internal_static_google_cloud_pubsublite_v1_ListTopicsResponse_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_pubsublite_v1_ListTopicsResponse_descriptor,
            new java.lang.String[] {
              "Topics", "NextPageToken",
            });
    internal_static_google_cloud_pubsublite_v1_UpdateTopicRequest_descriptor =
        getDescriptor().getMessageTypes().get(6);
    internal_static_google_cloud_pubsublite_v1_UpdateTopicRequest_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_pubsublite_v1_UpdateTopicRequest_descriptor,
            new java.lang.String[] {
              "Topic", "UpdateMask",
            });
    internal_static_google_cloud_pubsublite_v1_DeleteTopicRequest_descriptor =
        getDescriptor().getMessageTypes().get(7);
    internal_static_google_cloud_pubsublite_v1_DeleteTopicRequest_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_pubsublite_v1_DeleteTopicRequest_descriptor,
            new java.lang.String[] {
              "Name",
            });
    internal_static_google_cloud_pubsublite_v1_ListTopicSubscriptionsRequest_descriptor =
        getDescriptor().getMessageTypes().get(8);
    internal_static_google_cloud_pubsublite_v1_ListTopicSubscriptionsRequest_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_pubsublite_v1_ListTopicSubscriptionsRequest_descriptor,
            new java.lang.String[] {
              "Name", "PageSize", "PageToken",
            });
    internal_static_google_cloud_pubsublite_v1_ListTopicSubscriptionsResponse_descriptor =
        getDescriptor().getMessageTypes().get(9);
    internal_static_google_cloud_pubsublite_v1_ListTopicSubscriptionsResponse_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_pubsublite_v1_ListTopicSubscriptionsResponse_descriptor,
            new java.lang.String[] {
              "Subscriptions", "NextPageToken",
            });
    internal_static_google_cloud_pubsublite_v1_CreateSubscriptionRequest_descriptor =
        getDescriptor().getMessageTypes().get(10);
    internal_static_google_cloud_pubsublite_v1_CreateSubscriptionRequest_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_pubsublite_v1_CreateSubscriptionRequest_descriptor,
            new java.lang.String[] {
              "Parent", "Subscription", "SubscriptionId", "SkipBacklog",
            });
    internal_static_google_cloud_pubsublite_v1_GetSubscriptionRequest_descriptor =
        getDescriptor().getMessageTypes().get(11);
    internal_static_google_cloud_pubsublite_v1_GetSubscriptionRequest_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_pubsublite_v1_GetSubscriptionRequest_descriptor,
            new java.lang.String[] {
              "Name",
            });
    internal_static_google_cloud_pubsublite_v1_ListSubscriptionsRequest_descriptor =
        getDescriptor().getMessageTypes().get(12);
    internal_static_google_cloud_pubsublite_v1_ListSubscriptionsRequest_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_pubsublite_v1_ListSubscriptionsRequest_descriptor,
            new java.lang.String[] {
              "Parent", "PageSize", "PageToken",
            });
    internal_static_google_cloud_pubsublite_v1_ListSubscriptionsResponse_descriptor =
        getDescriptor().getMessageTypes().get(13);
    internal_static_google_cloud_pubsublite_v1_ListSubscriptionsResponse_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_pubsublite_v1_ListSubscriptionsResponse_descriptor,
            new java.lang.String[] {
              "Subscriptions", "NextPageToken",
            });
    internal_static_google_cloud_pubsublite_v1_UpdateSubscriptionRequest_descriptor =
        getDescriptor().getMessageTypes().get(14);
    internal_static_google_cloud_pubsublite_v1_UpdateSubscriptionRequest_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_pubsublite_v1_UpdateSubscriptionRequest_descriptor,
            new java.lang.String[] {
              "Subscription", "UpdateMask",
            });
    internal_static_google_cloud_pubsublite_v1_DeleteSubscriptionRequest_descriptor =
        getDescriptor().getMessageTypes().get(15);
    internal_static_google_cloud_pubsublite_v1_DeleteSubscriptionRequest_fieldAccessorTable =
        new com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
            internal_static_google_cloud_pubsublite_v1_DeleteSubscriptionRequest_descriptor,
            new java.lang.String[] {
              "Name",
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
    com.google.protobuf.EmptyProto.getDescriptor();
    com.google.protobuf.FieldMaskProto.getDescriptor();
    com.google.protobuf.TimestampProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
