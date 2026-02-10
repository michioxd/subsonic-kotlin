package dev.zt64.subsonic.api.model

import kotlinx.serialization.Serializable
import kotlin.time.Instant

/**
 * A chat message
 *
 * @property username
 * @property time
 * @property message
 */
@Serializable
public data class ChatMessage internal constructor(
    val username: String,
    val time: Instant,
    val message: String
)