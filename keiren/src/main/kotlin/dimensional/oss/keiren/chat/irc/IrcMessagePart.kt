package dimensional.oss.keiren.chat.irc

public sealed class IrcMessagePart {
    public data class Tags(val tags: Map<String, IrcMessageTag>) : IrcMessagePart()

    public data class Source(val host: String, val nickname: String?, val username: String?) : IrcMessagePart()

    public data class Command(val name: String, val params: List<IrcCommandParameter>) : IrcMessagePart()
}
