package io.shortcut.network

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.http.*

const val PEOPLEINSPACE_URL = "api.open-notify.org"
const val GITHUB_URL = "api.github.com"

class HttpClientFactory {

    fun create() = HttpClient {
        defaultRequest {
            url {
                host = GITHUB_URL
                protocol = URLProtocol.HTTPS
            }
        }
        Json {
            serializer = KotlinxSerializer(
                    json = kotlinx.serialization.json.Json(from = kotlinx.serialization.json.Json.Default) {
                        ignoreUnknownKeys = true
                        useArrayPolymorphism = true
                    }
            )
        }
        Logging {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }
    }
}
