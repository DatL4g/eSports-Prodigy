plugins {
    kotlin("jvm")
    id("com.google.protobuf")
}

group = "dev.datlag.esports.prodigy.datastore.proto"

dependencies {
    api("com.google.protobuf:protobuf-java:3.23.3")
    api("io.grpc:grpc-protobuf:1.56.0")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.23.2"
    }
}
