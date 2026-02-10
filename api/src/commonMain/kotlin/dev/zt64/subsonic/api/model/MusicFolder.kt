package dev.zt64.subsonic.api.model

import kotlinx.serialization.Serializable

@Serializable
public data class MusicFolder internal constructor(
    val id: Int,
    val name: String
)