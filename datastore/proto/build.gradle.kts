plugins {
    kotlin("jvm")
    alias(libs.plugins.protobuf)
}

group = "dev.datlag.esports.prodigy.datastore.proto"

dependencies {
    api(libs.protobuf)
    api(libs.grpc)
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.23.2"
    }
}
