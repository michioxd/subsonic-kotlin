package dev.zt64.subsonic.api.model

import kotlinx.serialization.Serializable

@Serializable
public data class Podcast(
    val id: String,
    val url: String,
    val title: String,
    val description: String,
)
