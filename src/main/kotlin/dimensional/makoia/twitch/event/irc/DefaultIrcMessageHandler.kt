package dimensional.makoia.twitch.event.irc

import dimensional.makoia.tools.ext.firstOfInstance
import dimensional.makoia.twitch.Twitch
import dimensional.makoia.twitch.entity.Message
import dimensional.makoia.twitch.event.MessageEvent
import dimensional.makoia.twitch.event.ReadyEvent
import dimensional.makoia.twitch.event.TwitchEvent
import dimensional.makoia.twitch.irc.message.IrcCommandParameter
import dimensional.makoia.twitch.irc.message.IrcMessage
import mu.KotlinLogging

open class DefaultIrcMessageHandler : IrcMessageHandler {
    companion object {
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
                twitch.irc.send("PONG ${message.command.params.firstOfInstance<IrcCommandParameter.Content>().value}")
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
