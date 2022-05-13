package dimensional.oss.keiren.eventsub

import dimensional.common.encodeHex
import dimensional.oss.keiren.eventsub.json.EventNotification
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.JsonObject
import mu.KotlinLogging
import javax.crypto.Mac
import javax.crypto.SecretKey

public class TwitchSubscription(
    public val eventSub: TwitchEventSub,
    public val secret: SecretKey
) {
    public companion object {
        private const val HMAC_PREFIX = "sha256="
        private val log = KotlinLogging.logger {  }
    }

    public suspend fun verifySignature(call: ApplicationCall, body: String): Boolean {
        val message = call.request.twitchMessageId() + call.request.twitchMessageTimestamp() + body

        /* get hmac signature */
        val signature = hmacSha1(message.toByteArray())?.let { HMAC_PREFIX + it }
        if (signature == null) {
            log.debug { "Unable to get HmacSHA256 signature." }
            return false
        }

        println("our signature: $signature")
        println("their signature: ${call.request.twitchMessageSignature()}")

        return call.request.twitchMessageSignature() == signature
    }

    public suspend fun onNotification(call: ApplicationCall, body: String) {
        val notification = DefaultTwitchEventSub.json.decodeFromString<JsonObject>(body)
        println(notification)

        call.response.status(HttpStatusCode.NoContent)
    }

    private fun hmacSha1(message: ByteArray): String? {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(secret)

        return mac.doFinal(message)?.encodeHex()
    }
}
