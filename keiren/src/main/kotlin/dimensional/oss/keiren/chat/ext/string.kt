package dimensional.oss.keiren.chat.ext

public data class SpecialChar(val escaped: String, val actual: String)

public val specialChars: List<SpecialChar> = listOf(
    SpecialChar("""\\""", """\"""),
    SpecialChar("""\:""", ";"),
    SpecialChar("""\s""", " "),
    SpecialChar("""\n""", "\n"),
    SpecialChar("""\r""", "\r"),
    SpecialChar("""\""", ""),
)

private val decodeRegex = """\\\\|\\:|\\s|\\r|\\n|\\""".toRegex()

public fun String.decodeIrc(): String =
    decodeRegex.replace(this) { match -> specialChars.find { it.escaped == match.value }?.actual ?: "" }

private val encodeRegex = """\|;|\s|\r|\n""".toRegex()

public fun String.encodeIrc(): String =
    encodeRegex.replace(this) { match -> specialChars.find { it.actual == match.value }?.escaped ?: "" }

public val String.escaped: String
    get() = replace("\r", "\\r").replace("\n", "\\n")
