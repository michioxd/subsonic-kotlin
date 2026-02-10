package dev.zt64.subsonic.api.model

import kotlinx.serialization.Serializable
import kotlin.time.Instant

/**
 * Artist
 *
 * @property id
 * @property name
 * @property starred
 * @property coverArt
 * @property albumCount
 * @property userRating
 * @property artistImageUrl
 * @property musicBrainzId
 * @property sortName
 * @property roles
 * @constructor Create empty Artist
 */
@Serializable
public data class Artist(
    override val id: String,
    val name: String,
    override val starred: Instant? = null,
    override val coverArt: String? = null,
    val albumCount: Int = 0,
    val userRating: Int = -1,
    val artistImageUrl: String? = null,
    val musicBrainzId: String? = null,
    val sortName: String? = null,
    val roles: List<String> = emptyList(),
    val album: List<Album> = emptyList()
) : Resource