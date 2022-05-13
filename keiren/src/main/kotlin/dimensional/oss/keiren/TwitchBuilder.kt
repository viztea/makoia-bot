package dimensional.oss.keiren

import dimensional.oss.keiren.event.TwitchEvent
import dimensional.oss.keiren.chat.TwitchChatBuilder
import dimensional.oss.keiren.eventsub.EmptyTwitchEventSub
import dimensional.oss.keiren.eventsub.TwitchEventSub
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow

public class TwitchBuilder {
    /**
     *
     */
    public var chatBuilder: TwitchChatBuilder.() -> Unit = {}

    /**
     * The event sub builder to use.
     */
    public var eventSubBuilder: () -> TwitchEventSub = { EmptyTwitchEventSub }

    /**
     * The event flow to use.
     */
    public var eventFlow: MutableSharedFlow<TwitchEvent> = MutableSharedFlow(extraBufferCapacity = Int.MAX_VALUE)

    /**
     * The coroutine dispatcher to use.
     */
    public var dispatcher: CoroutineDispatcher = Dispatchers.Default

    public fun chat(block: TwitchChatBuilder.() -> Unit): TwitchBuilder {
        chatBuilder = block
        return this
    }

    /**
     * Creates a new [Twitch]
     */
    public fun build(): Twitch {
        val irc = TwitchChatBuilder()
            .apply(chatBuilder)
            .build()

        val eventSub = eventSubBuilder()
        return Twitch(irc, eventSub, eventFlow, dispatcher)
    }
}
