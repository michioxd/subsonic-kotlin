package dev.zt64.subsonic.api.model

import dev.zt64.subsonic.api.model.serializer.SubsonicDurationSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Instant

@Serializable
public data class Song(
    override val id: String,
    val parent: String? = null,
    val title: String,
    val artist: String,
    val artistId: String,
    val album: String,
    val bitRate: Int,
    val bitDepth: Int,
    @Serializable(SubsonicDurationSerializer::class)
    val duration: Duration,
    @SerialName("samplingRate")
    val sampleRate: Int,
    val channelCount: Int,
    val userRating: Int = -1,
    val averageRating: Int = -1,
    val playCount: Int = 0,
    val track: Int,
    val year: Int? = null,
    val genre: String? = null,
    val size: Long,
    val discNumber: Int,
    val suffix: String,
    val contentType: String,
    val path: String,
    val albumId: String,
    override val starred: Instant? = null,
    override val coverArt: String? = null
) : Resource {
    @Serializable
    public enum class Type {
        MUSIC,
        VIDEO
    }
}