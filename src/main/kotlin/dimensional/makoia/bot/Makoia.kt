package dimensional.makoia.bot

import dimensional.makoia.bot.event.MakoiaDiscordEvent
import dimensional.makoia.bot.event.MakoiaEvent
import dimensional.makoia.bot.event.MakoiaLifecycleEvent
import dimensional.makoia.bot.event.MakoiaTwitchEvent
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import mu.KotlinLogging
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.concurrent.thread
import kotlin.coroutines.CoroutineContext

class Makoia {
    companion object : CoroutineScope {
        private var threadCount by atomic(0)
        private val log = KotlinLogging.logger { }

        val threads: ExecutorService = Executors.newCachedThreadPool { r ->
            thread(start = false, name = "Makoia-Thread-${threadCount++}") { r.run() }
        }

        override val coroutineContext: CoroutineContext = threads.asCoroutineDispatcher() + SupervisorJob()
    }

    val events = MutableSharedFlow<MakoiaEvent>(extraBufferCapacity = Int.MAX_VALUE)

    suspend fun initialize() {
        /* we're initializing like good boys ^_^ */
        events.emit(MakoiaLifecycleEvent.Initializing)

        /* wait for twitch & discord to be ready */
        val twitch = async { events.first { it == MakoiaTwitchEvent.Connected } }
        val discord = async { events.first { it == MakoiaDiscordEvent.Ready } }

        twitch.await()
        discord.await()

        /* we're now initialized. */
        log.info { "* makoia has been initialized..." }
        events.emit(MakoiaLifecycleEvent.Initialized)
    }

    inline fun <reified T : MakoiaEvent> on(scope: CoroutineScope = Makoia, noinline block: suspend T.() -> Unit): Job {
        return events
            .filterIsInstance<T>()
            .onEach { event -> block.invoke(event) }
            .launchIn(scope)
    }
}
