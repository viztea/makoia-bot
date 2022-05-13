package dimensional.oss.keiren.event

import dimensional.oss.keiren.Twitch
import dimensional.oss.keiren.entity.Message

/**
 * An event emitted when a `PRIVMSG` message is received.
 */
public data class MessageEvent(
    override val twitch: Twitch,
    val message: Message,
) : TwitchEvent()
