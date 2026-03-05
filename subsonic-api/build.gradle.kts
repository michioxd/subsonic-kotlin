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

mavenPublishing {
    coordinates("dev.zt64.subsonic", "subsonic-api", version.toString())
    publishToMavenCentral()

    // Only sign if signing keys are configured
    val hasSigningKey = providers
        .environmentVariable("ORG_GRADLE_PROJECT_signingInMemoryKey")
        .orElse(providers.gradleProperty("signing.keyId"))
        .orNull != null

    if (hasSigningKey) {
        signAllPublications()
    }

    val path = "zt64/subsonic-kotlin"

    pom {
        name = "subsonic-kotlin"
        description = "Kotlin Multiplatform library for the Subsonic API"
        inceptionYear = "2026"
        url = "https://github.com/$path"

        licenses {
            license {
                name = "MIT License"
                url = "https://opensource.org/licenses/MIT"
            }
        }

        developers {
            developer {
                id = "zt64"
                name = "zt64"
                url = "https://zt64.dev"
            }
        }

        scm {
            url = "https://github.com/$path"
            connection = "scm:git:github.com/$path.git"
            developerConnection = "scm:git:ssh://github.com/$path.git"
        }
    }
}