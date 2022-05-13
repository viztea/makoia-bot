package dimensional.oss.keiren.credentials

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import mu.KotlinLogging

// TODO: improve the flow of requesting credentials and keeping it updated.

public class TwitchClientCredentials(
    private val httpClient: HttpClient,
    override val clientId: String,
    private val clientSecret: String
) : TwitchCredentials {
    public companion object {
        private val log = KotlinLogging.logger {  }
    }

    private val mutex = Mutex()

    private var token: AccessToken? = null

    override suspend fun getHeader(): String? {
        val currentToken = token
        if (currentToken == null || currentToken.expiresAt < System.currentTimeMillis()) {
            log.debug { "Token expired or did not exist, requesting new" }
            requestBearerToken()
        }

        return token?.header
    }

    override suspend fun refresh() {
        requestBearerToken()
    }

    private suspend fun requestBearerToken(): AccessToken? = mutex.withLock {
        try {
            val response = httpClient.post("https://id.twitch.tv/oauth2/token") {
                parameter("client_id", clientId)
                parameter("client_secret", clientSecret)
                parameter("grant_type", "client_credentials")
            }

            when (response.status) {
                HttpStatusCode.OK -> {
                    token = response.body<AccessToken>()
                    token
                }

                else -> {
                    log.error { "Received status ${response.status} while requesting bearer token." }
                    null
                }
            }
        } catch (ex: Exception) {
            log.error(ex) { "Failed to retrieve access token due to exception." }
            null
        }
    }

    @Serializable
    private data class AccessToken(
        @SerialName("access_token")
        val accessToken: String,
        @SerialName("expires_in")
        val expiresIn: Int,
        @SerialName("token_type")
        val tokenType: String
    ) {
        val expiresAt: Long = System.currentTimeMillis() + (expiresIn * 1000)

        val header: String
            get() = when (tokenType) {
                "bearer" -> "Bearer $accessToken"
                else -> accessToken
            }
    }
}
