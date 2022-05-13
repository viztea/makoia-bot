package dimensional.common

@DslMarker
public annotation class TokenizerDsl

@TokenizerDsl
public open class Tokenizer<T>(public val content: String) {
    private val tokenList: MutableList<T> = mutableListOf()

    /**
     * The current position of this tokenizer.
     */
    protected var cursor: Int = 0

    /**
     * The tokens that have been tokenized.
     */
    public val tokens: List<T>
        get() = tokenList.toList()

    /**
     * Whether the tokenizer has reached the end of the content.
     */
    public val eof: Boolean
        get() = cursor >= content.length

    /**
     * The remaining content.
     */
    public val remaining: String
        get() = content.drop(cursor)

    /**
     * Adds the provided [token]
     *
     * @param token the [Token][T] to add.
     */
    public fun addToken(token: T) {
        tokenList.add(token)
    }

    /**
     * Increments the cursor by the given [amount].
     *
     * @return the previous cursor value.
     */
    public fun increment(amount: Int = 1): Int = cursor.also { cursor += amount }
}

@TokenizerDsl
public fun <T> String.tokenize(tokenize: Tokenizer<T>.() -> Unit): List<T> {
    val tokenizer = Tokenizer<T>(this)
        .apply(tokenize)

    return tokenizer.tokens
}
