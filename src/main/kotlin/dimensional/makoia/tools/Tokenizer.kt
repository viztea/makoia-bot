package dimensional.makoia.tools

@DslMarker
annotation class TokenizerDsl

@TokenizerDsl
open class Tokenizer<T>(val content: String) {
    private val tokenList: MutableList<T> = mutableListOf()

    /**
     * The current position of this tokenizer.
     */
    protected var cursor: Int = 0

    /**
     * The tokens that have been tokenized.
     */
    val tokens: List<T>
        get() = tokenList.toList()

    /**
     * Whether the tokenizer has reached the end of the content.
     */
    val eof: Boolean
        get() = cursor >= content.length

    /**
     * The remaining content.
     */
    val remaining
        get() = content.drop(cursor)

    /**
     * Adds the provided [token]
     *
     * @param token the [Token][T] to add.
     */
    fun addToken(token: T) {
        tokenList.add(token)
    }

    /**
     * Increments the cursor by the given [amount].
     *
     * @return the previous cursor value.
     */
    fun increment(amount: Int = 1): Int = cursor.also { cursor += amount }
}

@TokenizerDsl
fun <T> String.tokenize(tokenize: Tokenizer<T>.() -> Unit): List<T> {
    val tokenizer = Tokenizer<T>(this)
        .apply(tokenize)

    return tokenizer.tokens
}
