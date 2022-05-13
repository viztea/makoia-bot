package dimensional.common.ext

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

/**
 * Returns the first element in the collection that is of type [R], or `null` if an element could not be found.
 *
 * @param R the type of the element to find
 */
public suspend inline fun <reified R> Flow<*>.firstOfInstance(): R {
    return filterIsInstance<R>().first()
}

/**
 * Returns the first element in the collection that is of type [R], or `null` if an element could not be found.
 *
 * @param R the type of the element to find
 */
public suspend inline fun <reified R> Flow<*>.firstOfInstanceOrNull(): R? {
    return filterIsInstance<R>().firstOrNull()
}
