package dimensional.oss.keiren.chat

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

public class TwitchChat(public val connection: TwitchChatConnection) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob() + CoroutineName("Twitch Chat"))

    /**
     *
     */
    public suspend fun connect(identity: TwitchChatIdentity): Unit = coroutineScope {
       launch {
           connection.state.first { it is TwitchChatState.Connected }
           requestCapabilities(identity.capabilities)
           connection.send("PASS ${identity.authorization}")
           connection.send("NICK ${identity.nickname}")
       }

        connection.connect()
    }

    /**
     * Requests the following [TwitchChatCapabilities][TwitchChatCapability].
     *
     * @parma capabilities the capabilities to request.
     */
    public suspend fun requestCapabilities(capabilities: List<TwitchChatCapability>) {
        if (capabilities.isEmpty()) {
            return
        }

        requireConnected()
        connection.send("CAP REQ :${capabilities.joinToString(" ")}")
    }

    /**
     * Joins the specified [channels].
     *
     * @param channels the channels to join
     */
    public suspend fun join(vararg channels: String): Unit =
        join(channels.toList())

    /**
     * Joins the specified [channels].
     *
     * @param channels the channels to join
     */
    public suspend fun join(channels: List<String>) {
        requireConnected()

        val str = channels.joinToString(",") { "#" + it.removePrefix("#") }
        connection.send("JOIN $str")
    }

    /**
     * Sends a [message] to the specified [channel].
     *
     * @param channel the channel to send the message to
     * @param message the message to send.
     * @throws IllegalStateException if this [TwitchChat] is not connected, see [TwitchChat.connect].
     */
    public suspend fun privmsg(channel: String, message: String) {
        requireConnected()
        connection.send("PRIVMSG #$channel :$message")
    }

    private fun requireConnected() {
        require(connection.state.value == TwitchChatState.Connected) {
            "Not currently connected to Twitch Chat, see TwitchChat#connect(identity)"
        }
    }
}
