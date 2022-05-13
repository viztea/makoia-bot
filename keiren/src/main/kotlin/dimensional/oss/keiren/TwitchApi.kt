package dimensional.oss.keiren

import dimensional.oss.keiren.credentials.TwitchCredentials
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*

public class TwitchApi(
    public val credentials: TwitchCredentials,
    public val httpClient: HttpClient
) {
    public companion object {
        private const val TWITCH_API = "https://api.twitch.tv/helix"
        private val AuthenticatedRequest = AttributeKey<Boolean>("twitch-authenticated-request")
        private val RetryCount = AttributeKey<Int>("twitch-retry-count")
    }

    public suspend fun request(endpoint: String, authenticated: Boolean = true, block: HttpRequestBuilder.() -> Unit = {}): HttpResponse {
        val statement = httpClient.request("$TWITCH_API$endpoint") {
            method = HttpMethod.Get
            if (authenticated) {
                val header = credentials.getHeader()
                    ?: error("Unable to get authorization header for Twitch API request")

                attributes.put(AuthenticatedRequest, true)
                header("Authorization", header)
                header("Client-Id", credentials.clientId)
            }

            block()
        }

        println(statement)

        if (statement.status.isSuccess()) {
            return statement
        }

        if (statement.status == HttpStatusCode.Unauthorized) {
            if (!authenticated) {
                throw RequestFailedException(endpoint, statement, "$endpoint requires authentication but credentials were not provided")
            }

            val retryCount = statement.request.attributes.getOrNull(RetryCount) ?: 0
            if (retryCount >= 5) {
                throw RequestFailedException(endpoint, statement)
            }

            credentials.refresh()
            return request(endpoint, true) {
                attributes.put(RetryCount, retryCount + 1)
                block()
            }
        }

        return statement
    }
}

public class RequestFailedException(
    endpoint: String,
    response: HttpResponse,
    message: String = "Request $endpoint failed with status: ${response.status}",
) : RuntimeException(message)
