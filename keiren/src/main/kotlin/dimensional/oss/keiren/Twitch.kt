package dimensional.oss.keiren

import dimensional.oss.keiren.chat.TwitchChat
import dimensional.oss.keiren.event.TwitchEvent
import dimensional.oss.keiren.event.irc.DefaultIrcMessageHandler
import dimensional.oss.keiren.chat.TwitchChatIdentity
import dimensional.oss.keiren.chat.chatIdentity
import dimensional.oss.keiren.eventsub.TwitchEventSub
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.*
import mu.KLogger
import mu.KotlinLogging
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@PublishedApi
internal val twitchOnLog: KLogger = KotlinLogging.logger("Twitch.on")

public class Twitch(
    public val chat: TwitchChat,
    public val eventSub: TwitchEventSub,
    internal val eventFlow: MutableSharedFlow<TwitchEvent>,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val ircHandler = DefaultIrcMessageHandler()

    /**
     * The coroutine scope for this [Twitch] instance.
     */
    public val scope: CoroutineScope =
        CoroutineScope(dispatcher + SupervisorJob() + CoroutineName("Twitch"))

    /**
     * The events emitted by this [Twitch] instance. Call [Twitch.login] to start receiving events from the Twitch IRC server.
     */
    public val events: SharedFlow<TwitchEvent>
        get() = eventFlow

    init {
        chat.connection.events
            .buffer(UNLIMITED)
            .map { message -> ircHandler.handle(this, message) }
            .filterNotNull()
            .onEach { event -> eventFlow.emit(event) }
            .launchIn(scope)
    }

    /**
     * Connect to the Twitch IRC server.
     */
    public suspend fun login(identityBuilder: TwitchChatIdentity.Builder.() -> Unit): Unit =
        login(chatIdentity(identityBuilder))

    /**
     * Connect to the Twitch IRC server.
     */
    public suspend fun login(identity: TwitchChatIdentity) {
        chat.connect(identity)
    }
}

/**
 * Creates a new instance of [Twitch].
 */
@OptIn(ExperimentalContracts::class)
public inline fun Twitch(build: TwitchBuilder.() -> Unit = {}): Twitch {
    contract {
        callsInPlace(build, InvocationKind.EXACTLY_ONCE)
    }

    return TwitchBuilder()
        .apply(build)
        .build()
}

/**
 * Convenience method for subscribing to events from the Twitch IRC server.
 *
 * @param scope The coroutine scope to launch the listener job in.
 * @param block The block to execute when an event is received.
 * @return A [Job] that can be cancelled using [Job.cancel] to stop further processing of events.
 */
public inline fun <reified T : TwitchEvent> Twitch.on(scope: CoroutineScope = this.scope, noinline block: suspend T.() -> Unit): Job {
    return events.buffer(UNLIMITED)
        .filterIsInstance<T>()
        .onEach { event ->
            scope.launch {
                event
                    .runCatching { block() }
                    .onFailure { twitchOnLog.catching(it) }
            }
        }
        .launchIn(scope)
}
