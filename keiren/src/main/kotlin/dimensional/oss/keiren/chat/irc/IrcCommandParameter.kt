package dimensional.oss.keiren.chat.irc

public sealed class IrcCommandParameter {
    public data class Room(val value: String) : IrcCommandParameter()

    public data class Content(val value: String) : IrcCommandParameter()

    public data class Unknown(val value: String) : IrcCommandParameter()
}
