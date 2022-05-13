package dimensional.makoia.bot.config

import dimensional.oss.keiren.chat.TwitchChatIdentity

data class Config(
    val makoia: Makoia
) {
    data class Makoia(
        val discord: Discord,
        val twitch: Twitch
    ) {
        data class Discord(
            val token: String
        )

        data class Twitch(
            val identity: TwitchChatIdentity,
            val prefix: String = "!",
            val channels: List<String> = listOf("mykytuhh"),
        )
    }
}
