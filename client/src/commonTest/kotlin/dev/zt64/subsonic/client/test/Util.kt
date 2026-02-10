package dev.zt64.subsonic.client.test

import dev.zt64.subsonic.client.SubsonicAuth
import dev.zt64.subsonic.client.SubsonicClient
import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlin.test.assertEquals
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

val USERNAME = env("SUBSONIC_USERNAME") ?: "zoot"
val PASSWORD = env("SUBSONIC_PASSWORD") ?: "ihatemedia3"
val EMAIL = "$USERNAME@abc.xyz"

val OK_RESPONSE = """
{
  "subsonic-response": {
    "status": "ok",
    "version": "1.16.1",
    "type": "AwesomeServerName",
    "serverVersion": "0.1.3 (tag)",
    "openSubsonic": true
  }
}
""".trimIndent()

val responses = mutableMapOf<String, String?>()

@OptIn(ExperimentalUuidApi::class)
val client by lazy {
    val apiUrl = env("SUBSONIC_KT_API_URL") ?: "https://navi.maize.moe/rest/"

    if (apiUrl != null) {
        SubsonicClient(
            apiUrl = apiUrl,
            auth = SubsonicAuth.Token(USERNAME, PASSWORD)
        )
    } else {
        val apiKey = Uuid.generateV4().toHexString()
        SubsonicClient(
            engine = MockEngine { req ->
                val parameters = req.url.parameters

                if ("apiKey" in parameters) {
                    assertEquals(apiKey, parameters["apiKey"])
                } else {
                    assertEquals(USERNAME, parameters["u"])

                    // if ("p" in parameters) {
                    //     assertEquals(PASSWORD, parameters["p"])
                    // } else {
                    //     assertEquals(PASSWORD, parameters["t"])
                    //     assertEquals(PASSWORD, parameters["s"])
                    // }
                }

                assertEquals(parameters["v"], "1.16.1")
                assertEquals(parameters["c"], "subsonic-kotlin")
                assertEquals(parameters["f"], "json")

                val endpoint = req.url.segments.first()

                if (endpoint in responses) {
                    val response = responses[endpoint]

                    respond(response)
                } else {
                    error("Unhandled $endpoint")
                }
            },
            apiUrl = env("SUBSONIC_API_URL") ?: "subsonic.zt64.dev",
            auth = SubsonicAuth.Token(USERNAME, PASSWORD)
        )
    }
}

expect fun env(name: String): String?

suspend fun <T, R> testEndpoint(
    endpoint: String,
    response: String? = null,
    call: suspend SubsonicClient.() -> T,
    block: (result: T) -> R
): R {
    responses[endpoint] = response
    return block(client.call())
}

suspend fun <T> testEndpoint(
    endpoint: String,
    response: String? = null,
    call: suspend SubsonicClient.() -> T
): T? {
    responses[endpoint] = response
    return client.call().also { println(it) }
}

fun MockRequestHandleScope.respond(content: String? = null): HttpResponseData {
    val str = if (content == null) {
        OK_RESPONSE
    } else {
        buildString {
            append(
                """
                {
                    "subsonic-response": {
                        "status": "ok",
                        "version": "1.16.1",
                        "type": "AwesomeServerName",
                        "serverVersion": "0.1.3 (tag)",
                        "openSubsonic": true,
                """.trimIndent()
            )

            append(content)
            append("}}")
        }
    }

    return respond(
        content = str,
        status = HttpStatusCode.OK,
        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
    )
}