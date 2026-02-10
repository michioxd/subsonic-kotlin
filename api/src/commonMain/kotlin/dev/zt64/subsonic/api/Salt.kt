package dev.zt64.subsonic.api

import org.kotlincrypto.hash.md.MD5

public object Salt {
    private val chars = ('a'..'z')+('0'..'9')

    /**
     * Generates salt used for enc
     */
    public fun generateSalt(): String {
        return CharArray(6) { chars.random() }.concatToString()
    }

    /**
     * Calculates the salted hash of a password for user authentication
     */
    public fun saltPassword(password: String, salt: String = generateSalt()): Pair<String, String> {
        val salted = MD5()
            .digest((password + salt).encodeToByteArray())
            .toHexString()

        return salted to salt
    }
}