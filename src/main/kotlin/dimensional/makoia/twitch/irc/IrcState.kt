package dimensional.makoia.twitch.irc

sealed class IrcState {
    object Disconnected : IrcState()

    object Connected : IrcState()
}
