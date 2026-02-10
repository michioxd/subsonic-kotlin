package dev.zt64.subsonic.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Lyrics(
    val artist: String,
    val title: String,
    val value: String
)

@Serializable
public data class StructuredLyrics internal constructor(
    val lang: String,
    val synced: Boolean,
    val displayArtist: String,
    val displayTitle: String,
    val offset: Int,
    @SerialName("line")
    val lines: List<Line>
) {
    /**
     * Line
     *
     * @property start In milliseconds
     * @property value
     * @constructor Create empty Line
     */
    @Serializable
    public data class Line internal constructor(
        val start: Int,
        val value: String
    )
}