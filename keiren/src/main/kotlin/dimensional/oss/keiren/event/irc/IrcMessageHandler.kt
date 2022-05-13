package dimensional.oss.keiren.event.irc

import dimensional.oss.keiren.Twitch
import dimensional.oss.keiren.event.TwitchEvent
import dimensional.oss.keiren.chat.irc.IrcMessage

public interface IrcMessageHandler {
    /**
     * Handles an incoming IRC message.
     *
     * @param message The message to handle.
     */
    public suspend fun handle(twitch: Twitch, message: IrcMessage): TwitchEvent?
}
