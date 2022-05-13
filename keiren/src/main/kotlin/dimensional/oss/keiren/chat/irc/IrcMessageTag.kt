package dimensional.oss.keiren.chat.irc

import dimensional.oss.keiren.common.Badge

public sealed class IrcMessageTag {
    public data class Badges(val badges: List<Badge>) : IrcMessageTag()

    public data class BadgeInfo(val badges: Map<String, String>) : IrcMessageTag()

    public data class Color(val color: String) : IrcMessageTag()

//    data class Emotes(val emotes: Map<String, >) : Tag()

    public sealed class Primitive : IrcMessageTag() {
        public data class Text(val value: String) : Primitive()

        public data class Int(val value: Long) : Primitive()

        public data class Number(val value: Double) : Primitive()

        public data class Bool(val value: Boolean) : Primitive()
    }

    public data class Unknown(val value: String) : IrcMessageTag()
}
