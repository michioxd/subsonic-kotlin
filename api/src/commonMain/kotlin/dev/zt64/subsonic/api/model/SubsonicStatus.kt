package dev.zt64.subsonic.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public enum class SubsonicStatus {
    @SerialName("ok")
    OK,

    @SerialName("failed")
    FAILED
}