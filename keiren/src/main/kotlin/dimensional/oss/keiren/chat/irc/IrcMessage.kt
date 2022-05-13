package dimensional.oss.keiren.chat.irc

import dimensional.common.ext.firstOfInstance
import dimensional.common.ext.firstOfInstanceOrNull

public data class IrcMessage(
    val tags: Map<String, IrcMessageTag>,
    val command: IrcMessagePart.Command,
    val source: IrcMessagePart.Source?,
) {
    public companion object {
        /**
         * Parses a message from a raw string and returns the [result][IrcMessage].
         *
         * @param raw The raw message to parse.
         * @return the parsed [IrcMessage]
         */
        public fun parse(raw: String): IrcMessage = fromParts(IrcMessageParser.parse(raw))

        /**
         * Creates a new [IrcMessage] from the given [parts].
         *
         * @param parts The parts of the message.
         */
        public fun fromParts(parts: List<IrcMessagePart>): IrcMessage {
            return IrcMessage(
                tags = parts.firstOfInstanceOrNull<IrcMessagePart.Tags>()?.tags ?: emptyMap(),
                command = parts.firstOfInstance(),
                source = parts.firstOfInstanceOrNull()
            )
        }
    }
}
