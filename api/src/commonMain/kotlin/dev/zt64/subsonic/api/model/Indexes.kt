package dev.zt64.subsonic.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Indexes(
    @SerialName("shortcut")
    val shortcut: List<Artist>,
    @SerialName("child")
    val child: List<Artist>,
    @SerialName("index")
    val index: List<Index>
) {
    @Serializable
    public data class Index(
        val name: String,
        @SerialName("artist")
        val artists: List<Artist>
    )
}
