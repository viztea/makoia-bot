package dimensional.makoia.twitch.irc.ext

data class SpecialChar(val escaped: String, val actual: String)

val specialChars = listOf(
    SpecialChar("""\\""", """\"""),
    SpecialChar("""\:""", ";"),
    SpecialChar("""\s""", " "),
    SpecialChar("""\n""", "\n"),
    SpecialChar("""\r""", "\r"),
    SpecialChar("""\""", ""),
)

private val decodeRegex = """\\\\|\\:|\\s|\\r|\\n|\\""".toRegex()

fun String.decodeIrc(): String =
    decodeRegex.replace(this) { match -> specialChars.find { it.escaped == match.value }?.actual ?: "" }

private val encodeRegex = """\|;|\s|\r|\n""".toRegex()

fun String.encodeIrc(): String =
    encodeRegex.replace(this) { match -> specialChars.find { it.actual == match.value }?.escaped ?: "" }

val String.escaped: String
    get() = replace("\r", "\\r").replace("\n", "\\n")
