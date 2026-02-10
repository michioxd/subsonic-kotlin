package dev.zt64.subsonic.api.model

public data class NowPlayingEntry(
    val username: String,
    val minutesAgo: Int,
    val playerId: Int,
    val playerName: String? = null,
)
