package dev.zt64.subsonic.api.model

/**
 * Subsonic exception
 *
 * @constructor
 *
 * @param message
 */
public class SubsonicException(message: String) : Exception(message) {
    public constructor(code: Int, message: String) : this("$code: $message")
}