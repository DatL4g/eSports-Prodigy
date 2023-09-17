plugins {
    kotlin("jvm")
    alias(libs.plugins.mosaic)
}

group = "dev.datlag.esports.prodigy.terminal"

dependencies {
    implementation(libs.airline)

    implementation(project(":game"))
}
