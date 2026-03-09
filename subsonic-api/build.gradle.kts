import dev.zt64.subsonic.gradle.publish

plugins {
    id("kmp-configuration")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.publish)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.serialization.json)
                implementation(libs.datetime)
                implementation(libs.hash.md5)
                implementation(libs.ktor.client.core)
            }
        }
    }
}

publish("subsonic-api")