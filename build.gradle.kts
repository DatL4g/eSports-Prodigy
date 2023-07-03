// Top-level build file where you can add configuration options common to all sub-projects/modules.
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform") version "1.8.20" apply false
    kotlin("plugin.serialization") version "1.8.20" apply false
    kotlin("android") version "1.8.20" apply false
    id("org.jetbrains.compose") version "1.4.1" apply false
    id("com.google.devtools.ksp") version "1.8.20-1.0.11" apply false
    id("com.google.protobuf") version "0.9.3" apply false
    id("com.squareup.sqldelight") version "1.5.5" apply false
    id("com.mikepenz.aboutlibraries.plugin") version "10.8.0" apply false
    id("de.jensklingenberg.ktorfit") version "1.0.0" apply false
    id("com.google.osdetector") version "1.7.3" apply false
    id("com.github.ben-manes.versions") version "0.47.0"
    id("org.gradle.android.cache-fix") version "2.7.1" apply false
    id("net.afanasev.sekret") version "0.1.4" apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://plugins.gradle.org/m2/") }
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.4.2")
        classpath("dev.icerock.moko:resources-generator:0.23.0")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://plugins.gradle.org/m2/") }
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()
    }
}

tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        isNonStable(candidate.version) && !isNonStable(currentVersion)
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}