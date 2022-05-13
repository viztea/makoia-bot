package dimensional.makoia

import com.sksamuel.hoplite.ConfigLoader
import com.sksamuel.hoplite.sources.EnvironmentVariablesPropertySource
import dimensional.makoia.bot.Makoia
import dimensional.makoia.bot.config.Config
import dimensional.makoia.bot.config.ExternalFilePropertySource
import dimensional.makoia.bot.discord.MakoiaDiscord
import dimensional.makoia.bot.twitch.MakoiaTwitch
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import mixtape.oss.kyuso.Kyuso
import org.koin.dsl.module
import java.util.concurrent.Executors

val mainModule = module {
    single(createdAtStart = true) { MakoiaDiscord() }

    single(createdAtStart = true) { MakoiaTwitch() }

    single { Makoia() }

    single { Kyuso(Executors.newCachedThreadPool()) }

    single {
        HttpClient(CIO) {
            install(WebSockets)
        }
    }

    single<Config> {
        val loader = ConfigLoader {
            addSource(ExternalFilePropertySource("makoia.toml"))
            addSource(EnvironmentVariablesPropertySource(useUnderscoresAsSeparator = true, allowUppercaseNames = true))
        }

        loader.loadConfigOrThrow()
    }
}
