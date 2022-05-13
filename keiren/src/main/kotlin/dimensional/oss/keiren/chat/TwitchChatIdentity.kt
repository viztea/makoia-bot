package dimensional.oss.keiren.chat

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public data class TwitchChatIdentity(
    val nickname: String,
    val authorization: String,
    val capabilities: List<TwitchChatCapability> = emptyList()
) {
    public class Builder {
        public lateinit var nickname: String

        public lateinit var authorization: String

        public var capabilities: List<TwitchChatCapability> = emptyList()

        public fun build(): TwitchChatIdentity {
            return TwitchChatIdentity(nickname, authorization, capabilities)
        }
    }
}

public inline fun chatIdentity(init: TwitchChatIdentity.Builder.() -> Unit): TwitchChatIdentity {
    contract {
        callsInPlace(init, InvocationKind.EXACTLY_ONCE)
    }

    return TwitchChatIdentity.Builder()
        .apply(init)
        .build()
}
