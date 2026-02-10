package dev.zt64.subsonic.client

import org.kotlincrypto.hash.md.MD5

private const val SALT_LENGTH = 6

/**
 * Subsonic auth
 */
public sealed interface SubsonicAuth {
    public data object Unsecured : SubsonicAuth

    public class Key(public val apiKey: String) : SubsonicAuth

    public class Token(public val username: String, password: String) : SubsonicAuth {
        private val chars = ('a'..'z') + ('0'..'9')

        public val salt: String = CharArray(SALT_LENGTH) { chars.random() }.concatToString()
        public val token: String = MD5()
            .digest((password + salt).encodeToByteArray())
            .toHexString()
    }
}