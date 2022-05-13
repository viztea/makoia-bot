package dimensional.common

import dimensional.common.ext.withCursor

public val HEX_ALPHABET: List<Char> = ('0'..'9') + ('a'..'f') + ('A'..'F')

public fun ByteArray.encodeHex(): String =
    joinToString("") { "%02x".format(it) }

public fun String.decodeHex(): ByteArray {
    require(all { it in HEX_ALPHABET }) {
        "This string illegal hex characters"
    }

    val output = ByteArray(length / 2)
    return output.withCursor(resize = false) {
        var i = 0
        while (i < length) {
            val (h, l) = get(i) to get(i + 1)
            write((h.toDecimal() shl 4) or l.toDecimal())
            i += 2
        }
    }
}

public fun Char.toDecimal(): Int = when (this) {
    in '0'..'9' -> this - '0'
    in 'A'..'F' -> this - 'A' + 10
    in 'a'..'f' -> this - 'a' + 10
    else -> throw IllegalArgumentException("Invalid hex character: $this")
}
