package dev.zt64.subsonic.api.model

import kotlinx.serialization.Serializable

@Serializable
public data class Bookmark<T : Resource>(
    val changed: String,
    val comment: String,
    val created: String,
    val position: Int,
    val username: String,
    val entry: T,
)