package dimensional.makoia.twitch

import dimensional.makoia.twitch.event.TwitchEvent
import dimensional.makoia.twitch.irc.IrcBuilder
import dimensional.makoia.twitch.irc.IrcIdentity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow

class TwitchBuilder(val identity: IrcIdentity) {
    /**
     *
     */
    var ircBuilder: IrcBuilder.() -> Unit = {}

    /**
     *
     */
    var eventFlow: MutableSharedFlow<TwitchEvent> = MutableSharedFlow(extraBufferCapacity = Int.MAX_VALUE)

    /**
     * The coroutine dispatcher to use.
     */
    var dispatcher: CoroutineDispatcher = Dispatchers.Default

    /**
     * Creates a new [Twitch]
     */
    fun build(): Twitch {
        val irc = IrcBuilder(identity)
            .apply(ircBuilder)
            .build()

        return Twitch(irc, eventFlow, dispatcher)
    }
}
