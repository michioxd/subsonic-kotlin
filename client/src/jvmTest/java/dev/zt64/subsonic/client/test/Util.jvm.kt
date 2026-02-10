package dev.zt64.subsonic.client.test

actual fun env(name: String): String? {
    return System.getenv(name)
}