package dev.zt64.subsonic.api.model

import kotlinx.serialization.Serializable

@Serializable
public sealed class AlbumListType(public val value: String) {
    public data object Random : AlbumListType("random")

    public data object Newest : AlbumListType("newest")

    public data object Highest : AlbumListType("highest")

    public data object Frequent : AlbumListType("frequent")

    public data object Recent : AlbumListType("recent")

    public data object Starred : AlbumListType("starred")

    public data object AlphabeticalByName : AlbumListType("alphabeticalByName")

    public data object AlphabeticalByArtist : AlbumListType("alphabeticalByArtist")

    @Serializable
    public data class ByYear(val fromYear: Int, val toYear: Int) : AlbumListType("byYear")

    @Serializable
    public data class ByGenre(val genre: String) : AlbumListType("byGenre")
}