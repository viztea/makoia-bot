package dimensional.makoia.twitch.event.irc

import dimensional.makoia.twitch.Twitch
import dimensional.makoia.twitch.event.TwitchEvent
import dimensional.makoia.twitch.irc.message.IrcMessage

interface IrcMessageHandler {
    /**
     * Handles an incoming IRC message.
     *
     * @param message The message to handle.
     */
    suspend fun handle(twitch: Twitch, message: IrcMessage): TwitchEvent?
}
