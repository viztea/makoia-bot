package dimensional.makoia.twitch

import dimensional.makoia.twitch.event.TwitchEvent
import dimensional.makoia.twitch.event.irc.DefaultIrcMessageHandler
import dimensional.makoia.twitch.irc.Irc
import dimensional.makoia.twitch.irc.IrcIdentity
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.*
import mu.KotlinLogging
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

val twitchOnLog = KotlinLogging.logger("Twitch.on")

class Twitch(
    val irc: Irc,
    internal val eventFlow: MutableSharedFlow<TwitchEvent>,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val ircHandler = DefaultIrcMessageHandler()

    /**
     * The coroutine scope for this [Twitch] instance.
     */
    val scope: CoroutineScope =
        CoroutineScope(dispatcher + SupervisorJob() + CoroutineName("Twitch"))

    /**
     * The events emitted by this [Twitch] instance. Call [Twitch.login] to start receiving events from the Twitch IRC server.
     */
    val events: SharedFlow<TwitchEvent>
        get() = eventFlow

    init {
        irc.events
            .buffer(UNLIMITED)
            .map { message -> ircHandler.handle(this, message) }
            .filterNotNull()
            .onEach { event -> eventFlow.emit(event) }
            .launchIn(scope)
    }

    /**
     * Joins the specified [channels].
     *
     * @param channels the channels to join
     */
    suspend fun join(vararg channels: String) =
        join(channels.toList())

    /**
     * Joins the specified [channels].
     *
     * @param channels the channels to join
     */
    suspend fun join(channels: List<String>) {
        val str = channels.joinToString(",") { "#" + it.removePrefix("#") }
        irc.send("JOIN $str")
    }

    /**
     * Connect to the Twitch IRC server.
     */
    suspend fun login() {
        irc.connect()
    }
}

/**
 * Creates a new instance of [Twitch].
 */
@OptIn(ExperimentalContracts::class)
inline fun Twitch(nickname: String, authorization: String, build: TwitchBuilder.() -> Unit = {}): Twitch {
    contract {
        callsInPlace(build, InvocationKind.EXACTLY_ONCE)
    }

    val identity = IrcIdentity(nickname, authorization)
    return TwitchBuilder(identity)
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
suspend inline fun <reified T : TwitchEvent> Twitch.on(scope: CoroutineScope = this.scope, noinline block: suspend T.() -> Unit): Job {
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
