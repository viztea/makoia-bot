package dimensional.makoia.twitch.irc

class IrcBuilder(val identity: IrcIdentity) {
    var url: String = "wss://irc-ws.chat.twitch.tv:443"

    fun build(): Irc = Irc(url, identity)
}
