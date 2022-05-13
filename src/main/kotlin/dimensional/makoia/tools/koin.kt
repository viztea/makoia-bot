package dimensional.makoia.tools

import com.kotlindiscord.kord.extensions.utils.getKoin

inline fun <reified T> get(): T =
    getKoin().get()
