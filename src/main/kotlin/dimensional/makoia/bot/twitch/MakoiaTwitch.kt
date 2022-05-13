package dimensional.makoia.bot.twitch

import dimensional.makoia.bot.Makoia
import dimensional.makoia.bot.config.Config
import dimensional.makoia.bot.event.MakoiaLifecycleEvent
import dimensional.makoia.bot.event.MakoiaTwitchEvent
import dimensional.oss.keiren.Twitch
import dimensional.oss.keiren.event.ReadyEvent
import dimensional.oss.keiren.on
import mu.KotlinLogging
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MakoiaTwitch : KoinComponent {
    companion object {
        private val log = KotlinLogging.logger {  }
    }

    private val makoia by inject<Makoia>()
    private val config by inject<Config>()

    val twitch = Twitch()

    init {
        twitch.on<ReadyEvent> {
            log.info { "* connected to twitch" }
            makoia.events.emit(MakoiaTwitchEvent.Connected)
        }

        makoia.on<MakoiaLifecycleEvent.Initializing> {
            log.info { "* initializing twitch chat..." }

            val identity = config.makoia.twitch.identity
            twitch.login(identity)
        }

        makoia.on<MakoiaLifecycleEvent.Initialized> {
            log.info { "joining channels ${config.makoia.twitch.channels}" }
            twitch.chat.join(config.makoia.twitch.channels)
        }
    }
}
