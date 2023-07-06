rootProject.name = "eSports Prodigy"

include("app", "app:sekret")
include("color")
include("model")
include("game")
include(
    "datastore",
    "datastore:proto"
)
include("nanoid")
include("charts")
include("network")
include("database")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://plugins.gradle.org/m2/") }
    }
}