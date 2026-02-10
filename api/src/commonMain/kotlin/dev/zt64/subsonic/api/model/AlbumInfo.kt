package dev.zt64.subsonic.api.model

import kotlinx.serialization.Serializable

@Serializable
public data class AlbumInfo(
    val musicBrainzId: String? = null,
    val largeImageUrl: String? = null,
    val mediumImageUrl: String? = null,
    val smallImageUrl: String? = null,
    val lastFmUrl: String? = null,
    val notes: String? = null
)