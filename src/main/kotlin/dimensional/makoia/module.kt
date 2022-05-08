package dimensional.makoia

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import mixtape.oss.kyuso.Kyuso
import org.koin.dsl.module
import java.util.concurrent.Executors

val mainModule = module {
    single { Kyuso(Executors.newCachedThreadPool()) }

    single {
        HttpClient(CIO) {
            install(WebSockets)
        }
    }
}
