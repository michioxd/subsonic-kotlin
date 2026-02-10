package dev.zt64.subsonic.api.model

import kotlinx.serialization.Serializable

@Serializable
public data class InternetRadioStation(
    val id: String,
    val name: String,
    val streamUrl: String,
    val homepageUrl: String? = null,
)
