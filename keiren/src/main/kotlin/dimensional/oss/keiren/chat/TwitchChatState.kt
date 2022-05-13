package dimensional.oss.keiren.chat

public sealed class TwitchChatState {
    public object Disconnected : TwitchChatState()

    public object Connected : TwitchChatState()
}
