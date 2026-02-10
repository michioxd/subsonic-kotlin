package dev.zt64.subsonic.api.model

import dev.zt64.subsonic.api.model.serializer.SubsonicDurationSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Instant

/**
 * TODO
 *
 * @property id
 * @property name
 * @property version
 * @property year
 * @property coverArt
 * @property starred
 * @property duration
 * @property playCount
 * @property genre
 * @property created
 * @property songCount
 * @property played
 * @property userRating
 * @property musicBrainzId
 * @property songs
 */
@Serializable
public data class Album(
    override val id: String,
    override val name: String,
    val artist: String,
    val artistId: String,
    val version: String? = null,
    val year: Int,
    override val coverArt: String,
    override val starred: Instant? = null,
    @Serializable(SubsonicDurationSerializer::class)
    override val duration: Duration,
    val playCount: Int = 0,
    val genre: String? = null,
    val created: Instant,
    override val songCount: Int,
    val played: Instant? = null,
    val userRating: Int? = null,
    val musicBrainzId: String? = null,
    @SerialName("song")
    override val songs: List<Song> = emptyList()
) : Resource, TrackCollection