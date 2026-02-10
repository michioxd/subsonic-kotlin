package dev.zt64.subsonic.api.model

import kotlinx.serialization.Serializable

@Serializable
public data class ScanStatus(
    val scanning: Boolean,
    val count: Int
)
