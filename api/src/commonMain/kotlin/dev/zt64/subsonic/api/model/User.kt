package dev.zt64.subsonic.api.model

import kotlinx.serialization.*
import kotlinx.serialization.json.*

internal object UserSerializer : JsonTransformingSerializer<User>(User.generatedSerializer()) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        val user = element.jsonObject

        val roles = listOf(
            "admin" to "adminRole",
            "settings" to "settingsRole",
            "download" to "downloadRole",
            "upload" to "uploadRole"
        ).mapNotNull { (roleName, key) ->
            user[key]?.jsonPrimitive?.booleanOrNull
                ?.takeIf { it }
                ?.let { JsonPrimitive(roleName) }
        }

        return buildJsonObject {
            put("folder", user["folder"]!!)
            put("username", user["username"]!!)
            user["email"]?.let { put("email", it) }
            put("scrobblingEnabled", user["scrobblingEnabled"]!!)
            put("roles", JsonArray(roles))
        }
    }
}

@OptIn(ExperimentalSerializationApi::class)
@KeepGeneratedSerializer
@Serializable(UserSerializer::class)
public data class User(
    @SerialName("username")
    val name: String,
    val email: String? = null,
    val scrobblingEnabled: Boolean,
    val folder: List<Int> = emptyList(),
    val roles: List<Role> = emptyList()
)