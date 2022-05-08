package dimensional.makoia.twitch.irc.message

sealed class IrcMessagePart {
    data class Tags(val tags: Map<String, IrcMessageTag>) : IrcMessagePart()

    data class Source(val host: String, val nickname: String?, val username: String?) : IrcMessagePart()

    data class Command(val name: String, val params: List<IrcCommandParameter>) : IrcMessagePart()
}
