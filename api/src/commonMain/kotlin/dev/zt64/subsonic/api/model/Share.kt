package dev.zt64.subsonic.api.model

import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
public data class Share(
    val id: String,
    val url: String,
    val description: String,
    val username: String,
    val created: Instant,
    val expires: Instant,
    val lastVisited: Instant? = null,
    val visitCount: Int,
    val items: List<Resource> = emptyList()
)