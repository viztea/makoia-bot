package dimensional.oss.keiren.eventsub

import dimensional.common.decodeHex
import dimensional.common.encodeHex
import dimensional.oss.keiren.TwitchApi
import dimensional.oss.keiren.eventsub.json.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import mu.KotlinLogging
import java.time.Instant
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

public class DefaultTwitchEventSub(
    public val api: TwitchApi,
    override val server: Application,
    public val callbackUrl: (SubscriptionType) -> String,
) : TwitchEventSub {
    public companion object {
        private val log = KotlinLogging.logger {  }
        public val json: Json = Json {
            isLenient = true
            ignoreUnknownKeys = true
            encodeDefaults = true
            prettyPrint = true
        }

        public const val TWITCH_MESSAGE_TYPE_WEBHOOK_VERIFICATION: String = "webhook_callback_verification"
        public const val TWITCH_MESSAGE_TYPE_NOTIFICATION: String = "notification"
        public const val TWITCH_MESSAGE_TYPE_REVOCATION: String = "revocation"

        public fun generateSecret(): SecretKey {
            val keyGenerator = KeyGenerator.getInstance("AES")
            keyGenerator.init(256)

            return keyGenerator.generateKey()
        }
    }

    public val subscriptionMap: MutableMap<SubscriptionType, TwitchSubscription> = mutableMapOf()

    override val subscriptions: Map<SubscriptionType, TwitchSubscription>
        get() = subscriptionMap

    override suspend fun handleRequest(
        call: ApplicationCall
    ) {
        try {
            println("${call.request.origin.method} ${call.request.origin.uri} ${call.request.headers}")

            /* verify content-type is correct */
            if (call.request.contentType() != ContentType.Application.Json) {
                call.respond(HttpStatusCode.BadRequest, "Content-Type must be application/json")
                return
            }

            val body = call.receiveText()

            /* verify timestamp. */
            val timestamp = call.request.twitchMessageTimestamp()
                ?.let { Instant.parse(it) }
                ?: return call.respond(HttpStatusCode.BadRequest, "Missing header: Twitch-Eventsub-Message-Timestamp")

            // TODO: check if timestamp is older than 10 minutes (as per Twitch's documentation)

            /* handle subscription types. */
            val type = call.request.twitchMessageType()
                ?: return call.respond(HttpStatusCode.BadRequest, "Missing header: Twitch-Eventsub-Message-Type")

            when (type) {
                TWITCH_MESSAGE_TYPE_NOTIFICATION -> {
                    val subscriptionType = call.request.twitchSubscriptionType()
                        ?: return call.respond(HttpStatusCode.BadRequest, "Missing header: Twitch-Eventsub-Subscription-Type")

                    println(subscriptionType)

                    /* get the local subscription for this notification */
                    val subscription = subscriptionMap[subscriptionType]
                        ?: return call.respond(HttpStatusCode.NotAcceptable, "Unable to handle subscription.")

                    /* verify the signature. */
                    if (!subscription.verifySignature(call, body)) {
                        log.debug { "Received EventSub notification with an invalid signature." }
                        return call.respond(HttpStatusCode.Forbidden, "Invalid signature")
                    }

                    subscription.onNotification(call, body)
                }

                TWITCH_MESSAGE_TYPE_WEBHOOK_VERIFICATION -> {
                    val (challenge) = json.decodeFromString<VerificationPayload>(body)
                    call.respondText(challenge)
                }

                TWITCH_MESSAGE_TYPE_REVOCATION -> {
                    val subscriptionType = call.request.twitchSubscriptionType()
                        ?: return call.respond(HttpStatusCode.BadRequest, "Missing header: Twitch-Eventsub-Subscription-Type")

                    /* check if we indeed do have a subscription for the received type. */
                    val subscription = subscriptionMap[subscriptionType]
                        ?: return call.respond(HttpStatusCode.NotAcceptable, "Unable to handle subscription revocation.")

                    /* verify the message signature */
                    if (!subscription.verifySignature(call, body)) {
                        log.debug { "Received revocation message with an invalid signature." }
                        return call.respond(HttpStatusCode.Forbidden, "Invalid signature")
                    }

                    /* remove the subscription */
                    subscriptionMap.remove(subscriptionType)
                }

                else -> {
                    call.respond(HttpStatusCode.BadRequest, "Unknown message type: $type")
                }
            }
        } catch (ex: Exception) {
            log.error(ex) { "Error occurred while handling eventsub callback request." }
            call.respond(HttpStatusCode.InternalServerError, "Internal server error")
        }
    }

    @OptIn(InternalAPI::class)
    override suspend fun subscribe(event: SubscriptionType, condition: Map<String, String>): TwitchSubscription {
        require (!subscriptionMap.containsKey(event)) {
            "A subscription already exists for event: $event"
        }

        /*  */
        val secret = "poo poo poo poo".encodeToByteArray().encodeHex()

        /* create the response secret */
        val response = api.request("/eventsub/subscriptions") {
            method = HttpMethod.Post

            body = TextContent(
                json.encodeToString(
                    CreateEventSubSubscription(
                        type = event.name,
                        condition = JsonObject(condition.mapValues { (_, v) -> JsonPrimitive(v) }),
                        transport = CreateEventSubSubscription.Transport(
                            secret = secret,
                            callback = callbackUrl(event),
                            method = TransportMethod.Webhook
                        )
                    )
                ).also(::println),
                ContentType.Application.Json
            )

        }

        println(response.bodyAsText())
        if (response.status.isSuccess()) {
            val subscription = TwitchSubscription(this, SecretKeySpec(secret.decodeHex(), "HmacSHA256"))
            subscriptionMap[event] = subscription

            return subscription
        }

//         TODO: handle errors better
        error("Unable to create subscription: ${response.status}")
    }

    @Serializable
    public data class RevocationPayload(val subscription: Subscription)

    @Serializable
    public data class VerificationPayload(val challenge: String)
}
