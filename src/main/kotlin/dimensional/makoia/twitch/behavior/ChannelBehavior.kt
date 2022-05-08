package dimensional.makoia.twitch.behavior

import dimensional.makoia.twitch.Twitch
import dimensional.makoia.twitch.TwitchObject

interface ChannelBehavior : TwitchObject {
    /**
     * The name of this channel.
     */
    val name: String

    /**
     * Sends a message to this channel.
     */
    suspend fun sendMessage(message: String) = twitch.irc.send("PRIVMSG #$name :$message")
}

fun ChannelBehavior(twitch: Twitch, name: String): ChannelBehavior = object : ChannelBehavior {
    override val name = name

    override val twitch: Twitch = twitch

    override fun toString() = "ChannelBehavior(name=$name)"
}
