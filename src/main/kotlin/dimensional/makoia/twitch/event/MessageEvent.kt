package dimensional.makoia.twitch.event

import dimensional.makoia.twitch.Twitch
import dimensional.makoia.twitch.entity.Message

/**
 * An event emitted when a `PRIVMSG` message is received.
 */
data class MessageEvent(
    override val twitch: Twitch,
    val message: Message,
) : TwitchEvent()
