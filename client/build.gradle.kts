plugins {
    id("kmp-configuration")
    alias(libs.plugins.kotlin.serialization)
}

description = "Kotlin multiplatform client for the SubSonic API"

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.api)

                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.contentNegotiation)
                implementation(libs.ktor.serialization)
                implementation(libs.hash.md5)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.coroutines.test)
                implementation(libs.ktor.test)
            }
        }

        jvmTest {
            dependencies {
                implementation(libs.ktor.client.okhttp)
            }
        }

        nativeTest {
            dependencies {
                implementation(libs.ktor.client.curl)
            }
        }

        jsTest {
            dependencies {
                implementation(libs.ktor.client.js)
            }
        }
    }
}