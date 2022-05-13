package dimensional.oss.keiren.entity

import dimensional.common.ext.firstOfInstance
import dimensional.oss.keiren.Twitch
import dimensional.oss.keiren.behavior.MessageBehavior
import dimensional.oss.keiren.chat.irc.IrcCommandParameter
import dimensional.oss.keiren.chat.irc.IrcMessage
import dimensional.oss.keiren.chat.irc.IrcMessagePart

/**
 * Represents a message sent by a user.
 */
public class Message(
    override val twitch: Twitch,
    public val data: IrcMessage,
) : MessageBehavior {
    init {
        require (data.command.name == "PRIVMSG")
    }

    /**
     * The content of this message.
     */
    public val content: String get() = data.command.params.firstOfInstance<IrcCommandParameter.Content>().value

    /**
     * The source of the message.
     */
    public val source: IrcMessagePart.Source? get() = data.source

    /**
     * The name of the channel this message was sent in.
     */
    override val channelName: String get() = data.command.params.firstOfInstance<IrcCommandParameter.Room>().value
}
