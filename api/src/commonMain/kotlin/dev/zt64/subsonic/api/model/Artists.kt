package dev.zt64.subsonic.api.model

import kotlinx.serialization.Serializable

@Serializable
public data class Artists(
    val ignoredArticles: String,
    val index: List<Index>
) {
    @Serializable
    public data class Index(
        val name: String,
        val artist: List<Artist>
    )
}