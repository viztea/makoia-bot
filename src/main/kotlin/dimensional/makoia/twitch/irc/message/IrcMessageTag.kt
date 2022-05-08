package dimensional.makoia.twitch.irc.message

import dimensional.makoia.twitch.common.Badge

sealed class IrcMessageTag {
    data class Badges(val badges: List<Badge>) : IrcMessageTag()

    data class BadgeInfo(val badges: Map<String, String>) : IrcMessageTag()

    data class Color(val color: String) : IrcMessageTag()

//    data class Emotes(val emotes: Map<String, >) : Tag()

    sealed class Primitive : IrcMessageTag() {
        data class Text(val value: String) : Primitive()

        data class Int(val value: Long) : Primitive()

        data class Number(val value: Double) : Primitive()

        data class Bool(val value: Boolean) : Primitive()
    }

    data class Unknown(val value: String) : IrcMessageTag()
}
