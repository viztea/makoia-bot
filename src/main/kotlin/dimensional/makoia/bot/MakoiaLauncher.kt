package dimensional.makoia.bot

import dimensional.makoia.mainModule
import dimensional.makoia.tools.get
import kotlinx.coroutines.job
import mixtape.oss.kyuso.currentThread
import org.koin.core.context.startKoin

suspend fun main() {
    currentThread.name = "Makoia-Main-Thread"

    startKoin {
        modules(mainModule)
    }

    get<Makoia>().initialize()

    /*  */
    Makoia.coroutineContext.job.join()
}
