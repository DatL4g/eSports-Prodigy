plugins {
    kotlin("jvm")
    id("com.google.protobuf")
}

group = "dev.datlag.esports.prodigy.datastore.proto"

dependencies {
    api("com.google.protobuf:protobuf-java:3.23.0")
    api("io.grpc:grpc-protobuf:1.55.1")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.22.3"
    }
}
