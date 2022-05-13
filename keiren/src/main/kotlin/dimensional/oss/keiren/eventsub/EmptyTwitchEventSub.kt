package dimensional.oss.keiren.eventsub

import dimensional.oss.keiren.eventsub.json.RequestCondition
import io.ktor.server.application.*

public object EmptyTwitchEventSub : TwitchEventSub {
    private fun empty(): Nothing = TODO("This is an empty implementation of TwitchEventSub")

    override val server: Application get() = empty()

    override val subscriptions: Map<SubscriptionType, TwitchSubscription> get() = empty()

    override suspend fun handleRequest(call: ApplicationCall): Unit = empty()

    override suspend fun subscribe(event: SubscriptionType, condition: Map<String, String>): TwitchSubscription = empty()
}
