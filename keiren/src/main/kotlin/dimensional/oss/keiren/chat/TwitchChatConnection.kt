package dimensional.oss.keiren.chat

import dimensional.oss.keiren.chat.ext.escaped
import dimensional.oss.keiren.chat.irc.IrcMessage
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
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

public class TwitchChatConnection(
    private val url: String,
    private val httpClient: HttpClient
) {
    public companion object {
        private val log = KotlinLogging.logger {  }

        private fun <T> ReceiveChannel<T>.asFlow() = flow {
            try {
                for (value in this@asFlow) emit(value)
            } catch (ignore: CancellationException) {
            }
        }
    }

    private lateinit var socket: DefaultWebSocketSession

    private val connectMutex = Mutex()
    private val stateFlow: MutableStateFlow<TwitchChatState> = MutableStateFlow(TwitchChatState.Disconnected)
    private val eventFlow: MutableSharedFlow<IrcMessage> = MutableSharedFlow(extraBufferCapacity = Int.MAX_VALUE)

    public val state: StateFlow<TwitchChatState>
        get() = stateFlow

    public val events: SharedFlow<IrcMessage>
        get() = eventFlow

    public suspend fun connect(): Unit = connectMutex.withLock {
        while (true) {
            log.debug { "creating chat connection..." }

            try {
                socket = httpClient.webSocketSession {
                    url(this@TwitchChatConnection.url)
                }
            } catch (e: Exception) {
                log.error(e) { "failed to create chat connection" }
                break
            }

            stateFlow.update { TwitchChatState.Connected }
            log.debug { "successfully created chat connection" }

            try {
                readSocket()
            } catch (ex: Exception) {
                log.error(ex) { "failed to read from chat connection" }
            }

            log.debug { "chat connection closing" }

            try {
                handleClose()
            } catch (ex: Exception) {
                log.error(ex) { "failed to handle chat connection close." }
            }

            log.debug { "handled chat connection close" }
            stateFlow.update { TwitchChatState.Disconnected }

            delay(5.seconds)
        }
    }

    private suspend fun handleClose() {
        val reason = withTimeoutOrNull(1500) { socket.closeReason.await() }
            ?: return

        log.trace { "chat connection closed: ${reason.code} ${reason.message}" }
    }

    public suspend fun send(message: String) {
        log.trace { "Twitch Chat <<< $message" }
        socket.send(Frame.Text(message))
    }

    private suspend fun read(frame: Frame) {
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
    private suspend fun readCommand(raw: String) {
        try {
            val (message, took) = measureTimedValue {
                IrcMessage.parse(raw)
            }

            log.trace { "Twitch Chat >>> ${raw.escaped} ($took)" }
            eventFlow.emit(message)
        } catch (ex: Exception) {
            log.error(ex) { "Failed to parse and emit message: '${raw.escaped}'" }
        }
    }

    private suspend fun readSocket() {
        socket.incoming.asFlow().collect {
            when (it) {
                is Frame.Text, is Frame.Binary -> read(it)
                else -> log.trace { "Received non-text/binary frame." }
            }
        }
    }
}
