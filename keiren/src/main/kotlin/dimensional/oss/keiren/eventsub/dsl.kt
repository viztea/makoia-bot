package dimensional.oss.keiren.eventsub

import io.ktor.server.request.*

public fun ApplicationRequest.twitchSubscriptionType(): SubscriptionType? =
    header("Twitch-Eventsub-Subscription-Type")?.let { SubscriptionType.valueOf(it) }

public fun ApplicationRequest.twitchMessageType(): String? =
    header("Twitch-Eventsub-Message-Type")

public fun ApplicationRequest.twitchMessageId(): String? =
    header("Twitch-Eventsub-Message-Id")

public fun ApplicationRequest.twitchMessageTimestamp(): String? =
    header("Twitch-Eventsub-Message-Timestamp")

public fun ApplicationRequest.twitchMessageSignature(): String? =
    header("Twitch-Eventsub-Message-Signature")
