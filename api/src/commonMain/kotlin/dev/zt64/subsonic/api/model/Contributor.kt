package dev.zt64.subsonic.api.model

import kotlinx.serialization.Serializable

/**
 * TODO
 *
 * @property role
 * @property subRole
 */
@Serializable
public data class Contributor(
    val role: String,
    val subRole: String
) {
}
