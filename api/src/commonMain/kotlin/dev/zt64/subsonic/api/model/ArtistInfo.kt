package dev.zt64.subsonic.api.model

import kotlinx.serialization.Serializable

@Serializable
public data class ArtistInfo internal constructor(
    val musicBrainzId: String,
    val biography: String,
    val smallImageUrl: String,
    val mediumImageUrl: String,
    val largeImageUrl: String,
    val lastFmUrl: String? = null,
    val similarArtists: List<Artist> = emptyList()
)