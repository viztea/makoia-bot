package dimensional.makoia.twitch.irc.message

sealed class IrcCommandParameter {
    data class Room(val value: String) : IrcCommandParameter()

    data class Content(val value: String) : IrcCommandParameter()

    data class Unknown(val value: String) : IrcCommandParameter()
}
