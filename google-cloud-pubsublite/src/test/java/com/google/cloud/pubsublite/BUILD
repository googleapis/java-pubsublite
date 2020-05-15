load("//testing/build_defs:junit_test_suites.bzl", "junit_test_suites")

licenses(["notice"])

java_library(
    name = "tests",
    testonly = True,
    srcs = glob(
        ["**/*.java"],
    ),
    deps = [
        "//google/cloud/pubsublite/v1:admin_java_grpc",
        "//google/cloud/pubsublite/v1:admin_java_proto",
        "//google/cloud/pubsublite/v1:common_java_proto",
        "//google/cloud/pubsublite/v1:cursor_java_grpc",
        "//google/cloud/pubsublite/v1:cursor_java_proto",
        "//google/cloud/pubsublite/v1:publisher_java_grpc",
        "//google/cloud/pubsublite/v1:publisher_java_proto",
        "//google/cloud/pubsublite/v1:subscriber_java_grpc",
        "//google/cloud/pubsublite/v1:subscriber_java_proto",
        "//google/protobuf:empty_java_proto",
        "//google/protobuf:field_mask_java_proto",
        "//google/protobuf:struct_java_proto",
        "//java/com/google/common/base",
        "//java/com/google/common/collect",
        "//java/com/google/common/util/concurrent",
        "//java/com/google/protobuf:protobuf_lite",
        "//java/com/google/protobuf/util:time",
        "//third_party/java/cloud/pubsub:client",
        "//third_party/java/error_prone:annotations",
        "//third_party/java/gax",
        "//third_party/java/google_api_common",
        "//third_party/java/grpc:core",
        "//third_party/java/grpc:inprocess",
        "//third_party/java/grpc:stub",
        "//third_party/java/grpc:testing",
        "//third_party/java/junit",
        "//third_party/java/mockito",
        "//third_party/java/threetenbp",
        "//third_party/java/truth",
        "//third_party/pubsub_lite/java_client",
    ],
)

junit_test_suites(
    name = "junit_tests",
    default_size = "medium",
    sizes = ["medium"],
    src_dir = "third_party/pubsub_lite/java_client/google-cloud-pubsublite/src/test/java",
    deps = [":tests"],
)
