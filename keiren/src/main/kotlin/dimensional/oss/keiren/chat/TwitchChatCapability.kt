package dimensional.oss.keiren.chat

public enum class TwitchChatCapability {
    /**
     * Get additional metadata for command and membership messages.
     * See [Twitch Tags](https://dev.twitch.tv/docs/irc/tags)
     */
    Tags,

    /**
     * Ability to send `PRIVMSG` commands that include Twitch chat commands and Twitch-specific irc commands.
     */
    Commands,

    /**
     * Receive `JOIN` and `PART` messages when users join and leave the chat room.
     */
    Membership;

    internal val actual: String
        get() = "twitch.tv/${name.lowercase()}"
}
