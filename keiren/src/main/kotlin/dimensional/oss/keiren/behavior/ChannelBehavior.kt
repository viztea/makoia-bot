package dimensional.oss.keiren.behavior

import dimensional.oss.keiren.Twitch
import dimensional.oss.keiren.TwitchObject

public interface ChannelBehavior : TwitchObject {
    /**
     * The name of this channel.
     */
    public val name: String

    /**
     * Sends a message to this channel.
     */
    public suspend fun sendMessage(message: String): Unit = twitch.chat.privmsg(name, message)
}

public fun ChannelBehavior(twitch: Twitch, name: String): ChannelBehavior = object : ChannelBehavior {
    override val name = name

    override val twitch: Twitch = twitch

    override fun toString() = "ChannelBehavior(name=$name)"
}
