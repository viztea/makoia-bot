package dimensional.makoia.twitch.irc

import dimensional.makoia.twitch.irc.ext.escaped
import dimensional.makoia.twitch.irc.message.IrcMessage
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.utils.io.CancellationException
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import mu.KotlinLogging
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

class Irc(private val url: String, private val identity: IrcIdentity) : KoinComponent {
    companion object {
        private val log = KotlinLogging.logger {  }

        private fun <T> ReceiveChannel<T>.asFlow() = flow {
            try {
                for (value in this@asFlow) emit(value)
            } catch (ignore: CancellationException) {
                //reading was stopped from somewhere else, ignore
            }
        }
    }

    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob() + CoroutineName("Twitch IRC"))
    private val connectMutex = Mutex()

    private lateinit var socket: DefaultWebSocketSession

    private val eventFlow: MutableSharedFlow<IrcMessage> = MutableSharedFlow(extraBufferCapacity = Int.MAX_VALUE)
    private val stateFlow: MutableStateFlow<IrcState> = MutableStateFlow(IrcState.Disconnected)

    val state: StateFlow<IrcState>
        get() = stateFlow

    val events: SharedFlow<IrcMessage>
        get() = eventFlow

    suspend fun connect() = connectMutex.withLock {
        while (true) {
            log.debug { "creating irc connection..." }
            scope.launch {
                state.first { it is IrcState.Connected }
                send("CAP REQ :twitch.tv/tags")
                send("PASS ${identity.authorization}")
                send("NICK ${identity.nickname}")
            }

            try {
                socket = get<HttpClient>().webSocketSession {
                    url(this@Irc.url)
                }
            } catch (e: Exception) {
                log.error(e) { "failed to create irc connection" }
                break
            }

            stateFlow.update { IrcState.Connected }
            log.debug { "successfully created irc connection" }

            try {
                readSocket()
            } catch (ex: Exception) {
                log.error(ex) { "failed to read from irc connection" }
            }

            log.debug { "irc connection closing" }

            try {
                handleClose()
            } catch (ex: Exception) {
                log.error(ex) { "failed to handle irc connection close." }
            }

            log.debug { "handled irc connection close" }
            stateFlow.update { IrcState.Disconnected }

            delay(5.seconds)
        }
    }

    private suspend fun handleClose() {
        val reason = withTimeoutOrNull(1500) { socket.closeReason.await() }
            ?: return

        log.trace { "irc connection closed: ${reason.code} ${reason.message}" }
    }

    suspend fun send(message: String) {
        log.trace { "Twitch <<< $message" }
        socket.send(Frame.Text(message))
    }

    suspend fun read(frame: Frame) {
        val raw = frame.data
            .decodeToString()
            .trim()

        if (raw.contains("\r\n")) {
            val commands = raw.split("\r\n")
            for (command in commands) {
                readCommand(command)
            }
        } else {
            readCommand(raw)
        }
    }

    @OptIn(ExperimentalTime::class)
    suspend fun readCommand(raw: String) {
        try {
            val (message, took) = measureTimedValue {
                IrcMessage.parse(raw)
            }

            log.debug { "Twitch >>> ${raw.escaped} ($took)" }
            eventFlow.emit(message)
        } catch (ex: Exception) {
            log.error(ex) { "Failed to parse and emit message: '${raw.escaped}'" }
        }
    }

    suspend fun readSocket() {
        socket.incoming.asFlow().collect {
            when (it) {
                is Frame.Text, is Frame.Binary -> read(it)
                else -> log.debug { "Received non-text/binary frame." }
            }
        }
    }
}
