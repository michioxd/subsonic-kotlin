package dev.zt64.subsonic.client

import dev.zt64.subsonic.api.SubsonicApi
import dev.zt64.subsonic.api.SubsonicApiImpl
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

private const val USER_AGENT = "github.com/zt64/subsonic-kotlin"
private const val API_VERSION = "1.16.1"
private const val CLIENT_NAME = "subsonic-kotlin"

/**
 * Subsonic API client
 *
 * Based off Open Subsonic API
 */
public class SubsonicClient(
    private val httpClient: HttpClient,
    private val baseUrl: String,
    private val authParams: Map<String, String>
) : SubsonicApi by SubsonicApiImpl(httpClient, baseUrl, authParams) {
    public companion object {
        /**
         * Create a SubsonicClient with API key authentication
         */
        public operator fun invoke(
            apiUrl: String,
            auth: SubsonicAuth,
            client: String = CLIENT_NAME,
            userAgent: String = USER_AGENT,
            engine: HttpClientEngine? = null,
            clientConfig: HttpClientConfig<*>.() -> Unit = {}
        ): SubsonicClient {
            val authParams = buildAuthParams(client, auth)
            val config = getHttpClientConfiguration(apiUrl, client, auth, userAgent)
            val httpClient = if (engine != null) {
                HttpClient(engine) {
                    config()
                    clientConfig()
                }
            } else {
                HttpClient {
                    config()
                    clientConfig()
                }
            }
            return SubsonicClient(httpClient, apiUrl, authParams)
        }
    }
}

private fun buildAuthParams(client: String, auth: SubsonicAuth): Map<String, String> {
    return buildMap {
        put("f", "json")
        put("v", API_VERSION)
        put("c", client)

        when (auth) {
            is SubsonicAuth.Key -> {
                put("apiKey", auth.apiKey)
            }

            is SubsonicAuth.Token -> {
                put("u", auth.username)
                put("t", auth.token)
                put("s", auth.salt)
            }

            SubsonicAuth.Unsecured -> {
                // No auth params
            }
        }
    }
}

private fun getHttpClientConfiguration(
    apiUrl: String,
    client: String = CLIENT_NAME,
    auth: SubsonicAuth,
    userAgent: String = USER_AGENT
): HttpClientConfig<*>.() -> Unit = {
    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
            }
        )
    }

    install(UserAgent) {
        agent = userAgent
    }

    install(HttpTimeout)

    defaultRequest {
        contentType(ContentType.Application.Json)
        url(apiUrl)

        url {
            parameters.appendAll(
                parameters {
                    set("f", "json")
                    set("v", API_VERSION)
                    set("c", client)

                    when (auth) {
                        is SubsonicAuth.Key -> {
                            set("apiKey", auth.apiKey)
                        }

                        is SubsonicAuth.Token -> {
                            set("u", auth.username)
                            set("t", auth.token)
                            set("s", auth.salt)
                        }

                        SubsonicAuth.Unsecured -> {
                            TODO()
                        }
                    }
                }
            )
        }
    }
}