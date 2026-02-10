package dev.zt64.subsonic.api.model

import kotlinx.serialization.Serializable

@Serializable
public data class TokenInfo(
    val username: String
)