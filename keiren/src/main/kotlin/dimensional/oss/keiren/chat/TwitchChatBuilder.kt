package dimensional.oss.keiren.chat

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*

public class TwitchChatBuilder {
    public var url: String = "wss://irc-ws.chat.twitch.tv:443"

    public var httpClient: HttpClient? = null

    public fun build(): TwitchChat {
        val httpClient = httpClient ?: HttpClient(CIO) {
            install(WebSockets)
        }

        val connection = TwitchChatConnection(url, httpClient)

        return TwitchChat(connection)
    }
}
