package dev.zt64.subsonic.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class SearchResult(
    @SerialName("artist")
    val artists: List<Artist> = emptyList(),
    @SerialName("album")
    val albums: List<Album> = emptyList(),
    @SerialName("song")
    val songs: List<Song> = emptyList()
)
