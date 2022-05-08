package dimensional.makoia.tools.ext

/**
 * Returns the first element in the collection that is of type [R], or `null` if an element could not be found.
 *
 * @param R the type of the element to find
 */
inline fun <reified R> Iterable<*>.firstOfInstance(): R {
    return filterIsInstance<R>().first()
}

/**
 * Returns the first element in the collection that is of type [R], or `null` if an element could not be found.
 *
 * @param R the type of the element to find
 */
inline fun <reified R> Iterable<*>.firstOfInstanceOrNull(): R? {
    return filterIsInstance<R>().firstOrNull()
}
