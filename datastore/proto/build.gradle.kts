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
        artifact = libs.protoc.get().toString()
    }
}