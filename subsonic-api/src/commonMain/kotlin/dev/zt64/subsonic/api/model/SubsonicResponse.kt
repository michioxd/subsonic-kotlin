package dev.zt64.subsonic.api.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

internal class SubsonicResponseSerializer<T : Any>(
    val tSerializer: KSerializer<T>
) : KSerializer<SubsonicResponse<T>> {
    override val descriptor = buildClassSerialDescriptor(
        "SubsonicResponse",
        tSerializer.descriptor
    )

    override fun deserialize(decoder: Decoder): SubsonicResponse<T> {
        require(decoder is JsonDecoder)

        val element = decoder.decodeJsonElement().jsonObject
        val status = element.jsonObject["status"]!!.jsonPrimitive.content
        return if (status == "ok") {
            if (element.entries.size == 6) {
                // There is a data field
                val dataEntry = element.entries.last()
                val dataKey = dataEntry.key
                val dataValue = dataEntry.value
                val obj = element.jsonObject.toMutableMap().apply {
                    remove(dataKey)
                    this["data"] = dataValue
                }

                decoder.json.decodeFromJsonElement(
                    SubsonicResponse.Success.serializer(tSerializer),
                    JsonObject(obj)
                )
            } else {
                SubsonicResponse.Empty.serializer().deserialize(decoder)
            }
        } else {
            SubsonicResponse.Error.serializer().deserialize(decoder)
        }
    }

    override fun serialize(encoder: Encoder, value: SubsonicResponse<T>) {
        error("Not needed")
    }
}

@Serializable
public enum class SubsonicStatus {
    @SerialName("ok")
    OK,

    @SerialName("failed")
    FAILED
}

/**
 * Generic Subsonic response
 *
 * @param T
 * @property status Status of the response
 * @property version API version
 * @property type Server name
 * @property serverVersion Server version
 * @property openSubsonic
 */
@Serializable(SubsonicResponseSerializer::class)
public sealed interface SubsonicResponse<out T : Any> {
    public val status: SubsonicStatus
    public val version: String
    public val type: String
    public val serverVersion: String
    public val openSubsonic: Boolean

    /**
     * An empty response with no data, used for endpoints that return no data.
     */
    @Serializable
    public data class Empty internal constructor(
        override val status: SubsonicStatus,
        override val version: String,
        override val type: String,
        override val serverVersion: String,
        override val openSubsonic: Boolean
    ) : SubsonicResponse<Nothing>

    /**
     * Success
     *
     * @param T Type of data
     * @property data The deserialized data as [T]
     */
    @Serializable
    public data class Success<out T : Any> internal constructor(
        override val status: SubsonicStatus,
        override val version: String,
        override val type: String,
        override val serverVersion: String,
        override val openSubsonic: Boolean,
        val data: T
    ) : SubsonicResponse<T>

    /**
     * Subsonic error
     *
     * @property error
     */
    @Serializable
    public data class Error internal constructor(
        override val status: SubsonicStatus,
        override val version: String,
        override val type: String,
        override val serverVersion: String,
        override val openSubsonic: Boolean,
        public val error: Error
    ) : SubsonicResponse<Nothing> {
        @Serializable
        public data class Error internal constructor(
            val code: SubsonicErrorCode,
            val helpUrl: String? = null,
            val message: String
        )
    }
}

@Serializable
public enum class SubsonicErrorCode(public val description: String) {
    @SerialName("0")
    GENERIC("A generic error."),

    @SerialName("10")
    REQUIRED_PARAMETER_MISSING("Required parameter is missing."),

    @SerialName("20")
    INCOMPATIBLE_CLIENT_VERSION(
        "Incompatible Subsonic REST protocol version. Client must upgrade."
    ),

    @SerialName("30")
    INCOMPATIBLE_SERVER_VERSION(
        "Incompatible Subsonic REST protocol version. Server must upgrade."
    ),

    @SerialName("40")
    WRONG_USERNAME_OR_PASSWORD("Wrong username or password."),

    @SerialName("41")
    TOKEN_AUTH_NOT_SUPPORTED_LDAP("Token authentication not supported for LDAP users."),

    @SerialName("42")
    AUTH_METHOD_NOT_SUPPORTED("Provided authentication mechanism not supported."),

    @SerialName("43")
    MULTIPLE_CONFLICTING_AUTH("Multiple conflicting authentication mechanisms provided."),

    @SerialName("44")
    INVALID_API_KEY("Invalid API key."),

    @SerialName("50")
    UNAUTHORIZED("User is not authorized for the given operation."),

    @SerialName("60")
    TRIAL_EXPIRED(
        "The trial period for the Subsonic server is over. Please upgrade to Subsonic Premium. " +
            "Visit subsonic.org for details."
    ),

    @SerialName("70")
    DATA_NOT_FOUND("The requested data was not found.")
}