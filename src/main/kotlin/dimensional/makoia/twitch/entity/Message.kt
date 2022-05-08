package dimensional.makoia.twitch.entity

import dimensional.makoia.tools.ext.firstOfInstance
import dimensional.makoia.twitch.Twitch
import dimensional.makoia.twitch.behavior.MessageBehavior
import dimensional.makoia.twitch.irc.message.IrcCommandParameter
import dimensional.makoia.twitch.irc.message.IrcMessage
import dimensional.makoia.twitch.irc.message.IrcMessagePart

/**
 * Represents a message sent by a user.
 */
class Message(
    override val twitch: Twitch,
    val data: IrcMessage,
) : MessageBehavior {
    init {
        require (data.command.name == "PRIVMSG")
    }

    /**
     * The content of this message.
     */
    val content: String get() = data.command.params.firstOfInstance<IrcCommandParameter.Content>().value

    /**
     * The source of the message.
     */
    val source: IrcMessagePart.Source? get() = data.source

    /**
     * The name of the channel this message was sent in.
     */
    override val channelName: String get() = data.command.params.firstOfInstance<IrcCommandParameter.Room>().value
}
