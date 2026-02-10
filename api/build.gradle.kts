plugins {
    id("kmp-configuration")
    alias(libs.plugins.kotlin.serialization)
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