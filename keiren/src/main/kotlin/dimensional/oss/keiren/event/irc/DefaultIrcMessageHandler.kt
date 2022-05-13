package dimensional.oss.keiren.event.irc

import dimensional.common.ext.firstOfInstance
import dimensional.oss.keiren.Twitch
import dimensional.oss.keiren.entity.Message
import dimensional.oss.keiren.event.MessageEvent
import dimensional.oss.keiren.event.ReadyEvent
import dimensional.oss.keiren.event.TwitchEvent
import dimensional.oss.keiren.chat.irc.IrcCommandParameter
import dimensional.oss.keiren.chat.irc.IrcMessage
import mu.KotlinLogging

public open class DefaultIrcMessageHandler : IrcMessageHandler {
    public companion object {
        private val log = KotlinLogging.logger {  }
    }

    override suspend fun handle(twitch: Twitch, message: IrcMessage): TwitchEvent? {
        return when (message.command.name) {
            "001" -> {
                log.trace { "successfully authenticated" }
                ReadyEvent(twitch)
            }

            "PING" -> {
                log.trace { "ping ponged!" }
                twitch.chat.connection.send("PONG ${message.command.params.firstOfInstance<IrcCommandParameter.Content>().value}")
                null
            }

            "PRIVMSG" -> {
                val msg = Message(twitch, message)
                MessageEvent(twitch, msg)
            }

            else -> null
        }
    }
}
