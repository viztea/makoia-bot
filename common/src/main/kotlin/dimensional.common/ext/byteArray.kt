package dimensional.common.ext

public class MutableByteArrayCursor(data: ByteArray, public val autoResize: Boolean = false) {
    public var cursor: Int = 0
    public val isExhausted: Boolean get() = cursor == data.size + 1

    public var data: ByteArray = data
        internal set

    public fun write(byte: Int): Unit =
        write(byte.toByte())

    public fun write(byte: Byte) {
        requireBytes(1)
        data[cursor] = byte
        cursor++
    }

    public fun grow(by: Int): Boolean =
        resize(cursor + by)

    public fun resize(newSize: Int): Boolean {
        if (data.size < newSize) {
            return false
        }

        val newData = ByteArray(newSize)
        if (newSize < data.size) {
            data.copyInto(newData, 0, 0, newSize)
        } else {
            data.copyInto(newData, 0, 0, data.size)
        }

        data = newData
        return true
    }

    private fun requireBytes(len: Int) {
        if (!isExhausted) {
            return
        }

        require(autoResize) { "Cannot write $len bytes to a buffer of size ${data.size}" }
        grow(len)
    }

}

public fun ByteArray.withCursor(resize: Boolean = false, block: MutableByteArrayCursor.() -> Unit): ByteArray {
    return MutableByteArrayCursor(this, resize)
        .apply(block)
        .data
}
