package dev.zt64.subsonic.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public enum class Role {
    @SerialName("guest")
    GUEST,
    @SerialName("admin")
    ADMIN,
    @SerialName("settings")
    SETTINGS,
    @SerialName("download")
    DOWNLOAD,
    @SerialName("upload")
    UPLOAD,
    @SerialName("playlist")
    PLAYLIST,
    @SerialName("cover_art")
    COVER_ART,
    @SerialName("comment")
    COMMENT,
    @SerialName("podcast")
    PODCAST,
    @SerialName("stream")
    STREAM,
    @SerialName("jukebox")
    JUKEBOX,
    @SerialName("share")
    SHARE,
    @SerialName("videoconversion")
    VIDEO_CONVERSION
}