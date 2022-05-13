package dimensional.oss.keiren.eventsub

import io.ktor.server.application.*

public interface TwitchEventSub {
    public val server: Application

    public val subscriptions: Map<SubscriptionType, TwitchSubscription>

    public suspend fun handleRequest(call: ApplicationCall)

    public suspend fun subscribe(event: SubscriptionType, condition: Map<String, String>): TwitchSubscription
}

