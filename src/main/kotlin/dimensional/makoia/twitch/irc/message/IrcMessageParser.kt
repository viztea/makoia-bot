package dimensional.makoia.twitch.irc.message

import dimensional.makoia.tools.Tokenizer
import dimensional.makoia.tools.tokenize
import dimensional.makoia.twitch.common.Badge
import dimensional.makoia.twitch.irc.ext.decodeIrc
import io.ktor.utils.io.core.*

/**
 * Parses Twitch IRC messages.
 * Thanks to [robotty/dank-twitch-irc](https://github.com/robotty/dank-twitch-irc/blob/master/lib/message/parser/irc-message.ts)
 */
class IrcMessageParser(message: String) : Tokenizer<IrcMessagePart>(message) {
    companion object {
        fun parse(message: String): List<IrcMessagePart> {
            val parser = IrcMessageParser(message)
            parser.start()

            return parser.tokens
        }
    }

    fun start() {
        while (!eof || cursor + 1 < content.length) next()
    }

    fun next() {
        /* check for eof */
        if (eof) throw EOFException()

        /* yeah some bullshit lmao */
        val token: IrcMessagePart = when (remaining.first()) {
            /* tags */
            '@' -> {
                increment()

                val space = remaining.indexOf(' ')
                require(space >= 0)

                /* find the irc tags */
                val ircTags = remaining.take(space)
                increment(space + 1)

                /* parse and add the tags */
                val tags = ircTags.parseTags()
                IrcMessagePart.Tags(tags.mapValues { (name, tag) ->
                    when (name) {
                        "badges" -> IrcMessageTag.Badges(tag.value
                            .parseKeyValueTag().entries
                            .map { Badge(it.key, it.value) })

                        "badge-info" -> IrcMessageTag.BadgeInfo(tag.value.parseKeyValueTag())

                        else -> if (tag.value.isNotBlank() && tag.value.all { it.isDigit() }) {
                            IrcMessageTag.Primitive.Int(tag.value.toLong())
                        } else if (tag.value.isNotBlank() && tag.value.all { it.isDigit() || it == '.' }) {
                            IrcMessageTag.Primitive.Number(tag.value.toDouble())
                        } else {
                            IrcMessageTag.Primitive.Text(tag.value)
                        }
                    }
                })
            }

            /* source */
            ':' -> {
                increment()

                val space = remaining.indexOf(' ')
                require(space >= 0)

                /* find the irc source */
                val ircSource = remaining.take(space)
                increment(space + 1)

                /* parse the irc source */
                if (ircSource.contains('@')) {
                    val atIndex = ircSource.indexOf('@')

                    /* parse identity (nickname!username) */
                    val identity = ircSource.take(atIndex)
                    val (nick, user) = run {
                        val exclamationIndex = identity.indexOf('!')
                        if (exclamationIndex < 0) identity to null else identity.splitAt(exclamationIndex, true)
                    }

                    require(nick.isNotEmpty())

                    /* parse the host */
                    val host = ircSource.drop(atIndex + 1)
                    require(host.isNotBlank())

                    IrcMessagePart.Source(host, nick, user)
                } else {
                    IrcMessagePart.Source(ircSource, null, null)
                }
            }

            else -> {
                val spaceAfterCommandIdx = remaining.indexOf(' ')

                /* parse the command */
                if (spaceAfterCommandIdx > 0) {
                    /* command has parameters */

                    val (command, parameters) = remaining.splitAt(spaceAfterCommandIdx, true)
                    increment(remaining.length + 1)

                    val params = parameters.tokenize<IrcCommandParameter> {
                        while (!eof) {
                            if (remaining.startsWith(':')) {
                                this@tokenize.increment()
                                this@tokenize.addToken(IrcCommandParameter.Content(remaining))
                                break
                            }

                            /* look for a space, if there isn't one then just treat the remaining content as a single parameter and break out of the loop */
                            val spaceIdx = remaining.indexOf(' ')
                            if (spaceIdx < 0) {
                                addToken(IrcCommandParameter.Unknown(remaining))
                                break
                            }

                            /* get the parameter content */
                            val param = remaining.take(spaceIdx)
                            increment(spaceIdx + 1)

                            /* parse the parameter into a token */
                            val token = when (param.first()) {
                                '#' -> IrcCommandParameter.Room(param.drop(1))
                                else -> IrcCommandParameter.Unknown(param)
                            }

                            addToken(token)
                        }
                    }

                    IrcMessagePart.Command(command.uppercase(), params)
                } else {
                    /* command doesn't have any parameters */
                    IrcMessagePart.Command(remaining.uppercase(), emptyList())
                }
            }
        }

        addToken(token)
    }

    private fun String.parseKeyValueTag(): Map<String, String> {
        return this
            .split(',')
            .map { part -> part.split('/', limit = 2) }
            .filter { it.size == 2 }
            .associate { (key, value) -> key to value.decodeIrc() }
    }

    private fun String.parseTags(): Map<String, IrcMessageTag.Unknown> {
        return this
            .split(';')
            .map { part -> part.split('=', limit = 2) }
            .filter { it.size == 2 }
            .associate { (key, value) -> key to IrcMessageTag.Unknown(value.decodeIrc()) }
    }

    private fun String.splitAt(idx: Int, incrementStart: Boolean = false): Pair<String, String> =
        take(idx) to drop(if (incrementStart) idx + 1 else idx)
}
